package spouseReminder.Reminders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class DBHelper {

    private static final String DATABASE_NAME = "reminders";
    private static final String TABLE_DBVERSION = "dbversion";
    private static final String TABLE_REMINDERS = "reminders";
    private static final int DATABASE_VERSION = 1;
    private static String TAG = "DBHelper";
    Context myCtx;

    private static final int FIELD_REMINDER_ID = 1;
    private static final int FIELD_USER = 2;
    private static final int FIELD_BODY = 3;
    private static final int FIELD_DATE = 4;
    private static final int FIELD_LOCATION = 5;
    private static final int FIELD_ADDEDON = 6;
    private static final int FIELD_CURRENTALARM = 7;
    
    private static final String DBVERSION_CREATE =
        "create table " + TABLE_DBVERSION + " ("
                + "version integer not null);";

    private static final String REMINDERS_CREATE =
        "create table " + TABLE_REMINDERS + " ("
        	+ "reminderID text primary key,"
            + "user text,"
            + "body text, "
            + "date text,"
            + "location text,"
            + "addedon text,"
            + "currentalarm integer);";

    private static final String REMINDERS_DROP =
        "drop table " + TABLE_REMINDERS + ";";

    private SQLiteDatabase db;


    public DBHelper(final Context ctx) {
        myCtx = ctx;
                try {
                        db = myCtx.openOrCreateDatabase(DATABASE_NAME, 0, null);

                        // Check for the existence of the DBVERSION table
                        // If it doesn't exist than create the overall data,
                        // otherwise double check the version
                        Cursor c =
                                db.query("sqlite_master", new String[] {"name"},
                                                "type='table' and name='" + TABLE_DBVERSION + "'", null, null, null, null);
                        int numRows = c.getCount();
                        if (numRows < 1) {
                                CreateDatabase(db);
                        } else {
                                int version = 0;
                                Cursor vc = db.query(true, TABLE_DBVERSION, new String[] {"version"},
                                                null, null, null, null, null, null);
                                if (vc.getCount() > 0) {
                                    vc.moveToFirst();
                                    version = vc.getInt(0);
                                }
                                vc.close();
                                if (version != DATABASE_VERSION) {
                                        Log.e(TAG, "database version mismatch");
                                }
                        }
                        c.close();

                } catch (SQLException e) {
                        Log.d(TAG, "SQLite exception: " + e.getLocalizedMessage());
                } finally {
                        db.close();
                }
    }

    private void CreateDatabase(SQLiteDatabase db) {
        try {
                db.execSQL(DBVERSION_CREATE);
                ContentValues args = new ContentValues();
                args.put("version", DATABASE_VERSION);
                db.insert(TABLE_DBVERSION, null, args);

                db.execSQL(REMINDERS_CREATE);
        } catch (SQLException e) {
                Log.d(TAG, "SQLite exception: " + e.getLocalizedMessage());
        }
    }

    public void deleteDatabase() {
        try {
            db = myCtx.openOrCreateDatabase(DATABASE_NAME, 0, null);
            db.execSQL(REMINDERS_DROP);
            db.execSQL(REMINDERS_CREATE);
        } catch (SQLException e) {
            Log.d(TAG, "SQLite exception: " + e.getLocalizedMessage());
        } finally {
            db.close();
        }
    }

    public void close() {
        try {
                db.close();
            } catch (SQLException e) {
                Log.d(TAG, "close exception: " + e.getLocalizedMessage());
            }
    }

    public void addReminder(ReminderEntry entry) {
    	SimpleDateFormat textDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        ContentValues initialValues = new ContentValues();
        initialValues.put("reminderID", entry.reminderID);
        initialValues.put("user", entry.user);
        initialValues.put("body", entry.body);
        initialValues.put("date", textDateFormat.format(entry.date));
        initialValues.put("location", entry.location);
        initialValues.put("addedon", dateFormat.format(entry.addedOn));

        try {
        	db = myCtx.openOrCreateDatabase(DATABASE_NAME, 0, null);
            db.insert(TABLE_REMINDERS, null, initialValues);
        } catch (SQLException e) {
            Log.d(TAG, "SQLite exception: " + e.getLocalizedMessage());
        } finally {
            db.close();
        }
    }

    public final void deleteReminder(String Id) {
        try {
            db = myCtx.openOrCreateDatabase(DATABASE_NAME, 0, null);
            db.delete(TABLE_REMINDERS, "reminderID=" + Id, null);
        } catch (SQLException e) {
            Log.d(TAG, "SQLite exception: " + e.getLocalizedMessage());
        } finally {
            db.close();
        }
    }

    public final Cursor fetchAllRows() {
        Cursor c;

        db = myCtx.openOrCreateDatabase(DATABASE_NAME, 0, null);
        c = db.rawQuery("select rowid _id,* from reminders", null);

        return c;
    }

    public final ArrayList<ReminderEntry> fetchAllRowsList() {
    	SimpleDateFormat textDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    	Cursor remCursor = fetchAllRows();
    	ArrayList<ReminderEntry> rems = new ArrayList<ReminderEntry>();
    	ReminderEntry row;
    	remCursor.moveToFirst();
        do {
        	 row = new ReminderEntry();
        	 row.reminderID = remCursor.getString(FIELD_REMINDER_ID);
             row.user = remCursor.getString(FIELD_USER);
             row.body = remCursor.getString(FIELD_BODY);
             try {
				row.date = textDateFormat.parse(remCursor.getString(FIELD_DATE));
			} catch (ParseException e) {
				Log.d(TAG, "row.date Parse exception: " + e.getLocalizedMessage());
			}
             row.location = remCursor.getString(FIELD_LOCATION);
             try {
				row.addedOn = dateFormat.parse(remCursor.getString(FIELD_ADDEDON));
			} catch (ParseException e) {
				Log.d(TAG, "row.addedon Parse exception: " + e.getLocalizedMessage());
			}
             
             rems.add(row);
        } while (remCursor.moveToNext());
        return rems;
    }

    public final ReminderEntry fetchReminder(String reminderID) {
        ReminderEntry row = new ReminderEntry();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat textDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        try {
            db = myCtx.openOrCreateDatabase(DATABASE_NAME, 0, null);
            Cursor c =
                db.query(true, TABLE_REMINDERS, new String[] {
                		"reminderID", "user", "body", "date", "location", "addedon"},
                		"reminderID='" + reminderID + "'", null, null, null, null, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                row.reminderID = c.getString(FIELD_REMINDER_ID);
                row.user = c.getString(FIELD_USER);
                row.body = c.getString(FIELD_BODY);
                try {
					row.date = textDateFormat.parse(c.getString(FIELD_DATE));
				} catch (ParseException e) {
					Log.d(TAG, "SQLite exception: " + e.getStackTrace());
				}
                row.location = c.getString(FIELD_LOCATION);
                try {
					row.addedOn = dateFormat.parse(c.getString(FIELD_ADDEDON));
				} catch (ParseException e) {
					Log.d(TAG, "SQLite exception: " + e.getStackTrace());
				}
            } else {
                row.id = -1;
            }
            c.close();
        } catch (SQLException e) {
        	Log.d(TAG, "SQLite exception: "
        			+ e.getLocalizedMessage());
        } finally {
            db.close();
        }
        return row;
    }

    public Date getLastUpdate() {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    	Date lastEntry = null;
    	
    	try {
    		lastEntry = dateFormat.parse("2012-01-01T00:00:00.000Z");
    	} catch (ParseException e) {
    		Log.d(TAG, "LastEntry Date Parse Exception: " + e.getLocalizedMessage());
    	}
    	try {
             db = myCtx.openOrCreateDatabase(DATABASE_NAME, 0, null);
             Cursor c = db.rawQuery("select addedon from reminders order by rowid desc limit 1", null);

             if (c.getCount() > 0) {
                 c.moveToFirst();
			     lastEntry = dateFormat.parse(c.getString(0));
             }

             c.close();
         } catch (SQLException e) {
                Log.d(TAG, "SQLite exception: " + e.getLocalizedMessage());
         } catch (ParseException e) {
				Log.d(TAG, "Last Entry 2 Date Parse Exception: " + e.getLocalizedMessage());
		 }finally {
                 db.close();
         }
    	return lastEntry;
    }

    public long getCurrentAlarmedReminderDate() {

    	String reminderDateString = "";
    	long reminderDate = 0;
    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    	

    	try {
             db = myCtx.openOrCreateDatabase(DATABASE_NAME, 0, null);
             Cursor c = db.rawQuery("select date from reminders where currentalarm = 1", null);
             reminderDateString = "01/01/2012 00:00";
             if (c.getCount() > 0) {
                 c.moveToFirst();
                 reminderDateString = c.getString(0);
             }

             c.close();
         } catch (SQLException e) {
        	 Log.d(TAG, "SQLite exception: " + e.getLocalizedMessage());
         } finally {
                 db.close();
         }

    	try {
    		reminderDate = dateFormat.parse(reminderDateString).getTime();
		} catch (ParseException e) {
			Log.d("DBHelper", "StackTrace: " + e.getMessage());
		}
    	return reminderDate;
    }

    public String getCurrentAlarmedReminderID() {

    	String reminderID = "";

    	try {
             db = myCtx.openOrCreateDatabase(DATABASE_NAME, 0, null);
             Cursor c = db.rawQuery("select reminderID from reminders where currentalarm = 1", null);

             if (c.getCount() > 0) {
                 c.moveToFirst();
                 reminderID = c.getString(0);
             }

             c.close();
         } catch (SQLException e) {
                 Log.d(TAG, "SQLite exception: " + e.getLocalizedMessage());
         } finally {
                 db.close();
         }

    	return reminderID;
    }

    public void clearOldReminders() {
    	ArrayList<ReminderEntry> rems = fetchAllRowsList();
    	long currentTime = System.currentTimeMillis();

    	long remDate = currentTime;

    	for (ReminderEntry entr : rems) {
			remDate = entr.date.getTime();
			if (currentTime < remDate) {
				 deleteReminder(entr.reminderID);
        	}
    	}
    }

    public void setCurrentAlarmedReminder() {

    	ArrayList<ReminderEntry> rems = fetchAllRowsList();
    	long currentTime = System.currentTimeMillis();

    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        long remDate = currentTime;
        long currentClosestDate = 0;
		try {
			currentClosestDate = dateFormat.parse("01/01/2030 00:00").getTime();
		} catch (ParseException e1) {
			Log.d("DBHelper", "setCurrentAlarmReminder-currentClosestDate: " + e1.getMessage());
		}
        String mostRecentReminderID = "";
    	for (ReminderEntry entr : rems) {
    		
			remDate = entr.date.getTime();
			if ((remDate > currentTime) && (remDate < currentClosestDate)) {
				currentClosestDate = remDate;
				mostRecentReminderID = entr.reminderID;
			}
    	}

    	db.rawQuery("update reminders set currentalarm = 1 where reminderiD ='" + mostRecentReminderID + "'", null);

    }
}

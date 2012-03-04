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
    private static final String TABLE_SYNC = "sync";
    private static final String TABLE_ALARMED = "alarmed";
    private static final int DATABASE_VERSION = 1;
    private static String TAG = "DBHelper";
    Context myCtx;

    private static final int FIELD_REMINDER_ID = 1;
    private static final int FIELD_USER = 2;
    private static final int FIELD_BODY = 3;
    private static final int FIELD_DATE = 4;
    private static final int FIELD_LOCATION = 5;
    private static final int FIELD_ADDEDON = 6;
    
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
            + "addedon text);";
    
    private static final String SYNC_CREATE = 
    	"create table " + TABLE_SYNC + " ("
    		+ "syncid integer primary key,"
    		+ "lastupdate integer);";

    private static final String ALARMED_CREATE =
    		"create table " + TABLE_ALARMED + " ("
    		+ "holdid integer primary key,"
    		+ "reminderID text)";
    
    private static final String REMINDERS_DROP =
        "drop table " + TABLE_REMINDERS + ";";
    
    private static final String SYNC_DROP = 
    	"drop table " + TABLE_SYNC + ";";

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
                db.execSQL(SYNC_CREATE);
                db.execSQL(ALARMED_CREATE);
                db.execSQL("insert into " + TABLE_SYNC + " values (0, 0)");
                db.execSQL("insert into " + TABLE_ALARMED + " values (1,'empty')");
        } catch (SQLException e) {
                Log.d(TAG, "SQLite exception: " + e.getLocalizedMessage());
        }
    }

    public void deleteDatabase() {
        try {
            db = myCtx.openOrCreateDatabase(DATABASE_NAME, 0, null);
            db.execSQL(REMINDERS_DROP);
            db.execSQL(REMINDERS_CREATE);
            db.execSQL(SYNC_DROP);
            db.execSQL(SYNC_CREATE);
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
    	SimpleDateFormat textDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        ContentValues initialValues = new ContentValues();
        initialValues.put("reminderID", entry.reminderID);
        initialValues.put("user", entry.user);
        initialValues.put("body", entry.body);
        initialValues.put("date", textDateFormat.format(entry.date));
        initialValues.put("location", entry.location+"'>"+entry.location);
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
            db.delete(TABLE_REMINDERS, "reminderID='" + Id + "'", null);
        } catch (SQLException e) {
            Log.d(TAG, "SQLite exception: " + e.getMessage());
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
    	SimpleDateFormat textDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    	Cursor remCursor = fetchAllRows();
    	ArrayList<ReminderEntry> rems = new ArrayList<ReminderEntry>();
    	ReminderEntry row;
    	if (remCursor.getCount() > 0) {
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
    	}
        return rems;
    }

    public final ReminderEntry fetchReminder(String reminderID) {
        ReminderEntry row = new ReminderEntry();
        SimpleDateFormat textDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        try {
            db = myCtx.openOrCreateDatabase(DATABASE_NAME, 0, null);
            Cursor 	c = db.rawQuery("select rowid _id,* from reminders where reminderID = '" + reminderID + "'", null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                row.reminderID = c.getString(FIELD_REMINDER_ID);
                row.user = c.getString(FIELD_USER);
                row.body = c.getString(FIELD_BODY);
                try {
					row.date = textDateFormat.parse(c.getString(FIELD_DATE));
				} catch (ParseException e) {
					Log.d(TAG, "SQLite exception: " + e.getMessage());
				}
                row.location = c.getString(FIELD_LOCATION);
                try {
					row.addedOn = dateFormat.parse(c.getString(FIELD_ADDEDON));
				} catch (ParseException e) {
					Log.d(TAG, "SQLite exception: " + e.getMessage());
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
             Cursor c = db.rawQuery("select lastupdate from sync", null);

             if (c.getCount() > 0) {
                 c.moveToFirst();
			     lastEntry = dateFormat.parse(c.getString(0));
             }

             c.close();
         } catch (SQLException e) {
                Log.d(TAG, "SQLite exception: " + e.getMessage());
         } catch (ParseException e) {
				Log.d(TAG, "Last Entry 2 Date Parse Exception: " + e.getLocalizedMessage());
		 }finally {
                 db.close();
         }
    	return lastEntry;
    }

    public void setLastUpdate(Date date){
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    	
    	
    	ContentValues initialValues = new ContentValues();
    	initialValues.put("lastupdate", dateFormat.format(new Date(System.currentTimeMillis())));
    	db.update(TABLE_SYNC, initialValues, null, null);
    }
    
    public long getCurrentAlarmedReminderDate() {

    	String reminderIDString = "";
    	String reminderDateString = "";
    	long reminderDate = 0;
    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
    	

    	try {
             db = myCtx.openOrCreateDatabase(DATABASE_NAME, 0, null);
             Cursor c = db.rawQuery("select reminderID from alarmed where holdid = 1", null);
             
             if (c.getCount() > 0) {
                 c.moveToFirst();
                 reminderIDString = c.getString(0);
             }

             c.close();
             
             Cursor d = db.rawQuery("select date from reminders where reminderID = '" + reminderIDString+"'", null);
            		 
    		 if (d.getCount() > 0) {
                 d.moveToFirst();
                 reminderDateString = d.getString(0);
             }

            d.close();
         } catch (SQLException e) {
        	 Log.d(TAG, "SQLite exception: " + e.getMessage());
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
             Cursor c = db.rawQuery("select reminderID from alarmed", null);

             if (c.getCount() > 0) {
                 c.moveToFirst();
                 reminderID = c.getString(0);
             }

             c.close();
         } catch (SQLException e) {
                 Log.d(TAG, "SQLite exception: " + e.getMessage());
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

    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");

        long remDate = 0;
        long currentClosestDate = 0;
		try {
			currentClosestDate = dateFormat.parse("01-Jan-2030 00:00").getTime();
		} catch (ParseException e1) {
			Log.d("DBHelper", "setCurrentAlarmReminder-currentClosestDate: " + e1.getMessage());
		}
        String mostRecentReminderID = "";
    	for (ReminderEntry entr : rems) {
    		
			remDate = entr.date.getTime();
			if ((remDate > currentTime) && (remDate < currentClosestDate)) {
				currentClosestDate = remDate;
				mostRecentReminderID = entr.reminderID;
				Log.d("DBHelper","setCurrentAlarmReminder: CurrentClosestDate = " + new Date(currentClosestDate).toLocaleString());
			}
    	}

    	db.execSQL("UPDATE alarmed SET reminderID = '" + mostRecentReminderID + "'");
    }
}

package spouseReminder.Reminders;

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

    private static final String DBVERSION_CREATE = 
        "create table " + TABLE_DBVERSION + " ("
                + "version integer not null);";

    private static final String REMINDERS_CREATE =
        "create table " + TABLE_REMINDERS + " ("
        	+ "reminderID text primary key,"
            + "user text,"
            + "title text, "
            + "body text,"
            + "date text);";

    private static final String REMINDERS_DROP =
        "drop table " + TABLE_REMINDERS + ";";

    private SQLiteDatabase db;

    /**
     * 
     * @param ctx
     */
    public DBHelper(Context ctx) {
        myCtx = ctx;
                try {
                        db = myCtx.openOrCreateDatabase(DATABASE_NAME, 0,null);

                        // Check for the existence of the DBVERSION table
                        // If it doesn't exist than create the overall data,
                        // otherwise double check the version
                        Cursor c =
                                db.query("sqlite_master", new String[] { "name" },
                                                "type='table' and name='"+TABLE_DBVERSION+"'", null, null, null, null);
                        int numRows = c.getCount();
                        if (numRows < 1) {
                                CreateDatabase(db);
                        } else {
                                int version=0;
                                Cursor vc = db.query(true, TABLE_DBVERSION, new String[] {"version"},
                                                null, null, null, null, null,null);
                                if(vc.getCount() > 0) {
                                    vc.moveToFirst();
                                    version=vc.getInt(0);
                                }
                                vc.close();
                                if (version!=DATABASE_VERSION) {
                                        Log.e(TAG,"database version mismatch");
                                }
                        }
                        c.close();
                        

                } catch (SQLException e) {
                        Log.d(TAG,"SQLite exception: " + e.getLocalizedMessage());
                } finally {
                        db.close();
                }
    }

    private void CreateDatabase(SQLiteDatabase db)
    {
                try {
                        db.execSQL(DBVERSION_CREATE);
                        ContentValues args = new ContentValues();
                        args.put("version", DATABASE_VERSION);
                        db.insert(TABLE_DBVERSION, null, args);

                        db.execSQL(REMINDERS_CREATE);
                } catch (SQLException e) {
                        Log.d(TAG,"SQLite exception: " + e.getLocalizedMessage());
                } 
    }
    
    public void deleteDatabase()
    {
        try {
                        db = myCtx.openOrCreateDatabase(DATABASE_NAME, 0,null);
                        db.execSQL(REMINDERS_DROP);
                        db.execSQL(REMINDERS_CREATE);
        } catch (SQLException e) {
                        Log.d(TAG,"SQLite exception: " + e.getLocalizedMessage());
                } finally {
                        db.close();
                }       
    }
    
    /**
     * Close database connection
     */
    public void close() {
        try {
                db.close();
            } catch (SQLException e) {
                Log.d(TAG,"close exception: " + e.getLocalizedMessage());
            }
    }

////////// Notes Functions ////////////////
        
        
        /**
         * 
         * @param entry
         */
        public void addReminder(ReminderEntry entry) {
            ContentValues initialValues = new ContentValues();
            initialValues.put("reminderID", entry.reminderID);
            initialValues.put("user", entry.User);
            initialValues.put("title", entry.Title);
            initialValues.put("body", entry.Body);
            initialValues.put("date", entry.Body);

            try{
            	db = myCtx.openOrCreateDatabase(DATABASE_NAME, 0,null);
                db.insert(TABLE_REMINDERS, null, initialValues);
            }catch (SQLException e) {
                Log.d(TAG,"SQLite exception: " + e.getLocalizedMessage());
            }finally{
                db.close();
            }
        }
        
        /**
         * 
         * @param Id
         */
        public void deleteReminder(long Id) {
            try {
                        db = myCtx.openOrCreateDatabase(DATABASE_NAME, 0,null);
                        db.delete(TABLE_REMINDERS, "reminderID=" + Id, null);
                } catch (SQLException e) {
                        Log.d(TAG,"SQLite exception: " + e.getLocalizedMessage());
                } finally {
                        db.close();
                }
        }

        /**
         * 
         * @return
         */
        public Cursor fetchAllRows(){
            Cursor c;

            db = myCtx.openOrCreateDatabase(DATABASE_NAME, 0,null);
            c = db.rawQuery("select rowid _id,* from reminders", null);

            return c;
        }
        
        /**
         * 
         * @param Id
         * @return
         */
        public ReminderEntry fetchReminder(String reminderID) {
            ReminderEntry row = new ReminderEntry();
            try {
                db = myCtx.openOrCreateDatabase(DATABASE_NAME, 0,null);
                Cursor c =
                    db.query(true, TABLE_REMINDERS, new String[] {
                    		"reminderID", "user", "title", "body", "date"}, "reminderID='" + reminderID+"'", null, null, null, null, null);
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    row.reminderID = c.getString(0);
                    row.User = c.getString(1);
                    row.Title = c.getString(2);
                    row.Body = c.getString(3);
                    row.Date = c.getString(4);
                } else {
                    row.id = -1;
                }
                c.close();
                } catch (SQLException e) {
                        Log.d(TAG,"SQLite exception: " + e.getLocalizedMessage());
                } finally {
                        db.close();
                }
            return row;
        }
        
        /**
         * 
         * @param Id
         * @param entry
         */
        public void updateReminder(long Id, ReminderEntry entry) {
            ContentValues args = new ContentValues();
            args.put("title", entry.Title);
            args.put("body", entry.Body);
            args.put("date", entry.Body);
            try {
                        db = myCtx.openOrCreateDatabase(DATABASE_NAME, 0,null);
                        db.update(TABLE_REMINDERS, args, "id=" + Id, null);
                } catch (SQLException e) {
                        Log.d(TAG,"SQLite exception: " + e.getLocalizedMessage());
                } finally {
                        db.close();
                }
        }
}

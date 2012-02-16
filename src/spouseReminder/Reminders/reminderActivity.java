package spouseReminder.Reminders;


import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter;

public class reminderActivity extends ListActivity implements Runnable {

	private ProgressDialog pd;
	Cursor cursor;
    @Override
    public void onCreate(final Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.listplaceholder);

        if (!ReminderService.isInstanceCreated()) {
        	startService(new Intent(this, ReminderService.class));
        }
        refreshDB();
        refreshList();
        //pd = ProgressDialog.show(this, "Working..", "Refreshing Reminders", true,
        //        false);
	    //Thread thread = new Thread(this);
	    //thread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reminder_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        	case R.id.refresh:
        		pd = ProgressDialog.show(this, "Working..", "Refreshing Reminders", true,
                    false);
			    Thread thread = new Thread(this);
			    thread.start();
        		refreshList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void refreshDB() {
    	SharedPreferences settings = getSharedPreferences("spouse-reminder-perfs", 0);
    	SyncManager.syncReminders(getApplicationContext(), settings);
    	DBHelper db = new DBHelper(getApplicationContext());
    	try {
     		cursor = db.fetchAllRows();
     	} catch (SQLException e) {
 		    Log.d("reminderActivity", "SQLite exception: " + e.getLocalizedMessage());
 		} finally {
 		}
    }

    public void refreshList() {

         String[] columns = new String[] {"reminderID", "body", "date", "location"};
    	 int[] to = new int[] {R.id.reminderID, R.id.body};
    	 SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, R.layout.list_reminder_entry, cursor, columns, to);
         setListAdapter(mAdapter);

         final ListView lv = getListView();
         lv.setTextFilterEnabled(true);
         lv.setOnItemClickListener(new OnItemClickListener() {
         	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {        		

         		cursor.moveToPosition(position);
         		Intent valid = new Intent(getBaseContext(), SpecificReminderActivity.class);
         		valid.putExtra("reminderID", cursor.getString(1));
 			    startActivity(valid);
 			}
 		});
    }

    public void run() {
        refreshDB();
        handler.sendEmptyMessage(0);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
        	refreshList();
        	pd.dismiss();
        }
    };
}

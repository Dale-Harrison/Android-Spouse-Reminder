package spouseReminder.Reminders;



import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter;

public class reminderActivity extends ListActivity {
	 /** Called when the activity is first created. */
	
	Cursor cursor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.listplaceholder);
        
        DBHelper db = new DBHelper(getApplicationContext());
        
        try{
        	  cursor = db.fetchAllRows();
        	  String[] columns = new String[] { "reminderID", "title", "body" };
        	  int[] to = new int[] { R.id.reminderID, R.id.title, R.id.body };

        	  SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, R.layout.list_reminder_entry, cursor, columns, to);
              setListAdapter(mAdapter);
		} catch (SQLException e) {
		    Log.d("reminderActivity","SQLite exception: " + e.getLocalizedMessage());
		} finally {
		        db.close();
		}
        
        final ListView lv = getListView();
        lv.setTextFilterEnabled(true);	
        lv.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {        		
        		
        		cursor.moveToPosition(position);
        		Intent valid = new Intent(getBaseContext(), SpecificReminderActivity.class);
        		valid.putExtra("reminderID",cursor.getString(1));
			    startActivity(valid);
			}
		});
        
       

        
    }


}
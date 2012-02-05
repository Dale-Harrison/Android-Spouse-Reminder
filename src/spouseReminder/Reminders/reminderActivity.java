package spouseReminder.Reminders;

import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
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
        
        SharedPreferences settings = getSharedPreferences("spouse-reminder-perfs", 0);
        String UserName = settings.getString("UserName", "empty");
        String Password = settings.getString("Password", "emptypw");
        
        SpouseAlarmManager man = new SpouseAlarmManager();
        JSONObject json = JSONfunctions.getJSONfromURL("http://192.168.2.2:8080/remservice/reminders?username="+UserName+"&password="+Password);
        DBHelper db = new DBHelper(getApplicationContext());
        ReminderEntry entry;
        try{
        	
        	JSONArray  reminders = json.getJSONArray("reminders");
        	
	        for(int i=0;i<reminders.length();i++){						
				
				JSONObject e = reminders.getJSONObject(i);
				entry = new ReminderEntry();
				entry.reminderID = e.getString("_id");
				entry.User = e.getString("user");
				entry.Title = e.getString("title");
				entry.Body = e.getString("body");
				entry.Date = e.getString("date");
                
				db.addReminder(entry);			
				man.AddNewAlarms(getApplicationContext(), entry);
			}		
        }catch(JSONException e)        {
        	 Log.e("log_tag", "Error parsing data "+e.toString());
        } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
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
package spouseReminder.Reminders;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class reminderActivity extends ListActivity {
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.listplaceholder);
        
        SharedPreferences settings = getSharedPreferences("spouse-reminder-perfs", 0);
        String UserName = settings.getString("UserName", "empty");
        String Password = settings.getString("Password", "emptypw");
        
        SpouseAlarmManager man = new SpouseAlarmManager();
        JSONObject json = JSONfunctions.getJSONfromURL("http://192.168.2.3:8080/remservice/reminders?username="+UserName+"&password="+Password);
        DBHelper db = new DBHelper(getApplicationContext());
        try{
        	
        	JSONArray  reminders = json.getJSONArray("reminders");
        	
	        for(int i=0;i<reminders.length();i++){						
				
				JSONObject e = reminders.getJSONObject(i);
				ReminderEntry entry = new ReminderEntry();
				entry.reminderID = e.getString("_id");
				entry.Title = e.getString("title");
				entry.Body = e.getString("body");
				entry.Date = e.getString("date");
                man.AddNewAlarms(getApplicationContext(), entry);
				db.addReminder(entry);			
			}		
        }catch(JSONException e)        {
        	 Log.e("log_tag", "Error parsing data "+e.toString());
        }
      
        ListAdapter adapter = new ArrayAdapter<ReminderEntry>(this, android.R.layout.simple_list_item_1, db.fetchAllRows());
        setListAdapter(adapter);
        
        final ListView lv = getListView();
        lv.setTextFilterEnabled(true);	
        lv.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {        		
        		@SuppressWarnings("unchecked")
				HashMap<String, String> o = (HashMap<String, String>) lv.getItemAtPosition(position);	        		
        		Toast.makeText(reminderActivity.this, o.get("body"), Toast.LENGTH_SHORT).show(); 

			}
		});
        
       

        
    }
}
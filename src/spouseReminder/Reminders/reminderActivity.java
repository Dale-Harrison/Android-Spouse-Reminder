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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
        
        ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
        
        JSONObject json = JSONfunctions.getJSONfromURL("http://192.168.2.3:8080/remservice?username=Dale&password=12345");
                
        try{
        	
        	JSONArray  reminders = json.getJSONArray("reminders");
        	
	        for(int i=0;i<reminders.length();i++){						
				HashMap<String, String> map = new HashMap<String, String>();	
				JSONObject e = reminders.getJSONObject(i);
				
				map.put("id", e.getString("_id"));
				map.put("title",  e.getString("title"));
	        	map.put("body", e.getString("body"));
	        	map.put("date",  e.getString("date"));
	        	mylist.add(map);			
			}		
        }catch(JSONException e)        {
        	 Log.e("log_tag", "Error parsing data "+e.toString());
        }
        

        Intent intent = new Intent(reminderActivity.this, AlarmReceiver.class);

        PendingIntent appIntent = PendingIntent.getBroadcast(reminderActivity.this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 5);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), appIntent);      
        
        ListAdapter adapter = new SimpleAdapter(this, mylist , R.layout.main, 
                        new String[] { "Title", "title" }, 
                        new int[] { R.id.item_title, R.id.item_subtitle });
        
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
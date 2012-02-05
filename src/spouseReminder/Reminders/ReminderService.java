package spouseReminder.Reminders;

import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ReminderService extends Service{


	    public class LocalBinder extends Binder {
	    	
	       
	    }

	    @Override
	    public void onCreate() {
	    	new Thread(new Runnable(){
	    	    public void run() {
	    	    
		    	    while(true)
		    	    {
		    	       try {
						Thread.sleep(60000);
						syncReminders();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		    	    }
	    	    }
	    	}).start();
	    }

	    @Override
	    public int onStartCommand(Intent intent, int flags, int startId) {
	        Log.i("LocalService", "Received start id " + startId + ": " + intent);
	        // We want this service to continue running until it is explicitly
	        // stopped, so return sticky.
	        return START_STICKY;
	    }

	    @Override
	    public void onDestroy() {
	       
	    }

	    @Override
	    public IBinder onBind(Intent intent) {
	        return mBinder;
	    }

	    // This is the object that receives interactions from clients.  See
	    // RemoteService for a more complete example.
	    private final IBinder mBinder = new LocalBinder();

	    public void syncReminders(){
	    	SharedPreferences settings = getSharedPreferences("spouse-reminder-perfs", 0);
			String UserName = settings.getString("UserName", "empty");
			String Password = settings.getString("Password", "emptypw");
			 
			 DBHelper db = new DBHelper(getApplicationContext());
			 SpouseAlarmManager man = new SpouseAlarmManager();
			 JSONObject json = JSONfunctions.getJSONfromURL("http://192.168.2.2:8080/remservice/reminders?username="+UserName+"&password="+Password);
			
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
	    
	    }
}

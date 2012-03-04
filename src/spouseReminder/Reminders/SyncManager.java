package spouseReminder.Reminders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class SyncManager {

	private static String TAG = "SyncManager";

    public static void syncReminders(Context context, SharedPreferences settings) {
    	 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    	 SimpleDateFormat textDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    	 String userName = settings.getString("UserName", "emptyusername");
		 String password = settings.getString("Password", "emptypw");
		 DBHelper db = new DBHelper(context);
		 SpouseAlarmManager man = new SpouseAlarmManager();
		 JSONObject json = JSONfunctions.getJSONfromURL("http://192.168.2.2:8080/remservice/reminders?username=" + userName + "&password=" + password + "&lastupdate=" + db.getLastUpdate().getTime());

		 ReminderEntry entry;
		 try {

		 	JSONArray  reminders = json.getJSONArray("reminders");

		    for (int i = 0; i < reminders.length(); i++) {

				JSONObject e = reminders.getJSONObject(i);
				entry = new ReminderEntry();
				entry.reminderID = e.getString("_id");
				entry.user = e.getString("user");
				entry.body = e.getString("body");			
				entry.date = textDateFormat.parse(e.getString("date"));
				entry.location = e.getString("location");
				entry.addedOn = new Date(new Long(e.getString("addedon")));
				db.addReminder(entry);
			}
		    
		    db.setCurrentAlarmedReminder();
			man.AddNewAlarm(context);
			
		 } catch (JSONException e) {
		 	 Log.e("log_tag", "Error parsing JSON data: " + e.toString());
		 } catch (ParseException e) {
			 Log.e("log_tag", "Error parsing Date: " + e.toString());
		}	
    }
}

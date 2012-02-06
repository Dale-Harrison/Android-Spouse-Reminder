package spouseReminder.Reminders;

import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SyncManager {

    public static void syncReminders(Context context, SharedPreferences settings){
    	
    	 String UserName = settings.getString("UserName", "emptyusername");
		 String Password = settings.getString("Password", "emptypw");
		 DBHelper db = new DBHelper(context);
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
				entry.AddedOn = e.getString("addedon");
		         
				db.addReminder(entry);			
				man.AddNewAlarms(context, entry);
			}		
		 }catch(JSONException e){
		 	 Log.e("log_tag", "Error parsing data "+e.toString());
		 } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
    }
}

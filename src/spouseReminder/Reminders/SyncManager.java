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
				entry.Body = e.getString("body");
				entry.Date = e.getString("date");
				entry.Location = e.getString("location");
				entry.AddedOn = e.getString("addedon");
		         
				if(!entryAlreadyExists(context,entry)){
					db.addReminder(entry);			
					man.AddNewAlarms(context, entry);
				}
			}		
		 }catch(JSONException e){
		 	 Log.e("log_tag", "Error parsing data "+e.toString());
		 } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
    }
    
    private static boolean entryAlreadyExists(Context context, ReminderEntry entry){
    	
    	DBHelper db = new DBHelper(context);
    	SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String LastUpdate = db.getLastUpdate();
        Date DateLastUpdate = null;
        Date DateRemAddedOn = null;
        
        //Needs refactoring
        if(LastUpdate == ""){
        	LastUpdate = "1972-01-01T00:00:00.000Z";
        }

        try {
        	DateRemAddedOn = formatter.parse(entry.AddedOn.substring(0, 24));
        	DateLastUpdate = formatter.parse(LastUpdate.substring(0,24));
        } catch (ParseException e2) {
        	Log.d(TAG,"Dateparse exception: " + e2.getLocalizedMessage());
		}
        
    	if(DateRemAddedOn.getTime() > DateLastUpdate.getTime()){
    		return false;
    	}else{
    		return true;
    	}
    		
    }
    
    
}

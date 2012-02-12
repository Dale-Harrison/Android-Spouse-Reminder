package spouseReminder.Reminders;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.app.AlarmManager;

public class SpouseAlarmManager {

	public void AddNewAlarms(Context ctx, ReminderEntry rem) throws ParseException{
		
		Intent reminderAlarmIntent = new Intent(ctx, AlarmReceiver.class);
		reminderAlarmIntent.putExtra("reminderID", rem.reminderID);
        PendingIntent appIntent = PendingIntent.getBroadcast(ctx, 0, reminderAlarmIntent, 0);
        
        long remDate = Date.parse(rem.Date);
        
        Log.d("SpouseAlarmManager","System Time ms = "+new Date().getTime()+ " Reminder Date "+ remDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(remDate);
        //calendar.setTimeInMillis(new Date().getTime());
        
        AlarmManager am = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE); 
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), appIntent);      
	}
	
}

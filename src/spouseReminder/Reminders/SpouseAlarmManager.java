package spouseReminder.Reminders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.app.AlarmManager;

public class SpouseAlarmManager {

	public void AddNewAlarms(Context ctx, ReminderEntry rem) throws ParseException{
		
		Intent reminderAlarmIntent = new Intent(ctx, AlarmReceiver.class);
		reminderAlarmIntent.putExtra("reminderID", rem.reminderID);
        PendingIntent appIntent = PendingIntent.getBroadcast(ctx, 0, reminderAlarmIntent, 0);
        
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        Date remDate = formatter.parse(rem.Date.substring(0, 24));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(remDate.getTime());

        AlarmManager am = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), appIntent);      
	}
	
}

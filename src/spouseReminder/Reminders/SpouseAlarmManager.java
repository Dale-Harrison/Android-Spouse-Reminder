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

	public void AddNewAlarm(Context ctx) {

		DBHelper db = new DBHelper(ctx);
		
		long remDate = db.getCurrentAlarmedReminderDate();
		Log.d("SpouseAlarmManager.AddNewAlarm","Adding " + new Date(remDate).toLocaleString() + " as the next reminder time");
		if (remDate > System.currentTimeMillis()) {
			Log.d("SpouseAlarmManager", "Alarm date current");
			Intent reminderAlarmIntent = new Intent(ctx, AlarmReceiver.class);

        	PendingIntent appIntent = PendingIntent.getBroadcast(ctx, (int) System.currentTimeMillis(), reminderAlarmIntent, 0);

	        Calendar calendar = Calendar.getInstance();
	        calendar.setTimeInMillis(remDate);
	        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE); 
	        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), appIntent);
        }
	}
}

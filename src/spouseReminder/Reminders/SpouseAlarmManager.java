package spouseReminder.Reminders;

import java.util.Calendar;
import java.util.Date;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.app.AlarmManager;

public class SpouseAlarmManager {

	public void AddNewAlarms(Context ctx, ReminderEntry rem){
		
		Intent reminderAlarmIntent = new Intent(ctx, AlarmReceiver.class);
		reminderAlarmIntent.putExtra("reminderID", rem.reminderID);
        PendingIntent appIntent = PendingIntent.getBroadcast(ctx, 0, reminderAlarmIntent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Date.parse(rem.Date));

        AlarmManager am = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), appIntent);      
	}
	
}

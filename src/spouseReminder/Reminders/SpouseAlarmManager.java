package spouseReminder.Reminders;

import java.text.ParseException;
import java.util.Calendar;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.app.AlarmManager;

public class SpouseAlarmManager {

	public void AddNewAlarms(Context ctx, ReminderEntry rem) throws ParseException {

		DBHelper db = new DBHelper(ctx);
		Intent reminderAlarmIntent = new Intent(ctx, AlarmReceiver.class);

        PendingIntent appIntent = PendingIntent.getBroadcast(ctx, (int) System.currentTimeMillis(), reminderAlarmIntent, 0);

        long remDate = db.getCurrentAlarmedReminderDate();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(remDate);
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE); 
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), appIntent);      
	}
}

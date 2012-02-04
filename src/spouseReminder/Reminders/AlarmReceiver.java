package spouseReminder.Reminders;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
    	String ns = Context.NOTIFICATION_SERVICE;
    	NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
    	
    	DBHelper db = new DBHelper(context);
    	Bundle extras = intent.getExtras();
    	
    	int icon = R.drawable.rolling_pin_256;
    	String reminderID = extras.getString("reminderID");
    	long when = System.currentTimeMillis();

    	ReminderEntry entry = db.fetchReminder(extras.getString("reminderID"));
    	
    	Notification notification = new Notification(icon, entry.Title, when);
    	
    	CharSequence contentTitle = entry.Title;
    	CharSequence contentText = entry.Body;
    	
    	Intent notificationIntent = new Intent(context, SpecificReminderActivity.class);
    	notificationIntent.putExtra("reminderID", entry.reminderID);
    	PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

    	notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

    	mNotificationManager.notify(1, notification);
    }

}

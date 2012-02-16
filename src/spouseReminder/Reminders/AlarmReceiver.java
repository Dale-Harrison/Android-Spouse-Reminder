package spouseReminder.Reminders;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
    	String ns = Context.NOTIFICATION_SERVICE;
    	NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);

    	DBHelper db = new DBHelper(context);


    	int icon = R.drawable.rolling_pin_256;
    	long when = System.currentTimeMillis();

    	ReminderEntry entry = db.fetchReminder(db.getCurrentAlarmedReminderID());
    	Notification notification = new Notification(icon, entry.body, when);
    	notification.flags = Notification.FLAG_AUTO_CANCEL;
    	CharSequence contentTitle = "Reminder";
    	CharSequence contentText = entry.body;

    	Intent notificationIntent = new Intent(context, SpecificReminderActivity.class);
    	PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

    	notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

    	mNotificationManager.notify(1, notification);

    }

}

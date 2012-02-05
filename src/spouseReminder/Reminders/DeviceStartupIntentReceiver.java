package spouseReminder.Reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DeviceStartupIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		Intent serviceIntent = new Intent();
		serviceIntent.setAction("ReminderService");
		context.startService(serviceIntent);
	}

}

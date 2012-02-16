package spouseReminder.Reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DeviceStartupIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		Log.d("Received Device Startup Intent", "Received startup intent");
		if (arg1.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Intent i = new Intent();
			i.setAction(".ReminderService");
			context.startService(i);
		}
	}

}

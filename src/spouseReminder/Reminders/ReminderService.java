package spouseReminder.Reminders;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ReminderService extends Service{

		SharedPreferences settings;
		private static ReminderService instance = null;

	   public static boolean isInstanceCreated() { 
	      return instance != null; 
	   }
		   
	    public class LocalBinder extends Binder {
	    	
	       
	    }

	    @Override
	    public void onCreate() {
	    	instance = this;
	    	settings = getSharedPreferences("spouse-reminder-perfs", 0);
	    	
	    	String UserName = settings.getString("UserName", "emptyusername");

	    	if(UserName != "emptyusername"){

		    	Log.d("ReminderService","Starting Service");
		    	new Thread(new Runnable(){
		    	    public void run() {
		    	    
			    	    while(true)
			    	    {
			    	       try {
							Thread.sleep(300000);
							SyncManager.syncReminders(getApplicationContext(), settings);
							Log.d("ReminderService","Syncing to server");
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			    	    }
		    	    }
		    	}).start();
	    	}else{
	    		String ns = Context.NOTIFICATION_SERVICE;
	        	NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(ns);
	        	
	    		int icon = R.drawable.rolling_pin_256;
	        	long when = System.currentTimeMillis();
	    		Notification notification = new Notification(icon, "Spouse Reminder Login", when);
	        	
	        	CharSequence contentTitle = "Spouse Reminder Login";
	        	CharSequence contentText = "Spouse Reminder requires a username and password";
	        	Intent notificationIntent = new Intent(getApplicationContext(), SpecificReminderActivity.class);
	        	PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

	        	notification.setLatestEventInfo(getApplicationContext(), contentTitle, contentText, contentIntent);

	        	mNotificationManager.notify(1, notification);
	    	}
	    }

	    @Override
	    public int onStartCommand(Intent intent, int flags, int startId) {
	        Log.i("LocalService", "Received start id " + startId + ": " + intent);
	        // We want this service to continue running until it is explicitly
	        // stopped, so return sticky.
	        return START_STICKY;
	    }

	    @Override
	    public void onDestroy() {
	    	instance = null; 
	    }

	    @Override
	    public IBinder onBind(Intent intent) {
	        return mBinder;
	    }

	    // This is the object that receives interactions from clients.  See
	    // RemoteService for a more complete example.
	    private final IBinder mBinder = new LocalBinder();


	
	   
}

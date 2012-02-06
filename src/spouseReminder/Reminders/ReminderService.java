package spouseReminder.Reminders;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ReminderService extends Service{

		SharedPreferences settings;
	    public class LocalBinder extends Binder {
	    	
	       
	    }

	    @Override
	    public void onCreate() {
	    	
	    	settings = getSharedPreferences("spouse-reminder-perfs", 0);

	    	new Thread(new Runnable(){
	    	    public void run() {
	    	    
		    	    while(true)
		    	    {
		    	       try {
						Thread.sleep(60000);
						SyncManager.syncReminders(getApplicationContext(), settings);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		    	    }
	    	    }
	    	}).start();
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
	       
	    }

	    @Override
	    public IBinder onBind(Intent intent) {
	        return mBinder;
	    }

	    // This is the object that receives interactions from clients.  See
	    // RemoteService for a more complete example.
	    private final IBinder mBinder = new LocalBinder();


	
	   
}

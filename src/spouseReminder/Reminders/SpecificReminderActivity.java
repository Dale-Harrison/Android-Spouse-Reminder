package spouseReminder.Reminders;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SpecificReminderActivity extends Activity{

	TextView body;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.individual_entry);
	    
		DBHelper db = new DBHelper(getApplicationContext());
    	Bundle extras = getIntent().getExtras();

    	ReminderEntry entry = db.fetchReminder(extras.getString("reminderID"));
    	
    	body = (TextView)findViewById(R.id.body);
    	body.setText(entry.Body);
	}
	
}

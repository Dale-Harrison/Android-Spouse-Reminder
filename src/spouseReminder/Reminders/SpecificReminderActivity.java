package spouseReminder.Reminders;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SpecificReminderActivity extends Activity{

	TextView title;
	TextView body;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.individualentry);
	    
		DBHelper db = new DBHelper(getApplicationContext());
    	Bundle extras = getIntent().getExtras();

    	ReminderEntry entry = db.fetchReminder(extras.getString("reminderID"));
    	
    	title = (TextView)findViewById(R.id.title);
    	title.setText(entry.Title);
    	body = (TextView)findViewById(R.id.body);
    	body.setText(entry.Body);
	}
	
}

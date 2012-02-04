package spouseReminder.Reminders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SpecificReminderActivity extends Activity{

	TextView title;
	TextView body;
	
	public void onReceive(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.individualentry);

		DBHelper db = new DBHelper(getApplicationContext());
    	Bundle extras = getIntent().getExtras();

    	String reminderID = extras.getString("reminderID");
    	
    	ReminderEntry entry = db.fetchReminder(extras.getString("reminderID"));
    	
    	title.setText(entry.Title);
    	body.setText(entry.Body);
	}
	
}

package spouseReminder.Reminders;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SpecificReminderActivity extends Activity {

	TextView body;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.individual_entry);

		DBHelper db = new DBHelper(getApplicationContext());

    	ReminderEntry entry = db.fetchReminder(db.getCurrentAlarmedReminderID());

    	body = (TextView) findViewById(R.id.body);
    	body.setText(entry.body);
	}
}

package spouseReminder.Reminders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {
	
	EditText un,pw;
   	TextView error;
    Button ok;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        un=(EditText)findViewById(R.id.et_un);
        pw=(EditText)findViewById(R.id.et_pw);
        ok=(Button)findViewById(R.id.btn_login);
        error=(TextView)findViewById(R.id.tv_error);

        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            	 JSONObject json = JSONfunctions.getJSONfromURL("http://192.168.2.3:8080/remservice/hello?username="+un.getText().toString()+"&password="+pw.getText().toString());
                try{
                	
                	JSONArray  reminders = json.getJSONArray("helloresponse");
                	
        	        for(int i=0;i<reminders.length();i++){						
        				
        				JSONObject e = reminders.getJSONObject(i);
        				
        				if(e.getString("hello") != null){
        					SharedPreferences settings = getSharedPreferences("spouse-reminder-perfs", 0);
        				    SharedPreferences.Editor editor = settings.edit();
        				    editor.putString("UserName", un.getText().toString());
        				    editor.putString("Password", pw.getText().toString());
        				    editor.commit();
        				    
        					Intent valid = new Intent(getBaseContext(), reminderActivity.class);
        				    startActivity(valid);
        				}

        			}		
                }catch(JSONException e)        {
                	 Log.e("log_tag", "Error parsing data "+e.toString());
                }
            }
        });
    }
}
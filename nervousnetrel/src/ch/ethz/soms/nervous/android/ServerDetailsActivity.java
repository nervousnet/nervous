package ch.ethz.soms.nervous.android;

import ch.ethz.soms.nervous.utils.NervousStatics;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class ServerDetailsActivity extends Activity {
	
	private SharedPreferences uploadPreferences;
	EditText edt_ServerIP, edt_ServerPort; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server_details);
		
		uploadPreferences = getSharedPreferences(NervousStatics.UPLOAD_PREFS, 0);		
		
		edt_ServerIP = (EditText) findViewById(R.id.edt_ServerIP);
		edt_ServerPort = (EditText) findViewById(R.id.edt_ServerPortNo);				
	}

	@Override
	protected void onPause() {
		toastToScreen("IP: " + edt_ServerIP.getText() + "\nPort: " + edt_ServerPort.getText(), true);
				
		Editor editor = uploadPreferences.edit();
		editor.putString("serverIP", edt_ServerIP.getText().toString());
				
		int port = -1;	
		try {
			port = Integer.parseInt(edt_ServerPort.getText().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		editor.putInt("serverPort", port);
		
		if (editor.commit()) {
			if (SensorService.isServiceRunning(this)) {
				SensorService.stopService(this);
				SensorService.startService(this);
			}
			if (UploadService.isServiceRunning(this)) {
				UploadService.stopService(this);
				UploadService.startService(this);
			}
		}
		
		super.onPause();
	}
	
	public void toastToScreen(String msg, boolean lengthLong) {

		int toastLength = lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
		Toast.makeText(getApplicationContext(), msg, toastLength).show();
	}
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.server_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/
}
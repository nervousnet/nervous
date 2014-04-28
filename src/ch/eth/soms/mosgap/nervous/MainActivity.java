package ch.eth.soms.mosgap.nervous;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private static final String DEBUG_TAG = "MainActivity";

	private ToggleButton toggleButtonOnOff;
	private TextView textStatus;
	private Button buttonSettings;
	private Button buttonUpload;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		toggleButtonOnOff = (ToggleButton) findViewById(R.id.togglebutton);
		textStatus = (TextView) findViewById(R.id.text_status);		
		buttonSettings = (Button) findViewById(R.id.button_settings);
		buttonUpload = (Button) findViewById(R.id.button_upload);
		
		toggleButtonOnOff.setChecked(serviceRunning());
	}
	
	public void startSensorService() {
		// Schedule
		AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(), SensorService.class);
		PendingIntent scheduledIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 30 seconds
		long interval = 30 * 1000;

		scheduler.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, scheduledIntent);
		
		Log.d(DEBUG_TAG, "Service started");

	}

	public void stopSensorService() {
		// Cancel
		AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(), SensorService.class);
		PendingIntent scheduledIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		scheduler.cancel(scheduledIntent);
		Log.d(DEBUG_TAG, "Service stopped");
	}
	
	public void onToggleClicked(View view) {
	    boolean on = ((ToggleButton) view).isChecked();
	    if (on) {
	    	textStatus.setText("The service is running.");
	    	startSensorService();
	    } else {
	    	textStatus.setText("The service is not running.");
	    	stopSensorService();
	    }
	}
	
	private boolean serviceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if ("ch.eth.soms.mosgap.nervous.SensorService".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}

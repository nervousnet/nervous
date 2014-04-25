package ch.eth.soms.mosgap.nervous;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String DEBUG_TAG = "MainActivity";

	private Button buttonStartService;
	private Button buttonStopService;
	private Button buttonSettings;
	private Button buttonUpload;

	private TextView textStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		buttonStartService = (Button) findViewById(R.id.button_start_service);
		buttonStopService = (Button) findViewById(R.id.button_stop_service);
		buttonSettings = (Button) findViewById(R.id.button_settings);
		buttonUpload = (Button) findViewById(R.id.button_upload);
		
		textStatus = (TextView) findViewById(R.id.text_status);
		

		buttonStartService.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startSensorService();
			}
		});
		
		buttonStopService.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				stopSensorService();
			}
		});

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
		Intent intent = new Intent(this, SensorService.class);
		PendingIntent scheduledIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		scheduler.cancel(scheduledIntent);
		Log.d(DEBUG_TAG, "Service stopped");
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

package ch.ethz.soms.nervous.android;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.soms.nervous.android.sensors.SensorDescAccelerometer;
import ch.ethz.soms.nervous.android.sensors.SensorDescBattery;
import ch.ethz.soms.nervous.android.sensors.SensorDescLight;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static final String DEBUG_TAG = "MainActivity";

	private TextView textStatus;
	private ToggleButton buttonOnOff;

	private boolean serviceRunning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textStatus = (TextView) findViewById(R.id.text_status);
		buttonOnOff = (ToggleButton) findViewById(R.id.togglebutton);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateServiceInfo();
	}

	public void startSensorService() {

		// Schedule
		AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent sensorIntent = new Intent(getApplicationContext(), SensorService.class);
		PendingIntent scheduledSensorIntent = PendingIntent.getService(getApplicationContext(), 0, sensorIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Intent uploadIntent = new Intent(getApplicationContext(), UploadService.class);
		PendingIntent scheduledUploadIntent = PendingIntent.getService(getApplicationContext(), 0, uploadIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 30 seconds
		long sensorInterval = 30 * 1000;

		// 60 seconds
		long uploadInterval = 60 * 1000;

		scheduler.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), sensorInterval, scheduledSensorIntent);

		scheduler.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), uploadInterval, scheduledUploadIntent);

		serviceRunning = true;
		new ServiceInfo(getApplicationContext()).clean();
		textStatus.setText("Service started");
		Log.d(DEBUG_TAG, "Service started");
	}

	public void stopSensorService() {
		// Cancel
		AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent sensorIntent = new Intent(getApplicationContext(), SensorService.class);
		PendingIntent scheduledSensorIntent = PendingIntent.getService(getApplicationContext(), 0, sensorIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Intent uploadIntent = new Intent(getApplicationContext(), UploadService.class);
		PendingIntent scheduledUploadIntent = PendingIntent.getService(getApplicationContext(), 0, uploadIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		scheduler.cancel(scheduledSensorIntent);
		scheduler.cancel(scheduledUploadIntent);

		serviceRunning = false;
		new ServiceInfo(getApplicationContext()).clean();
		Log.d(DEBUG_TAG, "Service stopped");
	}

	public void onToggleClicked(View view) {
		boolean on = ((ToggleButton) view).isChecked();
		if (on) {
			startSensorService();

		} else {
			stopSensorService();
		}

	}

	public void updateServiceInfo() {

		final ServiceInfo info = new ServiceInfo(getApplicationContext());

		serviceRunning = info.serviceIsRunning();

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				final StringBuilder strBuf = new StringBuilder("Service started. \nStarted at: " + info.getTimeOfFirstFrame() + " \nFrames gathered: " + info.getAmountOfFrames() + "\nFile size: " + info.getFileSize() + " Bytes");
				if (!serviceRunning) {
					strBuf.append("\n\nService stopped.");
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						buttonOnOff.setChecked(serviceRunning);
						textStatus.setText(strBuf); // Runs on UI Thread
					}
				});
			}
		}, 0, 1000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_IfThisThenThat:
			intent = new Intent(this, IfThisThenThatActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_SensorLoggingToggle:
			intent = new Intent(this, SensorLoggingToggleActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_TestQuery_Battery_MinBattery:
			SensorDescBattery minBattSensDesc = SensorQueries.minBattery(1, Long.MAX_VALUE, getFilesDir());
			if (minBattSensDesc != null) {
				toastToScreen("Minimum Battery: " + minBattSensDesc.getBatteryPercent() + "\nat " + getDate(minBattSensDesc.getTimestamp()));
			} else {
				toastToScreen("No Data Found");
			}
			break;
		case R.id.menu_TestQuery_Battery_MaxBattery:
			SensorDescBattery maxBattSensDesc = SensorQueries.maxBattery(1, Long.MAX_VALUE, getFilesDir());
			if (maxBattSensDesc != null) {
				toastToScreen("Maximum Battery: " + maxBattSensDesc.getBatteryPercent() + "\nat " + getDate(maxBattSensDesc.getTimestamp()));
			} else {
				toastToScreen("No Data Found");
			}
			break;
		case R.id.menu_TestQuery_Accelerometer_MaxAccelerometer:
			SensorDescAccelerometer maxAccSensDesc = SensorQueries.maxAccelerometerAverage(1, Long.MAX_VALUE, getFilesDir());
			if (maxAccSensDesc != null) {
				toastToScreen("Maximum Accelerometer Average: \n x:" + maxAccSensDesc.getAccX() + "\ny: " + maxAccSensDesc.getAccY() + "\nz: " + maxAccSensDesc.getAccZ() + "\nDate: " + getDate(maxAccSensDesc.getTimestamp()));
			} else {
				toastToScreen("No Data Found");
			}
			break;
		case R.id.menu_TestQuery_Light_MaxLight:
			SensorDescLight maxLightSensDesc = SensorQueries.maxLight(1, Long.MAX_VALUE, getFilesDir());
			if (maxLightSensDesc != null) {
				toastToScreen("Maximum Light: " + maxLightSensDesc.getLight() + "\nDate: " + getDate(maxLightSensDesc.getTimestamp()));
			} else {
				toastToScreen("No Data Found");
			}
			break;
		case R.id.menu_TestQuery_Light_MinLight:
			SensorDescLight minLightSensDesc = SensorQueries.minLight(1, Long.MAX_VALUE, getFilesDir());
			if (minLightSensDesc != null) {
				toastToScreen("Minimum Light: " + minLightSensDesc.getLight() + "\nDate: " + getDate(minLightSensDesc.getTimestamp()));
			} else {
				toastToScreen("No Data Found");
			}
			break;
		case R.id.menu_TestQuery_Accelerometer_MinAccelerometer:
			SensorDescAccelerometer minAccSensDesc = SensorQueries.minAccelerometerAverage(1, Long.MAX_VALUE, getFilesDir());
			if (minAccSensDesc != null) {
				toastToScreen("Minimum Accelerometer Average: \n x:" + minAccSensDesc.getAccX() + "\ny: " + minAccSensDesc.getAccY() + "\nz: " + minAccSensDesc.getAccZ() + "\nDate: " + getDate(minAccSensDesc.getTimestamp()));
			} else {
				toastToScreen("No Data Found");
			}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private String getDate(long time) {
		Calendar cal = Calendar.getInstance(Locale.ENGLISH);
		cal.setTimeInMillis(time);
		String date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
		return date;
	}

	private void toastToScreen(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}

}
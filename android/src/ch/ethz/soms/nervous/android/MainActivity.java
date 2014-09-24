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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesAccelerometer;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesBattery;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesLight;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesProximity;
import ch.ethz.soms.nervous.android.sensors.SensorDescAccelerometer;
import ch.ethz.soms.nervous.android.sensors.SensorDescBattery;
import ch.ethz.soms.nervous.android.sensors.SensorDescLight;
import ch.ethz.soms.nervous.android.sensors.SensorDescProximity;
import ch.ethz.soms.nervous.android.test.PerformanceTestTask;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static final String DEBUG_TAG = "MainActivity";

	private TextView textStatus;
	private ToggleButton buttonOnOff;
	private Button buttonPerfTest;

	private boolean serviceRunning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textStatus = (TextView) findViewById(R.id.text_status);
		buttonOnOff = (ToggleButton) findViewById(R.id.togglebutton);
		buttonPerfTest = (Button) findViewById(R.id.perftestbutton);
		
		buttonPerfTest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new PerformanceTestTask(getApplicationContext()).execute();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateServiceInfo();
	}

	public void startSensorService() {

		// Schedule
		AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent sensorIntent = new Intent(getApplicationContext(),
				SensorService.class);
		PendingIntent scheduledSensorIntent = PendingIntent.getService(
				getApplicationContext(), 0, sensorIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Intent uploadIntent = new Intent(getApplicationContext(),
				UploadService.class);
		PendingIntent scheduledUploadIntent = PendingIntent.getService(
				getApplicationContext(), 0, uploadIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		// 30 seconds
		long sensorInterval = 30 * 1000;

		// 60 seconds
		long uploadInterval = 60 * 1000;

		scheduler.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), sensorInterval,
				scheduledSensorIntent);

		scheduler.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), uploadInterval,
				scheduledUploadIntent);

		serviceRunning = true;
		new ServiceInfo(getApplicationContext()).clean();
		textStatus.setText("Service started");
		Log.d(DEBUG_TAG, "Service started");
	}

	public void stopSensorService() {
		// Cancel
		AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent sensorIntent = new Intent(getApplicationContext(),
				SensorService.class);
		PendingIntent scheduledSensorIntent = PendingIntent.getService(
				getApplicationContext(), 0, sensorIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Intent uploadIntent = new Intent(getApplicationContext(),
				UploadService.class);
		PendingIntent scheduledUploadIntent = PendingIntent.getService(
				getApplicationContext(), 0, uploadIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

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
				final StringBuilder strBuf = new StringBuilder(
						"Service started. \nStarted at: "
								+ info.getTimeOfFirstFrame()
								+ " \nFrames gathered: "
								+ info.getAmountOfFrames() + "\nFile size: "
								+ info.getFileSize() + " Bytes");
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
			minBattery();
			break;
		case R.id.menu_TestQuery_Battery_MaxBattery:
			maxBattery();
			break;
		case R.id.menu_TestQuery_Light_MaxLight:
			maxLight();
			break;
		case R.id.menu_TestQuery_Light_MinLight:
			minLight();
			break;
		case R.id.menu_TestQuery_Accelerometer_MaxAccAverage:
			maxAverageAccelerometer();
			break;
		case R.id.menu_TestQuery_Accelerometer_MinAccAverage:
			minAverageAccelerometer();
			break;
		case R.id.menu_TestQuery_Proximity_MaxProx:
			maxProximity();
			break;
		case R.id.menu_TestQuery_Proximity_MinProx:
			minProximity();
			break;
		case R.id.menu_settings:
			intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void minLight() {
		SensorQueriesLight sensorQ_Light2 = new SensorQueriesLight(1,
				Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Light2.containsReadings()) {
			SensorDescLight minLightDesc = sensorQ_Light2.getMinValue();
			toastToScreen("Minimum Light: " + minLightDesc.getLight() + "\nat "
					+ getDate(minLightDesc.getTimestamp()));
		} else {
			toastToScreen("No Data Found");
		}
	}

	private void maxLight() {
		SensorQueriesLight sensorQ_Light = new SensorQueriesLight(1,
				Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Light.containsReadings()) {
			SensorDescLight maxLightDesc = sensorQ_Light.getMaxValue();
			toastToScreen("Maximum Light: " + maxLightDesc.getLight() + "\nat "
					+ getDate(maxLightDesc.getTimestamp()));
		} else {
			toastToScreen("No Data Found");
		}
	}

	private void minProximity() {
		SensorQueriesProximity sensorQ_Proximity = new SensorQueriesProximity(
				1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Proximity.containsReadings()) {
			SensorDescProximity minProxDesc = sensorQ_Proximity.getMinValue();
			toastToScreen("Minimum Proximity: " + minProxDesc.getProximity()
					+ "\nat " + getDate(minProxDesc.getTimestamp()));
		} else {
			toastToScreen("No Data Found");
		}
	}

	private void maxProximity() {
		SensorQueriesProximity sensorQ_Prox = new SensorQueriesProximity(1,
				Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Prox.containsReadings()) {
			SensorDescProximity maxProxDesc = sensorQ_Prox.getMaxValue();
			toastToScreen("Maximum Prox: " + maxProxDesc.getProximity()
					+ "\nat " + getDate(maxProxDesc.getTimestamp()));
		} else {
			toastToScreen("No Data Found");
		}
	}

	private void maxAverageAccelerometer() {
		SensorQueriesAccelerometer sensorQ_Accel = new SensorQueriesAccelerometer(
				1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Accel.containsReadings()) {
			SensorDescAccelerometer maxAccAverageSensDesc = sensorQ_Accel
					.getMaxAverageValue();
			toastToScreen("Maximum Accelerometer Average: \n x:"
					+ maxAccAverageSensDesc.getAccX() + "\ny: "
					+ maxAccAverageSensDesc.getAccY() + "\nz: "
					+ maxAccAverageSensDesc.getAccZ() + "\nDate: "
					+ getDate(maxAccAverageSensDesc.getTimestamp()));
		} else {
			toastToScreen("No Data Found");
		}
	}

	private void minAverageAccelerometer() {
		SensorQueriesAccelerometer sensorQ_Accel = new SensorQueriesAccelerometer(
				1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Accel.containsReadings()) {
			SensorDescAccelerometer minAccAverageSensDesc = sensorQ_Accel
					.getMinAverageValue();
			toastToScreen("Minimum Accelerometer Average: \n x:"
					+ minAccAverageSensDesc.getAccX() + "\ny: "
					+ minAccAverageSensDesc.getAccY() + "\nz: "
					+ minAccAverageSensDesc.getAccZ() + "\nDate: "
					+ getDate(minAccAverageSensDesc.getTimestamp()));
		} else {
			toastToScreen("No Data Found");
		}
	}

	private void maxBattery() {
		SensorQueriesBattery sensorQ_Batteries2 = new SensorQueriesBattery(1,
				Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Batteries2.containsReadings()) {
			SensorDescBattery maxBatDesc = sensorQ_Batteries2.getMaxValue();
			toastToScreen("Max Battery: " + maxBatDesc.getBatteryPercent()
					+ "\nat " + getDate(maxBatDesc.getTimestamp()));
		} else {
			toastToScreen("No Data Found");
		}
	}

	private void minBattery() {
		SensorQueriesBattery sensorQ_Batteries = new SensorQueriesBattery(1,
				Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Batteries.containsReadings()) {
			SensorDescBattery minBatDesc = sensorQ_Batteries.getMinValue();
			toastToScreen("Minimum Battery: " + minBatDesc.getBatteryPercent()
					+ "\nat " + getDate(minBatDesc.getTimestamp()));
		} else {
			toastToScreen("No Data Found");
		}
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
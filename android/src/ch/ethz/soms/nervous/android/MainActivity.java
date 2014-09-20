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
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import ch.ethz.soms.nervous.android.sensors.SensorDescAccelerometer;
import ch.ethz.soms.nervous.android.sensors.SensorDescBattery;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;
import ch.ethz.soms.nervous.vm.NervousVM;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static final String DEBUG_TAG = "NERVOUS_DEBUG";

	private TextView textStatus;
	private Button buttonExport;
	private ToggleButton buttonOnOff;

	private boolean serviceRunning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textStatus = (TextView) findViewById(R.id.text_status);
		buttonExport = (Button) findViewById(R.id.button_export);
		buttonOnOff = (ToggleButton) findViewById(R.id.togglebutton);

		buttonExport.setOnClickListener(export_handler);
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
		long sensorInterval = 2 * 1000;

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

	View.OnClickListener export_handler = new View.OnClickListener() {

		public void onClick(View v) {

			String state = Environment.getExternalStorageState();

			if (Environment.MEDIA_MOUNTED.equals(state)) {
				Log.d(DEBUG_TAG, "SD card detected");

				stopSensorService();

				Log.d(DEBUG_TAG, "stopSensorService for file transfer");

				long TS = System.currentTimeMillis();
				StringBuilder stringBuilder = new StringBuilder();

				stringBuilder.append(TS);
				stringBuilder.append(".txt");

				String file_name = stringBuilder.toString();
				Log.d(DEBUG_TAG, file_name);
				Log.d(DEBUG_TAG, "file_name assigned to time");

				String sdCard = Environment.getExternalStorageDirectory()
						.getAbsolutePath();

				File dir = new File(sdCard + "/nervous");
				dir.mkdirs();
				File file_ext = new File(dir, file_name);

				Log.d(DEBUG_TAG, "store location of new file");

				try {
					file_ext.createNewFile();
					Log.d(DEBUG_TAG, "Create file with file_name");

					File file = getBaseContext().getFileStreamPath(
							"SensorLog.txt");

					if (file.exists()) {
						Log.d(DEBUG_TAG, "SensorLog.txt exists");
						FileInputStream read_file = openFileInput("SensorLog.txt");

						Log.d(DEBUG_TAG,
								"created Sensorlog.txt file obj read_file");

						InputStreamReader inputStreamReader = new InputStreamReader(
								read_file);
						BufferedReader bufferedReader = new BufferedReader(
								inputStreamReader);
						StringBuilder sb = new StringBuilder();

						sb.append("Timestamp of export to SD : " + TS + "\n");
						String line;
						while ((line = bufferedReader.readLine()) != null) {
							sb.append(line);
						}
						BufferedWriter bufWr = null;

						bufWr = new BufferedWriter(new FileWriter(file_ext,
								false));

						bufWr.append(sb.toString());
						inputStreamReader.close();
						bufWr.close();
						read_file.close();

						getApplicationContext().deleteFile("SensorLog.txt");
						Log.d(DEBUG_TAG, "deleted sensorLog.txt");

					}
					Log.d(DEBUG_TAG, "done with file transfer");

				}

				catch (Exception e) {

					e.printStackTrace();
				}

				if (new ServiceInfo(getApplicationContext()).serviceIsRunning()) {
					startSensorService();
				}
				Log.d(DEBUG_TAG, "startSensorService for file transfer");

			} else

			{
				Log.d(DEBUG_TAG,
						"No external storage detected(cannot copy file)");
				Toast.makeText(getApplicationContext(), "No external storage",
						Toast.LENGTH_LONG).show();

			}
		}
	};

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

		switch (item.getItemId()) {
		case R.id.menu_IfThisThenThat:
			Intent intent = new Intent(this, IfThisThenThatActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_SensorLoggingToggle:
			Intent intent2 = new Intent(this, SensorLoggingToggleActivity.class);
			startActivity(intent2);
			break;
		case R.id.menu_TestQuery_Battery_MinBattery:
			SensorDescBattery minBattSensDesc = SensorQueries.minBattery(1,
					Long.MAX_VALUE, getFilesDir());
			if (minBattSensDesc != null) {
				toast_to_Screen("Minimum Battery: "
						+ minBattSensDesc.getBatteryPercent() + "\nat "
						+ getDate(minBattSensDesc.getTimestamp()));
			} else {
				toast_to_Screen("No Data Found");
			}
			break;
		case R.id.menu_TestQuery_Battery_MaxBattery:
			SensorDescBattery maxBattSensDesc = SensorQueries.maxBattery(1,
					Long.MAX_VALUE, getFilesDir());
			if (maxBattSensDesc != null) {
				toast_to_Screen("Maximum Battery: "
						+ maxBattSensDesc.getBatteryPercent() + "\nat "
						+ getDate(maxBattSensDesc.getTimestamp()));
			} else {
				toast_to_Screen("No Data Found");
			}
			break;
		case R.id.menu_TestQuery_Accelerometer_MaxAccelerometer:
			SensorDescAccelerometer maxAccSensDesc = SensorQueries
					.maxAccelerometerAverage(1, Long.MAX_VALUE, getFilesDir());
			if (maxAccSensDesc != null) {
				toast_to_Screen("Maximum Accelerometer Average: \n x:"
						+ maxAccSensDesc.getAccX() + "\ny: "
						+ maxAccSensDesc.getAccY() + "\nz: "
						+ maxAccSensDesc.getAccZ() + "\nDate: "
						+ getDate(maxAccSensDesc.getTimestamp()));
			} else {
				toast_to_Screen("No Data Found");
			}
			break;
		case R.id.menu_TestQuery_Accelerometer_MinAccelerometer:
			SensorDescAccelerometer minAccSensDesc = SensorQueries
			.minAccelerometerAverage(1, Long.MAX_VALUE, getFilesDir());
			if (minAccSensDesc != null) {
				toast_to_Screen("Minimum Accelerometer Average: \n x:"
						+ minAccSensDesc.getAccX() + "\ny: "
						+ minAccSensDesc.getAccY() + "\nz: "
						+ minAccSensDesc.getAccZ() + "\nDate: "
						+ getDate(minAccSensDesc.getTimestamp()));
			} else {
				toast_to_Screen("No Data Found");
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

	private void toast_to_Screen(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}

}
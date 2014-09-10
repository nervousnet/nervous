package ch.ethz.soms.nervous.android;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import java.util.Timer;
import java.util.TimerTask;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String DEBUG_TAG = "MainActivity";

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
		Intent intent = new Intent(getApplicationContext(), SensorService.class);
		PendingIntent scheduledIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 30 seconds
		long interval = 30 * 1000;

		scheduler.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, scheduledIntent);

		serviceRunning = true;
		new ServiceInfo(getApplicationContext()).clean();
		textStatus.setText("Service started");
		Log.d(DEBUG_TAG, "Service started");
	}

	public void stopSensorService() {
		// Cancel
		AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(), SensorService.class);
		PendingIntent scheduledIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		scheduler.cancel(scheduledIntent);

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

				String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath();

				File dir = new File(sdCard + "/nervous");
				dir.mkdirs();
				File file_ext = new File(dir, file_name);

				Log.d(DEBUG_TAG, "store location of new file");

				try {
					file_ext.createNewFile();
					Log.d(DEBUG_TAG, "Create file with file_name");

					File file = getBaseContext().getFileStreamPath("SensorLog.txt");

					if (file.exists()) {
						Log.d(DEBUG_TAG, "SensorLog.txt exists");
						FileInputStream read_file = openFileInput("SensorLog.txt");

						Log.d(DEBUG_TAG, "created Sensorlog.txt file obj read_file");

						InputStreamReader inputStreamReader = new InputStreamReader(read_file);
						BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
						StringBuilder sb = new StringBuilder();

						sb.append("Timestamp of export to SD : " + TS + "\n");
						String line;
						while ((line = bufferedReader.readLine()) != null) {
							sb.append(line);
						}
						BufferedWriter bufWr = null;

						bufWr = new BufferedWriter(new FileWriter(file_ext, false));

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

				if(new ServiceInfo(getApplicationContext()).serviceIsRunning()) {
					startSensorService();
				}
				Log.d(DEBUG_TAG, "startSensorService for file transfer");

			} else

			{
				Log.d(DEBUG_TAG, "No external storage detected(cannot copy file)");
				Toast.makeText(getApplicationContext(), "No external storage", Toast.LENGTH_LONG).show();

			}
		}
	};

	public void updateServiceInfo() {
		
		final ServiceInfo info = new ServiceInfo(getApplicationContext());
		
		serviceRunning = info.serviceIsRunning();
		
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				final String str;
				if (serviceRunning) {
					str = "Service started. \nStarted at: " + info.getTimeOfFirstFrame() + " \nFrames gathered: " + info.getAmountOfFrames() + "\nFile size: " + info.getFileSize() + " Bytes";
				} else {
					str = "Service stopped.";
				}
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						buttonOnOff.setChecked(serviceRunning);
						textStatus.setText(str); // Runs on UI Thread
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
		default:
			break;
		}
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

}
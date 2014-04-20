package ch.eth.soms.mosgap.sensorservice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class SensorService extends Service implements SensorEventListener {

	private static final String DEBUG_TAG = "SensorService";

	private final IBinder mBinder = new SensorBinder();
	private SensorManager sensorManager = null;

	private SensorFrame sensorFrame = null;
	private SensorHeader sensorHeader = null;

	private Intent batteryStatus = null;
	private Sensor sensorAccelerometer = null;
	private Sensor sensorLight = null;

	// TODO: Add all other sensors

	public class SensorBinder extends Binder {
		SensorService getService() {
			return SensorService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		// TODO: Add all other sensors

		sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);

		sensorHeader = new SensorHeader(sensorManager);
		// True means the sensor works with a listener and we don't know when it will be triggered
		sensorHeader.addSensor(SensorDataAccelerometer.class, true);
		// False means it's a parameter we can query, therefore we don't have to wait for it
		sensorHeader.addSensor(SensorDataBattery.class, false);

		sensorFrame = new SensorFrame(sensorHeader);

		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		batteryStatus = getApplicationContext().registerReceiver(null, ifilter);

		Log.d(DEBUG_TAG, "Service execution started");
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {

	}

	@Override
	public void onDestroy() {

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Do nothing

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		Sensor sensor = event.sensor;
		switch (sensor.getType()) {
		case Sensor.TYPE_LIGHT:
			// TODO
			break;
		case Sensor.TYPE_PROXIMITY:
			// TODO
			break;
		case Sensor.TYPE_ACCELEROMETER:
			sensorFrame.addSensorData(new SensorDataAccelerometer(event.values[0], event.values[1], event.values[2]));
			Log.d(DEBUG_TAG, "Accelerometer data added to frame");
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			// TODO
			break;
		}
		
		
		if (sensorFrame.isComplete()) {
			// Add sensor data which can be queried
			
			int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
			int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
			boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
			boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
			float batteryPct = level / (float) scale;

			SensorDataBattery sensorDataBattey = new SensorDataBattery(batteryPct, isCharging, usbCharge, acCharge);
			sensorFrame.addSensorData(sensorDataBattey);
			Log.d(DEBUG_TAG, "Battery data added to frame");

			
			// Append frame to log
			new SensorServiceLoggerTask().execute(sensorFrame);
			
			// Stop service until it's triggered the next time
			sensorManager.unregisterListener(this);
			Log.d(DEBUG_TAG, "Service execution stopped");
			stopSelf();
		}
	}

	/**
	 * Asynchronous task to write to the log file
	 */
	private class SensorServiceLoggerTask extends AsyncTask<SensorFrame, Void, Void> {

		@Override
		protected Void doInBackground(SensorFrame... frames) {
			SensorFrame frame = frames[0];

			// Capture the timestamp
			frame.setTimestamp();

			BufferedWriter bufWr = null;
			try {
				File file = new File(getApplicationContext().getFilesDir(), "SensorLog.txt");
				if (file.exists()) {
					// Write to new file
					bufWr = new BufferedWriter(new FileWriter(file, true));

				} else {
					file.createNewFile();
					Log.d(DEBUG_TAG, "New log file created");
					// Append to existing file
					bufWr = new BufferedWriter(new FileWriter(file, false));
					// Write header
					bufWr.append(sensorHeader.toString());
				}
				// Write frame
				bufWr.append(sensorFrame.toString());
				bufWr.flush();
				Log.d(DEBUG_TAG, "Added frame to log");
			} catch (IOException ex) {
				// TODO: useful error handling
			} finally {
				// Cleanup
				if (bufWr != null) {
					try {
						bufWr.close();
					} catch (IOException ex) {
						// TODO: useful error handling
					}
				}
			}
			return null;
		}
	}

}

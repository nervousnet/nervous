package ch.eth.soms.mosgap.sensorservice;

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

public class SensorService extends Service implements SensorEventListener {
	
	private static final String DEBUG_TAG = "SensorService";

	private final IBinder mBinder = new SensorBinder();
	private SensorManager sensorManager = null;
	
	private SensorFrame sensorFrame = null;
	
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
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		// TODO: Add all other sensors
		
		sensorFrame = new SensorFrame();
		
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
		
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
		if(sensorFrame.isComplete())
		{
			int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
			                     status == BatteryManager.BATTERY_STATUS_FULL;
			int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
			boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
			boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
			float batteryPct = level / (float)scale;

		}
		sensorManager.unregisterListener(this);
		stopSelf();
	}
	
	
	private class SensorServiceLoggerTask extends AsyncTask<SensorFrame, Void, Void>
	{

		@Override
		protected Void doInBackground(SensorFrame... frames) {
			SensorFrame frame = frames[0];
			
			// TODO: logging
			
			return null;
		}
		
	}

}

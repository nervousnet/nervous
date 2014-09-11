package ch.ethz.soms.nervous.android;

import java.util.HashSet;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import ch.ethz.soms.nervous.vm.StoreTask;

public class SensorService extends Service implements SensorEventListener {

	private static final String DEBUG_TAG = "SensorService";

	private final IBinder mBinder = new SensorBinder();
	private SensorManager sensorManager = null;

	private Intent batteryStatus = null;
	private Sensor sensorAccelerometer = null;
	private Sensor sensorLight = null;
	private Sensor sensorMagnet = null;
	private Sensor sensorProximity = null;
	private Sensor sensorGyroscope = null;
	private Sensor sensorTemperature = null;
	private Sensor sensorHumidity = null;

	private boolean hasAccelerometer = false;
	private boolean hasLight = false;
	private boolean hasMagnet = false;
	private boolean hasProximity = false;
	private boolean hasGyroscope = false;
	private boolean hasTemperature = false;
	private boolean hasHumidity = false;

	private HashSet<Class<? extends SensorDesc>> sensorCollected;

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
		sensorMagnet = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		sensorProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		sensorTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
		sensorHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

		hasAccelerometer = sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		hasLight = sensorManager.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);
		hasMagnet = sensorManager.registerListener(this, sensorMagnet, SensorManager.SENSOR_DELAY_NORMAL);
		hasProximity = sensorManager.registerListener(this, sensorProximity, SensorManager.SENSOR_DELAY_NORMAL);
		hasGyroscope = sensorManager.registerListener(this, sensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
		hasTemperature = sensorManager.registerListener(this, sensorTemperature, SensorManager.SENSOR_DELAY_NORMAL);
		hasHumidity = sensorManager.registerListener(this, sensorHumidity, SensorManager.SENSOR_DELAY_NORMAL);

		sensorCollected = new HashSet<Class<? extends SensorDesc>>();
		if (hasAccelerometer) {
			sensorCollected.add(SensorDescAccelerometer.class);
		}
		if (hasLight) {
			sensorCollected.add(SensorDescLight.class);
		}
		if (hasMagnet) {
			sensorCollected.add(SensorDescMagnetic.class);
		}
		if (hasProximity) {
			sensorCollected.add(SensorDescProximity.class);
		}
		if (hasGyroscope) {
			sensorCollected.add(SensorDescGyroscope.class);
		}
		if (hasTemperature) {
			sensorCollected.add(SensorDescTemperature.class);
		}
		if (hasHumidity) {
			sensorCollected.add(SensorDescHumidity.class);
		}

		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

		batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
		int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
		boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
		float batteryPct = level / (float) scale;

		long timestamp = System.currentTimeMillis();
		SensorDescBattery sensorDescBattey = new SensorDescBattery(timestamp, batteryPct, isCharging, usbCharge, acCharge);
		SensorDesc sensorDesc = sensorDescBattey;
		Log.d(DEBUG_TAG, "Battery data collected");
		new StoreTask(getApplicationContext()).execute(sensorDesc);

		Log.d(DEBUG_TAG, "Service execution started");
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		// Do nothing

	}

	@Override
	public void onDestroy() {
		// Do nothing
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Do nothing

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		long timestamp = System.currentTimeMillis();
		Sensor sensor = event.sensor;
		SensorDesc sensorDesc = null;

		switch (sensor.getType()) {
		case Sensor.TYPE_LIGHT:
			sensorDesc = new SensorDescLight(timestamp, event.accuracy, event.values[0]);
			Log.d(DEBUG_TAG, "Light data collected");
			break;
		case Sensor.TYPE_PROXIMITY:
			sensorDesc = new SensorDescProximity(timestamp, event.accuracy, event.values[0]);
			Log.d(DEBUG_TAG, "Proximity data collected");
			break;
		case Sensor.TYPE_ACCELEROMETER:
			sensorDesc = new SensorDescAccelerometer(timestamp, event.accuracy, event.values[0], event.values[1], event.values[2]);
			Log.d(DEBUG_TAG, "Accelerometer data collected");
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			sensorDesc = new SensorDescMagnetic(timestamp, event.accuracy, event.values[0], event.values[1], event.values[2]);
			Log.d(DEBUG_TAG, "Magnetic data collected");
			break;
		case Sensor.TYPE_GYROSCOPE:
			sensorDesc = new SensorDescGyroscope(timestamp, event.accuracy, event.values[0], event.values[1], event.values[2]);
			Log.d(DEBUG_TAG, "Gyroscope data collected");
			break;
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
			sensorDesc = new SensorDescTemperature(timestamp, event.accuracy, event.values[0]);
			Log.d(DEBUG_TAG, "Temperature data collected");
			break;
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			sensorDesc = new SensorDescHumidity(timestamp, event.accuracy, event.values[0]);
			Log.d(DEBUG_TAG, "Humidity data collected");
			break;
		}

		if (sensorDesc != null) {
			if (sensorCollected.contains(sensorDesc.getClass())) {
				sensorCollected.remove(sensorDesc.getClass());
				new StoreTask(getApplicationContext()).execute(sensorDesc);
			}
		}

		if (sensorCollected.isEmpty()) {
			// Stop service until it's triggered the next time
			sensorManager.unregisterListener(this);

			Log.d(DEBUG_TAG, "Service execution stopped");

			ServiceInfo info = new ServiceInfo(getApplicationContext());
			info.setTimeOfLastFrame();
			stopSelf();
		}
	}
}

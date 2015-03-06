package ch.ethz.soms.nervous.android;

import java.util.HashMap;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import ch.ethz.soms.nervous.android.sensors.BLEBeaconRecord;
import ch.ethz.soms.nervous.android.sensors.BLESensor;
import ch.ethz.soms.nervous.android.sensors.BLESensor.BLEBeaconListener;
import ch.ethz.soms.nervous.android.sensors.BatterySensor;
import ch.ethz.soms.nervous.android.sensors.BatterySensor.BatteryListener;
import ch.ethz.soms.nervous.android.sensors.NoiseSensor;
import ch.ethz.soms.nervous.android.sensors.NoiseSensor.NoiseListener;
import ch.ethz.soms.nervous.android.sensors.SensorDesc;
import ch.ethz.soms.nervous.android.sensors.SensorDescAccelerometer;
import ch.ethz.soms.nervous.android.sensors.SensorDescBLEBeacon;
import ch.ethz.soms.nervous.android.sensors.SensorDescBattery;
import ch.ethz.soms.nervous.android.sensors.SensorDescGyroscope;
import ch.ethz.soms.nervous.android.sensors.SensorDescHumidity;
import ch.ethz.soms.nervous.android.sensors.SensorDescLight;
import ch.ethz.soms.nervous.android.sensors.SensorDescMagnetic;
import ch.ethz.soms.nervous.android.sensors.SensorDescNoise;
import ch.ethz.soms.nervous.android.sensors.SensorDescPressure;
import ch.ethz.soms.nervous.android.sensors.SensorDescProximity;
import ch.ethz.soms.nervous.android.sensors.SensorDescTemperature;

public class SensorService extends Service implements SensorEventListener, NoiseListener, BatteryListener, BLEBeaconListener {

	private static final String DEBUG_TAG = "SensorService";

	private final IBinder mBinder = new SensorBinder();
	private SensorManager sensorManager = null;

	private SensorConfiguration sensorConfiguration;
	private int serviceRound = 0;

	private Sensor sensorAccelerometer = null;
	private BatterySensor sensorBattery = null;
	private Sensor sensorLight = null;
	private Sensor sensorMagnet = null;
	private Sensor sensorProximity = null;
	private Sensor sensorGyroscope = null;
	private Sensor sensorTemperature = null;
	private Sensor sensorHumidity = null;
	private Sensor sensorPressure = null;
	private NoiseSensor sensorNoise = null;
	private BLESensor sensorBLEBeacon = null;

	private SensorCollectStatus scAccelerometer = null;
	private SensorCollectStatus scBattery = null;
	private SensorCollectStatus scLight = null;
	private SensorCollectStatus scMagnet = null;
	private SensorCollectStatus scProximity = null;
	private SensorCollectStatus scGyroscope = null;
	private SensorCollectStatus scTemperature = null;
	private SensorCollectStatus scHumidity = null;
	private SensorCollectStatus scPressure = null;
	private SensorCollectStatus scNoise = null;
	private SensorCollectStatus scBLEBeacon = null;

	private boolean hasAccelerometer = false;
	private boolean hasBattery = false;
	private boolean hasLight = false;
	private boolean hasMagnet = false;
	private boolean hasProximity = false;
	private boolean hasGyroscope = false;
	private boolean hasTemperature = false;
	private boolean hasHumidity = false;
	private boolean hasPressure = false;
	private boolean hasNoise = false;
	private boolean hasBLEBeacon = false;

	private HashMap<Class<? extends SensorDesc>, SensorCollectStatus> sensorCollected;

	public class SensorBinder extends Binder {
		SensorService getService() {
			return SensorService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		sensorConfiguration = SensorConfiguration.getInstance(getApplicationContext());
		serviceRound = sensorConfiguration.getServiceRound();
		sensorConfiguration.increaseServiceRound();

		scAccelerometer = sensorConfiguration.getInitialSensorCollectStatus(SensorDescAccelerometer.SENSOR_ID, serviceRound);
		scBattery = sensorConfiguration.getInitialSensorCollectStatus(SensorDescBattery.SENSOR_ID, serviceRound);
		scLight = sensorConfiguration.getInitialSensorCollectStatus(SensorDescLight.SENSOR_ID, serviceRound);
		scMagnet = sensorConfiguration.getInitialSensorCollectStatus(SensorDescMagnetic.SENSOR_ID, serviceRound);
		scProximity = sensorConfiguration.getInitialSensorCollectStatus(SensorDescProximity.SENSOR_ID, serviceRound);
		scGyroscope = sensorConfiguration.getInitialSensorCollectStatus(SensorDescGyroscope.SENSOR_ID, serviceRound);
		scTemperature = sensorConfiguration.getInitialSensorCollectStatus(SensorDescTemperature.SENSOR_ID, serviceRound);
		scHumidity = sensorConfiguration.getInitialSensorCollectStatus(SensorDescHumidity.SENSOR_ID, serviceRound);
		scPressure = sensorConfiguration.getInitialSensorCollectStatus(SensorDescPressure.SENSOR_ID, serviceRound);
		scNoise = sensorConfiguration.getInitialSensorCollectStatus(SensorDescNoise.SENSOR_ID, serviceRound);
		scBLEBeacon = sensorConfiguration.getInitialSensorCollectStatus(SensorDescBLEBeacon.SENSOR_ID, serviceRound);

		hasAccelerometer = scAccelerometer.isCollect(serviceRound);
		hasBattery = scAccelerometer.isCollect(serviceRound);
		hasLight = scAccelerometer.isCollect(serviceRound);
		hasMagnet = scAccelerometer.isCollect(serviceRound);
		hasProximity = scAccelerometer.isCollect(serviceRound);
		hasGyroscope = scAccelerometer.isCollect(serviceRound);
		hasTemperature = scAccelerometer.isCollect(serviceRound);
		hasHumidity = scAccelerometer.isCollect(serviceRound);
		hasPressure = scAccelerometer.isCollect(serviceRound);
		hasNoise = scAccelerometer.isCollect(serviceRound);
		hasBLEBeacon = scBLEBeacon.isCollect(serviceRound);

		// Noise sensor
		sensorNoise = new NoiseSensor();
		sensorNoise.addListener(this);
		// Noise sensor doesn't really make sense with less than 250ms
		sensorNoise.startRecording(Math.max(scNoise.getMeasureDuration(), 250));

		// Battery sensor
		sensorBattery = new BatterySensor(getApplicationContext());
		sensorBattery.addListener(this);
		sensorBattery.start();

		// Connectivity sensor
		// TODO

		// BLE sensor
		sensorBLEBeacon = new BLESensor(getApplicationContext());
		sensorBLEBeacon.addListener(this);
		sensorBLEBeacon.startScanning(Math.max(scBLEBeacon.getMeasureDuration(), 2000));

		// Normal android sensors
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		sensorMagnet = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		sensorProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		sensorTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
		sensorHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
		sensorPressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

		hasAccelerometer = sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		hasLight = sensorManager.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);
		hasMagnet = sensorManager.registerListener(this, sensorMagnet, SensorManager.SENSOR_DELAY_NORMAL);
		hasProximity = sensorManager.registerListener(this, sensorProximity, SensorManager.SENSOR_DELAY_NORMAL);
		hasGyroscope = sensorManager.registerListener(this, sensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
		hasTemperature = sensorManager.registerListener(this, sensorTemperature, SensorManager.SENSOR_DELAY_NORMAL);
		hasHumidity = sensorManager.registerListener(this, sensorHumidity, SensorManager.SENSOR_DELAY_NORMAL);
		hasPressure = sensorManager.registerListener(this, sensorPressure, SensorManager.SENSOR_DELAY_NORMAL);

		sensorCollected = new HashMap<Class<? extends SensorDesc>, SensorCollectStatus>();
		if (hasAccelerometer) {
			sensorCollected.put(SensorDescAccelerometer.class, scAccelerometer);
		}
		if (hasLight) {
			sensorCollected.put(SensorDescLight.class, scLight);
		}
		if (hasMagnet) {
			sensorCollected.put(SensorDescMagnetic.class, scMagnet);
		}
		if (hasProximity) {
			sensorCollected.put(SensorDescProximity.class, scProximity);
		}
		if (hasGyroscope) {
			sensorCollected.put(SensorDescGyroscope.class, scGyroscope);
		}
		if (hasTemperature) {
			sensorCollected.put(SensorDescTemperature.class, scTemperature);
		}
		if (hasHumidity) {
			sensorCollected.put(SensorDescHumidity.class, scHumidity);
		}
		if (hasPressure) {
			sensorCollected.put(SensorDescPressure.class, scPressure);
		}
		if (hasNoise) {
			sensorCollected.put(SensorDescNoise.class, scNoise);
		}
		if (hasBattery) {
			sensorCollected.put(SensorDescBattery.class, scBattery);
		}
		if (hasBLEBeacon) {
			sensorCollected.put(SensorDescBLEBeacon.class, scBLEBeacon);
		}

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
			sensorDesc = new SensorDescLight(timestamp, event.values[0]);
			Log.d(DEBUG_TAG, "Light data collected");
			break;
		case Sensor.TYPE_PROXIMITY:
			sensorDesc = new SensorDescProximity(timestamp, event.values[0]);
			Log.d(DEBUG_TAG, "Proximity data collected");
			break;
		case Sensor.TYPE_ACCELEROMETER:
			sensorDesc = new SensorDescAccelerometer(timestamp, event.values[0], event.values[1], event.values[2]);
			Log.d(DEBUG_TAG, "Accelerometer data collected");
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			sensorDesc = new SensorDescMagnetic(timestamp, event.values[0], event.values[1], event.values[2]);
			Log.d(DEBUG_TAG, "Magnetic data collected");
			break;
		case Sensor.TYPE_GYROSCOPE:
			sensorDesc = new SensorDescGyroscope(timestamp, event.values[0], event.values[1], event.values[2]);
			Log.d(DEBUG_TAG, "Gyroscope data collected");
			break;
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
			sensorDesc = new SensorDescTemperature(timestamp, event.values[0]);
			Log.d(DEBUG_TAG, "Temperature data collected");
			break;
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			sensorDesc = new SensorDescHumidity(timestamp, event.values[0]);
			Log.d(DEBUG_TAG, "Humidity data collected");
			break;
		case Sensor.TYPE_PRESSURE:
			sensorDesc = new SensorDescProximity(timestamp, event.values[0]);
			Log.d(DEBUG_TAG, "Pressure data collected");
			break;
		}

		store(sensorDesc);
	}

	@Override
	public void noiseSensorDataReady(long timestamp, float rms, float spl, float[] bands) {
		SensorDesc sensorDesc = new SensorDescNoise(timestamp, rms, spl, bands);
		store(sensorDesc);
	}

	@Override
	public void batterySensorDataReady(long timestamp, float batteryPercent, boolean isCharging, boolean isUsbCharge, boolean isAcCharge) {
		SensorDesc sensorDesc = new SensorDescBattery(timestamp, batteryPercent, isCharging, isUsbCharge, isAcCharge);
		store(sensorDesc);
	}

	@Override
	public void bleSensorDataReady(List<BLEBeaconRecord> beaconRecordList) {
		if (beaconRecordList != null && beaconRecordList.size() > 0) {
			for (BLEBeaconRecord bbr : beaconRecordList) {
				SensorDesc sensorDesc = new SensorDescBLEBeacon(bbr.getTokenDetectTime(), bbr.getRssi(), bbr.getMac(), bbr.getAdvertisement().getMostSignificantBits(), bbr.getAdvertisement().getLeastSignificantBits(), bbr.getUuid().getMostSignificantBits(), bbr.getUuid().getLeastSignificantBits(), bbr.getMajor(), bbr.getMinor(), bbr.getTxpower());
				store(sensorDesc);
			}
		}
		// Kick this sensor out anyways as it is possible to retrieve no data at all after the measurement interval
		sensorCollected.remove(SensorDescBLEBeacon.class);
	}

	private synchronized void store(SensorDesc sensorDesc) {
		if (sensorDesc != null) {
			SensorCollectStatus scs = sensorCollected.get(sensorDesc.getClass());
			if (scs != null) {
				if (!scs.isDone(System.currentTimeMillis())) {
					// Collected new data of this type, count it
					scs.increaseCollectAmount();
					// Enough collected, remove from list
					if (scs.isDone(System.currentTimeMillis())) {
						sensorCollected.remove(sensorDesc.getClass());
						// Remove from listener list
						unregisterSensor(sensorDesc);
					}
					new StoreTask(getApplicationContext()).execute(sensorDesc);
				} else {
					sensorCollected.remove(sensorDesc.getClass());
				}
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

	private void unregisterSensor(SensorDesc sensorDesc) {
		if (sensorDesc.getSensorIdentifier() == SensorDescAccelerometer.SENSOR_ID) {
			sensorManager.unregisterListener(this, sensorAccelerometer);
		} else if (sensorDesc.getSensorIdentifier() == SensorDescPressure.SENSOR_ID) {
			sensorManager.unregisterListener(this, sensorPressure);
		} else if (sensorDesc.getSensorIdentifier() == SensorDescGyroscope.SENSOR_ID) {
			sensorManager.unregisterListener(this, sensorGyroscope);
		} else if (sensorDesc.getSensorIdentifier() == SensorDescHumidity.SENSOR_ID) {
			sensorManager.unregisterListener(this, sensorHumidity);
		} else if (sensorDesc.getSensorIdentifier() == SensorDescLight.SENSOR_ID) {
			sensorManager.unregisterListener(this, sensorLight);
		} else if (sensorDesc.getSensorIdentifier() == SensorDescMagnetic.SENSOR_ID) {
			sensorManager.unregisterListener(this, sensorMagnet);
		} else if (sensorDesc.getSensorIdentifier() == SensorDescProximity.SENSOR_ID) {
			sensorManager.unregisterListener(this, sensorProximity);
		} else if (sensorDesc.getSensorIdentifier() == SensorDescTemperature.SENSOR_ID) {
			sensorManager.unregisterListener(this, sensorTemperature);
		}
	}

}

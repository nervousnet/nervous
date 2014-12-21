package ch.ethz.soms.nervous.android;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import ch.ethz.soms.nervous.android.sensors.BLEBeaconRecord;
import ch.ethz.soms.nervous.android.sensors.BLESensor;
import ch.ethz.soms.nervous.android.sensors.BLESensor.BLEBeaconListener;
import ch.ethz.soms.nervous.android.sensors.BatterySensor;
import ch.ethz.soms.nervous.android.sensors.BatterySensor.BatteryListener;
import ch.ethz.soms.nervous.android.sensors.ConnectivitySensor;
import ch.ethz.soms.nervous.android.sensors.ConnectivitySensor.ConnectivityListener;
import ch.ethz.soms.nervous.android.sensors.NoiseSensor;
import ch.ethz.soms.nervous.android.sensors.NoiseSensor.NoiseListener;
import ch.ethz.soms.nervous.android.sensors.SensorDesc;
import ch.ethz.soms.nervous.android.sensors.SensorDescAccelerometer;
import ch.ethz.soms.nervous.android.sensors.SensorDescBLEBeacon;
import ch.ethz.soms.nervous.android.sensors.SensorDescBattery;
import ch.ethz.soms.nervous.android.sensors.SensorDescConnectivity;
import ch.ethz.soms.nervous.android.sensors.SensorDescGyroscope;
import ch.ethz.soms.nervous.android.sensors.SensorDescHumidity;
import ch.ethz.soms.nervous.android.sensors.SensorDescLight;
import ch.ethz.soms.nervous.android.sensors.SensorDescMagnetic;
import ch.ethz.soms.nervous.android.sensors.SensorDescNoise;
import ch.ethz.soms.nervous.android.sensors.SensorDescPressure;
import ch.ethz.soms.nervous.android.sensors.SensorDescProximity;
import ch.ethz.soms.nervous.android.sensors.SensorDescTemperature;

public class SensorService extends Service implements SensorEventListener, NoiseListener, BatteryListener, BLEBeaconListener, ConnectivityListener {

	private static final String LOG_TAG = SensorService.class.getSimpleName();

	private final IBinder mBinder = new SensorBinder();
	private SensorManager sensorManager = null;

	private PowerManager.WakeLock wakeLock;
	private HandlerThread hthread;
	private Handler handler;
	private Lock wakeLockMutex;

	private SensorConfiguration sensorConfiguration;
	private SensorService sensorListenerClass;

	// Only initialize these once
	private Sensor sensorAccelerometer = null;
	private BatterySensor sensorBattery = null;
	private ConnectivitySensor sensorConnectivity = null;
	private Sensor sensorLight = null;
	private Sensor sensorMagnet = null;
	private Sensor sensorProximity = null;
	private Sensor sensorGyroscope = null;
	private Sensor sensorTemperature = null;
	private Sensor sensorHumidity = null;
	private Sensor sensorPressure = null;
	private NoiseSensor sensorNoise = null;
	private BLESensor sensorBLEBeacon = null;

	// Those need to be reset on every collect call
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
	private SensorCollectStatus scConnectivity = null;

	// Threadsafe because handling can get called from different threads
	private ConcurrentHashMap<Class<? extends SensorDesc>, SensorCollectStatus> sensorCollected;

	public class SensorBinder extends Binder {
		SensorService getService() {
			return SensorService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Prepare the wakelock
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOG_TAG);
		wakeLockMutex = new ReentrantLock();

		// Reference for inner runnable
		sensorListenerClass = this;
		sensorConfiguration = SensorConfiguration.getInstance(getApplicationContext());

		// Initialize sensor manager
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Hash map to register sensor collect status references
		sensorCollected = new ConcurrentHashMap<Class<? extends SensorDesc>, SensorCollectStatus>();

		// Initialize sensor collect status from configuration
		scAccelerometer = sensorConfiguration.getInitialSensorCollectStatus(SensorDescAccelerometer.SENSOR_ID);
		scBattery = sensorConfiguration.getInitialSensorCollectStatus(SensorDescBattery.SENSOR_ID);
		scLight = sensorConfiguration.getInitialSensorCollectStatus(SensorDescLight.SENSOR_ID);
		scMagnet = sensorConfiguration.getInitialSensorCollectStatus(SensorDescMagnetic.SENSOR_ID);
		scProximity = sensorConfiguration.getInitialSensorCollectStatus(SensorDescProximity.SENSOR_ID);
		scGyroscope = sensorConfiguration.getInitialSensorCollectStatus(SensorDescGyroscope.SENSOR_ID);
		scTemperature = sensorConfiguration.getInitialSensorCollectStatus(SensorDescTemperature.SENSOR_ID);
		scHumidity = sensorConfiguration.getInitialSensorCollectStatus(SensorDescHumidity.SENSOR_ID);
		scPressure = sensorConfiguration.getInitialSensorCollectStatus(SensorDescPressure.SENSOR_ID);
		scNoise = sensorConfiguration.getInitialSensorCollectStatus(SensorDescNoise.SENSOR_ID);
		scBLEBeacon = sensorConfiguration.getInitialSensorCollectStatus(SensorDescBLEBeacon.SENSOR_ID);
		scConnectivity = sensorConfiguration.getInitialSensorCollectStatus(SensorDescConnectivity.SENSOR_ID);

		// Get references to android default sensors
		sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		sensorMagnet = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		sensorProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		sensorTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
		sensorHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
		sensorPressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

		// Custom sensors
		sensorBattery = new BatterySensor(getApplicationContext());
		sensorConnectivity = new ConnectivitySensor(getApplicationContext());
		sensorBLEBeacon = new BLESensor(getApplicationContext());
		sensorNoise = new NoiseSensor();

		// Schedule all sensors (initially)
		scheduleSensor(SensorDescAccelerometer.SENSOR_ID);
		scheduleSensor(SensorDescLight.SENSOR_ID);
		scheduleSensor(SensorDescMagnetic.SENSOR_ID);
		scheduleSensor(SensorDescProximity.SENSOR_ID);
		scheduleSensor(SensorDescGyroscope.SENSOR_ID);
		scheduleSensor(SensorDescTemperature.SENSOR_ID);
		scheduleSensor(SensorDescHumidity.SENSOR_ID);
		scheduleSensor(SensorDescPressure.SENSOR_ID);
		scheduleSensor(SensorDescNoise.SENSOR_ID);
		scheduleSensor(SensorDescBLEBeacon.SENSOR_ID);
		scheduleSensor(SensorDescConnectivity.SENSOR_ID);
		scheduleSensor(SensorDescBattery.SENSOR_ID);

		Log.d(LOG_TAG, "Service execution started");
		return START_STICKY;
	}

	private void scheduleSensor(final long sensorId) {
		handler = new Handler(hthread.getLooper());
		final Runnable run = new Runnable() {
			@Override
			public void run() {

				boolean doCollect = false;
				Class<? extends SensorDesc> sensorDescClass = null;
				SensorCollectStatus sensorCollectStatus = null;
				long startTime = System.currentTimeMillis();

				if (sensorId == SensorDescAccelerometer.SENSOR_ID) {
					scAccelerometer.setMeasureStart(startTime);
					doCollect = scAccelerometer.isCollect();
					doCollect = doCollect ? sensorManager.registerListener(sensorListenerClass, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL) : false;
					sensorCollectStatus = scAccelerometer;
					sensorDescClass = SensorDescAccelerometer.class;

				} else if (sensorId == SensorDescPressure.SENSOR_ID) {
					scPressure.setMeasureStart(startTime);
					doCollect = scPressure.isCollect();
					doCollect = doCollect ? sensorManager.registerListener(sensorListenerClass, sensorPressure, SensorManager.SENSOR_DELAY_NORMAL) : false;
					sensorCollectStatus = scPressure;
					sensorDescClass = SensorDescPressure.class;

				} else if (sensorId == SensorDescGyroscope.SENSOR_ID) {
					scGyroscope.setMeasureStart(startTime);
					doCollect = scGyroscope.isCollect();
					doCollect = doCollect ? sensorManager.registerListener(sensorListenerClass, sensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL) : false;
					sensorCollectStatus = scGyroscope;
					sensorDescClass = SensorDescGyroscope.class;

				} else if (sensorId == SensorDescHumidity.SENSOR_ID) {
					scHumidity.setMeasureStart(startTime);
					doCollect = scHumidity.isCollect();
					doCollect = doCollect ? sensorManager.registerListener(sensorListenerClass, sensorHumidity, SensorManager.SENSOR_DELAY_NORMAL) : false;
					sensorCollectStatus = scHumidity;
					sensorDescClass = SensorDescHumidity.class;

				} else if (sensorId == SensorDescLight.SENSOR_ID) {
					scLight.setMeasureStart(startTime);
					doCollect = scLight.isCollect();
					doCollect = doCollect ? sensorManager.registerListener(sensorListenerClass, sensorLight, SensorManager.SENSOR_DELAY_NORMAL) : false;
					sensorCollectStatus = scLight;
					sensorDescClass = SensorDescLight.class;

				} else if (sensorId == SensorDescMagnetic.SENSOR_ID) {
					scMagnet.setMeasureStart(startTime);
					doCollect = scMagnet.isCollect();
					doCollect = doCollect ? sensorManager.registerListener(sensorListenerClass, sensorMagnet, SensorManager.SENSOR_DELAY_NORMAL) : false;
					sensorCollectStatus = scMagnet;
					sensorDescClass = SensorDescMagnetic.class;

				} else if (sensorId == SensorDescProximity.SENSOR_ID) {
					scProximity.setMeasureStart(startTime);
					doCollect = scProximity.isCollect();
					doCollect = doCollect ? sensorManager.registerListener(sensorListenerClass, sensorProximity, SensorManager.SENSOR_DELAY_NORMAL) : false;
					sensorCollectStatus = scProximity;
					sensorDescClass = SensorDescProximity.class;

				} else if (sensorId == SensorDescTemperature.SENSOR_ID) {
					scTemperature.setMeasureStart(startTime);
					doCollect = scTemperature.isCollect();
					doCollect = doCollect ? sensorManager.registerListener(sensorListenerClass, sensorTemperature, SensorManager.SENSOR_DELAY_NORMAL) : false;
					sensorCollectStatus = scTemperature;
					sensorDescClass = SensorDescTemperature.class;

				} else if (sensorId == SensorDescBattery.SENSOR_ID) {
					scBattery.setMeasureStart(startTime);
					doCollect = scBattery.isCollect();
					if (doCollect) {
						sensorBattery.addListener(sensorListenerClass);
						sensorBattery.start();
					}
					sensorCollectStatus = scBattery;
					sensorDescClass = SensorDescBattery.class;

				} else if (sensorId == SensorDescConnectivity.SENSOR_ID) {
					scConnectivity.setMeasureStart(startTime);
					doCollect = scConnectivity.isCollect();
					if (doCollect) {
						sensorConnectivity.addListener(sensorListenerClass);
						sensorConnectivity.start();
					}
					sensorCollectStatus = scConnectivity;
					sensorDescClass = SensorDescConnectivity.class;

				} else if (sensorId == SensorDescBLEBeacon.SENSOR_ID) {
					scBLEBeacon.setMeasureStart(startTime);
					doCollect = scBLEBeacon.isCollect();
					if (doCollect) {
						sensorBLEBeacon.addListener(sensorListenerClass);
						// Update this variable if the BLE sensor is currently unavailable
						doCollect = sensorBLEBeacon.startScanning(Math.max(scBLEBeacon.getMeasureDuration(), 2000));
					}
					sensorCollectStatus = scBLEBeacon;
					sensorDescClass = SensorDescBLEBeacon.class;

				} else if (sensorId == SensorDescNoise.SENSOR_ID) {
					scNoise.setMeasureStart(startTime);
					doCollect = scNoise.isCollect();
					if (doCollect) {
						sensorNoise.addListener(sensorListenerClass);
						// Noise sensor doesn't really make sense with less than 500ms
						sensorNoise.startRecording(Math.max(scNoise.getMeasureDuration(), 500));
					}
					sensorCollectStatus = scNoise;
					sensorDescClass = SensorDescNoise.class;

				}

				if (doCollect && sensorDescClass != null && sensorCollectStatus != null) {
					wakeLockMutex.lock();
					sensorCollected.put(sensorDescClass, sensorCollectStatus);
					// Acquire wakelock, some sensors on some phones need this
					if (!wakeLock.isHeld()) {
						wakeLock.acquire();
					}
					wakeLockMutex.unlock();
				}

				if (sensorCollectStatus != null) {
					long interval = sensorCollectStatus.getMeasureInterval();
					Log.d(LOG_TAG, "Logging sensor " + String.valueOf(sensorId) + " started with interval " + String.valueOf(interval) + " ms");
					handler.postDelayed(this, interval);
				}

			}
		};
		// 30 seconds initial delay
		handler.postDelayed(run, 30000);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		hthread = new HandlerThread("HandlerThread");
		hthread.start();
	}

	@Override
	public void onDestroy() {
		// Release the wakelock here, just to be safe, in order something went wrong
		if (wakeLock.isHeld()) {
			wakeLock.release();
		}
		sensorManager.unregisterListener(this);
		hthread.quit();
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
			Log.d(LOG_TAG, "Light data collected");
			break;
		case Sensor.TYPE_PROXIMITY:
			sensorDesc = new SensorDescProximity(timestamp, event.values[0]);
			Log.d(LOG_TAG, "Proximity data collected");
			break;
		case Sensor.TYPE_ACCELEROMETER:
			sensorDesc = new SensorDescAccelerometer(timestamp, event.values[0], event.values[1], event.values[2]);
			Log.d(LOG_TAG, "Accelerometer data collected");
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			sensorDesc = new SensorDescMagnetic(timestamp, event.values[0], event.values[1], event.values[2]);
			Log.d(LOG_TAG, "Magnetic data collected");
			break;
		case Sensor.TYPE_GYROSCOPE:
			sensorDesc = new SensorDescGyroscope(timestamp, event.values[0], event.values[1], event.values[2]);
			Log.d(LOG_TAG, "Gyroscope data collected");
			break;
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
			sensorDesc = new SensorDescTemperature(timestamp, event.values[0]);
			Log.d(LOG_TAG, "Temperature data collected");
			break;
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			sensorDesc = new SensorDescHumidity(timestamp, event.values[0]);
			Log.d(LOG_TAG, "Humidity data collected");
			break;
		case Sensor.TYPE_PRESSURE:
			sensorDesc = new SensorDescProximity(timestamp, event.values[0]);
			Log.d(LOG_TAG, "Pressure data collected");
			break;
		}

		store(sensorDesc);
	}

	@Override
	public void connectivitySensorDataReady(long timestamp, boolean isConnected, int networkType, boolean isRoaming, String wifiHashId, int wifiStrength, String mobileHashId) {
		SensorDesc sensorDesc = new SensorDescConnectivity(timestamp, isConnected, networkType, isRoaming, wifiHashId, wifiStrength, mobileHashId);
		Log.d(LOG_TAG, "Connectivity data collected");
		store(sensorDesc);
	}

	@Override
	public void noiseSensorDataReady(long timestamp, float rms, float spl, float[] bands) {
		SensorDesc sensorDesc = new SensorDescNoise(timestamp, rms, spl, bands);
		Log.d(LOG_TAG, "Noise data collected");
		store(sensorDesc);
	}

	@Override
	public void batterySensorDataReady(long timestamp, float batteryPercent, boolean isCharging, boolean isUsbCharge, boolean isAcCharge) {
		SensorDesc sensorDesc = new SensorDescBattery(timestamp, batteryPercent, isCharging, isUsbCharge, isAcCharge);
		Log.d(LOG_TAG, "Battery data collected");
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
		Log.d(LOG_TAG, "BLEBeacon data collected");
		// Kick this sensor out anyways as it is possible to retrieve no data at all after the measurement interval
		wakeLockMutex.lock();
		sensorCollected.remove(SensorDescBLEBeacon.class);
		wakeLockMutex.unlock();
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
						wakeLockMutex.lock();
						sensorCollected.remove(sensorDesc.getClass());
						wakeLockMutex.unlock();
						// Remove from listener list
						unregisterSensor(sensorDesc);
					}
					new StoreTask(getApplicationContext()).execute(sensorDesc);
				}
			}
			// Make sure the list isn't modified at the same time as the list
			wakeLockMutex.lock();
			if (sensorCollected.isEmpty()) {
				// Wakelock can be removed if all sensors are done for the moment (so none requires it for sure)
				if (wakeLock.isHeld()) {
					wakeLock.release();
				}
			}
			wakeLockMutex.unlock();
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
		} else if (sensorDesc.getSensorIdentifier() == SensorDescNoise.SENSOR_ID) {
			sensorNoise.removeListener(this);
		} else if (sensorDesc.getSensorIdentifier() == SensorDescBattery.SENSOR_ID) {
			sensorBattery.removeListener(this);
		} else if (sensorDesc.getSensorIdentifier() == SensorDescBLEBeacon.SENSOR_ID) {
			sensorBLEBeacon.removeListener(this);
		} else if (sensorDesc.getSensorIdentifier() == SensorDescConnectivity.SENSOR_ID) {
			sensorConnectivity.removeListener(this);
		}
	}

}

package ch.ethz.soms.nervous.android;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
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
	private Lock storeMutex;

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
	private ConcurrentHashMap<Long, SensorCollectStatus> sensorCollected;

	public class SensorBinder extends Binder {
		SensorService getService() {
			return SensorService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		storeMutex = new ReentrantLock();

		// Reference for inner runnable
		sensorListenerClass = this;
		sensorConfiguration = SensorConfiguration.getInstance(getApplicationContext());

		// Initialize sensor manager
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Hash map to register sensor collect status references
		sensorCollected = new ConcurrentHashMap<Long, SensorCollectStatus>();

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
				SensorCollectStatus sensorCollectStatus = null;
				long startTime = System.currentTimeMillis();

				if (sensorId == SensorDescAccelerometer.SENSOR_ID) {
					scAccelerometer.setMeasureStart(startTime);
					doCollect = scAccelerometer.isCollect();
					doCollect = doCollect ? sensorManager.registerListener(sensorListenerClass, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL) : false;
					sensorCollectStatus = scAccelerometer;
				} else if (sensorId == SensorDescPressure.SENSOR_ID) {
					scPressure.setMeasureStart(startTime);
					doCollect = scPressure.isCollect();
					doCollect = doCollect ? sensorManager.registerListener(sensorListenerClass, sensorPressure, SensorManager.SENSOR_DELAY_NORMAL) : false;
					sensorCollectStatus = scPressure;
				} else if (sensorId == SensorDescGyroscope.SENSOR_ID) {
					scGyroscope.setMeasureStart(startTime);
					doCollect = scGyroscope.isCollect();
					doCollect = doCollect ? sensorManager.registerListener(sensorListenerClass, sensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL) : false;
					sensorCollectStatus = scGyroscope;
				} else if (sensorId == SensorDescHumidity.SENSOR_ID) {
					scHumidity.setMeasureStart(startTime);
					doCollect = scHumidity.isCollect();
					doCollect = doCollect ? sensorManager.registerListener(sensorListenerClass, sensorHumidity, SensorManager.SENSOR_DELAY_NORMAL) : false;
					sensorCollectStatus = scHumidity;
				} else if (sensorId == SensorDescLight.SENSOR_ID) {
					scLight.setMeasureStart(startTime);
					doCollect = scLight.isCollect();
					doCollect = doCollect ? sensorManager.registerListener(sensorListenerClass, sensorLight, SensorManager.SENSOR_DELAY_NORMAL) : false;
					sensorCollectStatus = scLight;
				} else if (sensorId == SensorDescMagnetic.SENSOR_ID) {
					scMagnet.setMeasureStart(startTime);
					doCollect = scMagnet.isCollect();
					doCollect = doCollect ? sensorManager.registerListener(sensorListenerClass, sensorMagnet, SensorManager.SENSOR_DELAY_NORMAL) : false;
					sensorCollectStatus = scMagnet;
				} else if (sensorId == SensorDescProximity.SENSOR_ID) {
					scProximity.setMeasureStart(startTime);
					doCollect = scProximity.isCollect();
					doCollect = doCollect ? sensorManager.registerListener(sensorListenerClass, sensorProximity, SensorManager.SENSOR_DELAY_NORMAL) : false;
					sensorCollectStatus = scProximity;
				} else if (sensorId == SensorDescTemperature.SENSOR_ID) {
					scTemperature.setMeasureStart(startTime);
					doCollect = scTemperature.isCollect();
					doCollect = doCollect ? sensorManager.registerListener(sensorListenerClass, sensorTemperature, SensorManager.SENSOR_DELAY_NORMAL) : false;
					sensorCollectStatus = scTemperature;
				} else if (sensorId == SensorDescBattery.SENSOR_ID) {
					scBattery.setMeasureStart(startTime);
					doCollect = scBattery.isCollect();
					if (doCollect) {
						sensorBattery.clearListeners();
						sensorBattery.addListener(sensorListenerClass);
						sensorBattery.start();
					}
					sensorCollectStatus = scBattery;
				} else if (sensorId == SensorDescConnectivity.SENSOR_ID) {
					scConnectivity.setMeasureStart(startTime);
					doCollect = scConnectivity.isCollect();
					if (doCollect) {
						sensorConnectivity.clearListeners();
						sensorConnectivity.addListener(sensorListenerClass);
						sensorConnectivity.start();
					}
					sensorCollectStatus = scConnectivity;
				} else if (sensorId == SensorDescBLEBeacon.SENSOR_ID) {
					scBLEBeacon.setMeasureStart(startTime);
					doCollect = scBLEBeacon.isCollect();
					if (doCollect) {
						sensorBLEBeacon.clearListeners();
						sensorBLEBeacon.addListener(sensorListenerClass);
						// Update this variable if the BLE sensor is currently unavailable
						doCollect = sensorBLEBeacon.startScanning(Math.max(scBLEBeacon.getMeasureDuration(), 2000));
					}
					// TODO Fix for now, agressive BLE scanning
					scBLEBeacon.setMeasureInterval(3000);
					sensorCollectStatus = scBLEBeacon;
				} else if (sensorId == SensorDescNoise.SENSOR_ID) {
					scNoise.setMeasureStart(startTime);
					doCollect = scNoise.isCollect();
					if (doCollect) {
						sensorNoise.clearListeners();
						sensorNoise.addListener(sensorListenerClass);
						// Noise sensor doesn't really make sense with less than 500ms
						sensorNoise.startRecording(Math.max(scNoise.getMeasureDuration(), 500));
					}
					sensorCollectStatus = scNoise;
				}

				if (doCollect && sensorCollectStatus != null) {
					sensorCollected.put(sensorId, sensorCollectStatus);
				}

				if (sensorCollectStatus != null) {
					long interval = sensorCollectStatus.getMeasureInterval();
					Log.d(LOG_TAG, "Logging sensor " + String.valueOf(sensorId) + " started with interval " + String.valueOf(interval) + " ms");
					handler.postDelayed(this, interval);
				}

			}
		};
		// 10 seconds initial delay
		handler.postDelayed(run, 10000);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		// Prepare the wakelock
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOG_TAG);
		hthread = new HandlerThread("HandlerThread");
		hthread.start();
		// Acquire wakelock, some sensors on some phones need this
		if (!wakeLock.isHeld()) {
			wakeLock.acquire();
		}
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
		ArrayList<SensorDesc> sensorDescs = new ArrayList<SensorDesc>();

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
			sensorDesc = new SensorDescPressure(timestamp, event.values[0]);
			Log.d(LOG_TAG, "Pressure data collected");
			break;
		}

		sensorDescs.add(sensorDesc);
		store(sensorDesc.getSensorId(), sensorDescs);
	}

	@Override
	public void connectivitySensorDataReady(long timestamp, boolean isConnected, int networkType, boolean isRoaming, String wifiHashId, int wifiStrength, String mobileHashId) {
		ArrayList<SensorDesc> sensorDescs = new ArrayList<SensorDesc>();
		SensorDesc sensorDesc = new SensorDescConnectivity(timestamp, isConnected, networkType, isRoaming, wifiHashId, wifiStrength, mobileHashId);
		Log.d(LOG_TAG, "Connectivity data collected");
		sensorDescs.add(sensorDesc);
		store(sensorDesc.getSensorId(), sensorDescs);
	}

	@Override
	public void noiseSensorDataReady(long timestamp, float rms, float spl, float[] bands) {
		ArrayList<SensorDesc> sensorDescs = new ArrayList<SensorDesc>();
		SensorDesc sensorDesc = new SensorDescNoise(timestamp, rms, spl, bands);
		Log.d(LOG_TAG, "Noise data collected");
		sensorDescs.add(sensorDesc);
		store(sensorDesc.getSensorId(), sensorDescs);
	}

	@Override
	public void batterySensorDataReady(long timestamp, float batteryPercent, boolean isCharging, boolean isUsbCharge, boolean isAcCharge) {
		ArrayList<SensorDesc> sensorDescs = new ArrayList<SensorDesc>();
		SensorDesc sensorDesc = new SensorDescBattery(timestamp, batteryPercent, isCharging, isUsbCharge, isAcCharge);
		Log.d(LOG_TAG, "Battery data collected");
		sensorDescs.add(sensorDesc);
		store(sensorDesc.getSensorId(), sensorDescs);
	}

	@Override
	public void bleSensorDataReady(List<BLEBeaconRecord> beaconRecordList) {
		ArrayList<SensorDesc> sensorDescs = new ArrayList<SensorDesc>();
		if (beaconRecordList != null && beaconRecordList.size() > 0) {
			for (BLEBeaconRecord bbr : beaconRecordList) {
				SensorDesc sensorDesc = new SensorDescBLEBeacon(bbr.getTokenDetectTime(), bbr.getRssi(), bbr.getMac(), bbr.getAdvertisement().getMostSignificantBits(), bbr.getAdvertisement().getLeastSignificantBits(), bbr.getUuid().getMostSignificantBits(), bbr.getUuid().getLeastSignificantBits(), bbr.getMajor(), bbr.getMinor(), bbr.getTxpower());
				sensorDescs.add(sensorDesc);
			}
			store(SensorDescBLEBeacon.SENSOR_ID, sensorDescs);
		}
		Log.d(LOG_TAG, "BLEBeacon data collected");
		// Kick this sensor out anyways as it is possible to retrieve no data at all after the measurement interval
		sensorCollected.remove(SensorDescBLEBeacon.class);
	}

	private synchronized void store(long sensorId, List<SensorDesc> sensorDescs) {
		storeMutex.lock();
		SensorCollectStatus scs = sensorCollected.get(sensorId);
		if (sensorDescs != null && !sensorDescs.isEmpty()) {
			if (scs != null) {
				if (!scs.isDone(System.currentTimeMillis())) {
					// Collected new data of this type, count it
					scs.increaseCollectAmount();
					// Enough collected, remove from list
					if (scs.isDone(System.currentTimeMillis())) {
						sensorCollected.remove(scs.getSensorId());
						// Remove from listener list
						unregisterSensor(scs.getSensorId());
					}
					for (SensorDesc sensorDesc : sensorDescs) {
						new StoreTask(getApplicationContext()).execute(sensorDesc);
					}
				}
			}
		}
		storeMutex.unlock();
	}

	private void unregisterSensor(long sensorId) {
		if (sensorId == SensorDescAccelerometer.SENSOR_ID) {
			sensorManager.unregisterListener(this, sensorAccelerometer);
		} else if (sensorId == SensorDescPressure.SENSOR_ID) {
			sensorManager.unregisterListener(this, sensorPressure);
		} else if (sensorId == SensorDescGyroscope.SENSOR_ID) {
			sensorManager.unregisterListener(this, sensorGyroscope);
		} else if (sensorId == SensorDescHumidity.SENSOR_ID) {
			sensorManager.unregisterListener(this, sensorHumidity);
		} else if (sensorId == SensorDescLight.SENSOR_ID) {
			sensorManager.unregisterListener(this, sensorLight);
		} else if (sensorId == SensorDescMagnetic.SENSOR_ID) {
			sensorManager.unregisterListener(this, sensorMagnet);
		} else if (sensorId == SensorDescProximity.SENSOR_ID) {
			sensorManager.unregisterListener(this, sensorProximity);
		} else if (sensorId == SensorDescTemperature.SENSOR_ID) {
			sensorManager.unregisterListener(this, sensorTemperature);
		} else if (sensorId == SensorDescNoise.SENSOR_ID) {
			sensorNoise.removeListener(this);
		} else if (sensorId == SensorDescBattery.SENSOR_ID) {
			sensorBattery.removeListener(this);
		} else if (sensorId == SensorDescBLEBeacon.SENSOR_ID) {
			sensorBLEBeacon.removeListener(this);
		} else if (sensorId == SensorDescConnectivity.SENSOR_ID) {
			sensorConnectivity.removeListener(this);
		}
	}

	public static boolean isServiceRunning(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (SensorService.class.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static void startService(Context context) {
		Intent sensorIntent = new Intent(context, SensorService.class);
		context.startService(sensorIntent);
	}

	public static void stopService(Context context) {
		Intent sensorIntent = new Intent(context, SensorService.class);
		context.stopService(sensorIntent);
	}

}

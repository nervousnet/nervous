package ch.ethz.soms.nervous.android.sensors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ch.ethz.soms.nervous.android.sensors.NoiseSensor.NoiseListener;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;

public class BatterySensor {

	private Context context;

	public BatterySensor(Context context) {
		this.context = context;
	}

	private List<BatteryListener> listenerList = new ArrayList<BatteryListener>();
	private Lock listenerMutex = new ReentrantLock();


	public interface BatteryListener {
		public void batterySensorDataReady(long timestamp, float batteryPercent, boolean isCharging, boolean isUsbCharge, boolean isAcCharge);
	}

	public void addListener(BatteryListener listener) {
		listenerMutex.lock();
		listenerList.add(listener);
		listenerMutex.unlock();
	}
	
	public void removeListener(BatteryListener listener) {
		listenerMutex.lock();
		listenerList.remove(listener);
		listenerMutex.unlock();
	}
	
	public void clearListeners() {
		listenerMutex.lock();
		listenerList.clear();
		listenerMutex.unlock();
	}

	public void dataReady(long timestamp, float batteryPercent, boolean isCharging, boolean isUsbCharge, boolean isAcCharge) {
		listenerMutex.lock();
		for (BatteryListener listener : listenerList) {
			listener.batterySensorDataReady(timestamp, batteryPercent, isCharging, isUsbCharge, isAcCharge);
		}
		listenerMutex.unlock();
	}

	public class BatteryTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			Intent batteryStatus = context.registerReceiver(null, ifilter);
			int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
			int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
			boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
			boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
			float batteryPct = level / (float) scale;
			dataReady(System.currentTimeMillis(), batteryPct, isCharging, usbCharge, acCharge);
			return null;
		}

	}

	public void start() {
		new BatteryTask().execute();
	}

}

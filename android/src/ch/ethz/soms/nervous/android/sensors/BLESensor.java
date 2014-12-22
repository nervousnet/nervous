package ch.ethz.soms.nervous.android.sensors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.xml.sax.DTDHandler;

import ch.ethz.soms.nervous.android.sensors.NoiseSensor.NoiseListener;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

public class BLESensor {

	private static final String LOG_TAG = BLESensor.class.getSimpleName();

	private Context context;
	private BluetoothManager bluetoothManager;
	private BluetoothAdapter bluetoothAdapter;
	private long lastDetectedTime;

	private List<BLEBeaconListener> listenerList = new ArrayList<BLEBeaconListener>();
	private Lock listenerMutex = new ReentrantLock();

	public interface BLEBeaconListener {
		public void bleSensorDataReady(List<BLEBeaconRecord> beaconRecordList);
	}

	public void addListener(BLEBeaconListener listener) {
		listenerMutex.lock();
		listenerList.add(listener);
		listenerMutex.unlock();
	}

	public void removeListener(BLEBeaconListener listener) {
		listenerMutex.lock();
		listenerList.remove(listener);
		listenerMutex.unlock();
	}

	public void clearListeners() {
		listenerMutex.lock();
		listenerList.clear();
		listenerMutex.unlock();
	}

	@TargetApi(18)
	public BLESensor(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			this.context = context;
			bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
			bluetoothAdapter = bluetoothManager.getAdapter();
			lastDetectedTime = 0;
		}
	}

	private void dataReady(List<BLEBeaconRecord> beaconRecordList) {
		listenerMutex.lock();
		for (BLEBeaconListener listener : listenerList) {
			listener.bleSensorDataReady(beaconRecordList);
		}
		listenerMutex.unlock();
	}

	@TargetApi(18)
	public class BLETask extends AsyncTask<Long, Void, Void> {

		private LinkedList<BLEBeaconRecord> beaconRecordList;
		private HashSet<String> beaconHash;
		
		private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
			@Override
			public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
				addBLEBeaconRecord(device, rssi, scanRecord);
			}
		};

		// Add some thread safety
		private synchronized void addBLEBeaconRecord(BluetoothDevice device, int rssi, byte[] scanRecord) {
			long detectionTime = System.currentTimeMillis();
			if (lastDetectedTime >= detectionTime) {
				detectionTime = lastDetectedTime + 1;
			}

			BLEBeaconRecord record = new BLEBeaconRecord(detectionTime, device, rssi, scanRecord);

			// Filter for the nervous major IDs
			if (record.getMajor() == 0x8037 || record.getMajor() == 0x8143) {
				String hashString = record.getMajor() + "_" + record.getMinor();
				// Don't record a beacon more than once
				if(!beaconHash.contains(hashString)) {
					beaconHash.add(hashString);
					beaconRecordList.add(record);
					lastDetectedTime = detectionTime;
				}
			}
		}

		// Currently unused (for iBeacons)

		private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
			@Override
			public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			}

			@Override
			public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			}

			@Override
			public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristics, int status) {
			}

			@Override
			public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			}

			@Override
			public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			}

		};

		@Override
		protected Void doInBackground(Long... params) {
			beaconRecordList = new LinkedList<BLEBeaconRecord>();
			beaconHash = new HashSet<String>();
			scanLeDevice(params[0]);
			return null;
		}

		private void scanLeDevice(long duration) {
			// Even though this is deprecated, the BluetoothLe functionality requires minSdk 21, which is Android 5.0
			bluetoothAdapter.startLeScan(leScanCallback);
			try {
				Thread.sleep(duration);
			} catch (InterruptedException e) {
			}
			bluetoothAdapter.stopLeScan(leScanCallback);
		}

		@Override
		public void onPostExecute(Void params) {
			dataReady(beaconRecordList);
		}

	}

	public boolean startScanning(long duration) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
				return false;
			} else {
				new BLETask().execute(duration);
				return true;
			}
		} else {
			return false;
		}
	}

}

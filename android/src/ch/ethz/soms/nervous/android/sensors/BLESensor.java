package ch.ethz.soms.nervous.android.sensors;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.DTDHandler;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class BLESensor {

	private Context context;
	private BluetoothManager bluetoothManager;
	private BluetoothAdapter bluetoothAdapter;
	private long lastDetectedTime;

	private List<BLEListener> listenerList = new ArrayList<BLEListener>();

	public interface BLEListener {
		public void bleSensorDataReady();
	}

	public void addListener(BLEListener listener) {
		listenerList.add(listener);
	}

	public BLESensor(Context context) {
		this.context = context;
		bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();
		lastDetectedTime = 0;
	}

	private void dataReady() {
		for (BLEListener listener : listenerList) {
			listener.bleSensorDataReady();
		}
	}

	public class BLETask extends AsyncTask<Long, Void, Void> {

		private LinkedList<BLEBeaconRecord> leDevices;

		private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
			@Override
			public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
				long detectionTime = System.currentTimeMillis();
				if (lastDetectedTime >= detectionTime) {
					detectionTime = lastDetectedTime + 1;
				}
				leDevices.add(new BLEBeaconRecord(detectionTime, device, rssi, scanRecord));
				lastDetectedTime = detectionTime;
			}
		};

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
			leDevices = new LinkedList<BLEBeaconRecord>();
			scanLeDevice(params[0]);
			return null;
		}

		private void scanLeDevice(long duration) {
			bluetoothAdapter.startLeScan(leScanCallback);
			try {
				Thread.sleep(duration);
			} catch (InterruptedException e) {
			}
			bluetoothAdapter.stopLeScan(leScanCallback);
		}

		@Override
		public void onPostExecute(Void params) {
			dataReady();
		}

	}

	public boolean startScanning(long duration) {
		if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
			return false;
		} else {
			new BLETask().execute(duration);
			return true;
		}
	}

}

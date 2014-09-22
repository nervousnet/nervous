package ch.ethz.soms.nervous.android.sensors;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

public class BLEBeaconSensor {

	private Context context;
	private BluetoothManager bluetoothManager;
	private BluetoothAdapter bluetoothAdapter;

	private List<BLEListener> listenerList = new ArrayList<BLEListener>();

	public interface BLEListener {
		public void bleSensorDataReady();
	}

	public void addListener(BLEListener listener) {
		listenerList.add(listener);
	}

	public BLEBeaconSensor(Context context) {
		this.context = context;
		bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();
	}

	private void dataReady() {
		for (BLEListener listener : listenerList) {
			listener.bleSensorDataReady();
		}
	}

	public class BLETask extends AsyncTask<Long, Void, Void> {

		private LinkedList<BLETokenRecord> leDevices;

		private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
			@Override
			public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
				leDevices.add(new BLETokenRecord(System.currentTimeMillis(), device, rssi, scanRecord));
			}
		};

		private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
			@Override
			public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
				// TODO?
			}

			@Override
			public void onServicesDiscovered(BluetoothGatt gatt, int status) {
				// TODO?
			}

			@Override
			public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristics, int status) {
				// TODO?
			}

		};

		@Override
		protected Void doInBackground(Long... params) {
			leDevices = new LinkedList<BLETokenRecord>();
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
			for (BLETokenRecord bletr : leDevices) {
				BluetoothGatt bluetoothGatt = bletr.getBluetoothDevice().connectGatt(context, false, gattCallback);
				List<BluetoothGattService> services = bluetoothGatt.getServices();
			}
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

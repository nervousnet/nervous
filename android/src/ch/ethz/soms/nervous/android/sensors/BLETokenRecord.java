package ch.ethz.soms.nervous.android.sensors;

import android.bluetooth.BluetoothDevice;

public class BLETokenRecord {
	
	private long tokenDetectTime;
	private BluetoothDevice bluetoothDevice;
	private int rssi;
	private byte[] scanRecord;
	
	
	BLETokenRecord(long tokenDetectTime, BluetoothDevice device, int rssi, byte[] scanRecord)
	{
		this.tokenDetectTime = tokenDetectTime;
		this.bluetoothDevice = device;
		this.rssi = rssi;
		this.scanRecord = scanRecord;
	}


	public BluetoothDevice getBluetoothDevice() {
		return bluetoothDevice;
	}

}

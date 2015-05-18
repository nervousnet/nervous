package ch.ethz.soms.nervous.android.sensors;

import java.util.UUID;

import ch.ethz.soms.nervous.utils.UnsignedArithmetic;
import android.bluetooth.BluetoothDevice;

public class BLEBeaconRecord {

	private long tokenDetectTime;
	private BluetoothDevice bluetoothDevice;
	private int rssi;
	private byte[] scanRecord;

	private long mac;
	private UUID advertisement;
	private UUID uuid;
	private int major;
	private int minor;
	private int txpower;

	public BLEBeaconRecord(long tokenDetectTime, BluetoothDevice device, int rssi, byte[] scanRecord) {
		this.tokenDetectTime = tokenDetectTime;
		this.bluetoothDevice = device;
		this.rssi = rssi;
		this.scanRecord = scanRecord;

		// Parsing
		this.mac = UnsignedArithmetic.stringMacToLong(device.getAddress());
		this.advertisement = UnsignedArithmetic.toUUIDBigEndian(scanRecord, 0, 8);
		this.uuid = UnsignedArithmetic.toUUIDBigEndian(scanRecord, 9, 24);
		this.major = (UnsignedArithmetic.upcastToInt(scanRecord[25]) << 8) | (UnsignedArithmetic.upcastToInt(scanRecord[26]));
		this.minor = (UnsignedArithmetic.upcastToInt(scanRecord[27]) << 8) | (UnsignedArithmetic.upcastToInt(scanRecord[28]));
		this.txpower = scanRecord[29];
	}
	
	public long getTokenDetectTime() {
		return tokenDetectTime;
	}

	public int getRssi() {
		return rssi;
	}

	public long getMac() {
		return mac;
	}

	public UUID getAdvertisement() {
		return advertisement;
	}

	public UUID getUuid() {
		return uuid;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getTxpower() {
		return txpower;
	}

}

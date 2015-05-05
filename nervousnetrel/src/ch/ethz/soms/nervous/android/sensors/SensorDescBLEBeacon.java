package ch.ethz.soms.nervous.android.sensors;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescBLEBeacon extends SensorDesc {

	public static final long SENSOR_ID = 0x000000000000000BL;

	private int rssi;
	private long mac;
	private long advertisementMSB;
	private long advertisementLSB;
	private long bleuuidMSB;
	private long bleuuidLSB;
	private int major;
	private int minor;
	private int txpower;

	public SensorDescBLEBeacon(long timestamp, int rssi, long mac, long advertisementMSB, long advertisementLSB, long bleuuidMSB, long bleuuidLSB, int major, int minor, int txpower) {
		super(timestamp);
		this.rssi = rssi;
		this.mac = mac;
		this.advertisementMSB = advertisementMSB;
		this.advertisementLSB = advertisementLSB;
		this.bleuuidMSB = bleuuidMSB;
		this.bleuuidLSB = bleuuidLSB;
		this.major = major;
		this.minor = minor;
		this.txpower = txpower;
	}

	public SensorDescBLEBeacon(SensorData sensorData) {
		super(sensorData);
		this.rssi = sensorData.getValueInt32(0);
		this.major = sensorData.getValueInt32(1);
		this.minor = sensorData.getValueInt32(2);
		this.txpower = sensorData.getValueInt32(3);
		this.mac = sensorData.getValueInt64(0);
		this.advertisementMSB = sensorData.getValueInt64(1);
		this.advertisementLSB = sensorData.getValueInt64(2);
		this.bleuuidMSB = sensorData.getValueInt64(3);
		this.bleuuidLSB = sensorData.getValueInt64(4);
	}

	@Override
	public long getSensorId() {
		return SENSOR_ID;
	}

	public long getMac() {
		return mac;
	}

	public long getAdvertisementMSB() {
		return advertisementMSB;
	}

	public long getAdvertisementLSB() {
		return advertisementLSB;
	}

	public long getBleuuidMSB() {
		return bleuuidMSB;
	}

	public long getBleuuidLSB() {
		return bleuuidLSB;
	}

	public int getMajor() {
		return major;
	}

	public int getRssi() {
		return rssi;
	}

	public int getMinor() {
		return minor;
	}

	public int getTxpower() {
		return txpower;
	}

	@Override
	public SensorData toProtoSensor() {
		SensorData.Builder sdb = SensorData.newBuilder();
		sdb.setRecordTime(getTimestamp());
		sdb.addValueInt64(mac);
		sdb.addValueInt64(advertisementMSB);
		sdb.addValueInt64(advertisementLSB);
		sdb.addValueInt64(bleuuidMSB);
		sdb.addValueInt64(bleuuidLSB);
		sdb.addValueInt32(rssi);
		sdb.addValueInt32(major);
		sdb.addValueInt32(minor);
		sdb.addValueInt32(txpower);
		return sdb.build();
	}

}

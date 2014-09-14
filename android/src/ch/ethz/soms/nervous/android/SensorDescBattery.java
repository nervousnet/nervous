package ch.ethz.soms.nervous.android;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescBattery extends SensorDesc {
	
	public static final long SENSOR_ID = 0x0000000000000001L;

	private final float batteryPercent;
	private final boolean isCharging;
	private final boolean isUsbCharge;
	private final boolean isAcCharge;

	public SensorDescBattery(final long timestamp, float batteryPercent, final boolean isCharging, final boolean isUsbCharge, final boolean isAcCharge) {
		super(timestamp);
		this.batteryPercent = batteryPercent;
		this.isCharging = isCharging;
		this.isUsbCharge = isUsbCharge;
		this.isAcCharge = isAcCharge;
	}

	public SensorDescBattery(SensorData sensorData) {
		super(sensorData);
		this.batteryPercent = sensorData.getValueFloat(0);
		this.isCharging = sensorData.getValueBool(0);
		this.isUsbCharge = sensorData.getValueBool(1);
		this.isAcCharge = sensorData.getValueBool(2);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(getTimestamp()) + ";batteryPercent;" + String.valueOf(batteryPercent) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";isCharging;" + String.valueOf(isCharging ? 1 : 0) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";isUsbCharge;" + String.valueOf(isUsbCharge ? 1 : 0) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";isAcCharge;" + String.valueOf(isAcCharge ? 1 : 0) + "\n");
		return sb.toString();
	}


	public float getBatteryPercent() {
		return batteryPercent;
	}

	public boolean isCharging() {
		return isCharging;
	}

	public boolean isUsbCharge() {
		return isUsbCharge;
	}

	public boolean isAcCharge() {
		return isAcCharge;
	}

	@Override
	public SensorData toProtoSensor() {
		SensorData.Builder sdb = SensorData.newBuilder();
		sdb.setRecordTime(getTimestamp());
		sdb.addValueFloat(getBatteryPercent());
		sdb.addValueBool(isCharging());
		sdb.addValueBool(isUsbCharge());
		sdb.addValueBool(isAcCharge());
		return sdb.build();
	}

	@Override
	public long getSensorIdentifier() {
		return SENSOR_ID;
	}

}

package ch.ethz.soms.nervous.android;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescAccelerometer extends SensorDesc {
	
	public static final long SENSOR_ID = 0x0000000000000000L;
	
	private final int accuracy;
	private final float accX;
	private final float accY;
	private final float accZ;

	public SensorDescAccelerometer(final long timestamp, final int accuracy, final float accX, final float accY, final float accZ) {
		super(timestamp);
		this.accuracy = accuracy;
		this.accX = accX;
		this.accY = accY;
		this.accZ = accZ;
	}
	
	public SensorDescAccelerometer(SensorData sensorData) {
		super(sensorData);
		this.accuracy = sensorData.getValueInt32(0);
		this.accX = sensorData.getValueFloat(0);
		this.accY = sensorData.getValueFloat(1);
		this.accZ = sensorData.getValueFloat(2);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(getTimestamp()) + ";accAccuracy;" + String.valueOf(accuracy) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";accX;" + String.valueOf(accX) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";accY;" + String.valueOf(accY) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";accZ;" + String.valueOf(accZ) + "\n");
		return sb.toString();
	}

	public int getAccuracy() {
		return accuracy;
	}

	public float getAccX() {
		return accX;
	}

	public float getAccY() {
		return accY;
	}

	public float getAccZ() {
		return accZ;
	}

	@Override
	public SensorData toProtoSensor() {
		SensorData.Builder sdb = SensorData.newBuilder();
		sdb.setRecordTime(getTimestamp());
		sdb.addValueFloat(getAccX());
		sdb.addValueFloat(getAccY());
		sdb.addValueFloat(getAccZ());
		sdb.addValueInt32(getAccuracy());
		return sdb.build();
	}

	@Override
	public long getSensorIdentifier() {
		return SENSOR_ID;
	}

}

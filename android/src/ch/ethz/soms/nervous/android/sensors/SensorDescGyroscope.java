package ch.ethz.soms.nervous.android.sensors;

import android.graphics.SweepGradient;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescGyroscope extends SensorDesc {
	
	public static final long SENSOR_ID = 0x0000000000000002L;
	
	private final int accuracy;
	private final float gyrX;
	private final float gyrY;
	private final float gyrZ;

	public SensorDescGyroscope(final long timestamp, final int accuracy, final float gyrX, final float gyrY, final float gyrZ) {
		super(timestamp);
		this.accuracy = accuracy;
		this.gyrX = gyrX;
		this.gyrY = gyrY;
		this.gyrZ = gyrZ;
	}
	
	public SensorDescGyroscope(SensorData sensorData) {
		super(sensorData);
		this.accuracy = sensorData.getValueInt32(0);
		this.gyrX = sensorData.getValueFloat(0);
		this.gyrY = sensorData.getValueFloat(1);
		this.gyrZ = sensorData.getValueFloat(2);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(getTimestamp()) + ";gyrAccuracy;" + String.valueOf(accuracy) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";gyrX;" + String.valueOf(gyrX) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";gyrY;" + String.valueOf(gyrY) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";gyrZ;" + String.valueOf(gyrZ) + "\n");
		return sb.toString();
	}

	public int getAccuracy() {
		return accuracy;
	}

	public float getGyrX() {
		return gyrX;
	}

	public float getGyrY() {
		return gyrY;
	}

	public float getGyrZ() {
		return gyrZ;
	}

	@Override
	public long getSensorIdentifier() {
		return SENSOR_ID;
	}

	@Override
	public SensorData toProtoSensor() {
		SensorData.Builder sdb = SensorData.newBuilder();
		sdb.setRecordTime(getTimestamp());
		sdb.addValueFloat(getGyrX());
		sdb.addValueFloat(getGyrY());
		sdb.addValueFloat(getGyrZ());
		sdb.addValueInt32(getAccuracy());
		return sdb.build();
	}

}

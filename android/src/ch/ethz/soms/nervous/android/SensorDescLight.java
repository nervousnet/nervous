package ch.ethz.soms.nervous.android;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescLight extends SensorDesc {
	
	public static final long SENSOR_ID = 0x0000000000000004;

	
	private final int accuracy;
	private final float light;


	public SensorDescLight(final long timestamp, final int accuracy, final float light) {
		super(timestamp);
		this.accuracy = accuracy;
		this.light = light;
	}
	
	public SensorDescLight(SensorData sensorData) {
		super(sensorData);
		this.accuracy = sensorData.getValueInt32(0);
		this.light = sensorData.getValueFloat(0);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(getTimestamp()) + ";lightAccuracy;" + String.valueOf(accuracy) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";light;" + String.valueOf(light) + "\n");
		return sb.toString();
	}

	public int getAccuracy() {
		return accuracy;
	}

	public float getLight() {
		return light;
	}

	@Override
	public SensorData toProtoSensor() {
		SensorData.Builder sdb = SensorData.newBuilder();
		sdb.setRecordTime(getTimestamp());
		sdb.addValueFloat(getLight());
		sdb.addValueInt32(getAccuracy());
		return sdb.build();
	}
	
	@Override
	public long getSensorIdentifier() {
		return SENSOR_ID;
	}


}

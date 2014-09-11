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
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public long getSensorIdentifier() {
		return SENSOR_ID;
	}


}

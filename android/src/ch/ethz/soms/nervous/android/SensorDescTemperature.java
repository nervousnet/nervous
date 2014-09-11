package ch.ethz.soms.nervous.android;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescTemperature extends SensorDesc {
	
	public static final long SENSOR_ID = 0x0000000000000007;
	
	private final int accuracy;
	private final float temperature;


	public SensorDescTemperature(final long timestamp, final int accuracy, final float temperature) {
		super(timestamp);
		this.accuracy = accuracy;
		this.temperature = temperature;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(getTimestamp()) + ";tempAccuracy;" + String.valueOf(accuracy) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";temp;" + String.valueOf(temperature) + "\n");
		return sb.toString();
	}

	public int getAccuracy() {
		return accuracy;
	}

	public float getTemperature() {
		return temperature;
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

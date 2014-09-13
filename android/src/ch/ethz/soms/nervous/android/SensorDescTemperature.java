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
	
	public SensorDescTemperature(SensorData sensorData) {
		super(sensorData);
		this.accuracy = sensorData.getValueInt32(0);
		this.temperature = sensorData.getValueFloat(0);
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
		SensorData.Builder sdb = SensorData.newBuilder();
		sdb.setRecordTime(getTimestamp());
		sdb.addValueFloat(getTemperature());
		sdb.addValueInt32(getAccuracy());
		return sdb.build();
	}
	
	@Override
	public long getSensorIdentifier() {
		return SENSOR_ID;
	}


}

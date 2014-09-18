package ch.ethz.soms.nervous.android.sensors;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescTemperature extends SensorDesc {
	
	public static final long SENSOR_ID = 0x0000000000000007L;
	
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

package ch.ethz.soms.nervous.android.sensors;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescTemperature extends SensorDescSingleValue {

	public static final long SENSOR_ID = 0x0000000000000007L;

	private final float temperature;

	public SensorDescTemperature(final long timestamp, final float temperature) {
		super(timestamp);
		this.temperature = temperature;
	}

	public SensorDescTemperature(SensorData sensorData) {
		super(sensorData);
		this.temperature = sensorData.getValueFloat(0);
	}

	public float getTemperature() {
		return temperature;
	}

	@Override
	public SensorData toProtoSensor() {
		SensorData.Builder sdb = SensorData.newBuilder();
		sdb.setRecordTime(getTimestamp());
		sdb.addValueFloat(getTemperature());
		return sdb.build();
	}

	@Override
	public long getSensorId() {
		return SENSOR_ID;
	}

	@Override
	public float getValue() {
		return temperature;
	}

}

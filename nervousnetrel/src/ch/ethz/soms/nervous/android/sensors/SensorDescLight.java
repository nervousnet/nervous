package ch.ethz.soms.nervous.android.sensors;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescLight extends SensorDescSingleValue {

	public static final long SENSOR_ID = 0x0000000000000004L;

	private final float light;

	public SensorDescLight(final long timestamp, final float light) {
		super(timestamp);
		this.light = light;
	}

	public SensorDescLight(SensorData sensorData) {
		super(sensorData);
		this.light = sensorData.getValueFloat(0);
	}

	public float getLight() {
		return light;
	}

	@Override
	public SensorData toProtoSensor() {
		SensorData.Builder sdb = SensorData.newBuilder();
		sdb.setRecordTime(getTimestamp());
		sdb.addValueFloat(getLight());
		return sdb.build();
	}

	@Override
	public long getSensorId() {
		return SENSOR_ID;
	}

	@Override
	public float getValue() {
		return light;
	}

}

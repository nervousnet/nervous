package ch.ethz.soms.nervous.android.sensors;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescPressure extends SensorDescSingleValue {

	public static final long SENSOR_ID = 0x0000000000000009L;

	private final float pressure;

	public SensorDescPressure(final long timestamp, final float pressure) {
		super(timestamp);
		this.pressure = pressure;
	}

	public SensorDescPressure(SensorData sensorData) {
		super(sensorData);
		this.pressure = sensorData.getValueFloat(0);
	}

	public float getPressure() {
		return pressure;
	}

	@Override
	public SensorData toProtoSensor() {
		SensorData.Builder sdb = SensorData.newBuilder();
		sdb.setRecordTime(getTimestamp());
		sdb.addValueFloat(getPressure());
		return sdb.build();
	}

	@Override
	public long getSensorId() {
		return SENSOR_ID;
	}

	@Override
	public float getValue() {
		return pressure;
	}

}

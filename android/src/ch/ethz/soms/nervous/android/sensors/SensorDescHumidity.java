package ch.ethz.soms.nervous.android.sensors;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescHumidity extends SensorDescSingleValue {

	public static final long SENSOR_ID = 0x0000000000000003L;
	private final float humidity;

	public SensorDescHumidity(final long timestamp, final float humidity) {
		super(timestamp);
		this.humidity = humidity;
	}

	public SensorDescHumidity(SensorData sensorData) {
		super(sensorData);
		this.humidity = sensorData.getValueFloat(0);
	}

	public float getHumidity() {
		return humidity;
	}

	@Override
	public SensorData toProtoSensor() {
		SensorData.Builder sdb = SensorData.newBuilder();
		sdb.setRecordTime(getTimestamp());
		sdb.addValueFloat(getHumidity());
		return sdb.build();
	}

	@Override
	public long getSensorId() {
		return SENSOR_ID;
	}

	@Override
	public float getValue() {
		return humidity;
	}

}

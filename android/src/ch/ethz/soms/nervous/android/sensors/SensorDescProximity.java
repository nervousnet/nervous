package ch.ethz.soms.nervous.android.sensors;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescProximity extends SensorDescSingleValue {

	public static final long SENSOR_ID = 0x0000000000000006L;

	private final float proximity;

	public SensorDescProximity(final long timestamp, final float proximity) {
		super(timestamp);
		this.proximity = proximity;
	}

	public SensorDescProximity(SensorData sensorData) {
		super(sensorData);
		this.proximity = sensorData.getValueFloat(0);
	}

	public float getProximity() {
		return proximity;
	}

	@Override
	public SensorData toProtoSensor() {
		SensorData.Builder sdb = SensorData.newBuilder();
		sdb.setRecordTime(getTimestamp());
		sdb.addValueFloat(getProximity());
		return sdb.build();
	}

	@Override
	public long getSensorId() {
		return SENSOR_ID;
	}

	@Override
	public float getValue() {
		return proximity;
	}
}

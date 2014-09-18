package ch.ethz.soms.nervous.android.sensors;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescProximity extends SensorDesc {
	
	public static final long SENSOR_ID = 0x0000000000000006L;
	
	
	private final int accuracy;
	private final float proximity;


	public SensorDescProximity(final long timestamp, final int accuracy, final float proximity) {
		super(timestamp);
		this.accuracy = accuracy;
		this.proximity = proximity;
	}
	
	public SensorDescProximity(SensorData sensorData) {
		super(sensorData);
		this.accuracy = sensorData.getValueInt32(0);
		this.proximity = sensorData.getValueFloat(0);
	}

	public int getAccuracy() {
		return accuracy;
	}

	public float getProximity() {
		return proximity;
	}

	@Override
	public SensorData toProtoSensor() {
		SensorData.Builder sdb = SensorData.newBuilder();
		sdb.setRecordTime(getTimestamp());
		sdb.addValueFloat(getProximity());
		sdb.addValueInt32(getAccuracy());
		return sdb.build();
	}

	@Override
	public long getSensorIdentifier() {
		return SENSOR_ID;
	}

}

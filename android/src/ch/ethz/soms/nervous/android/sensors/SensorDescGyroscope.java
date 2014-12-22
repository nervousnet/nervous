package ch.ethz.soms.nervous.android.sensors;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescGyroscope extends SensorDesc {
	
	public static final long SENSOR_ID = 0x0000000000000002L;
	
	private final float gyrX;
	private final float gyrY;
	private final float gyrZ;

	public SensorDescGyroscope(final long timestamp, final float gyrX, final float gyrY, final float gyrZ) {
		super(timestamp);
		this.gyrX = gyrX;
		this.gyrY = gyrY;
		this.gyrZ = gyrZ;
	}
	
	public SensorDescGyroscope(SensorData sensorData) {
		super(sensorData);
		this.gyrX = sensorData.getValueFloat(0);
		this.gyrY = sensorData.getValueFloat(1);
		this.gyrZ = sensorData.getValueFloat(2);
	}

	public float getGyrX() {
		return gyrX;
	}

	public float getGyrY() {
		return gyrY;
	}

	public float getGyrZ() {
		return gyrZ;
	}

	@Override
	public long getSensorId() {
		return SENSOR_ID;
	}

	@Override
	public SensorData toProtoSensor() {
		SensorData.Builder sdb = SensorData.newBuilder();
		sdb.setRecordTime(getTimestamp());
		sdb.addValueFloat(getGyrX());
		sdb.addValueFloat(getGyrY());
		sdb.addValueFloat(getGyrZ());
		return sdb.build();
	}

}

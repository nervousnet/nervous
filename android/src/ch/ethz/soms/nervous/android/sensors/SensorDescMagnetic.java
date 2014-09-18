package ch.ethz.soms.nervous.android.sensors;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescMagnetic extends SensorDesc {

	public static final long SENSOR_ID = 0x0000000000000005L;

	private final int accuracy;
	private final float magX;
	private final float magY;
	private final float magZ;

	public SensorDescMagnetic(final long timestamp, final int accuracy, final float magX, final float magY, final float magZ) {
		super(timestamp);
		this.accuracy = accuracy;
		this.magX = magX;
		this.magY = magY;
		this.magZ = magZ;
	}

	public SensorDescMagnetic(SensorData sensorData) {
		super(sensorData);
		this.accuracy = sensorData.getValueInt32(0);
		this.magX = sensorData.getValueFloat(0);
		this.magY = sensorData.getValueFloat(1);
		this.magZ = sensorData.getValueFloat(2);
	}

	public int getAccuracy() {
		return accuracy;
	}

	public float getMagX() {
		return magX;
	}

	public float getMagY() {
		return magY;
	}

	public float getMagZ() {
		return magZ;
	}

	@Override
	public SensorData toProtoSensor() {
		SensorData.Builder sdb = SensorData.newBuilder();
		sdb.setRecordTime(getTimestamp());
		sdb.addValueFloat(getMagX());
		sdb.addValueFloat(getMagY());
		sdb.addValueFloat(getMagZ());
		sdb.addValueInt32(getAccuracy());
		return sdb.build();
	}

	@Override
	public long getSensorIdentifier() {
		return SENSOR_ID;
	}

}

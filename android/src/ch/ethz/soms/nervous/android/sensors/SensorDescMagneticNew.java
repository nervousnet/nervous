package ch.ethz.soms.nervous.android.sensors;

import java.util.ArrayList;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescMagneticNew extends SensorDescVectorValue {

	public static final long SENSOR_ID = 0x0000000000000005L;

	private final float magX;
	private final float magY;
	private final float magZ;

	public SensorDescMagneticNew(final long timestamp, final float magX, final float magY, final float magZ) {
		super(timestamp);
		this.magX = magX;
		this.magY = magY;
		this.magZ = magZ;
	}

	public SensorDescMagneticNew(SensorData sensorData) {
		super(sensorData);
		this.magX = sensorData.getValueFloat(0);
		this.magY = sensorData.getValueFloat(1);
		this.magZ = sensorData.getValueFloat(2);
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
		return sdb.build();
	}

	@Override
	public long getSensorId() {
		return SENSOR_ID;
	}

	@Override
	public ArrayList<Float> getValue() {
		ArrayList<Float> arrayList = new ArrayList<Float>();
		arrayList.add(magX);
		arrayList.add(magY);
		arrayList.add(magZ);
		return arrayList;
	}

}

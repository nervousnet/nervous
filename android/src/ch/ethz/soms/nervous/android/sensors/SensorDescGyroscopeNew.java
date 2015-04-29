package ch.ethz.soms.nervous.android.sensors;

import java.util.ArrayList;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescGyroscopeNew extends SensorDescVectorValue {
	
	public static final long SENSOR_ID = 0x0000000000000002L;
	
	private final float gyrX;
	private final float gyrY;
	private final float gyrZ;

	public SensorDescGyroscopeNew(final long timestamp, final float gyrX, final float gyrY, final float gyrZ) {
		super(timestamp);
		this.gyrX = gyrX;
		this.gyrY = gyrY;
		this.gyrZ = gyrZ;
	}
	
	public SensorDescGyroscopeNew(SensorData sensorData) {
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

	@Override
	public ArrayList<Float> getValue() {
		// TODO Auto-generated method stub
		ArrayList<Float> arrayList = new ArrayList<Float>();
		arrayList.add(gyrX);
		arrayList.add(gyrY);
		arrayList.add(gyrZ);
		return arrayList;
	}

}

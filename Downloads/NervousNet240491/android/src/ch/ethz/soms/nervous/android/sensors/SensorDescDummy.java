package ch.ethz.soms.nervous.android.sensors;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescDummy extends SensorDesc {

	public static final long SENSOR_ID = 0x00000000FFFFFFFFL;

	private boolean booleanTest;
	private int intTest;
	private float floatTest;
	private long longTest;
	private double doubleTest;
	private String stringTest;

	public SensorDescDummy(long timestamp, boolean booleanTest, int intTest, float floatTest, long longTest, double doubleTest, String stringTest) {
		super(timestamp);
		this.booleanTest = booleanTest;
		this.intTest = intTest;
		this.floatTest = floatTest;
		this.longTest = longTest;
		this.doubleTest = doubleTest;
		this.stringTest = stringTest;
	}

	public SensorDescDummy(SensorData sensorData) {
		super(sensorData);
		this.booleanTest = sensorData.getValueBool(0);
		this.intTest = sensorData.getValueInt32(0);
		this.floatTest = sensorData.getValueFloat(0);
		this.longTest = sensorData.getValueInt64(0);
		this.doubleTest = sensorData.getValueDouble(0);
		this.stringTest = sensorData.getValueString(0);
	}

	@Override
	public long getSensorId() {
		return SENSOR_ID;
	}

	@Override
	public SensorData toProtoSensor() {
		SensorData.Builder sdb = SensorData.newBuilder();
		sdb.setRecordTime(getTimestamp());
		sdb.addValueBool(booleanTest);
		sdb.addValueInt32(intTest);
		sdb.addValueFloat(floatTest);
		sdb.addValueInt64(longTest);
		sdb.addValueDouble(doubleTest);
		sdb.addValueString(stringTest);
		return sdb.build();
	}

}

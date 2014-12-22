package ch.ethz.soms.nervous.android.sensors;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescNoise extends SensorDesc {

	public static final long SENSOR_ID = 0x0000000000000008L;

	float rms;
	float spl;
	float[] bands;

	public SensorDescNoise(final long timestamp, final float rms, final float spl, final float[] bands) {
		super(timestamp);
		this.rms = rms;
		this.spl = spl;
		this.bands = bands;
	}

	public SensorDescNoise(SensorData sensorData) {
		super(sensorData);
		this.rms = sensorData.getValueFloat(0);
		this.spl = sensorData.getValueFloat(1);
		float[] bands = new float[sensorData.getValueFloatList().size() - 2];
		for (int i = 0; i < sensorData.getValueFloatList().size() - 2; i++) {
			bands[i] = sensorData.getValueFloat(2 + i);
		}
	}

	@Override
	public long getSensorId() {
		return SENSOR_ID;
	}

	@Override
	public SensorData toProtoSensor() {
		SensorData.Builder sdb = SensorData.newBuilder();
		sdb.setRecordTime(getTimestamp());
		sdb.addValueFloat(rms);
		sdb.addValueFloat(spl);
		for (int i = 0; i < bands.length; i++) {
			sdb.addValueFloat(bands[i]);
		}
		return sdb.build();
	}

}

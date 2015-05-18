package ch.ethz.soms.nervous.android.sensors;

import java.util.ArrayList;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescNoise extends SensorDescVectorValue {

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

	public float getRms(){
		return rms;
	}
	
	public float getSpl(){
		return spl;
	}
	
	public ArrayList<Float> getBands(){
		ArrayList<Float> b = new ArrayList<Float>();
		for (int i = 0; i < bands.length; i++) {
			b.add(bands[i]);
		}
		return b;
		
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

	@Override
	public ArrayList<Float> getValue() {
		// TODO Auto-generated method stub
		
		ArrayList<Float> n = new ArrayList<Float>();
		n.add(getRms());
		n.add(getSpl());
		n.addAll(getBands());//check this(does it add all the values???)
		return null;
	}

}

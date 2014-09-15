package ch.ethz.soms.nervous.android.sensors;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescNoise extends SensorDesc{

	public SensorDescNoise(SensorData sensorData) {
		super(sensorData);
		// TODO Auto-generated constructor stub
	}

	public SensorDescNoise(long timestamp) {
		super(timestamp);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getSensorIdentifier() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SensorData toProtoSensor() {
		// TODO Auto-generated method stub
		return null;
	}

}

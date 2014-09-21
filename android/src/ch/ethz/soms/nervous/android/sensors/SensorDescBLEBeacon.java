package ch.ethz.soms.nervous.android.sensors;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescBLEBeacon extends SensorDesc{

	public static final long SENSOR_ID = 0x000000000000000BL;
	
	public SensorDescBLEBeacon(long timestamp) {
		super(timestamp);
		// TODO Auto-generated constructor stub
	}
	
	public SensorDescBLEBeacon(SensorData sensorData) {
		super(sensorData);
		// TODO Auto-generated constructor stub
	}

	@Override
	public long getSensorIdentifier() {
		return SENSOR_ID;
	}

	@Override
	public SensorData toProtoSensor() {
		// TODO Auto-generated method stub
		return null;
	}

}

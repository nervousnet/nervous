package ch.ethz.soms.nervous.android.sensors;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescConnectivity extends SensorDesc {

	public static final long SENSOR_ID = 0x000000000000000AL;
	
	public SensorDescConnectivity(final long timestamp) {
		super(timestamp);
		// TODO Auto-generated constructor stub
	}

	
	public SensorDescConnectivity(SensorData sensorData)
	{
		super(sensorData);
		// TODO
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

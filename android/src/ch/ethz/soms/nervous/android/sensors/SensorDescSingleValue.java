package ch.ethz.soms.nervous.android.sensors;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public abstract class SensorDescSingleValue extends SensorDesc{

	public SensorDescSingleValue(long timestamp) {
		super(timestamp);
	}
	
	public SensorDescSingleValue(SensorData sensorData) {
		super(sensorData);
	}

	public abstract float getValue();

}

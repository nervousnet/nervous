package ch.ethz.soms.nervous.android.sensors;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public abstract class SensorSingleValueDesc extends SensorDesc{

	public SensorSingleValueDesc(long timestamp) {
		super(timestamp);
	}
	
	public SensorSingleValueDesc(SensorData sensorData) {
		super(sensorData);
	}

	public abstract float getValue();

}

package ch.ethz.soms.nervous.android.sensors;

import java.util.ArrayList;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public abstract class SensorDescVectorValue extends SensorDesc{

	public SensorDescVectorValue(long timestamp) {
		super(timestamp);
	}
	
	public SensorDescVectorValue(SensorData sensorData) {
		super(sensorData);
	}

	public abstract ArrayList<Float> getValue();//dnt need to remember individual get functions!!

}
package ch.ethz.soms.nervous.android.sensors;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public abstract class SensorDesc {
		
	private final long timestamp;
	
	public SensorDesc(final long timestamp)
	{
		this.timestamp = timestamp;
	}
	
	public SensorDesc(SensorData sensorData)
	{
		this.timestamp = sensorData.getRecordTime();
	}
	
	public static String[] getDataColumns() {
		return null;
	}
	
	
	public abstract long getSensorId();

	public long getTimestamp() {
		return timestamp;
	}
	
	
	public abstract SensorData toProtoSensor();	

}

package ch.ethz.soms.nervous.android;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public abstract class SensorDesc {
	
	public static final long SENSOR_ID = 0x0000000000000000;
	
	private final long timestamp;

	public abstract String toString();
	
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
	
	
	public static String getSensorIdentifier() {
		return null;
	}

	public long getTimestamp() {
		return timestamp;
	}
	
	
	public abstract SensorData toProtoSensor();	

}

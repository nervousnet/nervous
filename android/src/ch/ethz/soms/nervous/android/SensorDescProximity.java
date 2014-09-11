package ch.ethz.soms.nervous.android;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescProximity extends SensorDesc {
	
	public static final long SENSOR_ID = 0x0000000000000006;
	
	
	private final int accuracy;
	private final float proximity;


	public SensorDescProximity(final long timestamp, final int accuracy, final float proximity) {
		super(timestamp);
		this.accuracy = accuracy;
		this.proximity = proximity;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(getTimestamp()) + ";proxAccuracy;" + String.valueOf(accuracy) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";prox;" + String.valueOf(proximity) + "\n");
		return sb.toString();
	}

	public int getAccuracy() {
		return accuracy;
	}

	public float getProximity() {
		return proximity;
	}

	@Override
	public SensorData toProtoSensor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getSensorIdentifier() {
		return SENSOR_ID;
	}

}

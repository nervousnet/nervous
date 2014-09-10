package ch.ethz.soms.nervous.android;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescHumidity extends SensorDesc {
	
	public static final long SENSOR_ID = 0x0000000000000003;
	
	private final int accuracy;
	private final float humidity;


	public SensorDescHumidity(final long timestamp, final int accuracy, final float humidity) {
		super(timestamp);
		this.accuracy = accuracy;
		this.humidity = humidity;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(getTimestamp()) + ";humidAccuracy;" + String.valueOf(accuracy) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";humid;" + String.valueOf(humidity) + "\n");
		return sb.toString();
	}

	public int getAccuracy() {
		return accuracy;
	}

	public float getHumidity() {
		return humidity;
	}

	@Override
	public SensorData toProtoSensor() {
		// TODO Auto-generated method stub
		return null;
	}


}

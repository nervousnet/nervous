package ch.ethz.soms.nervous.android;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescHumidity extends SensorDesc {
	
	public static final long SENSOR_ID = 0x0000000000000003L;
	
	private final int accuracy;
	private final float humidity;


	public SensorDescHumidity(final long timestamp, final int accuracy, final float humidity) {
		super(timestamp);
		this.accuracy = accuracy;
		this.humidity = humidity;
	}
	
	public SensorDescHumidity(SensorData sensorData) {
		super(sensorData);
		this.accuracy = sensorData.getValueInt32(0);
		this.humidity = sensorData.getValueFloat(0);
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
		SensorData.Builder sdb = SensorData.newBuilder();
		sdb.setRecordTime(getTimestamp());
		sdb.addValueFloat(getHumidity());
		sdb.addValueInt32(getAccuracy());
		return sdb.build();
	}

	@Override
	public long getSensorIdentifier() {
		return SENSOR_ID;
	}

}

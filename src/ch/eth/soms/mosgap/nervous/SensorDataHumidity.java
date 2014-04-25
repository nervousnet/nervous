package ch.eth.soms.mosgap.nervous;

public class SensorDataHumidity extends SensorData {
	private final int accuracy;
	private final float humidity;


	public SensorDataHumidity(final long timestamp, final int accuracy, final float humidity) {
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


}

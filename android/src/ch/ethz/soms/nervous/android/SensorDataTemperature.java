package ch.ethz.soms.nervous.android;

public class SensorDataTemperature extends SensorData {
	private final int accuracy;
	private final float temperature;


	public SensorDataTemperature(final long timestamp, final int accuracy, final float temperature) {
		super(timestamp);
		this.accuracy = accuracy;
		this.temperature = temperature;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(getTimestamp()) + ";tempAccuracy;" + String.valueOf(accuracy) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";temp;" + String.valueOf(temperature) + "\n");
		return sb.toString();
	}

	public int getAccuracy() {
		return accuracy;
	}

	public float getTemperature() {
		return temperature;
	}


}
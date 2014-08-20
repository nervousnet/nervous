package ch.ethz.soms.nervous.android;

public class SensorDataLight extends SensorData {
	private final int accuracy;
	private final float light;


	public SensorDataLight(final long timestamp, final int accuracy, final float light) {
		super(timestamp);
		this.accuracy = accuracy;
		this.light = light;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(getTimestamp()) + ";lightAccuracy;" + String.valueOf(accuracy) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";light;" + String.valueOf(light) + "\n");
		return sb.toString();
	}

	public int getAccuracy() {
		return accuracy;
	}

	public float getLight() {
		return light;
	}


}

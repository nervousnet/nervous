package ch.eth.soms.nervous.android;

public class SensorDataGyroscope extends SensorData {
	private final int accuracy;
	private final float gyrX;
	private final float gyrY;
	private final float gyrZ;

	public SensorDataGyroscope(final long timestamp, final int accuracy, final float gyrX, final float gyrY, final float gyrZ) {
		super(timestamp);
		this.accuracy = accuracy;
		this.gyrX = gyrX;
		this.gyrY = gyrY;
		this.gyrZ = gyrZ;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(getTimestamp()) + ";gyrAccuracy;" + String.valueOf(accuracy) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";gyrX;" + String.valueOf(gyrX) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";gyrY;" + String.valueOf(gyrY) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";gyrZ;" + String.valueOf(gyrZ) + "\n");
		return sb.toString();
	}

	public int getAccuracy() {
		return accuracy;
	}

	public float getGyrX() {
		return gyrX;
	}

	public float getGyrY() {
		return gyrY;
	}

	public float getGyrZ() {
		return gyrZ;
	}

}

package ch.eth.soms.nervous.android;

public class SensorDataMagnetic extends SensorData {
	private final int accuracy;
	private final float magX;
	private final float magY;
	private final float magZ;

	public SensorDataMagnetic(final long timestamp, final int accuracy, final float magX, final float magY, final float magZ) {
		super(timestamp);
		this.accuracy = accuracy;
		this.magX = magX;
		this.magY = magY;
		this.magZ = magZ;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(getTimestamp()) + ";magAccuracy;" + String.valueOf(accuracy) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";magX;" + String.valueOf(magX) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";magY;" + String.valueOf(magY) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";magZ;" + String.valueOf(magZ) + "\n");
		return sb.toString();
	}

	public int getAccuracy() {
		return accuracy;
	}

	public float getMagX() {
		return magX;
	}

	public float getMagY() {
		return magY;
	}

	public float getMagZ() {
		return magZ;
	}

}

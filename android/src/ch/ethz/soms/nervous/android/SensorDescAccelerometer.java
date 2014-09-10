package ch.ethz.soms.nervous.android;

public class SensorDescAccelerometer extends SensorDesc {
	
	public static final long SENSOR_ID = 0x0000000000000000;
	
	private final int accuracy;
	private final float accX;
	private final float accY;
	private final float accZ;

	public SensorDescAccelerometer(final long timestamp, final int accuracy, final float accX, final float accY, final float accZ) {
		super(timestamp);
		this.accuracy = accuracy;
		this.accX = accX;
		this.accY = accY;
		this.accZ = accZ;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(getTimestamp()) + ";accAccuracy;" + String.valueOf(accuracy) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";accX;" + String.valueOf(accX) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";accY;" + String.valueOf(accY) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";accZ;" + String.valueOf(accZ) + "\n");
		return sb.toString();
	}

	public int getAccuracy() {
		return accuracy;
	}

	public float getAccX() {
		return accX;
	}

	public float getAccY() {
		return accY;
	}

	public float getAccZ() {
		return accZ;
	}

}

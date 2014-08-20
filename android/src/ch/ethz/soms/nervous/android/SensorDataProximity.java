package ch.ethz.soms.nervous.android;

public class SensorDataProximity extends SensorData {
	private final int accuracy;
	private final float proximity;


	public SensorDataProximity(final long timestamp, final int accuracy, final float proximity) {
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


}

package ch.ethz.soms.nervous.android;

public abstract class SensorData {
	
	private final long timestamp;

	public abstract String toString();
	
	public SensorData(final long timestamp)
	{
		this.timestamp = timestamp;
	}
	
	public static String[] getDataColumns() {
		return null;
	}
	
	
	public static String getSensorIdentifier() {
		return null;
	}

	public long getTimestamp() {
		return timestamp;
	}
	

}

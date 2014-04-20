package ch.eth.soms.mosgap.sensorservice;

public class SensorDataAccelerometer extends SensorData {

	private static final String SENSOR_IDENTIFIER = "ACCELEROMETER";
	private static final String[] DATA_COLUMNS= {"accX","accY","accZ"};
	
	private final float accX;
	private final float accY;
	private final float accZ;
	
	public SensorDataAccelerometer(final float accX, final float accY, final float accZ)
	{
		this.accX = accX;
		this.accY = accY;
		this.accZ = accZ;
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(accX)+";");
		sb.append(String.valueOf(accY)+";");
		sb.append(String.valueOf(accZ)+";");
		return sb.toString();
	}

	public static String getSensorIdentifier() {
		return SENSOR_IDENTIFIER;
	}


	public static String[] getDataColumns() {
		return DATA_COLUMNS;
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

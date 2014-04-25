package ch.eth.soms.mosgap.sensorservice;

public class SensorDataBattery extends SensorData {

	private static final String SENSOR_IDENTIFIER = "BATTERY";
	private static final String[] DATA_COLUMNS= {"batteryPercent","isCharging","isUsbCharge","isAcCharge"};
	
	private final float batteryPercent;
	private final boolean isCharging;
	private final boolean isUsbCharge;
	private final boolean isAcCharge;
	
	public SensorDataBattery(final float batteryPercent, final boolean isCharging, final boolean isUsbCharge, final boolean isAcCharge) {
		this.batteryPercent = batteryPercent;
		this.isCharging = isCharging;
		this.isUsbCharge = isUsbCharge;
		this.isAcCharge = isAcCharge;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(batteryPercent)+";");
		sb.append(String.valueOf(isCharging)+";");
		sb.append(String.valueOf(isUsbCharge)+";");
		sb.append(String.valueOf(isAcCharge)+";");
		return sb.toString();
	}

	public static String getSensorIdentifier() {
		return SENSOR_IDENTIFIER;
	}


	public static String[] getDataColumns() {
		return DATA_COLUMNS;
	}


	public float getBatteryPercent() {
		return batteryPercent;
	}


	public boolean isCharging() {
		return isCharging;
	}


	public boolean isUsbCharge() {
		return isUsbCharge;
	}


	public boolean isAcCharge() {
		return isAcCharge;
	}

}

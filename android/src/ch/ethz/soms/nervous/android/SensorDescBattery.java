package ch.ethz.soms.nervous.android;

public class SensorDescBattery extends SensorDesc {
	
	public static final long SENSOR_ID = 0x0000000000000001;

	private final float batteryPercent;
	private final boolean isCharging;
	private final boolean isUsbCharge;
	private final boolean isAcCharge;

	public SensorDescBattery(final long timestamp, float batteryPercent, final boolean isCharging, final boolean isUsbCharge, final boolean isAcCharge) {
		super(timestamp);
		this.batteryPercent = batteryPercent;
		this.isCharging = isCharging;
		this.isUsbCharge = isUsbCharge;
		this.isAcCharge = isAcCharge;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(getTimestamp()) + ";batteryPercent;" + String.valueOf(batteryPercent) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";isCharging;" + String.valueOf(isCharging ? 1 : 0) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";isUsbCharge;" + String.valueOf(isUsbCharge ? 1 : 0) + "\n");
		sb.append(String.valueOf(getTimestamp()) + ";isAcCharge;" + String.valueOf(isAcCharge ? 1 : 0) + "\n");
		return sb.toString();
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

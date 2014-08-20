package ch.ethz.soms.nervous.android;

public class SensorFrame {

	private final SensorHeader sensorHeader;
	private SensorData[] sensorDataArr;

	SensorFrame(SensorHeader sensorHeader) {
		this.sensorHeader = sensorHeader;
		this.sensorDataArr = new SensorData[sensorHeader.getSensorDataClasses().size()];
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (SensorData sensorData : sensorDataArr) {
			sb.append(sensorData.toString());
		}
		return sb.toString();
	}

	public void addSensorData(SensorData sensorData) {
		int i = 0;
		for (Class<? extends SensorData> sensorDataClass : sensorHeader.getSensorDataClasses()) {
			if (sensorData.getClass().equals(sensorDataClass)) {
				sensorDataArr[i] = sensorData;
				break;
			}
			i++;
		}
	}

	public boolean isComplete() {
		boolean complete = true;
		int i = 0;
		for (boolean sensorTypeListener : sensorHeader.getSensorTypeListenerList()) {
			if (sensorTypeListener) {
				complete = complete && (sensorDataArr[i] != null);
			}
			i++;
		}
		return complete;
	}

}

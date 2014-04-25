package ch.eth.soms.mosgap.nervous;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class SensorHeader {

	private static final String OS_INFO = System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
	private static final int API_LEVEL = android.os.Build.VERSION.SDK_INT;
	private static final String DEVICE = android.os.Build.DEVICE;
	private static final String MODEL = android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")";

	private final LinkedList<Class<? extends SensorData>> sensorDataClasses;
	private final LinkedList<Boolean> sensorTypeListenerList;

	private final SensorManager sensorManager;

	public SensorHeader(final SensorManager sensorManager) {
		sensorDataClasses = new LinkedList<Class<? extends SensorData>>();
		sensorTypeListenerList = new LinkedList<Boolean>();
		this.sensorManager = sensorManager;
	}

	public void addSensor(Class<? extends SensorData> sensorClass, boolean sensorTypeListener) {
		sensorDataClasses.add(sensorClass);
		sensorTypeListenerList.add(sensorTypeListener);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		// Header
		sb.append("SensorService Log file\n\n");
		sb.append("OS Version: " + OS_INFO + "\n");
		sb.append("API LEVEL: " + String.valueOf(API_LEVEL) + "\n");
		sb.append("Device: " + String.valueOf(DEVICE) + "\n");
		sb.append("Model: " + String.valueOf(MODEL) + "\n");
		sb.append("\n");
		// Sensor information
		List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
		for (Sensor sensor : sensorList) {
			boolean unsupported = false;
			switch (sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				sb.append(String.valueOf(sensor.getType()) + " - ACCELEROMETER\n");
				break;
			case Sensor.TYPE_AMBIENT_TEMPERATURE:
				sb.append(String.valueOf(sensor.getType()) + " - AMBIENT_TEMPERATURE\n");
				break;
			case Sensor.TYPE_GYROSCOPE:
				sb.append(String.valueOf(sensor.getType()) + " - GYROSCOPE\n");
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				sb.append(String.valueOf(sensor.getType()) + " - MAGNETIC_FIELD\n");
				break;
			case Sensor.TYPE_LIGHT:
				sb.append(String.valueOf(sensor.getType()) + " - LIGHT\n");
				break;
			case Sensor.TYPE_PRESSURE:
				sb.append(String.valueOf(sensor.getType()) + " - PRESSURE\n");
				break;
			case Sensor.TYPE_PROXIMITY:
				sb.append(String.valueOf(sensor.getType()) + " - PROXIMITY\n");
				break;
			default:
				unsupported = true;
			}
			if (!unsupported) {
				sb.append("Name: " + sensor.getName() + "\n");
				sb.append("Maximum Range: " + String.valueOf(sensor.getMaximumRange()) + "\n");
				sb.append("Min Delay: " + String.valueOf(sensor.getMinDelay()) + "\n");
				sb.append("Power: " + String.valueOf(sensor.getPower()) + "\n");
				sb.append("Resolution: " + String.valueOf(sensor.getResolution()) + "\n");
				sb.append("Type: " + String.valueOf(sensor.getType()) + "\n");
				sb.append("Vendor: " + String.valueOf(sensor.getVendor()) + "\n");
				sb.append("Version: " + String.valueOf(sensor.getVersion()) + "\n");
				sb.append("\n");
			}
		}
		// Tripplet columns
		sb.append("Timestamp;Type;Value\n");
		return sb.toString();
	}

	public LinkedList<Class<? extends SensorData>> getSensorDataClasses() {
		return sensorDataClasses;
	}

	public LinkedList<Boolean> getSensorTypeListenerList() {
		return sensorTypeListenerList;
	}

}

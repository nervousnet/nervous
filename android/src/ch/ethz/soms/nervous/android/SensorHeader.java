package ch.ethz.soms.nervous.android;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class SensorHeader {

    long TS = System.currentTimeMillis();

	private static final String OS_INFO = System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
	private static final int API_LEVEL = android.os.Build.VERSION.SDK_INT;
	private static final String DEVICE = android.os.Build.DEVICE;
	private static final String MODEL = android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")";

	private final LinkedList<Class<? extends SensorData>> sensorDataClasses;
	private final LinkedList<Boolean> sensorTypeListenerList;

	private final SensorManager sensorManager;

	public SensorHeader(final SensorManager sensorManager) {
        //assigns all the sensor classes
		sensorDataClasses = new LinkedList<Class<? extends SensorData>>();
		sensorTypeListenerList = new LinkedList<Boolean>();
		this.sensorManager = sensorManager;
	}

	public void addSensor(Class<? extends SensorData> sensorClass, boolean sensorTypeListener) {
		sensorDataClasses.add(sensorClass);
		sensorTypeListenerList.add(sensorTypeListener);
	}

	public String toString() {

        // a string that contains all the information
        // and the different types of sensors
        // and headers of the columns of observations
		StringBuilder sb = new StringBuilder();
		// Header

		sb.append(TS+";meta.os;" + OS_INFO + "\n");
		sb.append(TS+";meta.api.level;" + String.valueOf(API_LEVEL) + "\n");
		sb.append(TS+";device.type;" + String.valueOf(DEVICE) + "\n");
		sb.append(TS+";model.number;" + String.valueOf(MODEL) + "\n");
		sb.append("\n");

		// Sensor information
		List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
		for (Sensor sensor : sensorList) {
			boolean unsupported = false;
			switch (sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				sb.append(TS+";Type;"+String.valueOf(sensor.getType())+"-ACCELEROMETER\n" );
				break;
			case Sensor.TYPE_AMBIENT_TEMPERATURE:
				sb.append(TS+";Type;"+String.valueOf(sensor.getType()) + "-AMBIENT_TEMPERATURE\n");
				break;
			case Sensor.TYPE_GYROSCOPE:
				sb.append(TS+";Type;"+String.valueOf(sensor.getType()) + "-GYROSCOPE\n");
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				sb.append(TS+";Type;"+String.valueOf(sensor.getType()) + "-MAGNETIC_FIELD\n");
				break;
			case Sensor.TYPE_LIGHT:
				sb.append(TS+";Type;"+String.valueOf(sensor.getType()) + "-LIGHT\n");
				break;
			case Sensor.TYPE_PRESSURE:
				sb.append(TS+";Type;"+String.valueOf(sensor.getType()) + "-PRESSURE\n");
				break;
			case Sensor.TYPE_PROXIMITY:
				sb.append(TS+";Type;"+String.valueOf(sensor.getType()) + "-PROXIMITY\n");
				break;
			default:
				unsupported = true;
			}
			if (!unsupported) {

				sb.append(TS+";Name;" + sensor.getName() + "\n");
				sb.append(TS+";Maximum.Range;" + String.valueOf(sensor.getMaximumRange()) + "\n");
				sb.append(TS+";Min.Delay;" + String.valueOf(sensor.getMinDelay()) + "\n");
				sb.append(TS+";Power;" + String.valueOf(sensor.getPower()) + "\n");
				sb.append(TS+";Resolution;" + String.valueOf(sensor.getResolution()) + "\n");
				sb.append(TS+";Type;" + String.valueOf(sensor.getType()) + "\n");
				sb.append(TS+";Vendor;" + String.valueOf(sensor.getVendor()) + "\n");
				sb.append(TS+";Version;" + String.valueOf(sensor.getVersion()) + "\n");
				sb.append("\n");
			}
		}
		// Triplet columns
		sb.append("\nTimestamp;Type;Value\n");
		return sb.toString();
	}
    // getSensorDataClasses() returns a linked list of type class
	public LinkedList<Class<? extends SensorData>> getSensorDataClasses() {
		return sensorDataClasses;
	}

	public LinkedList<Boolean> getSensorTypeListenerList() {
		return sensorTypeListenerList;
	}

}

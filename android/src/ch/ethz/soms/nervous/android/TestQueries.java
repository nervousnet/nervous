package ch.ethz.soms.nervous.android;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesAccelerometer;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesBattery;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesLight;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesMultipleSensors;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesProximity;
import ch.ethz.soms.nervous.android.sensors.SensorDescAccelerometer;
import ch.ethz.soms.nervous.android.sensors.SensorDescBattery;
import ch.ethz.soms.nervous.android.sensors.SensorDescLight;
import ch.ethz.soms.nervous.android.sensors.SensorDescProximity;

public class TestQueries {
	
	private static final String DEBUG_TAG = TestQueries.class.getSimpleName();

	private Context context;
	private File filesDir;
	
	public TestQueries(Context context, File filesDir)
	{
		this.context = context;
		this.filesDir = filesDir;
	}
	
	public void lightProxKMean() {
		SensorQueriesMultipleSensors sq = new SensorQueriesMultipleSensors();
		SensorQueriesLight sensorQ_Light = new SensorQueriesLight(1, Long.MAX_VALUE, getFilesDir());
		SensorQueriesProximity sensorQ_Prox = new SensorQueriesProximity(1, Long.MAX_VALUE, getFilesDir());
		ArrayList<Vector<Float>> res = sq.getKMeans(sensorQ_Light.getSensorDescriptorList(), sensorQ_Prox.getSensorDescriptorList());
		toastToScreen(res.get(0).get(0) + "", false);
	}

	private File getFilesDir() {
		return filesDir;
	}

	public void minLight() {
		SensorQueriesLight sensorQ_Light2 = new SensorQueriesLight(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Light2.containsReadings()) {
			SensorDescLight minLightDesc = sensorQ_Light2.getMinValue();
			toastToScreen("Minimum Light: " + minLightDesc.getLight() + "\nat " + getDate(minLightDesc.getTimestamp()), true);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	public void maxLight() {
		SensorQueriesLight sensorQ_Light = new SensorQueriesLight(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Light.containsReadings()) {
			SensorDescLight maxLightDesc = sensorQ_Light.getMaxValue();
			toastToScreen("Maximum Light: " + maxLightDesc.getLight() + "\nat " + getDate(maxLightDesc.getTimestamp()), true);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	public void bottom10Light() {
		SensorQueriesLight sensorQ_Light = new SensorQueriesLight(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Light.containsReadings()) {
			ArrayList<SensorDescLight> topKLightDesc = sensorQ_Light.getBottomK(10);
			int i = 1;
			Log.d(DEBUG_TAG, "Bottom 10 Light:");
			for (SensorDescLight bat : topKLightDesc) {
				Log.d(DEBUG_TAG, i++ + ": " + bat.getLight() + " Date: " + getDate(bat.getTimestamp()));
			}
			toastToScreen("Bottom 10 logged", false);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	public void top10Light() {
		SensorQueriesLight sensorQ_Light = new SensorQueriesLight(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Light.containsReadings()) {
			ArrayList<SensorDescLight> topKLightDesc = sensorQ_Light.getTopK(10);
			int i = 1;
			Log.d(DEBUG_TAG, "Top 10 Light:");
			for (SensorDescLight light : topKLightDesc) {
				Log.d(DEBUG_TAG, i++ + ": " + light.getLight() + " Date: " + getDate(light.getTimestamp()));
			}
			toastToScreen("Top 10 logged", false);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	public void minProximity() {
		SensorQueriesProximity sensorQ_Proximity = new SensorQueriesProximity(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Proximity.containsReadings()) {
			SensorDescProximity minProxDesc = sensorQ_Proximity.getMinValue();
			toastToScreen("Minimum Proximity: " + minProxDesc.getProximity() + "\nat " + getDate(minProxDesc.getTimestamp()), true);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	public void maxProximity() {
		SensorQueriesProximity sensorQ_Prox = new SensorQueriesProximity(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Prox.containsReadings()) {
			SensorDescProximity maxProxDesc = sensorQ_Prox.getMaxValue();
			toastToScreen("Maximum Prox: " + maxProxDesc.getProximity() + "\nat " + getDate(maxProxDesc.getTimestamp()), true);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	public void bottom10Proximity() {
		SensorQueriesProximity sensorQ_Prox = new SensorQueriesProximity(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Prox.containsReadings()) {
			ArrayList<SensorDescProximity> topKProxDesc = sensorQ_Prox.getBottomK(10);
			int i = 1;
			Log.d(DEBUG_TAG, "Bottom 10 Prox:");
			for (SensorDescProximity proxDesc : topKProxDesc) {
				Log.d(DEBUG_TAG, i++ + ": " + proxDesc.getProximity() + " Date: " + getDate(proxDesc.getTimestamp()));
			}
			toastToScreen("Bottom 10 logged", false);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	public void top10Proximity() {
		SensorQueriesProximity sensorQ_Light = new SensorQueriesProximity(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Light.containsReadings()) {
			ArrayList<SensorDescProximity> topKProxDesc = sensorQ_Light.getTopK(10);
			int i = 1;
			Log.d(DEBUG_TAG, "Top 10 Prox:");
			for (SensorDescProximity proxDesc : topKProxDesc) {
				Log.d(DEBUG_TAG, i++ + ": " + proxDesc.getProximity() + " Date: " + getDate(proxDesc.getTimestamp()));
			}
			toastToScreen("Top 10 logged", false);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	public void maxAverageAccelerometer() {
		SensorQueriesAccelerometer sensorQ_Accel = new SensorQueriesAccelerometer(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Accel.containsReadings()) {
			SensorDescAccelerometer maxAccAverageSensDesc = sensorQ_Accel.getMaxAverageValue();
			toastToScreen("Maximum Accelerometer Average: \n x:" + maxAccAverageSensDesc.getAccX() + "\ny: " + maxAccAverageSensDesc.getAccY() + "\nz: " + maxAccAverageSensDesc.getAccZ() + "\nDate: " + getDate(maxAccAverageSensDesc.getTimestamp()), true);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	public void minAverageAccelerometer() {
		SensorQueriesAccelerometer sensorQ_Accel = new SensorQueriesAccelerometer(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Accel.containsReadings()) {
			SensorDescAccelerometer minAccAverageSensDesc = sensorQ_Accel.getMinAverageValue();
			toastToScreen("Minimum Accelerometer Average: \n x:" + minAccAverageSensDesc.getAccX() + "\ny: " + minAccAverageSensDesc.getAccY() + "\nz: " + minAccAverageSensDesc.getAccZ() + "\nDate: " + getDate(minAccAverageSensDesc.getTimestamp()), true);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	public void maxBattery() {
		SensorQueriesBattery sensorQ_Batteries2 = new SensorQueriesBattery(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Batteries2.containsReadings()) {
			SensorDescBattery maxBatDesc = sensorQ_Batteries2.getMaxValue();
			toastToScreen("Max Battery: " + maxBatDesc.getBatteryPercent() + "\nat " + getDate(maxBatDesc.getTimestamp()), true);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	public void minBattery() {
		SensorQueriesBattery sensorQ_Batteries = new SensorQueriesBattery(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Batteries.containsReadings()) {
			SensorDescBattery minBatDesc = sensorQ_Batteries.getMinValue();
			toastToScreen("Minimum Battery: " + minBatDesc.getBatteryPercent() + "\nat " + getDate(minBatDesc.getTimestamp()), true);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	public void bottom10Battery() {
		SensorQueriesBattery sensorQ_Batteries = new SensorQueriesBattery(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Batteries.containsReadings()) {
			ArrayList<SensorDescBattery> topKBatDesc = sensorQ_Batteries.getBottomK(10);
			int i = 1;
			Log.d(DEBUG_TAG, "Bottom 10 Bat:");
			for (SensorDescBattery bat : topKBatDesc) {
				toastToScreen("Bottom 10 logged", false);
				Log.d(DEBUG_TAG, i++ + ": " + bat.getBatteryPercent() + " Date: " + getDate(bat.getTimestamp()));
			}
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	public void top10Battery() {
		SensorQueriesBattery sensorQ_Batteries = new SensorQueriesBattery(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Batteries.containsReadings()) {
			ArrayList<SensorDescBattery> topKBatDesc = sensorQ_Batteries.getTopK(10);
			int i = 1;
			Log.d(DEBUG_TAG, "Top 10 Bat:");
			for (SensorDescBattery bat : topKBatDesc) {
				toastToScreen("Top 10 logged", false);
				Log.d(DEBUG_TAG, i++ + ": " + bat.getBatteryPercent() + " Date: " + getDate(bat.getTimestamp()));
			}
		} else {
			toastToScreen("No Data Found", false);
		}
	}
	
	public void toastToScreen(String msg, boolean lengthLong) {

		int toastLength = lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
		Toast.makeText(context, msg, toastLength).show();
	}
	
	private String getDate(long time) {
		Calendar cal = Calendar.getInstance(Locale.ENGLISH);
		cal.setTimeInMillis(time);
		String date = DateFormat.format("dd-MM-yyyy HH:mm:ss", cal).toString();
		return date;
	}
}

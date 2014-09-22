package ch.ethz.soms.nervous.android;

import java.io.File;
import java.util.List;

import android.util.Log;
import ch.ethz.soms.nervous.android.sensors.SensorDesc;
import ch.ethz.soms.nervous.android.sensors.SensorDescAccelerometer;
import ch.ethz.soms.nervous.android.sensors.SensorDescBattery;
import ch.ethz.soms.nervous.android.sensors.SensorDescLight;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;
import ch.ethz.soms.nervous.vm.NervousVM;

public class SensorQueries {

	static SensorDesc getMin(long sensorID, long timestamp_from,
			long timestamp_to, File file) {
		NervousVM nervousVm = NervousVM.getInstance(file);
		List<SensorData> list = nervousVm.retrieve(sensorID, timestamp_from,
				timestamp_to);
		if (list == null) {
			return null;
		}
		Log.d(MainActivity.DEBUG_TAG, "Retrieved List Size: " + list.size());

		int int_sensID = (int) sensorID;
		switch (int_sensID) {
		case (int) SensorDescBattery.SENSOR_ID:
			return minBattery(list);
		case (int) SensorDescLight.SENSOR_ID:
			return minLight(list);
		case (int) SensorDescAccelerometer.SENSOR_ID:
			return minAccelerometerAverage(list);
		default:
			return null;
		}
	}

	static SensorDesc getMax(long sensorID, long timestamp_from,
			long timestamp_to, File file) {
		NervousVM nervousVm = NervousVM.getInstance(file);
		List<SensorData> list = nervousVm.retrieve(sensorID, timestamp_from,
				timestamp_to);
		if (list == null) {
			return null;
		}

		Log.d(MainActivity.DEBUG_TAG, "Retrieved List Size: " + list.size());
		int int_sensID = (int) sensorID;
		switch (int_sensID) {
		case (int) SensorDescBattery.SENSOR_ID:
			return maxBattery(list);
		case (int) SensorDescLight.SENSOR_ID:
			return maxLight(list);
		case (int) SensorDescAccelerometer.SENSOR_ID:
			return maxAccelerometerAverage(list);
		default:
			return null;
		}

	}

	private static SensorDesc minBattery(List<SensorData> list) {
		SensorDescBattery minBattSensDesc = new SensorDescBattery(0,
				Float.MAX_VALUE, false, false, false);
		for (SensorData sensorData : list) {
			SensorDescBattery sensDesc = new SensorDescBattery(sensorData);
			if (sensDesc.getBatteryPercent() < minBattSensDesc
					.getBatteryPercent()) {
				minBattSensDesc = sensDesc;
			}
		}
		return minBattSensDesc;
	}

	private static SensorDesc minLight(List<SensorData> list) {
		SensorDescLight minLightSensDesc = new SensorDescLight(0,
				Float.MAX_VALUE);
		for (SensorData sensorData : list) {
			SensorDescLight sensDesc = new SensorDescLight(sensorData);
			if (sensDesc.getLight() < minLightSensDesc.getLight()) {
				minLightSensDesc = sensDesc;
			}
		}
		return minLightSensDesc;
	}

	private static SensorDesc minAccelerometerAverage(List<SensorData> list) {
		SensorDescAccelerometer minAccSensDesc = null;
		float maxAverage = Float.MAX_VALUE;
		for (SensorData sensorData : list) {
			SensorDescAccelerometer sensDesc = new SensorDescAccelerometer(
					sensorData);

			float x = Math.abs(sensDesc.getAccX());
			float y = Math.abs(sensDesc.getAccY());
			float z = Math.abs(sensDesc.getAccZ());
			float newAverage = (x + y + z) / 3;
			if (newAverage < maxAverage) {
				maxAverage = newAverage;
				minAccSensDesc = sensDesc;
			}
		}
		return minAccSensDesc;
	}

	private static SensorDesc maxAccelerometerAverage(List<SensorData> list) {
		SensorDescAccelerometer maxAccSensDesc = null;
		float maxAverage = 0;
		for (SensorData sensorData : list) {
			SensorDescAccelerometer sensDesc = new SensorDescAccelerometer(
					sensorData);

			float x = Math.abs(sensDesc.getAccX());
			float y = Math.abs(sensDesc.getAccY());
			float z = Math.abs(sensDesc.getAccZ());
			float newAverage = (x + y + z) / 3;
			if (newAverage > maxAverage) {
				maxAverage = newAverage;
				maxAccSensDesc = sensDesc;
			}
		}
		return maxAccSensDesc;
	}

	private static SensorDesc maxLight(List<SensorData> list) {
		SensorDescLight maxLightSensDesc = new SensorDescLight(0,
				Float.MIN_VALUE);
		for (SensorData sensorData : list) {
			SensorDescLight sensDesc = new SensorDescLight(sensorData);
			if (sensDesc.getLight() > maxLightSensDesc.getLight()) {
				maxLightSensDesc = sensDesc;
			}
		}
		return maxLightSensDesc;
	}

	private static SensorDesc maxBattery(List<SensorData> list) {
		SensorDescBattery maxBattSensDesc = new SensorDescBattery(0,
				Float.MIN_VALUE, false, false, false);
		for (SensorData sensorData : list) {
			SensorDescBattery sensDesc = new SensorDescBattery(sensorData);
			if (sensDesc.getBatteryPercent() > maxBattSensDesc
					.getBatteryPercent()) {
				maxBattSensDesc = sensDesc;
			}
		}
		return maxBattSensDesc;
	}

	static SensorDescBattery minBattery(long i, long j, File file) {
		NervousVM nervousVm = NervousVM.getInstance(file);
		List<SensorData> list = nervousVm.retrieve(SensorDescBattery.SENSOR_ID,
				i, j);

		if (list == null) {
			return null;
		}
		Log.d(MainActivity.DEBUG_TAG, "size: " + list.size());
		return (SensorDescBattery) minBattery(list);

	}

	static SensorDescBattery maxBattery(long i, long j, File file) {
		NervousVM nervousVm = NervousVM.getInstance(file);
		List<SensorData> list = nervousVm.retrieve(SensorDescBattery.SENSOR_ID,
				i, j);

		if (list == null) {
			return null;
		}
		Log.d(MainActivity.DEBUG_TAG, "size: " + list.size());
		return (SensorDescBattery) maxBattery(list);

	}

	static SensorDescAccelerometer maxAccelerometerAverage(long i, long j,
			File file) {
		NervousVM nervousVm = NervousVM.getInstance(file);
		List<SensorData> list = nervousVm.retrieve(
				SensorDescAccelerometer.SENSOR_ID, i, j);

		if (list == null) {
			return null;
		}
		Log.d(MainActivity.DEBUG_TAG, "size: " + list.size());
		return (SensorDescAccelerometer) maxAccelerometerAverage(list);

	}

	static SensorDescAccelerometer minAccelerometerAverage(long i, long j,
			File file) {
		NervousVM nervousVm = NervousVM.getInstance(file);
		List<SensorData> list = nervousVm.retrieve(
				SensorDescAccelerometer.SENSOR_ID, i, j);

		if (list == null) {
			return null;
		}
		Log.d(MainActivity.DEBUG_TAG, "size: " + list.size());
		return (SensorDescAccelerometer) minAccelerometerAverage(list);

	}

	static SensorDescLight maxLight(long i, long j, File file) {
		NervousVM nervousVm = NervousVM.getInstance(file);
		List<SensorData> list = nervousVm.retrieve(SensorDescLight.SENSOR_ID,
				i, j);

		if (list == null) {
			return null;
		}
		Log.d(MainActivity.DEBUG_TAG, "size: " + list.size());
		return (SensorDescLight) maxLight(list);

	}

	static SensorDescLight minLight(long i, long j, File file) {
		NervousVM nervousVm = NervousVM.getInstance(file);
		List<SensorData> list = nervousVm.retrieve(SensorDescLight.SENSOR_ID,
				i, j);

		if (list == null) {
			return null;
		}
		Log.d(MainActivity.DEBUG_TAG, "size: " + list.size());
		return (SensorDescLight) minLight(list);

	}
}

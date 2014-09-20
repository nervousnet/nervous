package ch.ethz.soms.nervous.android;

import java.io.File;
import java.util.List;

import android.util.Log;
import ch.ethz.soms.nervous.android.sensors.SensorDescAccelerometer;
import ch.ethz.soms.nervous.android.sensors.SensorDescBattery;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;
import ch.ethz.soms.nervous.vm.NervousVM;

public class SensorQueries {

	static SensorDescBattery minBattery(long i, long j, File file) {
		NervousVM nervousVm = NervousVM.getInstance(file);
		List<SensorData> list = nervousVm.retrieve(SensorDescBattery.SENSOR_ID,
				i, j);

		if (list == null) {
			return null;
		}
		Log.d(MainActivity.DEBUG_TAG, "size: " + list.size());
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

	static SensorDescBattery maxBattery(long i, long j, File file) {
		NervousVM nervousVm = NervousVM.getInstance(file);
		List<SensorData> list = nervousVm.retrieve(SensorDescBattery.SENSOR_ID,
				i, j);

		if (list == null) {
			return null;
		}
		Log.d(MainActivity.DEBUG_TAG, "size: " + list.size());
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

	static SensorDescAccelerometer maxAccelerometerAverage(long i, long j,
			File file) {
		NervousVM nervousVm = NervousVM.getInstance(file);
		List<SensorData> list = nervousVm.retrieve(
				SensorDescAccelerometer.SENSOR_ID, i, j);

		if (list == null) {
			return null;
		}
		Log.d(MainActivity.DEBUG_TAG, "size: " + list.size());
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
	static SensorDescAccelerometer minAccelerometerAverage(long i, long j,
			File file) {
		NervousVM nervousVm = NervousVM.getInstance(file);
		List<SensorData> list = nervousVm.retrieve(
				SensorDescAccelerometer.SENSOR_ID, i, j);
		
		if (list == null) {
			return null;
		}
		Log.d(MainActivity.DEBUG_TAG, "size: " + list.size());
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

}

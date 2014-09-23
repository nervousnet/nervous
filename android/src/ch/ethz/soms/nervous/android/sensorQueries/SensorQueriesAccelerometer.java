package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;

import ch.ethz.soms.nervous.android.sensors.SensorDesc;
import ch.ethz.soms.nervous.android.sensors.SensorDescAccelerometer;
import ch.ethz.soms.nervous.android.sensors.SensorDescLight;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorQueriesAccelerometer extends SensorQueries {

	@Override
	long getSensorId() {
		return SensorDescAccelerometer.SENSOR_ID;
	}

	public SensorQueriesAccelerometer(long timestamp_from, long timestamp_to,
			File file) {
		super(timestamp_from, timestamp_to, file);
	}

	public SensorDescAccelerometer getMaxAverageValue() {
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

	public SensorDescAccelerometer getMinAverageValue() {
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

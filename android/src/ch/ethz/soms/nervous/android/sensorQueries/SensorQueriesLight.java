package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;
import ch.ethz.soms.nervous.android.sensors.SensorDesc;
import ch.ethz.soms.nervous.android.sensors.SensorDescLight;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorQueriesLight extends SensorQueries {

	@Override
	long getSensorId() {
		return SensorDescLight.SENSOR_ID;
	}

	public SensorQueriesLight(long timestamp_from, long timestamp_to,
			File file) {
		super(timestamp_from, timestamp_to, file);
	}

	public SensorDescLight getMaxValue() {
		SensorDescLight maxLightSensDesc = new SensorDescLight(0,
				Float.MIN_VALUE);
		for (SensorData sensorData : list) {
			SensorDescLight sensDesc = new SensorDescLight(sensorData);
			if (sensDesc.getLight() > maxLightSensDesc
					.getLight()) {
				maxLightSensDesc = sensDesc;
			}
		}
		return maxLightSensDesc;
	}

	public SensorDescLight getMinValue() {
		SensorDescLight minLightSensDesc = new SensorDescLight(0,
				Float.MAX_VALUE);
		for (SensorData sensorData : list) {
			SensorDescLight sensDesc = new SensorDescLight(sensorData);
			if (sensDesc.getLight() < minLightSensDesc
					.getLight()) {
				minLightSensDesc = sensDesc;
			}
		}
		return minLightSensDesc;
	}
}

package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;

import ch.ethz.soms.nervous.android.sensors.SensorDesc;
import ch.ethz.soms.nervous.android.sensors.SensorDescLight;
import ch.ethz.soms.nervous.android.sensors.SensorDescProximity;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorQueriesProximity extends SensorQueries {

	@Override
	long getSensorId() {
		return SensorDescProximity.SENSOR_ID;
	}

	public SensorQueriesProximity(long timestamp_from, long timestamp_to,
			File file) {
		super(timestamp_from, timestamp_to, file);
	}

	public SensorDescProximity getMaxValue() {
		SensorDescProximity maxLightSensDesc = new SensorDescProximity(0,
				Float.MIN_VALUE);
		for (SensorData sensorData : list) {
			SensorDescProximity sensDesc = new SensorDescProximity(sensorData);
			if (sensDesc.getProximity() > maxLightSensDesc
					.getProximity()) {
				maxLightSensDesc = sensDesc;
			}
		}
		return maxLightSensDesc;
	}

	public SensorDescProximity getMinValue() {
		SensorDescProximity minLightSensDesc = new SensorDescProximity(0,
				Float.MAX_VALUE);
		for (SensorData sensorData : list) {
			SensorDescProximity sensDesc = new SensorDescProximity(sensorData);
			if (sensDesc.getProximity() < minLightSensDesc
					.getProximity()) {
				minLightSensDesc = sensDesc;
			}
		}
		return minLightSensDesc;
	}
}

package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;
import java.util.ArrayList;

import ch.ethz.soms.nervous.android.sensors.SensorDesc;
import ch.ethz.soms.nervous.android.sensors.SensorDescLight;
import ch.ethz.soms.nervous.android.sensors.SensorDescProximity;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorQueriesLight extends SensorQueries<SensorDescLight> {

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
	
	@Override
	public ArrayList<SensorDescLight> getSensorDescriptorList() {
		ArrayList<SensorDescLight> descList = new ArrayList<SensorDescLight>();
		for (SensorData sensorData : list) {
			descList.add(new SensorDescLight(sensorData));
		}
		return descList;
	}
	
}

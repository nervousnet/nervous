package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import ch.ethz.soms.nervous.android.sensors.SensorDescLight;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorQueriesLight extends
		SensorSingleValueQueries<SensorDescLight> {

	@Override
	long getSensorId() {
		return SensorDescLight.SENSOR_ID;
	}

	public SensorQueriesLight(long timestamp_from, long timestamp_to, File file) {
		super(timestamp_from, timestamp_to, file);
	}

	@Override
	SensorDescLight createSensorDescSingleValue(SensorData sensorData) {
		return new SensorDescLight(sensorData);
	}

	@Override
	SensorDescLight createDummyObject() {
		return new SensorDescLight(0, 0);
	}

}

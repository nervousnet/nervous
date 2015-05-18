package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;
import ch.ethz.soms.nervous.android.sensors.*;
import ch.ethz.soms.nervous.android.Queries.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import ch.ethz.soms.nervous.android.sensors.SensorDescLight;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorQueriesLight extends
		QueryNumSingleValue<SensorDescLight> {

	@Override
	public long getSensorId() {
		return SensorDescLight.SENSOR_ID;
	}

	public SensorQueriesLight(long timestamp_from, long timestamp_to, File file) {
		super(timestamp_from, timestamp_to, file);
	}

	@Override
	public SensorDescLight createSensorDescSingleValue(SensorData sensorData) {
		return new SensorDescLight(sensorData);
	}

	@Override
	public SensorDescLight createDummyObject() {
		return new SensorDescLight(0, 0);
	}

}

package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;
import ch.ethz.soms.nervous.android.sensors.*;
import ch.ethz.soms.nervous.android.Queries.*;
import java.util.ArrayList;

import ch.ethz.soms.nervous.android.sensors.SensorDescTemperature;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorQueriesTemperature extends
		QueryNumSingleValue<SensorDescTemperature> {

	@Override
	public long getSensorId() {
		return SensorDescTemperature.SENSOR_ID;
	}

	public SensorQueriesTemperature(long timestamp_from, long timestamp_to,
			File file) {
		super(timestamp_from, timestamp_to, file);
	}

	@Override
	public SensorDescTemperature createSensorDescSingleValue(SensorData sensorData) {
		return new SensorDescTemperature(sensorData);
	}

	@Override
	public SensorDescTemperature createDummyObject() {
		return new SensorDescTemperature(0, 0);
	}

}

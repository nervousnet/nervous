package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;
import java.util.ArrayList;

import ch.ethz.soms.nervous.android.sensors.SensorDescTemperature;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorQueriesTemparature extends
		SensorSingleValueQueries<SensorDescTemperature> {

	@Override
	long getSensorId() {
		return SensorDescTemperature.SENSOR_ID;
	}

	public SensorQueriesTemparature(long timestamp_from, long timestamp_to,
			File file) {
		super(timestamp_from, timestamp_to, file);
	}

	@Override
	SensorDescTemperature createSensorDesc(SensorData sensorData) {
		return new SensorDescTemperature(sensorData);
	}

	@Override
	SensorDescTemperature createDummyObject() {
		return new SensorDescTemperature(0, 0);
	}

}

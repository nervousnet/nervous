package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;
import java.util.ArrayList;

import ch.ethz.soms.nervous.android.sensors.SensorDescPressure;
import ch.ethz.soms.nervous.android.sensors.SensorDescTemperature;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorQueriesPressure extends
		SensorSingleValueQueries<SensorDescPressure> {

	@Override
	long getSensorId() {
		return SensorDescTemperature.SENSOR_ID;
	}

	public SensorQueriesPressure(long timestamp_from, long timestamp_to,
			File file) {
		super(timestamp_from, timestamp_to, file);
	}

	@Override
	SensorDescPressure createSensorDescSingleValue(SensorData sensorData) {
		return new SensorDescPressure(sensorData);
	}

	@Override
	SensorDescPressure createDummyObject() {
		return new SensorDescPressure(0, 0);
	}

}

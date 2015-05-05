package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;

import ch.ethz.soms.nervous.android.Queries.QueryNumSingleValue;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;
import ch.ethz.soms.nervous.android.sensors.*;

public class SensorQueriesPressure extends
		QueryNumSingleValue<SensorDescPressure> {

	@Override
	public
	long getSensorId() {
		return SensorDescPressure.SENSOR_ID;
	}

	public SensorQueriesPressure(long timestamp_from, long timestamp_to,
			File file) {
		super(timestamp_from, timestamp_to, file);
	}

	@Override
	public SensorDescPressure createSensorDescSingleValue(SensorData sensorData) {
		return new SensorDescPressure(sensorData);
	}

	@Override
	public SensorDescPressure createDummyObject() {
		return new SensorDescPressure(0, 0);
	}
}

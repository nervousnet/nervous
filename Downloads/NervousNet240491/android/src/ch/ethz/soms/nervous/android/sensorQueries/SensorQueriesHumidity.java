package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;
import ch.ethz.soms.nervous.android.sensors.*;
import ch.ethz.soms.nervous.android.Queries.*;
import java.util.ArrayList;

import ch.ethz.soms.nervous.android.sensors.SensorDescHumidity;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorQueriesHumidity extends
		QueryNumSingleValue<SensorDescHumidity> {

	@Override
	public long getSensorId() {
		return SensorDescHumidity.SENSOR_ID;
	}

	public SensorQueriesHumidity(long timestamp_from, long timestamp_to,
			File file) {
		super(timestamp_from, timestamp_to, file);
	}

	@Override
	public SensorDescHumidity createSensorDescSingleValue(SensorData sensorData) {
		return new SensorDescHumidity(sensorData);
	}

	@Override
	public SensorDescHumidity createDummyObject() {
		return new SensorDescHumidity(0, 0);
	}

}

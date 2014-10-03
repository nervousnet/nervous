package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;
import java.util.ArrayList;

import android.content.IntentSender.SendIntentException;
import ch.ethz.soms.nervous.android.sensors.SensorDesc;
import ch.ethz.soms.nervous.android.sensors.SensorDescLight;
import ch.ethz.soms.nervous.android.sensors.SensorDescProximity;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorQueriesProximity extends
		SensorSingleValueQueries<SensorDescProximity> {

	@Override
	long getSensorId() {
		return SensorDescProximity.SENSOR_ID;
	}

	public SensorQueriesProximity(long timestamp_from, long timestamp_to,
			File file) {
		super(timestamp_from, timestamp_to, file);
	}

	@Override
	SensorDescProximity createSensorDesc(SensorData sensorData) {
		return new SensorDescProximity(sensorData);
	}

	@Override
	SensorDescProximity createDummyObject() {
		return new SensorDescProximity(0, 0);
	}
}

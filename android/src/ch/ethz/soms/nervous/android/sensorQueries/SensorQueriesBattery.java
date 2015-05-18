package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import ch.ethz.soms.nervous.android.Queries.QueryNumSingleValue;
import ch.ethz.soms.nervous.android.sensors.SensorDescBattery;
import ch.ethz.soms.nervous.android.sensors.SensorDescProximity;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;
import ch.ethz.soms.nervous.android.sensors.*;
import ch.ethz.soms.nervous.android.sensorQueries.*;

public class SensorQueriesBattery extends
		QueryNumSingleValue<SensorDescBattery> {

	@Override
	public
	long getSensorId() {
		return SensorDescBattery.SENSOR_ID;
	}

	public SensorQueriesBattery(long timestamp_from, long timestamp_to,
			File file) {
		super(timestamp_from, timestamp_to, file);
	}

	@Override
	public
	SensorDescBattery createSensorDescSingleValue(SensorData sensorData) {
		return new SensorDescBattery(sensorData);
	}

	@Override
	public
	SensorDescBattery createDummyObject() {
		return new SensorDescBattery(0, 0, false, false, false);
	}
}

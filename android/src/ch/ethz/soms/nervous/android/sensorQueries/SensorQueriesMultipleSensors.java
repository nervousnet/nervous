package ch.ethz.soms.nervous.android.sensorQueries;

import java.util.ArrayList;
import java.util.Vector;

import ch.ethz.soms.nervous.android.sensors.SensorDescSingleValue;

public class SensorQueriesMultipleSensors {

	public ArrayList<Vector<Float>> getKMeans(
			ArrayList<? extends SensorDescSingleValue> arrList1,
			ArrayList<? extends SensorDescSingleValue> arrList2) {

		for (SensorDescSingleValue sensorDescSingleValue : arrList1) {
			sensorDescSingleValue.getValue();
			sensorDescSingleValue.getTimestamp();
		}
		return null;
	}

}

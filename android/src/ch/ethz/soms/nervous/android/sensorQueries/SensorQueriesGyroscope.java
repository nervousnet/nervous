package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;
import java.util.ArrayList;

import ch.ethz.soms.nervous.android.sensors.SensorDescGyroscope;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorQueriesGyroscope extends SensorQueries<SensorDescGyroscope> {

	@Override
	long getSensorId() {
		return SensorDescGyroscope.SENSOR_ID;
	}

	public SensorQueriesGyroscope(long timestamp_from, long timestamp_to,
			File file) {
		super(timestamp_from, timestamp_to, file);
	}

	public SensorDescGyroscope getMaxAverageValue() {
		SensorDescGyroscope maxGyrSensDesc = null;
		float maxAverage = 0;
		for (SensorData sensorData : list) {
			SensorDescGyroscope sensDesc = new SensorDescGyroscope(
					sensorData);

			float x = Math.abs(sensDesc.getGyrX());
			float y = Math.abs(sensDesc.getGyrY());
			float z = Math.abs(sensDesc.getGyrZ());
			float newAverage = (x + y + z) / 3;
			if (newAverage > maxAverage) {
				maxAverage = newAverage;
				maxGyrSensDesc = sensDesc;
			}
		}
		return maxGyrSensDesc;
	}

	public SensorDescGyroscope getMinAverageValue() {
		SensorDescGyroscope minGyrSensDesc = null;
		float maxAverage = Float.MAX_VALUE;
		for (SensorData sensorData : list) {
			SensorDescGyroscope sensDesc = new SensorDescGyroscope(
					sensorData);

			float x = Math.abs(sensDesc.getGyrX());
			float y = Math.abs(sensDesc.getGyrY());
			float z = Math.abs(sensDesc.getGyrZ());
			float newAverage = (x + y + z) / 3;
			if (newAverage < maxAverage) {
				maxAverage = newAverage;
				minGyrSensDesc = sensDesc;
			}
		}
		return minGyrSensDesc;
	}
	
	@Override
	public ArrayList<SensorDescGyroscope> getSensorDescriptorList() {
		ArrayList<SensorDescGyroscope> descList = new ArrayList<SensorDescGyroscope>();
		for (SensorData sensorData : list) {
			descList.add(new SensorDescGyroscope(sensorData));
		}
		return descList;
	}
}

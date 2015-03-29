package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;
import java.util.ArrayList;

import ch.ethz.soms.nervous.android.sensors.SensorDescMagnetic;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorQueriesMagnetic extends SensorQueries<SensorDescMagnetic> {

	@Override
	long getSensorId() {
		return SensorDescMagnetic.SENSOR_ID;
	}

	public SensorQueriesMagnetic(long timestamp_from, long timestamp_to,
			File file) {
		super(timestamp_from, timestamp_to, file);
	}

	public SensorDescMagnetic getMaxAverageValue() {
		SensorDescMagnetic maxAccSensDesc = null;
		float maxAverage = 0;
		for (SensorData sensorData : list) {
			SensorDescMagnetic sensDesc = new SensorDescMagnetic(
					sensorData);

			float x = Math.abs(sensDesc.getMagX());
			float y = Math.abs(sensDesc.getMagY());
			float z = Math.abs(sensDesc.getMagZ());
			float newAverage = (x + y + z) / 3;
			if (newAverage > maxAverage) {
				maxAverage = newAverage;
				maxAccSensDesc = sensDesc;
			}
		}
		return maxAccSensDesc;
	}

	public SensorDescMagnetic getMinAverageValue() {
		SensorDescMagnetic minAccSensDesc = null;
		float maxAverage = Float.MAX_VALUE;
		for (SensorData sensorData : list) {
			SensorDescMagnetic sensDesc = new SensorDescMagnetic(
					sensorData);

			float x = Math.abs(sensDesc.getMagX());
			float y = Math.abs(sensDesc.getMagY());
			float z = Math.abs(sensDesc.getMagZ());
			float newAverage = (x + y + z) / 3;
			if (newAverage < maxAverage) {
				maxAverage = newAverage;
				minAccSensDesc = sensDesc;
			}
		}
		return minAccSensDesc;
	}
	
	@Override
	public ArrayList<SensorDescMagnetic> getSensorDescriptorList() {
		ArrayList<SensorDescMagnetic> descList = new ArrayList<SensorDescMagnetic>();
		for (SensorData sensorData : list) {
			descList.add(new SensorDescMagnetic(sensorData));
		}
		return descList;
	}
}

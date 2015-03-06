package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;
import java.util.ArrayList;

import ch.ethz.soms.nervous.android.sensors.SensorDescHumidity;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorQueriesHumidity extends SensorQueries<SensorDescHumidity> {

	@Override
	long getSensorId() {
		return SensorDescHumidity.SENSOR_ID;
	}

	public SensorQueriesHumidity(long timestamp_from, long timestamp_to,
			File file) {
		super(timestamp_from, timestamp_to, file);
	}

	public SensorDescHumidity getMaxValue() {
		SensorDescHumidity maxHumiditySensDesc = new SensorDescHumidity(0,
				Float.MIN_VALUE);
		for (SensorData sensorData : list) {
			SensorDescHumidity sensDesc = new SensorDescHumidity(sensorData);
			if (sensDesc.getHumidity() > maxHumiditySensDesc
					.getHumidity()) {
				maxHumiditySensDesc = sensDesc;
			}
		}
		return maxHumiditySensDesc;
	}

	public SensorDescHumidity getMinValue() {
		SensorDescHumidity minHumiditySensDesc = new SensorDescHumidity(0,
				Float.MAX_VALUE);
		for (SensorData sensorData : list) {
			SensorDescHumidity sensDesc = new SensorDescHumidity(sensorData);
			if (sensDesc.getHumidity() < minHumiditySensDesc
					.getHumidity()) {
				minHumiditySensDesc = sensDesc;
			}
		}
		return minHumiditySensDesc;
	}
	
	@Override
	public ArrayList<SensorDescHumidity> getSensorDescriptorList() {
		ArrayList<SensorDescHumidity> descList = new ArrayList<SensorDescHumidity>();
		for (SensorData sensorData : list) {
			descList.add(new SensorDescHumidity(sensorData));
		}
		return descList;
	}
	
}

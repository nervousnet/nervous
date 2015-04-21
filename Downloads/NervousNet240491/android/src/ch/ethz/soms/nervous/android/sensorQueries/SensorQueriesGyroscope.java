package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;

import ch.ethz.soms.nervous.android.sensors.*;
import ch.ethz.soms.nervous.android.Queries.*;

import java.util.ArrayList;

import ch.ethz.soms.nervous.android.sensors.SensorDescGyroscope;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorQueriesGyroscope extends QueryNumVectorValue<SensorDescGyroscopeNew> {

	@Override
	public long getSensorId() {
		return SensorDescGyroscopeNew.SENSOR_ID;
	}

	public SensorQueriesGyroscope(long timestamp_from, long timestamp_to,
			File file) {
		super(timestamp_from, timestamp_to, file);
	}


	@Override
	public SensorDescGyroscopeNew createSensorDescVectorValue(
			SensorData sensorData) {
		// TODO Auto-generated method stub
		return new SensorDescGyroscopeNew(sensorData);
	}

	@Override
	public SensorDescGyroscopeNew createDummyObject() {
		// TODO Auto-generated method stub
		return new SensorDescGyroscopeNew(0, 0, 0, 0);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	/*public SensorDescGyroscopeNew getMaxAverageValue() {
		SensorDescGyroscopeNew maxGyrSensDesc = null;
		float maxAverage = 0;
		for (SensorData sensorData : list) {
			SensorDescGyroscopeNew sensDesc = new SensorDescGyroscopeNew(
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

	public SensorDescGyroscopeNew getMinAverageValue() {
		SensorDescGyroscopeNew minGyrSensDesc = null;
		float maxAverage = Float.MAX_VALUE;
		for (SensorData sensorData : list) {
			SensorDescGyroscopeNew sensDesc = new SensorDescGyroscopeNew(
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
	public ArrayList<SensorDescGyroscopeNew> getSensorDescriptorList() {
		ArrayList<SensorDescGyroscopeNew> descList = new ArrayList<SensorDescGyroscopeNew>();
		for (SensorData sensorData : list) {
			descList.add(new SensorDescGyroscopeNew(sensorData));
		}
		return descList;
	}*/
}

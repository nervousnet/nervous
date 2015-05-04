package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;
//need to put these functions in common above(whatever is possible)
import java.util.ArrayList;

import ch.ethz.soms.nervous.android.Queries.QueryNumVectorValue;
import ch.ethz.soms.nervous.android.sensors.*;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorQueriesMagnetic extends QueryNumVectorValue<SensorDescMagneticNew> {

	@Override
	public long getSensorId() {
		return SensorDescMagneticNew.SENSOR_ID;
	}

	public SensorQueriesMagnetic(long timestamp_from, long timestamp_to,
			File file) {
		super(timestamp_from, timestamp_to, file);
	}

	@Override
	public SensorDescMagneticNew createSensorDescVectorValue(
			SensorData sensorData) {
		// TODO Auto-generated method stub
		return new SensorDescMagneticNew(sensorData);
	}

	@Override
	public SensorDescMagneticNew createDummyObject() {
		// TODO Auto-generated method stub
		return new SensorDescMagneticNew(0, 0, 0, 0);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*public SensorDescAccelerometerNew getMaxAverageValue() {
	SensorDescAccelerometerNew maxAccSensDesc = null;
	float maxAverage = 0;
	for (SensorData sensorData : list) {
		SensorDescAccelerometerNew sensDesc = new SensorDescAccelerometerNew(
				sensorData);

		float x = Math.abs(sensDesc.getAccX());
		float y = Math.abs(sensDesc.getAccY());
		float z = Math.abs(sensDesc.getAccZ());
		float newAverage = (x + y + z) / 3;
		if (newAverage > maxAverage) {
			maxAverage = newAverage;
			maxAccSensDesc = sensDesc;
		}
	}
	return maxAccSensDesc;
}

public SensorDescAccelerometerNew getMinAverageValue() {
	SensorDescAccelerometerNew minAccSensDesc = null;
	float maxAverage = Float.MAX_VALUE;
	for (SensorData sensorData : list) {
		SensorDescAccelerometerNew sensDesc = new SensorDescAccelerometerNew(
				sensorData);

		float x = Math.abs(sensDesc.getAccX());
		float y = Math.abs(sensDesc.getAccY());
		float z = Math.abs(sensDesc.getAccZ());
		float newAverage = (x + y + z) / 3;
		if (newAverage < maxAverage) {
			maxAverage = newAverage;
			minAccSensDesc = sensDesc;
		}
	}
	return minAccSensDesc;
}

@Override
public ArrayList<SensorDescAccelerometerNew> getSensorDescriptorList() {
	ArrayList<SensorDescAccelerometerNew> descList = new ArrayList<SensorDescAccelerometerNew>();
	for (SensorData sensorData : list) {
		descList.add(new SensorDescAccelerometerNew(sensorData));
	}
	return descList;
}
*/
	
}

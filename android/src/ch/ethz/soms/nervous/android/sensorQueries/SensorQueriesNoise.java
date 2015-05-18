package ch.ethz.soms.nervous.android.sensorQueries;
//Please only use the query to obtain data withing a certain timestamp,and get the descriptors
// do not use this with other methods!!!
import java.io.File;

import ch.ethz.soms.nervous.android.Queries.QueryNumVectorValue;
import ch.ethz.soms.nervous.android.sensors.SensorDescNoise;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorQueriesNoise extends QueryNumVectorValue<SensorDescNoise> {

	public SensorQueriesNoise(long timestamp_from, long timestamp_to, File file) {
		super(timestamp_from, timestamp_to, file);
		// TODO Auto-generated constructor stub
	}
	
	public long getSensorId(){
		return SensorDescNoise.SENSOR_ID;
	}
	
	public SensorDescNoise createDummyObject(){
		float[] b = new float[1000];	//maximum number of bands??
		return new SensorDescNoise(0,0,0,b);
	}

	@Override
	public SensorDescNoise createSensorDescVectorValue(SensorData sensorData) {
		// TODO Auto-generated method stub
		return new SensorDescNoise(sensorData);
	}

}

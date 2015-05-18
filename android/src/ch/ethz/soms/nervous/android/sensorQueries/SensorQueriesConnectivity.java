package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;
import java.util.ArrayList;

import ch.ethz.soms.nervous.android.Queries.Query;
import ch.ethz.soms.nervous.android.Queries.QueryNumVectorValue;
import ch.ethz.soms.nervous.android.sensors.SensorDesc;
import ch.ethz.soms.nervous.android.sensors.SensorDescConnectivity;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;
// only uses few functions of query NOT ALL
//does not make sense otherwise
// find way to block other usage
public class SensorQueriesConnectivity extends QueryNumVectorValue<SensorDescConnectivity> {
	
	public long getSensorId(){
		
		return SensorDescConnectivity.SENSOR_ID;
	}
	
	public SensorQueriesConnectivity(long timestamp_from,long timestamp_to,File file)
	{
		super(timestamp_from, timestamp_to, file);
	}
	
	public SensorDescConnectivity createDummyObject()
	{
		return new SensorDescConnectivity(0,false,0,false,null,0,null);
	}

	@Override
	public SensorDescConnectivity createSensorDescVectorValue(
			SensorData sensorData) {
		// TODO Auto-generated method stub
		return new SensorDescConnectivity(sensorData);
	}

	
	//Please test this function !!


}

package ch.ethz.soms.nervous.android.sensorQueries;

import android.util.Log;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ch.ethz.soms.nervous.android.sensors.SensorDescAccelerometer;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorQueriesAccelerometer extends SensorQueries<SensorDescAccelerometer> {

	@Override
	long getSensorId() {
		return SensorDescAccelerometer.SENSOR_ID;
	}

	public SensorQueriesAccelerometer(long timestamp_from, long timestamp_to,
			File file) {
		super(timestamp_from, timestamp_to, file);
	}

    public ArrayList<SensorDescAccelerometer> getAllReadings() {
        ArrayList<SensorDescAccelerometer> allReadings = new ArrayList<SensorDescAccelerometer>();
        for (SensorData sensorData : list) {
            SensorDescAccelerometer sensDesc = new SensorDescAccelerometer(
                    sensorData);
            Date date = new Date(sensDesc.getTimestamp());
            Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
            Log.i("Reading", format.format(date));
            allReadings.add(sensDesc);
        }
        return allReadings;
    }

    public SensorDescAccelerometer getMaxAverageValue() {
		SensorDescAccelerometer maxAccSensDesc = null;
		float maxAverage = 0;
		for (SensorData sensorData : list) {
			SensorDescAccelerometer sensDesc = new SensorDescAccelerometer(
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

	public SensorDescAccelerometer getMinAverageValue() {
		SensorDescAccelerometer minAccSensDesc = null;
		float maxAverage = Float.MAX_VALUE;
		for (SensorData sensorData : list) {
			SensorDescAccelerometer sensDesc = new SensorDescAccelerometer(
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
	public ArrayList<SensorDescAccelerometer> getSensorDescriptorList() {
		ArrayList<SensorDescAccelerometer> descList = new ArrayList<SensorDescAccelerometer>();
		for (SensorData sensorData : list) {
			descList.add(new SensorDescAccelerometer(sensorData));
		}
		return descList;
	}
}

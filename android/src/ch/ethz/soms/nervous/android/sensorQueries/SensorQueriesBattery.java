package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;
import java.util.List;

import ch.ethz.soms.nervous.android.sensors.SensorDesc;
import ch.ethz.soms.nervous.android.sensors.SensorDescBattery;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorQueriesBattery extends SensorQueries {

	@Override
	long getSensorId() {
		return SensorDescBattery.SENSOR_ID;
	}

	public SensorQueriesBattery(long timestamp_from, long timestamp_to,
			File file) {
		super(timestamp_from, timestamp_to, file);
	}

	public SensorDescBattery getMaxValue() {
		SensorDescBattery maxBattSensDesc = new SensorDescBattery(0,
				Float.MIN_VALUE, false, false, false);
		for (SensorData sensorData : list) {
			SensorDescBattery sensDesc = new SensorDescBattery(sensorData);
			if (sensDesc.getBatteryPercent() > maxBattSensDesc
					.getBatteryPercent()) {
				maxBattSensDesc = sensDesc;
			}
		}
		return maxBattSensDesc;
	}

	public SensorDescBattery getMinValue() {
		SensorDescBattery minBattSensDesc = new SensorDescBattery(0,
				Float.MAX_VALUE, false, false, false);
		for (SensorData sensorData : list) {
			SensorDescBattery sensDesc = new SensorDescBattery(sensorData);
			if (sensDesc.getBatteryPercent() < minBattSensDesc
					.getBatteryPercent()) {
				minBattSensDesc = sensDesc;
			}
		}
		return minBattSensDesc;
	}
}

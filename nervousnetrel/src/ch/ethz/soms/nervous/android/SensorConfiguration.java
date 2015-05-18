package ch.ethz.soms.nervous.android;

import ch.ethz.soms.nervous.utils.NervousStatics;
import android.content.Context;
import android.content.SharedPreferences;

public class SensorConfiguration {

	private Context context;

	private static SensorConfiguration sensorConfiguration;

	static SensorConfiguration getInstance(Context context) {
		if (sensorConfiguration == null) {
			sensorConfiguration = new SensorConfiguration(context);
		}
		return sensorConfiguration;
	}

	private SensorConfiguration(Context context) {
		this.context = context;
	}

	public SensorCollectStatus getInitialSensorCollectStatus(long sensorID) {
		SharedPreferences settings = context.getSharedPreferences(NervousStatics.SENSOR_PREFS, 0);
		boolean doMeasure = settings.getBoolean(Long.toHexString(sensorID) + "_doMeasure", true);
		boolean doShare = settings.getBoolean(Long.toHexString(sensorID) + "_doShare", true);
		
		int measureInterval = (int)context.getSharedPreferences(NervousStatics.SENSOR_FREQ, 0)
			.getFloat(Long.toHexString(sensorID) + "_freqValue", 30) * 1000;
//		int measureInterval = settings.getInt(Long.toHexString(sensorID) + "_measureInterval", 30 * 1000);
		
		long measureDuration = settings.getLong(Long.toHexString(sensorID) + "_measureDuration", -1);
		int collectAmount = settings.getInt(Long.toHexString(sensorID) + "_collectAmount", 1);
		SensorCollectStatus scs = new SensorCollectStatus(sensorID, doMeasure, doShare, measureInterval, measureDuration, collectAmount);
		return scs;
	}

}

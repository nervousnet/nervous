package ch.ethz.soms.nervous.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SensorConfiguration {

	private final static String SENSOR_PREFS = "SensorPreferences";

	private Context context;
	private int sensorRound = 0;

	private static SensorConfiguration sensorConfiguration;

	static SensorConfiguration getInstance(Context context) {
		if (sensorConfiguration == null) {
			sensorConfiguration = new SensorConfiguration(context);
		}
		return sensorConfiguration;
	}

	public synchronized void load() {
		SharedPreferences settings = context.getSharedPreferences(SENSOR_PREFS, 0);
		sensorRound = settings.getInt("sensorRound", 0);
	}

	private SensorConfiguration(Context context) {
		this.context = context;
		load();
	}

	public SensorCollectStatus getInitialSensorCollectStatus(long sensorID, int serviceRound) {
		SharedPreferences settings = context.getSharedPreferences(SENSOR_PREFS, 0);
		boolean doMeasure = settings.getBoolean(Long.toHexString(sensorID) + "_doMeasure", true);
		boolean doShare = settings.getBoolean(Long.toHexString(sensorID) + "_doShare", true);
		int measureFrequency = settings.getInt(Long.toHexString(sensorID) + "_measureFrequency", 1);
		long measureDuration = settings.getLong(Long.toHexString(sensorID) + "_measureDuration", -1);
		int collectAmount = settings.getInt(Long.toHexString(sensorID) + "_collectAmount", 1);
		SensorCollectStatus scs = new SensorCollectStatus(doMeasure, doShare, measureFrequency, measureDuration, collectAmount);
		return scs;
	}

	public synchronized int getServiceRound() {
		return sensorRound;
	}

	public synchronized void increaseServiceRound() {
		sensorRound += 1;
		SharedPreferences settings = context.getSharedPreferences(SENSOR_PREFS, 0);
		Editor editor = settings.edit();
		editor.putInt("sensorRound", sensorRound);
		editor.commit();
	}

}

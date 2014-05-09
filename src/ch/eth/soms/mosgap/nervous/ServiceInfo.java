package ch.eth.soms.mosgap.nervous;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;
import android.util.Log;

public class ServiceInfo {

	private SharedPreferences preferences;

	private static final String DEBUG_TAG = "ServiceInfo";

	public ServiceInfo(Context context) {
		preferences = context.getSharedPreferences("ServiceInfo", Context.MODE_PRIVATE);
	}

	public void clean() {
		Editor editor = preferences.edit();
    	long now = System.currentTimeMillis();
		editor.putLong("first", now);
		editor.putLong("last", 0);
		editor.putInt("amountOfFrames", 0);
		editor.commit();
	}
	
	
	
	public void frameAdded() {
		Editor editor = preferences.edit();
		int amount = preferences.getInt("amountOfFrames", 0) + 1;
		editor.putInt("amountOfFrames", amount);
		editor.commit();
	}

	public int getAmountOfFrames() {
		return preferences.getInt("amountOfFrames", 0);
	}

	public String getTimeOfFirstFrame() {
		
		long first = preferences.getLong("first", 0);		
        Date resultdate = new Date(first);
        return SimpleDateFormat.getDateTimeInstance().format(resultdate);
	}

	public void setTimeOfLastFrame() {

		Editor editor = preferences.edit();
		long time = SystemClock.elapsedRealtime();
		editor.putLong("last", time);

		if (preferences.getLong("first", 0) == 0) {
			editor.putLong("first", SystemClock.elapsedRealtime());
			Log.d(DEBUG_TAG, "Time of first frame has been set.");
		}
		frameAdded();
		editor.commit();
	}

	public boolean serviceIsRunning() {

		long last = preferences.getLong("last", 0);
		long now = SystemClock.elapsedRealtime();
		double passedTime = Math.abs(((double)(now - last)) / 1000.d);
		if (last != 0 && passedTime < 60.d) {
			return true;
		} else {
			return false;
		}
	}
}

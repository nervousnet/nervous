package ch.ethz.soms.nervous.android;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class UploadService extends Service {

	private static final String DEBUG_TAG = "UploadService";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
		boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

		if (isConnected && isWiFi) {

		}

		Log.d(DEBUG_TAG, "Service execution started");
		return START_STICKY;
	}

	@Override
	public void onCreate() {
		// Do nothing

	}

	@Override
	public void onDestroy() {
		// Do nothing
	}

	/**
	 * Asynchronous task to write to the log file
	 */
	private class UploadServiceTask extends AsyncTask<SensorFrame, Void, Void> {

		@Override
		protected Void doInBackground(SensorFrame... frames) {
			return null;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}

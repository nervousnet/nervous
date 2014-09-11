package ch.ethz.soms.nervous.android;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

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

		// Conditions subject to change to fit app purpose and user settings
		if (isConnected && isWiFi) {
			UploadTask task = new UploadTask();
			task.execute();
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

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public class UploadTask extends AsyncTask<SensorDesc, Void, Void> {

		@Override
		protected Void doInBackground(SensorDesc... params) {
			try {
				Socket socket = new Socket("127.0.0.1", 25600);
				OutputStream os = socket.getOutputStream();
				//sensorupload.writeDelimitedTo(os);
				os.flush();
				os.close();
				socket.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Stop the service
			stopSelf();
			return null;
		}

	}
}

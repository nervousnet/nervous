package ch.ethz.soms.nervous.android;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import ch.ethz.soms.nervous.android.SensorService.SensorBinder;
import ch.ethz.soms.nervous.android.sensors.SensorDesc;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.Builder;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;
import ch.ethz.soms.nervous.vm.NervousVM;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class UploadService extends Service {

	private static final String DEBUG_TAG = "UploadService";

	private final IBinder mBinder = new UploadBinder();

	public class UploadBinder extends Binder {
		UploadService getService() {
			return UploadService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

		// Conditions subject to change to fit app purpose and user settings
		if (isConnected) {
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
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class UploadTask extends AsyncTask<SensorDesc, Void, Void> {

		@Override
		protected Void doInBackground(SensorDesc... params) {
			try {
				NervousVM nvm = NervousVM.getInstance(getApplicationContext().getFilesDir());
				Socket socket = new Socket("192.168.1.53", 25600);
				OutputStream os = socket.getOutputStream();
				for (long i = 0x0; i < 0xC; i++) {
					Builder sub = SensorUpload.newBuilder();
					sub.setHuuid(nvm.getUUID().getMostSignificantBits());
					sub.setLuuid(nvm.getUUID().getLeastSignificantBits());
					sub.setSensorId(i);
					// Upload everything with "timestamp" > "last uploaded timestamp"
					List<SensorData> sensorDataList = nvm.retrieve(i, nvm.getLastUploadedTimestamp(i) + 1, Long.MAX_VALUE);
					// Only upload if there is actual data
					if (sensorDataList != null && sensorDataList.size() > 0) {
						sub.addAllSensorValues(sensorDataList);
						sub.setUploadTime(System.currentTimeMillis());
						sub.build().writeDelimitedTo(os);
						nvm.setLastUploadedTimestamp(i, sensorDataList.get(sensorDataList.size() - 1).getRecordTime());
					}
				}
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

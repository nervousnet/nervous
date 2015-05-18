package ch.ethz.soms.nervous.android;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.EditText;
import ch.ethz.soms.nervous.android.sensors.SensorDesc;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.Builder;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;
import ch.ethz.soms.nervous.utils.NervousStatics;
import ch.ethz.soms.nervous.vm.NervousVM;

public class UploadService extends Service {

	private PowerManager.WakeLock wakeLock;

	private SharedPreferences uploadPreferences;	

	private String nervousIP = "inn.ac";
	private int nervousPort = 25600;
	
	private String additionalIP = null;
	private int additionalPort = -1;
	
	private static final String LOG_TAG = UploadService.class.getSimpleName();

	private final IBinder mBinder = new UploadBinder();

	private HandlerThread hthread;

	public class UploadBinder extends Binder {
		UploadService getService() {
			return UploadService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {								

		uploadPreferences = getSharedPreferences(NervousStatics.UPLOAD_PREFS, 0);		
		
		final int delay = uploadPreferences.getInt("UploadDelay", 10 * 1000);
		final int period = uploadPreferences.getInt("UploadFrequency", 10 * 1000);

		additionalIP = uploadPreferences.getString("serverIP", null);
		additionalPort = uploadPreferences.getInt("serverPort", -1);
		
		final Handler handler = new Handler(hthread.getLooper());

		final Runnable run = new Runnable() {
			@Override
			public void run() {
				Log.d(LOG_TAG, "Upload started");
				ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
				boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

				// Conditions subject to change to fit app purpose and user settings
				if (isConnected) {
					
					try {
						Socket nervousServer = new Socket(nervousIP, nervousPort);
						UploadTask task = new UploadTask(nervousServer);
						task.execute();
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println(additionalIP + " " + additionalPort);
					if (additionalIP != null && additionalPort != -1) {
						try {						
							Socket additionalServer = new Socket(additionalIP, additionalPort);
							UploadTask task = new UploadTask(additionalServer);
							task.execute();						
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				handler.postDelayed(this, period);
			}
		};

		handler.postDelayed(run, delay);

		Log.d(LOG_TAG, "Service execution started");
		return START_STICKY;
	}

	@Override
	public void onCreate() {
		// Prepare the wakelock
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOG_TAG);
		if (!wakeLock.isHeld()) {
			wakeLock.acquire();
		}
		hthread = new HandlerThread("HandlerThread");
		hthread.start();
	}

	@Override
	public void onDestroy() {
		hthread.quit();
		if (wakeLock.isHeld()) {
			wakeLock.release();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class UploadTask extends AsyncTask<SensorDesc, Void, Void> {

		Socket socket;
		public UploadTask(Socket socket) {
			this.socket = socket;
		}
		
		@Override
		protected Void doInBackground(SensorDesc... params) {
			final SharedPreferences settings = getApplicationContext().getSharedPreferences(NervousStatics.SENSOR_PREFS, 0);
			try {
				NervousVM nvm = NervousVM.getInstance(getApplicationContext().getFilesDir());				
				OutputStream os = socket.getOutputStream();
				for (long i = 0x0; i < 0xC; i++) {
					boolean doShare = settings.getBoolean(Long.toHexString(i) + "_doShare", true);
					if (doShare) {
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
				}
				os.flush();
				os.close();
				socket.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public static boolean isServiceRunning(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (UploadService.class.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static void startService(Context context) {
		Intent sensorIntent = new Intent(context, UploadService.class);
		context.startService(sensorIntent);
	}

	public static void stopService(Context context) {
		Intent sensorIntent = new Intent(context, UploadService.class);
		context.stopService(sensorIntent);
	}

}

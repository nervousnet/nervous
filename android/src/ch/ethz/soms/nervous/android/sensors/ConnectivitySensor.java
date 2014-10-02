package ch.ethz.soms.nervous.android.sensors;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class ConnectivitySensor {

	private Context context;

	public ConnectivitySensor(Context context) {
		this.context = context;
	}

	private List<ConnectivityListener> listenerList = new ArrayList<ConnectivityListener>();

	public void addListener(ConnectivityListener listener) {
		listenerList.add(listener);
	}

	public interface ConnectivityListener {
		public void connectivitySensorDataReady(long timestamp, boolean isConnected, int networkType, boolean isRoaming);
	}

	public void dataReady(long timestamp, boolean isConnected, int networkType, boolean isRoaming) {
		for (ConnectivityListener listener : listenerList) {
			listener.connectivitySensorDataReady(timestamp, isConnected, networkType, isRoaming);
		}
	}

	public class ConnectivityTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
			int networkType = -1;
			boolean isRoaming = false;
			if (isConnected) {
				networkType = activeNetwork.getType();
				isRoaming = activeNetwork.isRoaming();
			}
			dataReady(System.currentTimeMillis(), isConnected, networkType, isRoaming);
			return null;
		}
	}

	public void start() {
		new ConnectivityTask().execute();
	}

}

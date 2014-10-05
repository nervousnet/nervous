package ch.ethz.soms.nervous.android.sensors;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.soms.nervous.utils.ValueFormatter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;

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
		public void connectivitySensorDataReady(long timestamp, boolean isConnected, int networkType, boolean isRoaming, String wifiHashId, int wifiStrength, String mobileHashId);
	}

	public void dataReady(long timestamp, boolean isConnected, int networkType, boolean isRoaming, String wifiHashId, int wifiStrength, String mobileHashId) {
		for (ConnectivityListener listener : listenerList) {
			listener.connectivitySensorDataReady(timestamp, isConnected, networkType, isRoaming, wifiHashId, wifiStrength, mobileHashId);
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

			String wifiHashId = "";
			int wifiStrength = Integer.MIN_VALUE;

			if (networkType == ConnectivityManager.TYPE_WIFI) {
				WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wi = wm.getConnectionInfo();
				StringBuilder wifiInfoBuilder = new StringBuilder();
				wifiInfoBuilder.append(wi.getBSSID());
				wifiInfoBuilder.append(wi.getSSID());
				try {
					MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
					messageDigest.update(wifiInfoBuilder.toString().getBytes());
					wifiHashId = new String(messageDigest.digest());
				} catch (NoSuchAlgorithmException e) {
				}
				wifiStrength = wi.getRssi();
			}

			byte[] cdmaHashId = new byte[32];
			byte[] lteHashId = new byte[32];
			byte[] gsmHashId = new byte[32];
			byte[] wcdmaHashId = new byte[32];

			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

			List<CellInfo> cis = tm.getAllCellInfo();
			for (CellInfo ci : cis) {
				if (ci.isRegistered()) {
					if (ci instanceof CellInfoCdma) {
						CellInfoCdma cic = (CellInfoCdma) ci;
						StringBuilder cicBuilder = new StringBuilder();
						cicBuilder.append(ValueFormatter.leadingZeroHexUpperString(cic.getCellIdentity().getSystemId()));
						cicBuilder.append(ValueFormatter.leadingZeroHexUpperString(cic.getCellIdentity().getNetworkId()));
						cicBuilder.append(ValueFormatter.leadingZeroHexUpperString(cic.getCellIdentity().getBasestationId()));
						MessageDigest messageDigest;
						try {
							messageDigest = MessageDigest.getInstance("SHA-256");
							messageDigest.update(cicBuilder.toString().getBytes());
							cdmaHashId = messageDigest.digest();
						} catch (NoSuchAlgorithmException e) {
						}
					}
					if (ci instanceof CellInfoGsm) {
						CellInfoGsm cic = (CellInfoGsm) ci;
						StringBuilder cicBuilder = new StringBuilder();
						cicBuilder.append(ValueFormatter.leadingZeroHexUpperString(cic.getCellIdentity().getMcc()));
						cicBuilder.append(ValueFormatter.leadingZeroHexUpperString(cic.getCellIdentity().getMnc()));
						cicBuilder.append(ValueFormatter.leadingZeroHexUpperString(cic.getCellIdentity().getCid()));
						MessageDigest messageDigest;
						try {
							messageDigest = MessageDigest.getInstance("SHA-256");
							messageDigest.update(cicBuilder.toString().getBytes());
							gsmHashId = messageDigest.digest();
						} catch (NoSuchAlgorithmException e) {
						}
					}
					if (ci instanceof CellInfoLte) {
						CellInfoLte cic = (CellInfoLte) ci;
						StringBuilder cicBuilder = new StringBuilder();
						cicBuilder.append(ValueFormatter.leadingZeroHexUpperString(cic.getCellIdentity().getMcc()));
						cicBuilder.append(ValueFormatter.leadingZeroHexUpperString(cic.getCellIdentity().getMnc()));
						cicBuilder.append(ValueFormatter.leadingZeroHexUpperString(cic.getCellIdentity().getCi()));
						MessageDigest messageDigest;
						try {
							messageDigest = MessageDigest.getInstance("SHA-256");
							messageDigest.update(cicBuilder.toString().getBytes());
							lteHashId = messageDigest.digest();
						} catch (NoSuchAlgorithmException e) {
						}
					}
					if (ci instanceof CellInfoWcdma) {
						CellInfoWcdma cic = (CellInfoWcdma) ci;
						StringBuilder cicBuilder = new StringBuilder();
						cicBuilder.append(ValueFormatter.leadingZeroHexUpperString(cic.getCellIdentity().getMcc()));
						cicBuilder.append(ValueFormatter.leadingZeroHexUpperString(cic.getCellIdentity().getMnc()));
						cicBuilder.append(ValueFormatter.leadingZeroHexUpperString(cic.getCellIdentity().getCid()));
						MessageDigest messageDigest;
						try {
							messageDigest = MessageDigest.getInstance("SHA-256");
							messageDigest.update(cicBuilder.toString().getBytes());
							wcdmaHashId = messageDigest.digest();
						} catch (NoSuchAlgorithmException e) {
						}
					}
				}
			}

			StringBuilder mobileHashBuilder = new StringBuilder();
			mobileHashBuilder.append(new String(cdmaHashId));
			mobileHashBuilder.append(new String(lteHashId));
			mobileHashBuilder.append(new String(gsmHashId));
			mobileHashBuilder.append(new String(wcdmaHashId));
			
			dataReady(System.currentTimeMillis(), isConnected, networkType, isRoaming, wifiHashId, wifiStrength, mobileHashBuilder.toString());
			return null;
		}
	}

	public void start() {
		new ConnectivityTask().execute();
	}

}

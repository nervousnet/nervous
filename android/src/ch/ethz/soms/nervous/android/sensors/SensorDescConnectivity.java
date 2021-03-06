package ch.ethz.soms.nervous.android.sensors;

import java.util.ArrayList;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescConnectivity extends SensorDescVectorValue {

	public static final long SENSOR_ID = 0x000000000000000AL;

	private final boolean isConnected;
	private final int networkType;
	private final boolean isRoaming;
	private final String wifiHashId;
	private final int wifiStrength;
	private final String mobileHashId;

	public SensorDescConnectivity(final long timestamp, final boolean isConnected, final int networkType, final boolean isRoaming, String wifiHashId, int wifiStrength, String mobileHashId) {
		super(timestamp);
		this.isConnected = isConnected;
		this.networkType = networkType;
		this.isRoaming = isRoaming;
		this.wifiHashId = wifiHashId;
		this.wifiStrength = wifiStrength;
		this.mobileHashId = mobileHashId;
	}

	public SensorDescConnectivity(SensorData sensorData) {
		super(sensorData);
		this.isConnected = sensorData.getValueBool(0);
		this.networkType = sensorData.getValueInt32(0);
		this.isRoaming = sensorData.getValueBool(1);
		this.wifiHashId = sensorData.getValueString(0);
		this.wifiStrength = sensorData.getValueInt32(1);
		this.mobileHashId = sensorData.getValueString(1);
	}

	public boolean isConnected() {
		return isConnected;
	}

	public int getNetworkType() {
		return networkType;
	}

	public boolean isRoaming() {
		return isRoaming;
	}

	@Override
	public long getSensorId() {
		return SENSOR_ID;
	}
	
	public String getWifiHashId()
	{
		return wifiHashId;
	}
	
	public int getWifiStrength()
	{
		return wifiStrength;
	}
	
	public String getMobileHashId()
	{
		return mobileHashId;
	}
	@Override
	public SensorData toProtoSensor() {
		SensorData.Builder sdb = SensorData.newBuilder();
		sdb.setRecordTime(getTimestamp());
		sdb.addValueBool(isConnected);
		sdb.addValueInt32(networkType);
		sdb.addValueBool(isRoaming);
		sdb.addValueString(wifiHashId);
		sdb.addValueInt32(wifiStrength);
		sdb.addValueString(mobileHashId);
		return sdb.build();
	}

	@Override
	public ArrayList<Float> getValue() {//convert boolean and string to float!!
		// TODO Auto-generated method stub
		ArrayList<Float> arrayList = new ArrayList<Float>();
		int conn=0;
		boolean temp = isConnected();
		if(temp == true)
			conn=1;
		arrayList.add((float) conn);
		arrayList.add((float) getNetworkType());
		int roam =0;
		temp = isRoaming();
		if(temp == true)
			roam = 1;
		arrayList.add((float) roam);
		String moo = getWifiHashId();
		
		arrayList.add(Float.valueOf(moo));
		arrayList.add((float) getWifiStrength());
		moo = getMobileHashId();
		arrayList.add(Float.valueOf(moo));
		
		return arrayList;
	}
	
	

}

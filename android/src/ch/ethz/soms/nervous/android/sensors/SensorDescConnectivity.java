package ch.ethz.soms.nervous.android.sensors;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescConnectivity extends SensorDesc {

	public static final long SENSOR_ID = 0x000000000000000AL;
	
	private final boolean isConnected;
	private final int networkType;
	private final boolean isRoaming;
	
	public SensorDescConnectivity(final long timestamp, final boolean isConnected, final int networkType, final boolean isRoaming) {
		super(timestamp);
		this.isConnected = isConnected;
		this.networkType = networkType;
		this.isRoaming = isRoaming;
	}

	
	public SensorDescConnectivity(SensorData sensorData)
	{
		super(sensorData);
		this.isConnected = sensorData.getValueBool(0);
		this.networkType = sensorData.getValueInt32(0);
		this.isRoaming = sensorData.getValueBool(1);
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
	public long getSensorIdentifier() {
		return SENSOR_ID;
	}

	@Override
	public SensorData toProtoSensor() {
		SensorData.Builder sdb = SensorData.newBuilder();
		sdb.setRecordTime(getTimestamp());
		sdb.addValueBool(isConnected);
		sdb.addValueInt32(networkType);
		sdb.addValueBool(isRoaming);
		return sdb.build();
	}

}

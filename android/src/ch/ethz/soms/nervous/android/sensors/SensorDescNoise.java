package ch.ethz.soms.nervous.android.sensors;

import java.util.ArrayList;
import java.util.List;

import android.media.AudioFormat;
import android.media.AudioRecord;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorDescNoise extends SensorDesc {

	public static final long SENSOR_ID = 0x0000000000000008L;

	public static final int SAMPPERSEC = 8000;
	public static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	public static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;


	public SensorDescNoise(SensorData sensorData) {
		super(sensorData);
		// TODO Auto-generated constructor stub
	}

	public SensorDescNoise(long timestamp) {
		super(timestamp);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getSensorIdentifier() {
		return SENSOR_ID;
	}

	@Override
	public SensorData toProtoSensor() {
		// TODO Auto-generated method stub
		return null;
	}

}

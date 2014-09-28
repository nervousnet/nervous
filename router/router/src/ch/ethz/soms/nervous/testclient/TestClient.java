package ch.ethz.soms.nervous.testclient;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class TestClient {

	public static final long HUUID = 0x11223344;
	public static final long LUUID = 0x55667788;
	public static final long SENSOR_ID = 0; // Currently accelerometer
	public static final int VALUE_COUNT = 10;

	public static void main(String args[]) {

		SensorUpload.Builder sub = SensorUpload.newBuilder();
		sub.setHuuid(HUUID);
		sub.setLuuid(LUUID);
		sub.setSensorId(SENSOR_ID);

		Long baseTime = Calendar.getInstance().getTimeInMillis();

		for (int i = 0; i < VALUE_COUNT; i++) {
			SensorData.Builder sdb = SensorData.newBuilder();
			// Equispaced measurement plots
			sdb.setRecordTime(baseTime + i);
			// Create some random test data
			sdb.addValueFloat(((float) Math.random()));
			sdb.addValueFloat(((float) 0.f));
			sdb.addValueFloat(((float) Math.random()));

			sub.addSensorValues(sdb.build());
		}

		sub.setUploadTime(Calendar.getInstance().getTimeInMillis());
		SensorUpload sensorupload = sub.build();

		// Upload test

		try {
			Socket socket = new Socket("127.0.0.1", 25600);
			OutputStream os = socket.getOutputStream();
			sensorupload.writeDelimitedTo(os);
			os.flush();
			os.close();
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

package ch.ethz.soms.nervous.android.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import ch.ethz.soms.nervous.android.sensors.SensorDesc;
import ch.ethz.soms.nervous.android.sensors.SensorDescDummy;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;
import ch.ethz.soms.nervous.vm.NervousVM;

public class PerformanceTestTask2 extends AsyncTask<Void, Void, Void> {

	private static final String LOG_TAG = PerformanceTestTask2.class.getSimpleName();
	//private static final int TEST_COUNT = 24 * 60 * 60 / 5;
	//private static final int TEST_SENSOR_COUNT = 20;
	//private static final int REPETITIONS = 10;
	
	private static final int TEST_COUNT = 1000;
	private static final int TEST_SENSOR_COUNT = 3;
	private static final int REPETITIONS = 2;
	
	private Context context;

	public PerformanceTestTask2(Context context) {
		this.context = context;
	}

	@Override
	protected Void doInBackground(Void... params) {
		NervousVM vm = NervousVM.getInstance(context.getFilesDir());

		PerformanceLog plog = new PerformanceLog();

		long tick, tock;
		double time;

		// Reset
		for (int sid = 0; sid < TEST_SENSOR_COUNT; sid++) {
			vm.deleteSensor(SensorDescDummy.SENSOR_ID - sid);
		}

		// Test repetitions loop
		for (int test = 0; test < REPETITIONS; test++) {

			plog.log("RUN: " + String.valueOf(test) + " BATTERY BEFORE INSERT: " + String.valueOf(getBatteryLevel()) + " ms");
			// Test run (insert)
			tick = System.currentTimeMillis();
			for (int i = 0; i < TEST_COUNT; i++) {
				for (int sid = 0; sid < TEST_SENSOR_COUNT; sid++) {
					SensorDesc sensorDesc = new SensorDescDummy(i, true, 12345, 13.5f, 12345L, 13.5d, "TEST");
					vm.storeSensor(SensorDescDummy.SENSOR_ID - sid, sensorDesc.toProtoSensor());
				}
			}
			tock = System.currentTimeMillis();
			time = tock - tick;
			plog.log("RUN: " + String.valueOf(test) + " INSERT TIME: " + String.valueOf(time) + " ms");

			plog.log("RUN: " + String.valueOf(test) + " BATTERY BEFORE READ THROUGH: " + String.valueOf(getBatteryLevel()) + " ms");
			// Test run (read through)
			tick = System.currentTimeMillis();
			for (int sid = 0; sid < TEST_SENSOR_COUNT; sid++) {
				List<SensorData> peek = vm.retrieve(SensorDescDummy.SENSOR_ID - sid, 0, Long.MAX_VALUE);
			}
			tock = System.currentTimeMillis();
			time = tock - tick;
			plog.log("RUN: " + String.valueOf(test) + " RETRIEVE TIME: " + String.valueOf(time) + " ms");

			// Storage usage
			long storageSizeSum = 0;
			for (int sid = 0; sid < TEST_SENSOR_COUNT; sid++) {
				long[] storageSize = vm.getSensorStorageSize(SensorDescDummy.SENSOR_ID - sid);
				storageSizeSum = storageSize[0] + storageSize[1];
			}
			plog.log("RUN: " + String.valueOf(test) + " STORAGE USAGE: " + String.valueOf(storageSizeSum) + " B");
			plog.log("RUN: " + String.valueOf(test) + " BATTERY AFTER RUN: " + String.valueOf(getBatteryLevel()) + " ms");

			// Reset
			for (int sid = 0; sid < TEST_SENSOR_COUNT; sid++) {
				vm.deleteSensor(SensorDescDummy.SENSOR_ID - sid);
			}
		}

		plog.close();

		return null;
	}

	private class PerformanceLog {
		BufferedWriter bw;

		public PerformanceLog() {
			String path = context.getExternalFilesDir(null).getPath() + File.separator + "NervousPerformanceLog.txt";
			File file = new File(path);
			try {
				bw = new BufferedWriter(new FileWriter(file));
			} catch (IOException e) {
			}

		}

		public void log(String text) {
			try {
				bw.write(text + "\n");
				bw.flush();
			} catch (IOException e) {
			}
		}

		public void close() {
			try {
				bw.flush();
				bw.close();
			} catch (IOException e) {
			}
		}
	}

	public float getBatteryLevel() {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = context.registerReceiver(null, ifilter);
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		float batteryPct = level / (float) scale;
		return batteryPct;
	}
}

package ch.ethz.soms.nervous.android.test;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.soms.nervous.android.sensors.SensorDesc;
import ch.ethz.soms.nervous.android.sensors.SensorDescDummy;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;
import ch.ethz.soms.nervous.vm.NervousVM;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class PerformanceTestTask extends AsyncTask<Void, Void, Void> {

	private static final String LOG_TAG = "PerformanceTestTask";
	private static final int TEST_COUNT = 12000;
	private Context context;

	public PerformanceTestTask(Context context) {
		this.context = context;
	}

	@Override
	protected Void doInBackground(Void... params) {
		NervousVM vm = NervousVM.getInstance(context.getFilesDir());
		vm.deleteSensor(SensorDescDummy.SENSOR_ID);

		ArrayList<SensorDesc> sensorDescList = new ArrayList<SensorDesc>();

		for (int i = 0; i < TEST_COUNT; i++) {
			sensorDescList.add(new SensorDescDummy(i, true, (int) (Integer.MAX_VALUE * Math.random()), (float) Math.random(), (long) (Long.MAX_VALUE * Math.random()), (double) Math.random(), "TEST"));
		}

		long tick = System.currentTimeMillis();
		for (SensorDesc sensorDesc : sensorDescList) {
			vm.storeSensor(SensorDescDummy.SENSOR_ID, sensorDesc.toProtoSensor());
		}
		long tock = System.currentTimeMillis();
		double time = tock - tick;

		long[] storageSize = vm.getSensorStorageSize(SensorDescDummy.SENSOR_ID);

		Log.d(LOG_TAG, "(PERFORMANCE) TOTAL ENTRIES: " + String.valueOf(TEST_COUNT));

		Log.d(LOG_TAG, "(INSERT) TOTAL TIME: " + String.valueOf(time) + " ms");
		Log.d(LOG_TAG, "(INSERT) TIME PER ENTRY: " + String.valueOf(time / TEST_COUNT) + " ms");
		Log.d(LOG_TAG, "(INSERT) STORAGE TOTAL: " + String.valueOf(storageSize[0] + storageSize[1] + " B"));
		Log.d(LOG_TAG, "(INSERT) STORAGE PER ENTRY: " + String.valueOf((storageSize[0] + storageSize[1]) / TEST_COUNT + " B"));

		List<SensorData> peek;
		tick = System.currentTimeMillis();
		peek = vm.retrieve(SensorDescDummy.SENSOR_ID, 0, 0);
		tock = System.currentTimeMillis();
		time = tock - tick;
		Log.d(LOG_TAG, "(RETRIEVE) SEARCH TIME (FIRST): " + String.valueOf(time) + " ms");
		tick = System.currentTimeMillis();
		peek = vm.retrieve(SensorDescDummy.SENSOR_ID, TEST_COUNT - 1, TEST_COUNT - 1);
		tock = System.currentTimeMillis();
		time = tock - tick;
		Log.d(LOG_TAG, "(RETRIEVE) SEARCH TIME (LAST): " + String.valueOf(time) + " ms");
		tick = System.currentTimeMillis();
		peek = vm.retrieve(SensorDescDummy.SENSOR_ID, 0, Long.MAX_VALUE);
		tock = System.currentTimeMillis();
		time = tock - tick;
		Log.d(LOG_TAG, "(RETRIEVE) SEARCH TIME (ALL): " + String.valueOf(time) + " ms");

		vm.deleteSensor(SensorDescDummy.SENSOR_ID);
		return null;
	}
}

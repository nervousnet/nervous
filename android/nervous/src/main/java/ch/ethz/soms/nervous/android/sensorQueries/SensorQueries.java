package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import ch.ethz.soms.nervous.android.MainActivity;
import ch.ethz.soms.nervous.android.sensors.SensorDesc;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;
import ch.ethz.soms.nervous.vm.NervousVM;

public abstract class SensorQueries<G extends SensorDesc> {
	static List<SensorData> list;

	abstract long getSensorId();

	public SensorQueries(long timestamp_from, long timestamp_to, File file) {
		NervousVM nervousVm = NervousVM.getInstance(file);
		list = nervousVm.retrieve(getSensorId(), timestamp_from, timestamp_to);
		if (containsReadings()) {
			Log.d(MainActivity.DEBUG_TAG, "Retrieved List Size: " + list.size());
		}
	}

	public boolean containsReadings() {
		if (list == null || list.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * @return
	 * Returns the number of elements 
	 */
	public int getCount(){
		return list.size();
	}
	
	public abstract ArrayList<G> getSensorDescriptorList();
}


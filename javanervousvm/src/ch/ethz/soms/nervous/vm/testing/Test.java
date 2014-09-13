package ch.ethz.soms.nervous.vm.testing;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;
import ch.ethz.soms.nervous.vm.NervousVM;

public class Test {

	@org.junit.Test
	public void testStorage01() {
		NervousVM nervousVM = new NervousVM(new File("."));

		Long baseTime = Calendar.getInstance().getTimeInMillis();

		List<SensorData> input = new ArrayList<SensorData>();

		for (int i = 0; i < 50; i++) {
			SensorData.Builder sdb = SensorData.newBuilder();
			// Equispaced measurement plots
			sdb.setRecordTime(baseTime - ((5000 - i) * 30000));
			// Create some random test data
			sdb.addValueFloat(((float) Math.random()));
			sdb.addValueFloat(((float) 0.f));
			sdb.addValueFloat(((float) Math.random()));
			sdb.addValueInt32((int) (100 * Math.random()));
			SensorData sensorData = sdb.build();
			input.add(sensorData);
			nervousVM.storeSensor(0, sensorData);
		}

		List<SensorData> output = nervousVM.retrieve(0, 0, Long.MAX_VALUE);

		for (int i = 0; i < input.size(); i++) {
			try {
				assertEquals(input.get(i).getValueInt32(0), output.get(i).getValueInt32(0));
			} catch (AssertionError ex) {
				ex.printStackTrace();
			}
		}

	}

}

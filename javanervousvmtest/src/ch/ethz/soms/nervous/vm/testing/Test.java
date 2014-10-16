package ch.ethz.soms.nervous.vm.testing;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;
import ch.ethz.soms.nervous.vm.NervousVM;

public class Test {
	
	@org.junit.Test
	public void testPerformance() {
		int testWith = 20000;

		NervousVM nervousVM = new NervousVM(new File("."));

		Long baseTime = Calendar.getInstance().getTimeInMillis();
		
		long start = System.currentTimeMillis();
		for (int i = 0; i < testWith; i++) {
			SensorData.Builder sdb = SensorData.newBuilder();
			// Equispaced measurement plots
			sdb.setRecordTime(baseTime - ((testWith - i) * 30000));
			// Create some random test data
			sdb.addValueInt32((int) (100 * Math.random()));
			SensorData sensorData = sdb.build();
			nervousVM.storeSensor(0, sensorData);
		}
		long stop = System.currentTimeMillis();

		double durationAvg = stop-start;
		durationAvg = durationAvg / testWith;

		System.out.println("Average insert time: " + durationAvg + " ms");

	}

	@org.junit.Test
	public void testStorage01() {
		int testWith = 20000;

		NervousVM nervousVM = new NervousVM(new File("."));

		Long baseTime = Calendar.getInstance().getTimeInMillis();

		List<SensorData> input = new ArrayList<SensorData>();

		for (int i = 0; i < testWith; i++) {
			SensorData.Builder sdb = SensorData.newBuilder();
			// Equispaced measurement plots
			sdb.setRecordTime(baseTime - ((testWith - i) * 30000));
			// Create some random test data
			sdb.addValueInt32((int) (100 * Math.random()));
			SensorData sensorData = sdb.build();
			input.add(sensorData);
			nervousVM.storeSensor(0, sensorData);
		}

		List<SensorData> output = nervousVM.retrieve(0, 0, Long.MAX_VALUE);

		assertEquals(input.size(), output.size());
		for (int i = 0; i < input.size(); i++) {
			try {
				assertEquals(input.get(i).getValueInt32(0), output.get(i).getValueInt32(0));
			} catch (AssertionError ex) {
				ex.printStackTrace();
			}
		}

	}

	@org.junit.Test
	public void testStorage02() {
		int testWith = 4200;

		NervousVM nervousVM = new NervousVM(new File("."));

		List<SensorData> input = new ArrayList<SensorData>();

		for (int i = 0; i < testWith; i++) {
			SensorData.Builder sdb = SensorData.newBuilder();
			sdb.setRecordTime(i * 2);
			sdb.addValueInt32(12345);
			SensorData sensorData = sdb.build();
			input.add(sensorData);
			nervousVM.storeSensor(0, sensorData);
		}

		long lower = 4200*2;
		long upper = 4300*2;

		List<SensorData> output = nervousVM.retrieve(0, lower, upper);

		System.out.println(output.size());
		
		for (int i = (int) lower; i <= (int) upper; i++) {
			try {
				assertEquals((long) i, output.get(i).getRecordTime());
			} catch (AssertionError ex) {
				ex.printStackTrace();
			}
		}

	}

}

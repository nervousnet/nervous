package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

import ch.ethz.soms.nervous.android.sensors.SensorDesc;
import ch.ethz.soms.nervous.android.sensors.SensorDescBattery;
import ch.ethz.soms.nervous.android.sensors.SensorDescSingleValue;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

/**
 * Queries for sensor with only one float value
 * 
 * @author Patrick
 * @param <G>
 */
public abstract class SensorSingleValueQueries<G extends SensorDescSingleValue>
		extends SensorQueries<G> {

	public SensorSingleValueQueries(long timestamp_from, long timestamp_to,
			File file) {
		super(timestamp_from, timestamp_to, file);
	}

	abstract G createSensorDescSingleValue(SensorData sensorData);

	abstract G createDummyObject();

	@Override
	public ArrayList<G> getSensorDescriptorList() {
		ArrayList<G> descList = new ArrayList<G>();
		for (SensorData sensorData : list) {
			descList.add(createSensorDescSingleValue(sensorData));
		}
		return descList;
	}

	public G getMaxValue() {
		Float maxVal = Float.MIN_VALUE;
		G maxSensDesc = createDummyObject();
		for (SensorData sensorData : list) {
			G sensDesc = createSensorDescSingleValue(sensorData);
			if (sensDesc.getValue() > maxVal) {
				maxVal = sensDesc.getValue();
				maxSensDesc = sensDesc;
			}
		}
		return maxSensDesc;
	}

	public float getAvergage() {
		float totalSum = 0;
		for (SensorData sensorData : list) {
			G sensDesc = createSensorDescSingleValue(sensorData);
			totalSum += sensDesc.getValue();
		}

		float average = totalSum / (list.size());
		return average;
	}

	public float getSum() {
		float totalSum = 0;
		for (SensorData sensorData : list) {
			G sensDesc = createSensorDescSingleValue(sensorData);
			totalSum += sensDesc.getValue();
		}
		return totalSum;
	}

	public G getMinValue() {
		Float minVal = Float.MAX_VALUE;
		G minSensDesc = createDummyObject();

		for (SensorData sensorData : list) {
			G sensDesc = createSensorDescSingleValue(sensorData);
			if (sensDesc.getValue() < minVal) {
				minVal = sensDesc.getValue();
				minSensDesc = sensDesc;
			}
		}
		return minSensDesc;
	}

	public float getMedian() {
		Comparator<G> comparator = new LargestFirstComparator();
		ArrayList<G> arrList = new ArrayList<G>();

		// Add all SensorDesc
		for (SensorData sensorData : list) {
			arrList.add(createSensorDescSingleValue(sensorData));
		}
		Collections.sort(arrList, comparator);

		double middle = arrList.size() / 2;
		float result;
		if (arrList.size() % 2 == 1) {
			result = arrList.get((int) Math.ceil(middle)).getValue();
		} else {
			float r1 = arrList.get((int) middle).getValue();
			float r2 = arrList.get((int) middle + 1).getValue();
			result = (r1 + r2) / 2;
		}
		return result;
	}

	public ArrayList<G> getTopK(int k) {
		Comparator<G> comparator = new LargestFirstComparator();
		PriorityQueue<G> prioQueue = new PriorityQueue<G>(3, comparator);

		for (SensorData sensorData : list) {
			prioQueue.add(createSensorDescSingleValue(sensorData));
		}
		int i = 1;
		ArrayList<G> descList = new ArrayList<G>();
		while (i <= k && !prioQueue.isEmpty()) {
			descList.add(prioQueue.poll());
			++i;
		}
		return descList;
	}

	public ArrayList<G> getBottomK(int k) {
		Comparator<G> comparator = new SmallestFirstComparator();
		PriorityQueue<G> prioQueue = new PriorityQueue<G>(3, comparator);

		for (SensorData sensorData : list) {
			prioQueue.add(createSensorDescSingleValue(sensorData));
		}
		int i = 1;
		ArrayList<G> descList = new ArrayList<G>();
		while (i <= k && !prioQueue.isEmpty()) {
			descList.add(prioQueue.poll());
			++i;
		}
		return descList;
	}

	public class SmallestFirstComparator implements Comparator<G> {

		@Override
		public int compare(G lhs, G rhs) {

			float lVal = lhs.getValue();
			float rVal = rhs.getValue();
			if (lVal < rVal) {
				return -1;
			} else if (lVal > rVal) {
				return 1;
			} else {
				return 0;
			}
		}

	}

	public class LargestFirstComparator implements Comparator<G> {

		@Override
		public int compare(G lhs, G rhs) {

			float lVal = lhs.getValue();
			float rVal = rhs.getValue();
			if (lVal > rVal) {
				return -1;
			} else if (lVal < rVal) {
				return 1;
			} else {
				return 0;
			}
		}

	}

}

package ch.ethz.soms.nervous.android.sensorQueries;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import ch.ethz.soms.nervous.android.sensors.SensorDesc;
import ch.ethz.soms.nervous.android.sensors.SensorDescBattery;
import ch.ethz.soms.nervous.android.sensors.SensorSingleValueDesc;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

/**
 * Queries for sensor with only one float value
 * 
 * @author cpcrasher
 * @param <G>
 */
public abstract class SensorSingleValueQueries<G extends SensorSingleValueDesc>
		extends SensorQueries<G> {

	public SensorSingleValueQueries(long timestamp_from, long timestamp_to,
			File file) {
		super(timestamp_from, timestamp_to, file);
	}

	abstract G createSensorDesc(SensorData sensorData);

	abstract G createDummyObject();

	@Override
	public ArrayList<G> getSensorDescriptorList() {
		ArrayList<G> descList = new ArrayList<G>();
		for (SensorData sensorData : list) {
			descList.add(createSensorDesc(sensorData));
		}
		return descList;
	}

	public G getMaxValue() {
		Float maxVal = Float.MIN_VALUE;
		G maxSensDesc = createDummyObject();
		for (SensorData sensorData : list) {
			G sensDesc = createSensorDesc(sensorData);
			if (sensDesc.getValue() > maxVal) {
				maxVal = sensDesc.getValue();
				maxSensDesc = sensDesc;
			}
		}
		return maxSensDesc;
	}

	public G getMinValue() {
		Float minVal = Float.MAX_VALUE;
		G minSensDesc = createDummyObject();

		for (SensorData sensorData : list) {
			G sensDesc = createSensorDesc(sensorData);
			if (sensDesc.getValue() < minVal) {
				minVal = sensDesc.getValue();
				minSensDesc = sensDesc;
			}
		}
		return minSensDesc;
	}

	public ArrayList<G> getTopK(int k) {
		Comparator<G> comparator = new LargestFirstComparator();
		PriorityQueue<G> prioQueue = new PriorityQueue<G>(3, comparator);

		for (SensorData sensorData : list) {
			prioQueue.add(createSensorDesc(sensorData));
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
			prioQueue.add(createSensorDesc(sensorData));
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

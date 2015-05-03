package ch.ethz.soms.nervous.android.Queries;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;
import ch.ethz.soms.nervous.android.sensors.*;
import ch.ethz.soms.nervous.android.sensorQueries.*;

// i/p single values
// Generic functions for all of them using G
//single value sensors

public abstract class QueryNumSingleValue <G extends SensorDescSingleValue> extends QueryNum<G> {

	public QueryNumSingleValue(long timestamp_from, long timestamp_to,
			File file) {
		super(timestamp_from, timestamp_to, file);
	}
	public abstract G createSensorDescSingleValue(SensorData sensorData);
	
	public ArrayList<G> getSensorDescriptorList() {
		ArrayList<G> descList = new ArrayList<G>();
		for (SensorData sensorData : list) {
			descList.add(createSensorDescSingleValue(sensorData));
		}
		return descList;
	}
	
	public ArrayList<G> getTimeRange(ArrayList<G> desc_list,ArrayList<Float> s, ArrayList<Float> e) //send to function list of sensor descriptors
	{//return data within the range
		float start = s.get(0);
		 float end = e.get(0);// d is not used here
		ArrayList<G> answer = new ArrayList<G>();
		//get the particular query and send the object
		//after that loop through all the elements and check if value within range
		//output the descriptor in an ArrayList
		
		for(int i=0;i<desc_list.size();i++) //loop through the data
		{
			G sensDesc = desc_list.get(i);
			if(sensDesc.getValue() <= end && sensDesc.getValue() >= start)
			{
				answer.add(sensDesc);
			}
		}
		
		return answer;
	}
	
	public ArrayList<Float> sd()//sqrt(variance)
	{
		ArrayList<Float> sd = new ArrayList<Float>();
		
		 float totalSum = 0;
			for (SensorData sensorData : list) {
				G sensDesc = createSensorDescSingleValue(sensorData);
				totalSum += sensDesc.getValue();
			}

			float average = totalSum / (list.size());
			float temp = 0;
			for (SensorData sensorData : list) {
				G sensDesc = createSensorDescSingleValue(sensorData);
				temp = temp + (average - sensDesc.getValue())*(average - sensDesc.getValue());
			}
			
			temp = temp / list.size();
			temp = (float) Math.sqrt(temp);
			sd.add(0, temp);
		
		return sd;//sd!! not variance!!
	}
	
	public ArrayList<Float> var()//variance
	{
		ArrayList<Float> variance = new ArrayList<Float>();
		
		 float totalSum = 0;
			for (SensorData sensorData : list) {
				G sensDesc = createSensorDescSingleValue(sensorData);
				totalSum += sensDesc.getValue();
			}

			float average = totalSum / (list.size());//mean
			float temp = 0;
			for (SensorData sensorData : list) {
				G sensDesc = createSensorDescSingleValue(sensorData);
				temp = temp + (average - sensDesc.getValue()) * (average - sensDesc.getValue());
			}
			
			temp = temp / list.size();
			variance.add(0, temp);
		return variance;
		
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

	public ArrayList<Float> getAverage(){
		 float totalSum = 0;
		for (SensorData sensorData : list) {
			G sensDesc = createSensorDescSingleValue(sensorData);
			totalSum += sensDesc.getValue();
		}

		float average = totalSum / (list.size());
		ArrayList<Float> temp = new ArrayList<Float>();
		temp.add(0, average);
		return temp;
	}
	
	public ArrayList<Float> getRms(){
		 float totalSum = 0;
		for (SensorData sensorData : list) {
			G sensDesc = createSensorDescSingleValue(sensorData);
			totalSum += Math.pow(sensDesc.getValue(), 2); //sumsquare
		}

		float average = totalSum / (list.size());//meansquare
		ArrayList<Float> temp = new ArrayList<Float>();
		average = (float) Math.sqrt(average);//rms
		temp.add(0, average);
		return temp;
	}

	public ArrayList<Float> getMeanSquare(){
		 float totalSum = 0;
		for (SensorData sensorData : list) {
			G sensDesc = createSensorDescSingleValue(sensorData);
			totalSum += Math.pow(sensDesc.getValue(), 2);
		}

		float average = totalSum / (list.size());
		ArrayList<Float> temp = new ArrayList<Float>();
		
		temp.add(0, average);
		return temp;
	}
	
	public ArrayList<Float> getSum() {
		float totalSum = 0;
		for (SensorData sensorData : list) {
			G sensDesc = createSensorDescSingleValue(sensorData);
			totalSum += sensDesc.getValue();
		}
		ArrayList<Float> temp = new ArrayList<Float>();
		temp.add(0, totalSum);
		return temp;
	}
	
	public ArrayList<Float> getSumSquare() {
		float totalSum = 0;
		for (SensorData sensorData : list) {
			G sensDesc = createSensorDescSingleValue(sensorData);
			totalSum += Math.pow(sensDesc.getValue(), 2);
		}
		ArrayList<Float> temp = new ArrayList<Float>();
		temp.add(0, totalSum);
		return temp;
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
	
	public ArrayList<Float> getRmsError(ArrayList<Float> comp) // sum of squared diff between same sensor readings at different timestamps
	{// 1 val vs all
		ArrayList<Float> answer = new ArrayList<Float>();
		float temp = 0;
		float data = comp.get(0);//only 1 data
		for(SensorData sensordata:list)
		{
			G sensDesc = createSensorDescSingleValue(sensordata);
			temp = (float) (temp + Math.pow((sensDesc.getValue() - data), 2));
		}
		temp = temp / list.size();
		temp = (float) Math.sqrt(temp);
		answer.add(0, temp);
		return answer;//single value return
		
	}

	public ArrayList<Float> getMedian() {//middle element
		Comparator<G> comparator = new SmallestFirstComparator();
		ArrayList<G> arrList = new ArrayList<G>();

		// Add all SensorDesc
		for (SensorData sensorData : list) {
			arrList.add(createSensorDescSingleValue(sensorData));//get data
		}
		Collections.sort(arrList, comparator);//sort data

		double middle = arrList.size() / 2;//middle index
		float result;
		if (arrList.size() % 2 == 1) {//if even
			result = arrList.get((int) Math.ceil(middle)).getValue();
		} else {//if odd
			float r1 = arrList.get((int) middle).getValue();
			float r2 = arrList.get((int) middle + 1).getValue();
			result = (r1 + r2) / 2;
		}
		ArrayList<Float> temp = new ArrayList<Float>();
		temp.add(0, result);
		return temp;
	}
	
	

	public ArrayList<G> getLargest(int k) { //largest top 10
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

	public ArrayList<G> getSmallest(int k) {//smallest bottom 10
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
	
	public G getRankSmallest(int k) {//rank k from acsending order
		//1 based rank
		Comparator<G> comparator = new SmallestFirstComparator();//ascending order
		PriorityQueue<G> prioQueue = new PriorityQueue<G>(3, comparator);

		for (SensorData sensorData : list) {
			prioQueue.add(createSensorDescSingleValue(sensorData));
		}
		int i = 1;
		G dummydesc = createDummyObject();
		
		while (i <= k && !prioQueue.isEmpty()) {
			if(i == k)
			{
				dummydesc = prioQueue.poll();
				break;
			}
			
			prioQueue.poll();//don't care!
			++i;
		}
		return dummydesc;
	}
	
	public G getRankLargest(int k) {//largest elelment of rank k
		//1 based rank
		Comparator<G> comparator = new LargestFirstComparator();//descending order
		PriorityQueue<G> prioQueue = new PriorityQueue<G>(3, comparator);//more general than a queue

		for (SensorData sensorData : list) {
			prioQueue.add(createSensorDescSingleValue(sensorData)); //add elements to the queue
		}
		int i = 1;
		G dummydesc = createDummyObject();
		
		while (i <= k && !prioQueue.isEmpty()) {
			if(i == k)
			{
				dummydesc = prioQueue.poll();//get element
				break;
			}
			
			prioQueue.poll();//don't care!
			++i;
		}
		return dummydesc;
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
	
public ArrayList<Float> getCorrelation(ArrayList<G> comp,ArrayList<G> comp1)//single value sensors
{
	//output is the correlation index
	float totalSum = 0;
	for (int i = 0;i<comp.size();i++) {
		
		totalSum += comp.get(i).getValue();
	}

	float average = totalSum / (list.size());
	float totalSum1 = 0;
	for (int i = 0;i<comp.size();i++) {
		
		totalSum1 += comp1.get(i).getValue();
	}

	float average1 = totalSum1 / (list.size());
	//subtract each variable by the mean
	ArrayList<Float> c = new ArrayList<Float>();
	ArrayList<Float> c1 = new ArrayList<Float>();
	for (int i = 0;i<comp.size();i++) {
		
		float temp = comp.get(i).getValue();
		temp = temp - average;
		c.add(i, temp);
	}
	for (int i = 0;i<comp.size();i++) {
	
	float temp = comp1.get(i).getValue();
	temp = temp - average1;
	c1.add(i, temp);
    }
	
	float top=0;
	for (int i = 0;i<comp.size();i++) {
		
		//a*b+etc...
		
		top = top + c.get(i)*c1.get(i);
	    }
	
	//sum ^2 and b^2
	float a2 = 0;
	float b2 = 0;
	for (int i = 0;i<comp.size();i++) {
		a2 = (float) (a2 + Math.pow(c.get(i), 2));
		b2 = (float) (b2 + Math.pow(c1.get(i), 2));
		 }
	
	float bottom = (float) Math.sqrt(a2*b2);
	float coef = top / bottom;
	ArrayList<Float> c3 = new ArrayList<Float>();
	c3.add(coef);
	return c3;

	
	
}
	
	public ArrayList<Float> getEntropy()
	{
		float ent = 0;
		// get total sum
		// get probability
		// p * log(1/p) addition return
		
		//find the sum
		float totalSum = 0;
		for (SensorData sensorData : list) {
			G sensDesc = createSensorDescSingleValue(sensorData);
			totalSum += sensDesc.getValue();
		}
		//find the probability of each reading 
		ArrayList<Float> prob = new ArrayList<Float>();
		for (SensorData sensorData : list) {
			G sensDesc = createSensorDescSingleValue(sensorData);
			float temp = sensDesc.getValue() / totalSum;//single value
			prob.add(temp);
			}//store probabilities
		
		for(int i = 0;i < prob.size();i++)
		{
			ent = (float) (ent + prob.get(i)*Math.log10(1/prob.get(i)));
		}
		ArrayList<Float> n = new ArrayList<Float>();
		n.add(ent);
		return n;
	}

	/*public abstract G createSensorDescSingleValue(SensorData sensorData);

	public abstract G createDummyObject();

	public ArrayList<G> getSensorDescriptorList() {
		ArrayList<G> descList = new ArrayList<G>();
		for (SensorData sensorData : list) {
			descList.add(createSensorDescSingleValue(sensorData));
		}
		return descList;
	}*/
	
	public ArrayList<Float> getKMeans(int n,ArrayList<Float> init)
	{
		ArrayList<Float> moo = new ArrayList<Float>();
		
		
		return moo;
	}
	
	

}

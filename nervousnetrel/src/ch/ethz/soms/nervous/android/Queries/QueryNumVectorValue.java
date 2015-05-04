package ch.ethz.soms.nervous.android.Queries;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

import ch.ethz.soms.nervous.android.sensors.*;
import ch.ethz.soms.nervous.android.Queries.*;
import ch.ethz.soms.nervous.android.Queries.QueryNumSingleValue.LargestFirstComparator;
import ch.ethz.soms.nervous.android.Queries.QueryNumSingleValue.SmallestFirstComparator;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public abstract class QueryNumVectorValue<G extends SensorDescVectorValue> extends QueryNum<G>{

	public QueryNumVectorValue(long timestamp_from, long timestamp_to, File file) {
		super(timestamp_from, timestamp_to, file);
		// TODO Auto-generated constructor stub
	}
	
public abstract G createSensorDescVectorValue(SensorData sensorData);
	
	public ArrayList<G> getSensorDescriptorList() { //get sensors desc list
		ArrayList<G> descList = new ArrayList<G>();
		for (SensorData sensorData : list) {
			descList.add(createSensorDescVectorValue(sensorData));
		}
		return descList;
	}
	
	public ArrayList<G> getTimeRange(ArrayList<G> desc_list, ArrayList<Float> s, ArrayList<Float> e) //send to function list of sensor descriptors
																							// if d=0, range in x+y+z,start,end are integers 
																							// if d=1,else individual values within the range
																							//specified.
	{
		ArrayList<G> answer = new ArrayList<G>();
		/*if(d == 0){ //take x+y+z
			
		float start = s.get(0);
		 float end = e.get(0);
		
		//get the particular query and send the object
		//after that loop through all the elements and check if value within range
		//output the descriptor in an ArrayList
		
		for(int i=0;i<desc_list.size();i++) //loop through the data
		{
			G sensDesc = desc_list.get(i);
			ArrayList<Float> temp = new ArrayList<Float>();
			temp = sensDesc.getValue();
			float total_sub = 0;
			
			for(int j = 0 ; j < temp.size(); j++)  //all values of all dimensions lesser and greater than start and end
			{
				total_sub = total_sub + temp.get(i); //add x,y,z
			}
			
			if(total_sub >= start && total_sub <= end) //all values in range
			{
				answer.add(sensDesc);
			}
			else
			{
				continue;
			}
		}
		
		
		
		}
		else
		if(d == 1)
		{*/
			//start and end values for each x,y,z
			
			//get the particular query and send the object
			//after that loop through all the elements and check if value within range
			//output the descriptor in an ArrayList
			
			for(int i=0; i < desc_list.size(); i++) //loop through the data
			{
				G sensDesc = desc_list.get(i);
				ArrayList<Float> temp = new ArrayList<Float>();
				temp = sensDesc.getValue(); // can have 3 or more values
				
				int flag = 0;
				for(int j = 0 ; j < temp.size(); j++)  //all values of all dimensions lesser and greater than start and end
				{
					if(temp.get(i) <= s.get(i) && temp.get(i) >= e.get(i))
					{
						flag = 1;//IGNORE!!
						break;
					}
				}
				
				if(flag == 0)//came out of loop without beign out of range
				{
					answer.add(sensDesc);
				}
			}
				
		
		return answer;		
			
		
	}
	
	public ArrayList<Float> getRmsError(ArrayList<Float> comp)
	{
		ArrayList<Float> answer = new ArrayList<Float>();
		ArrayList<Float> average = new ArrayList<Float>();
		int s =0;
		for (SensorData sensorData : list) {
			G sensDesc = createSensorDescVectorValue(sensorData); // loop over the sensor data,get the object
			ArrayList<Float> temp = new ArrayList<Float>();       //temporary data
			temp = sensDesc.getValue();
			s = temp.size();
			for(int i = 0;i < s; i++)
			{
				float temptemp = answer.get(i);
				temptemp = (float) (temptemp + Math.pow((temp.get(i)-comp.get(i)), 2));
				answer.add(i, temptemp);
			}
			
		}
		for(int i = 0;i < s; i++)
		{
			float temptemp = answer.get(i);
			temptemp = temptemp/list.size();
			temptemp = (float) Math.sqrt(temptemp);
			answer.add(i,temptemp);
		}
		
		return answer;//rms vector!!
		
	}
	
	public ArrayList<Float> var() 
	{
		//
		ArrayList<Float> variance = new ArrayList<Float>(); //store variance
		//d = 0 returns arraylist of variance for each reading
		//d = 1 returns arraylist of variance for each variable of each reading(each x,y,z)
		
			ArrayList<Float> average = new ArrayList<Float>();
			for (SensorData sensorData : list) {
				G sensDesc = createSensorDescVectorValue(sensorData); // loop over the sensor data,get the object
				ArrayList<Float> temp = new ArrayList<Float>();       //temporary data
				temp = sensDesc.getValue();
				for(int i = 0; i < temp.size(); i++)                   //for each x,z & z
				{
					 float temptemp = average.get(i) + temp.get(i);   //add current data to the existing one and replace
					 average.set(i,temptemp); //sum
				}
				
			}

			for(int i = 0 ; i < average.size(); i++ )//divide by size
			{
				float temptemp = average.get(i);
				temptemp = temptemp/list.size();
				average.set(i,temptemp);//average
			}
			
			//average has average of x,y and z
			
			for (SensorData sensorData : list) {
				G sensDesc = createSensorDescVectorValue(sensorData); // loop over the sensor data,get the object
				ArrayList<Float> temp = new ArrayList<Float>();       //temporary data
				temp = sensDesc.getValue();
				for(int i = 0; i < temp.size(); i++)                   //for each x,z & z
				{
					float temptemp = variance.get(i);
					temptemp = temptemp + (average.get(i) - temp.get(i)) * (average.get(i) - temp.get(i));
					variance.add(i,temptemp);
				}
				for(int i = 0; i < temp.size(); i++)                   //for each x,z & z
				{
					float temptemp = variance.get(i);
					temptemp = temptemp / temp.size();
					variance.add(i,temptemp);
				}
				
				
			}
			
			
			
		
		// var between each reading
		/*
		 * for (SensorData sensorData : list) {
				
				G sensDesc = createSensorDescVectorValue(sensorData); // loop over the sensor data,get the object
				ArrayList<Float> temp = new ArrayList<Float>();       //temporary data
				temp = sensDesc.getValue();
				float mean = 0;
				for(int i = 0; i < temp.size(); i++)                   //for each x,y & z
				{
					mean = mean + temp.get(i);
				}
				mean = mean / temp.size();
				float v = 0;
				for(int i = 0; i < temp.size(); i++)                   //for each x,y & z
				{
					v = v + (mean - temp.get(i))*(mean - temp.get(i));
				}
				
				v = v /temp.size();
				variance.add(v);
				
				//index of sensdesc
				
				
			}*/

		
		
		return variance;
	}
	
	public ArrayList<Float> sd()
	{
		//
		ArrayList<Float> variance = new ArrayList<Float>(); //store variance
		//d = 0 returns arraylist of variance for each reading
		//d = 1 returns arraylist of variance for each variable of each reading
		
			ArrayList<Float> average = new ArrayList<Float>();
			for (SensorData sensorData : list) {
				G sensDesc = createSensorDescVectorValue(sensorData); // loop over the sensor data,get the object
				ArrayList<Float> temp = new ArrayList<Float>();       //temporary data
				temp = sensDesc.getValue();
				//get sum
				for(int i = 0; i < temp.size(); i++)                   //for each x,z & z
				{
					 float temptemp = average.get(i) + temp.get(i);   //add current data to the existing one and replace
					 average.set(i,temptemp); //individual averages
				}
				
			}
			// get average
			for(int i = 0 ; i < average.size(); i++ )
			{
				float temptemp = average.get(i);
				temptemp = temptemp / list.size();
				average.set(i,temptemp);
			}
			
			//average has average of x,y and z
			
			for (SensorData sensorData : list) {
				G sensDesc = createSensorDescVectorValue(sensorData); // loop over the sensor data,get the object
				ArrayList<Float> temp = new ArrayList<Float>();       //temporary data
				temp = sensDesc.getValue();
				
				for(int i = 0; i < temp.size(); i++)                   //for each x,z & z
				{
					float temptemp = variance.get(i);                 // initially variance 0?
					temptemp = temptemp + (average.get(i) - temp.get(i)) * (average.get(i) - temp.get(i));
					variance.add(i,temptemp);
				}
				
				for(int i = 0; i < temp.size(); i++)                   //for each x,z & z
				{
					float temptemp = variance.get(i);
					temptemp = temptemp / temp.size(); //   list.size() ??
					variance.add(i,temptemp);          // variance of each x,y,z?
				}
				
				
			}
			
			/*for (SensorData sensorData : list) {
				
				G sensDesc = createSensorDescVectorValue(sensorData); // loop over the sensor data,get the object
				ArrayList<Float> temp = new ArrayList<Float>();       //temporary data
				temp = sensDesc.getValue();
				float mean = 0;
				for(int i = 0; i < temp.size(); i++)                   //for each x,y & z
				{
					mean = mean + temp.get(i);
				}
				mean = mean / temp.size(); // average
				float v = 0;
				for(int i = 0; i < temp.size(); i++)                   //for each x,y & z
				{
					v = v + (mean - temp.get(i))*(mean - temp.get(i));
				}
				
				v = v /temp.size();
				variance.add(v);
				
				//index of sensdesc
				
				
			}*/

		
		
		for(int i = 0;i < variance.size(); i++)
		{
			float temp = variance.get(i);
			temp = (float) Math.sqrt(temp);
			variance.add(i,temp);
		}
		
		return variance;  //not variance SD!!
	}
	
	public G getMaxValue() { //add all three values and find the maximum
		G maxSensDesc = createDummyObject(); // dummy object
		float maxAverage = 0;
		for (SensorData sensorData : list) {
			G sensDesc = createSensorDescVectorValue(sensorData); // get sensor data
			ArrayList<Float> value = new ArrayList<Float>();    
			value = sensDesc.getValue(); //get arraylist of values of sensor(3 values generally)
			float newAverage = 0;
			for(int i = 0;i < value.size();i++)
			{
				newAverage = value.get(i) + newAverage;
			}
			newAverage = newAverage / value.size();
			
			
			
			if (newAverage > maxAverage) {
				maxAverage = newAverage;
				maxSensDesc = sensDesc;
			}
		}
		return maxSensDesc; //return the object itself
	}
	
	public G getMinValue() { // add all three values and find the minimum
		G minSensDesc = createDummyObject();
		float maxAverage = Float.MAX_VALUE;
		for (SensorData sensorData : list) {
			G sensDesc = createSensorDescVectorValue(sensorData);
			ArrayList<Float> value = new ArrayList<Float>();    
			value = sensDesc.getValue(); //get arraylist of values of sensor(3 values generally)
			float newAverage = 0;
			for(int i = 0;i < value.size();i++)
			{
				newAverage = value.get(i) + newAverage;
			}
			newAverage = newAverage / value.size();
			if (newAverage < maxAverage) {
				maxAverage = newAverage;
				minSensDesc = sensDesc;
			}
		}
		return minSensDesc; //return object itself
	}
	
	public ArrayList<G> getLargest(int k) {  //largest values within this range
		Comparator<G> comparator = new LargestFirstComparator();
		PriorityQueue<G> prioQueue = new PriorityQueue<G>(3,comparator);

		for (SensorData sensorData : list) {
			prioQueue.add(createSensorDescVectorValue(sensorData));
		}
		int i = 1;
		ArrayList<G> descList = new ArrayList<G>();
		while (i <= k && !prioQueue.isEmpty()) {
			descList.add(prioQueue.poll());
			++i;
		}
		return descList;
	}
	
	public G getRankLargest(int k) {  //largest values within this range
		Comparator<G> comparator = new LargestFirstComparator();
		PriorityQueue<G> prioQueue = new PriorityQueue<G>(11,comparator);

		for (SensorData sensorData : list) {
			prioQueue.add(createSensorDescVectorValue(sensorData));
		}
		G dummydesc = createDummyObject();
		int i = 1;
		
		while (i <= k && !prioQueue.isEmpty()) {
			if(i == k)
			{
				dummydesc = prioQueue.poll();
				break;
			}
			prioQueue.poll();
			++i;
		}
		return dummydesc;
	}
	
	public G getRankSmallest(int k) {  //largest values within this range
		Comparator<G> comparator = new SmallestFirstComparator();
		PriorityQueue<G> prioQueue = new PriorityQueue<G>(11,comparator);

		for (SensorData sensorData : list) {
			prioQueue.add(createSensorDescVectorValue(sensorData));
		}
		G dummydesc = createDummyObject();
		int i = 1;
		
		while (i <= k && !prioQueue.isEmpty()) {
			if(i == k)
			{
				dummydesc = prioQueue.poll();
				break;
			}
			prioQueue.poll();
			++i;
		}
		return dummydesc;
	}
	
	public ArrayList<G> getSmallest(int k) {
		Comparator<G> comparator = new SmallestFirstComparator();
		PriorityQueue<G> prioQueue = new PriorityQueue<G>(11, comparator);

		for (SensorData sensorData : list) {
			prioQueue.add(createSensorDescVectorValue(sensorData));
		}
		int i = 1;
		ArrayList<G> descList = new ArrayList<G>();
		while (i <= k && !prioQueue.isEmpty()) {
			descList.add(prioQueue.poll());
			++i;
		}
		return descList;
	}
	
	public ArrayList<Float> getAverage() {                       // find the average of all the values
		ArrayList<Float> average = new ArrayList<Float>();  
		          // 0-> avg of x and so on...
		for (SensorData sensorData : list) {
			G sensDesc = createSensorDescVectorValue(sensorData); // loop over the sensor data,get the object
			ArrayList<Float> temp = new ArrayList<Float>();       //temporary data
			temp = sensDesc.getValue();
			for(int i = 0; i < temp.size(); i++)                   //for each x,z & z
			{
				 float temptemp = average.get(i) + temp.get(i);   //add current data to the existing one and replace
				 average.set(i,temptemp); 
			}
			
		}

		for(int i = 0 ; i < average.size(); i++ )
		{
			float temptemp = average.get(i);
			temptemp = temptemp/list.size();
			average.set(i,temptemp);
		}
		
		return average;
	}
	
	public ArrayList<Float> getMedian() {
		
		/*public static double median(double[] m) {
		    int middle = m.length/2;
		    if (m.length%2 == 1) {
		        return m[middle];
		    } else {
		        return (m[middle-1] + m[middle]) / 2.0;
		    }
		}*/
		
		Comparator<G> comparator = new SmallestFirstComparator();
		ArrayList<G> arrList = new ArrayList<G>();

		// Add all SensorDesc
		for (SensorData sensorData : list) {
			arrList.add(createSensorDescVectorValue(sensorData));
		}
		Collections.sort(arrList, comparator);  //sort array
		int middle = (arrList.size()/2);
		
		if(arrList.size()%2 == 1)
		{
			return arrList.get(middle).getValue();
		}
		else
		{
			ArrayList<Float> temp = new ArrayList<Float>();
			ArrayList<Float> temp1 = new ArrayList<Float>();
			ArrayList<Float> median = new ArrayList<Float>();
			temp = arrList.get(middle).getValue();
			temp1 = arrList.get(middle+1).getValue();
			int s = temp.size();//each variable x,y,z?
			for(int i = 0; i < s ;i++) //usually size = 3
			{
				float temptemp = 0;
				temptemp = (temp.get(i) + temp1.get(i))/2;
				median.set(i, temptemp);
				
			}
			return median;
		}
	}
	

	public ArrayList<Float> getSum() {                       // find the average of all the values
		ArrayList<Float> sum = new ArrayList<Float>();        // 0-> avg of x and so on...
		for (SensorData sensorData : list) {
			G sensDesc = createSensorDescVectorValue(sensorData); // loop over the sensor data,get the object
			ArrayList<Float> temp = new ArrayList<Float>();       //temporary data
			temp = sensDesc.getValue();
			for(int i = 0; i < temp.size(); i++)                   //for each x,z & z
			{
				 float temptemp = sum.get(i) + temp.get(i);   //add current data to the existing one and replace
				 sum.set(i,temptemp); 
			}
			
		}
		
		return sum;
	}
	
	public ArrayList<Float> getSumSquare() {                       // find the average of all the values
		ArrayList<Float> sum = new ArrayList<Float>();        // 0-> avg of x and so on...
		for (SensorData sensorData : list) {
			G sensDesc = createSensorDescVectorValue(sensorData); // loop over the sensor data,get the object
			ArrayList<Float> temp = new ArrayList<Float>();       //temporary data
			temp = sensDesc.getValue();
			for(int i = 0; i < temp.size(); i++)                   //for each x,z & z
			{
				 float temptemp = (float) (sum.get(i) + Math.pow(temp.get(i),2));   //add current data to the existing one and replace
				 sum.set(i,temptemp); 
			}
			
		}
		
		return sum;
	}
	
	public ArrayList<Float> getRms() {                       // find the average of all the values
		ArrayList<Float> sum = new ArrayList<Float>();        // 0-> avg of x and so on...
		for (SensorData sensorData : list) {
			G sensDesc = createSensorDescVectorValue(sensorData); // loop over the sensor data,get the object
			ArrayList<Float> temp = new ArrayList<Float>();       //temporary data
			temp = sensDesc.getValue();
			for(int i = 0; i < temp.size(); i++)                   //for each x,z & z
			{
				 float temptemp = (float) (sum.get(i) + Math.pow(temp.get(i),2));   //add current data to the existing one and replace
				 sum.set(i,temptemp); 
			}
			
		}
		for(int i = 0 ; i < sum.size(); i++ )
		{
			float temptemp = sum.get(i);
			temptemp = temptemp/list.size();
			temptemp = (float) Math.sqrt(temptemp);
			sum.set(i,temptemp);
		}
		
		return sum;
	}

	public ArrayList<Float> getMeanSquare() {                       // find the average of all the values
		ArrayList<Float> sum = new ArrayList<Float>();        // 0-> avg of x and so on...
		for (SensorData sensorData : list) {
			G sensDesc = createSensorDescVectorValue(sensorData); // loop over the sensor data,get the object
			ArrayList<Float> temp = new ArrayList<Float>();       //temporary data
			temp = sensDesc.getValue();
			for(int i = 0; i < temp.size(); i++)                   //for each x,z & z
			{
				 float temptemp = (float) (sum.get(i) + Math.pow(temp.get(i),2));   //add current data to the existing one and replace
				 sum.set(i,temptemp); 
			}
			
		}
		for(int i = 0 ; i < sum.size(); i++ )
		{
			float temptemp = sum.get(i);
			temptemp = temptemp/list.size();
			sum.set(i,temptemp);
		}
		
		return sum;
	}

	public class SmallestFirstComparator implements Comparator<G> {  // one object smaller than the other

		@Override
		public int compare(G lhs, G rhs) {

			ArrayList<Float> lVal = new ArrayList<Float>();
			lVal = lhs.getValue();
			int i;
			Float lsum = (float) 0;
			for(i = 0; i < lVal.size(); i++)
			    lsum += lVal.get(i);
			
			ArrayList<Float> rVal = new ArrayList<Float>();
			rVal = rhs.getValue();
			
			Float rsum = (float) 0;
			for(i = 0; i < rVal.size(); i++)
			    lsum += rVal.get(i);
			
			if (lsum < rsum) {
				return -1;
			} else if (lsum > rsum) {
				return 1;
			} else {
				return 0;
			}
		}

	}
	
	public class LargestFirstComparator implements Comparator<G> {  //one object larger than the other

		@Override
		public int compare(G lhs, G rhs) {

			ArrayList<Float> lVal = new ArrayList<Float>();
			lVal = lhs.getValue();
			int i;
			Float lsum = (float) 0;
			for(i = 0; i < lVal.size(); i++)
			    lsum += lVal.get(i);
			
			ArrayList<Float> rVal = new ArrayList<Float>();
			rVal = rhs.getValue();
			
			Float rsum = (float) 0;
			for(i = 0; i < rVal.size(); i++)
			    lsum += rVal.get(i);
			
			if (lsum > rsum) {
				return -1;
			} else if (lsum < rsum) {
				return 1;
			} else {
				return 0;
			}
		}

	}
	
	public ArrayList<Float> getCorrelation(ArrayList<G> comp,ArrayList<G> comp1)
	{
		ArrayList<Float> moo = new ArrayList<Float>();//results
		
		ArrayList<Float> avg = new ArrayList<Float>();
		ArrayList<Float> avg1 = new ArrayList<Float>();
		
		ArrayList< ArrayList<Float> > a = new ArrayList<ArrayList<Float> >();//centered data
		ArrayList< ArrayList<Float> > b = new ArrayList<ArrayList<Float> >();
		//get average
		
		for(int i = 0 ; i<comp.size();i++)
		{
			ArrayList<Float> temp = comp.get(i).getValue();
			for(int j = 0;j < temp.size();j++)
			{
				float temptemp = avg.get(j);
				temptemp = temptemp + temp.get(j);
				avg.add(j, temptemp);
			}
		}
		for(int i = 0 ; i<comp1.size();i++)
		{
			ArrayList<Float> temp = comp1.get(i).getValue();
			for(int j = 0;j < temp.size();j++)
			{
				float temptemp = avg1.get(j);
				temptemp = temptemp + temp.get(j);
				avg1.add(j, temptemp);
			}
		}
		for(int i = 0;i<avg.size();i++)
		{
			float tmp = avg.get(i);
			tmp = tmp / comp1.size();
			avg.add(i,tmp);
			tmp = avg1.get(i);
			tmp = tmp / comp1.size();
			avg1.add(i,tmp);
		}
		//subtract variable by mean
		
		for(int i = 0 ; i < comp.size(); i++)
		{
			ArrayList<Float> temp = comp.get(i).getValue();
			for(int j = 0;j < temp.size();j++)
			{
				float temptemp = temp.get(j) - avg.get(j);
				temp.add(j,temptemp);//replace value with centered value
			}
			a.add(temp);
		}
		for(int i = 0 ; i < comp1.size() ; i++)
		{
			ArrayList<Float> temp = comp1.get(i).getValue();
			for(int j = 0;j < temp.size();j++)
			{
				float temptemp = temp.get(j) - avg1.get(j);
				temp.add(j,temptemp);//replace value with centered value
			}
			b.add(temp);
		}
		//now we have a and b centered value and avg and avg1 their averages
		// we need a*b and a2 and b^2
		
		
		ArrayList<Float> top = new ArrayList<Float>();//numerator
		
		ArrayList<Float> bota = new ArrayList<Float>();//denominator
		ArrayList<Float> botb = new ArrayList<Float>();
		
		for(int i = 0 ; i < comp1.size(); i++)
		{
			ArrayList<Float> temp = comp.get(i).getValue();
			ArrayList<Float> temp1 = comp1.get(i).getValue();
			for(int j = 0 ; j < temp.size() ; j++)
			{
				float temptemp = top.get(j);
				temptemp = temptemp + temp.get(j)*temp1.get(j);
				top.add(j,temptemp);//top
				
				temptemp = bota.get(j);
				temptemp = (float) (temptemp + Math.pow(temp.get(j), 2));
				bota.add(j,temptemp);//square a
				
				temptemp = botb.get(j);
				temptemp = (float) (temptemp + Math.pow(temp1.get(j), 2));
				botb.add(j,temptemp);//square b
			}
		}
		
		//need to divide and multiply
		for(int i = 0 ; i < top.size() ; i++)
		{
			float temp = top.get(i)/(bota.get(i)*botb.get(i));
			moo.add(i,temp);
		}
		
		return moo;//size of different variables
	}
		
		
	
	
	
	public ArrayList<Float> getEntropy()
		{
			ArrayList<Float> moo = new ArrayList<Float>();
			ArrayList<Float> average = new ArrayList<Float>();  
	          // 0-> avg of x and so on...
	for (SensorData sensorData : list) {
		G sensDesc = createSensorDescVectorValue(sensorData); // loop over the sensor data,get the object
		ArrayList<Float> temp = new ArrayList<Float>();       //temporary data
		temp = sensDesc.getValue();
		for(int i = 0; i < temp.size(); i++)                   //for each x,z & z
		{
			 float temptemp = average.get(i) + temp.get(i);   //add current data to the existing one and replace
			 average.set(i,temptemp); 
		}
		
	}
	
	ArrayList< ArrayList<Float> > prob = new ArrayList<ArrayList<Float> >();
	for (SensorData sensorData : list) {
		G sensDesc = createSensorDescVectorValue(sensorData); // loop over the sensor data,get the object
		ArrayList<Float> temp = new ArrayList<Float>();       //temporary data
		temp = sensDesc.getValue();
		for(int i = 0; i < temp.size(); i++)                  //for each x,z & z
		{
			 float temptemp = temp.get(i);                    //get x,y or z
			 temptemp = temptemp / average.get(i);
			 temp.add(i,temptemp);                            //now temp contains the probabilities of x,y,z
		}
		prob.add(temp);    
		
	}
	
	for(int i = 0;i<prob.size();i++)
	{
		ArrayList<Float> temp = new ArrayList<Float>();
		temp = prob.get(i);                                     //x,y,z of 1 reading
		for(int j = 0 ; j < temp.size(); j++)
		{
			float temptemp = moo.get(j);                        //get particular x,,y or z
			temptemp = (float) (temptemp + temp.get(j)*Math.log10(1/temp.get(j)));
			moo.add(j,temptemp);
		}
	}
	
	
	
	
			return moo;
		}
	
	public ArrayList<Float> getKMeans(int n,ArrayList<Float> init)
	{
		ArrayList<Float> moo = new ArrayList<Float>();
		
		
		return moo;
	}

	}
	
	



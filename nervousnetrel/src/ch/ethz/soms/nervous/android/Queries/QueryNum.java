package ch.ethz.soms.nervous.android.Queries;

import java.io.File;
import java.util.ArrayList;

import ch.ethz.soms.nervous.android.sensors.SensorDesc;


// i/p numerical values
public abstract class QueryNum<G extends SensorDesc> extends Query<G>{

	public QueryNum(long timestamp_from, long timestamp_to, File file) {
		super(timestamp_from, timestamp_to, file);
		// TODO Auto-generated constructor stub
	}
	
	// even some singlevaluedsensor functions give out an arraylist!!!
	
	public abstract G createDummyObject();
	
	public  abstract ArrayList<G> getSensorDescriptorList();
	
	public  abstract ArrayList<G> getTimeRange( ArrayList<G> desc_list,ArrayList<Float> start, ArrayList<Float> end);
	
	// d is applicable only for vector_valued_sensors
	// append the start and end values to the respective arraylists
	// according to the number of variables for each value
	
	public  abstract G getMaxValue();
	
	public abstract ArrayList<Float> sd();
	
	public abstract ArrayList<Float> var();
	
	public  abstract G getMinValue();
	
	public  abstract ArrayList<Float> getSum();
	
	public  abstract ArrayList<G> getLargest(int k);
	
	public  abstract ArrayList<G> getSmallest(int k);
	
	public abstract ArrayList<Float> getMedian();
	
	public abstract ArrayList<Float> getRms();
	
	public abstract ArrayList<Float> getMeanSquare();
	
	public abstract ArrayList<Float> getSumSquare(); 
	
	public abstract G getRankLargest(int n); 
	
	public abstract G getRankSmallest(int n); 
	
	public abstract ArrayList<Float> getRmsError(ArrayList<Float> comp);
	
	public abstract ArrayList<Float> getCorrelation(ArrayList<G> comp,ArrayList<G> comp1); //todo
	
	public abstract ArrayList<Float> getEntropy(); //todo
	
	public  abstract ArrayList<Float> getAverage(); 
	
	public abstract ArrayList<Float> getKMeans(int n, ArrayList<Float> init);
	
	
	
	



}

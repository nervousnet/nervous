package ch.ethz.soms.nervous.android.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesAccelerometer;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesBattery;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesLight;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesProximity;
import ch.ethz.soms.nervous.android.sensors.SensorDescAccelerometer;
import ch.ethz.soms.nervous.android.sensors.SensorDescAccelerometerNew;
import ch.ethz.soms.nervous.android.sensors.SensorDescBattery;
import ch.ethz.soms.nervous.android.sensors.SensorDescLight;
import ch.ethz.soms.nervous.android.sensors.SensorDescProximity;
import ch.ethz.soms.nervous.android.Queries.*;

public class TestQueries {
	
	private static final String DEBUG_TAG = TestQueries.class.getSimpleName();

	private Context context;
	private File filesDir;
	
	public TestQueries(Context context, File filesDir)
	{
		this.context = context;
		this.filesDir = filesDir;
	}
	
	

	private File getFilesDir() {
		return filesDir;
	}
	
	public void batteryRangeQuery(){
		
		SensorQueriesBattery batt = new SensorQueriesBattery(1,Long.MAX_VALUE,getFilesDir());
		if(batt.containsReadings())
		{
			ArrayList<SensorDescBattery> battlist = new ArrayList<SensorDescBattery>();
			ArrayList<Float> s = new ArrayList<Float>();
			ArrayList<Float> e  = new ArrayList<Float>();
			battlist = batt.getSensorDescriptorList();
			s.add((float) 0);
			e.add((float) 60);
			
			ArrayList<SensorDescBattery> battdesc = batt.getTimeRange(battlist, s, e);
			//Array of batterydescriptors within the range
			Log.d(DEBUG_TAG," Battery timerange executed "  );
		}
		
	}
	
	public void batteryMedian(){
		
		SensorQueriesBattery batt = new SensorQueriesBattery(1,Long.MAX_VALUE,getFilesDir());
		if(batt.containsReadings())
		{
			ArrayList<Float> b = batt.getMedian();
			Log.d(DEBUG_TAG,"Median Battery: " + b.get(0) );
		}
		
	}
	
	public void batteryMaxValue()
	{
		SensorQueriesBattery batt = new SensorQueriesBattery(1,Long.MAX_VALUE,getFilesDir());
		if(batt.containsReadings())
		{
			SensorDescBattery b = batt.getMaxValue();
			Float maxvalue = b.getValue();
			Log.d(DEBUG_TAG,"MaxValue Battery: " + maxvalue + "\nat " + getDate(b.getTimestamp()));
		}
	}
	
	public void batterySD(){
		
		SensorQueriesBattery batt = new SensorQueriesBattery(1,Long.MAX_VALUE,getFilesDir());
		if(batt.containsReadings())
		{
			ArrayList<Float> b = batt.sd();
			Float sdvalue = b.get(0);
			Log.d(DEBUG_TAG,"MaxValue Battery: " + sdvalue);
		}
		
	}
	
	

	public void minLight() {
		SensorQueriesLight sensorQ_Light2 = new SensorQueriesLight(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Light2.containsReadings()) {
			SensorDescLight minLightDesc = sensorQ_Light2.getMinValue();
			Log.d(DEBUG_TAG,"Minimum Light: " + minLightDesc.getLight() + "\nat " + getDate(minLightDesc.getTimestamp()));
		} else {
			Log.d(DEBUG_TAG,"No Data Found");
		}
	}
	
	/*class joinlp{
		public Float lvalue;
		public Float pvalue;
		public Long tsofvalue;
		
		joinlp(Float a,Float b,Long c)
		{
			lvalue = a;
			pvalue = b;
			tsofvalue = c;
		}
	}*/
	
	
	
	/*public ArrayList<joinlp> Join() // join queries
	{
		
		SensorQueriesLight light = new SensorQueriesLight(1, Long.MAX_VALUE, getFilesDir());
		SensorQueriesProximity proximity = new SensorQueriesProximity(1, Long.MAX_VALUE, getFilesDir());
		//for bottom10data
		ArrayList<SensorDescLight> light10 = light.getBottomK(10);
		ArrayList<SensorDescProximity> proximity10 = proximity.getBottomK(10);
		
		ArrayList<joinlp> joinedlp = new ArrayList<joinlp>();
		
		for(SensorDescLight i : light10)
		{
			Long currTime = i.getTimestamp();
			Long dummy = Long.MAX_VALUE;
			Float value = (float) 0;
			Long timestamp = (long) 0;
			int k =0;
			int best_index = 0;
			for(SensorDescProximity j : proximity10)
			{
				
				Long tempTime = j.getTimestamp();
				Long diff = Math.abs(tempTime-currTime);
				if(diff < dummy)
				{
					dummy = diff;
					value = j.getValue();
					timestamp = tempTime;
					best_index = k;
				}
				k++;
			}
			
			joinedlp.add(new joinlp(i.getValue(),value,timestamp));
			proximity10.remove(best_index);
			
		}
		
		
		return joinedlp;
}*/
	
	
	public void maxLight() {
		SensorQueriesLight sensorQ_Light = new SensorQueriesLight(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Light.containsReadings()) {
			SensorDescLight maxLightDesc = sensorQ_Light.getMaxValue();
			Log.d(DEBUG_TAG,"Maximum Light: " + maxLightDesc.getLight() + "\nat " + getDate(maxLightDesc.getTimestamp()));
		} else {
			Log.d(DEBUG_TAG,"No Data Found");
		}
	}

	public void bottom10Light() {
		SensorQueriesLight sensorQ_Light = new SensorQueriesLight(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Light.containsReadings()) {
			ArrayList<SensorDescLight> topKLightDesc = sensorQ_Light.getSmallest(10);
			int i = 1;
			Log.d(DEBUG_TAG, "Bottom 10 Light:");
			for (SensorDescLight bat : topKLightDesc) {
				Log.d(DEBUG_TAG, i++ + ": " + bat.getLight() + " Date: " + getDate(bat.getTimestamp()));
			}
			Log.d(DEBUG_TAG,"Bottom 10 logged");
		} else {
			Log.d(DEBUG_TAG,"No Data Found");
		}
	}

	public void top10Light() {
		SensorQueriesLight sensorQ_Light = new SensorQueriesLight(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Light.containsReadings()) {
			ArrayList<SensorDescLight> topKLightDesc = sensorQ_Light.getLargest(10);
			int i = 1;
			Log.d(DEBUG_TAG, "Top 10 Light:");
			for (SensorDescLight light : topKLightDesc) {
				Log.d(DEBUG_TAG, i++ + ": " + light.getLight() + " Date: " + getDate(light.getTimestamp()));
			}
			Log.d(DEBUG_TAG,"Top 10 logged");
		} else {
			Log.d(DEBUG_TAG,"No Data Found");
		}
	}
	
	
	public void minProximity() {
		SensorQueriesProximity sensorQ_Proximity = new SensorQueriesProximity(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Proximity.containsReadings()) {
			SensorDescProximity minProxDesc = sensorQ_Proximity.getMinValue();
			Log.d(DEBUG_TAG,"Minimum Proximity: " + minProxDesc.getProximity() + "\nat " + getDate(minProxDesc.getTimestamp()));
		} else {
			Log.d(DEBUG_TAG,"No Data Found");
		}
	}

	public void maxProximity() {
		SensorQueriesProximity sensorQ_Prox = new SensorQueriesProximity(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Prox.containsReadings()) {
			SensorDescProximity maxProxDesc = sensorQ_Prox.getMaxValue();
			Log.d(DEBUG_TAG,"Maximum Prox: " + maxProxDesc.getProximity() + "\nat " + getDate(maxProxDesc.getTimestamp()));
		} else {
			Log.d(DEBUG_TAG,"No Data Found ");
		}
	}

	public void bottom10Proximity() {
		SensorQueriesProximity sensorQ_Prox = new SensorQueriesProximity(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Prox.containsReadings()) {
			ArrayList<SensorDescProximity> topKProxDesc = sensorQ_Prox.getSmallest(10);
			int i = 1;
			Log.d(DEBUG_TAG, "Bottom 10 Prox:");
			for (SensorDescProximity proxDesc : topKProxDesc) {
				Log.d(DEBUG_TAG, i++ + ": " + proxDesc.getProximity() + " Date: " + getDate(proxDesc.getTimestamp()));
			}
			Log.d(DEBUG_TAG,"Bottom 10 logged");
		} else {
			Log.d(DEBUG_TAG,"No Data Found");
		}
	}

	public void top10Proximity() {
		SensorQueriesProximity sensorQ_Light = new SensorQueriesProximity(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Light.containsReadings()) {
			ArrayList<SensorDescProximity> topKProxDesc = sensorQ_Light.getLargest(10);
			int i = 1;
			Log.d(DEBUG_TAG, "Top 10 Prox:");
			for (SensorDescProximity proxDesc : topKProxDesc) {
				Log.d(DEBUG_TAG, i++ + ": " + proxDesc.getProximity() + " Date: " + getDate(proxDesc.getTimestamp()));
			}
			Log.d(DEBUG_TAG,"Top 10 logged");
		} else {
			Log.d(DEBUG_TAG,"No Data Found");
		}
	}

	public void maxAverageAccelerometer() {
		SensorQueriesAccelerometer sensorQ_Accel = new SensorQueriesAccelerometer(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Accel.containsReadings()) {
			SensorDescAccelerometerNew maxAccAverageSensDesc = sensorQ_Accel.getMaxValue();
			Log.d(DEBUG_TAG,"Maximum Accelerometer Average: \n x:" + maxAccAverageSensDesc.getAccX() + "\ny: " + maxAccAverageSensDesc.getAccY() + "\nz: " + maxAccAverageSensDesc.getAccZ() + "\nDate: " + getDate(maxAccAverageSensDesc.getTimestamp()));
		} else {
			Log.d(DEBUG_TAG,"No Data Found");
		}
	}

	public void minAverageAccelerometer() {
		SensorQueriesAccelerometer sensorQ_Accel = new SensorQueriesAccelerometer(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Accel.containsReadings()) {
			SensorDescAccelerometerNew minAccAverageSensDesc = sensorQ_Accel.getMinValue();
			Log.d(DEBUG_TAG,"Minimum Accelerometer Average: \n x:" + minAccAverageSensDesc.getAccX() + "\ny: " + minAccAverageSensDesc.getAccY() + "\nz: " + minAccAverageSensDesc.getAccZ() + "\nDate: " + getDate(minAccAverageSensDesc.getTimestamp()));
		} else {
			Log.d(DEBUG_TAG,"No Data Found");
		}
	}

	public void maxBattery() {
		SensorQueriesBattery sensorQ_Batteries2 = new SensorQueriesBattery(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Batteries2.containsReadings()) {
			SensorDescBattery maxBatDesc = sensorQ_Batteries2.getMaxValue();
			Log.d(DEBUG_TAG,"Max Battery: " + maxBatDesc.getBatteryPercent() + "\nat " + getDate(maxBatDesc.getTimestamp()));
		} else {
			Log.d(DEBUG_TAG,"No Data Found");
		}
	}

	public void minBattery() {
		SensorQueriesBattery sensorQ_Batteries = new SensorQueriesBattery(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Batteries.containsReadings()) {
			SensorDescBattery minBatDesc = sensorQ_Batteries.getMinValue();
			Log.d(DEBUG_TAG,"Minimum Battery: " + minBatDesc.getBatteryPercent() + "\nat " + getDate(minBatDesc.getTimestamp()));
		} else {
			Log.d(DEBUG_TAG,"No Data Found");
		}
	}

	public void bottom10Battery() {
		SensorQueriesBattery sensorQ_Batteries = new SensorQueriesBattery(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Batteries.containsReadings()) {
			ArrayList<SensorDescBattery> topKBatDesc = sensorQ_Batteries.getSmallest(10);
			int i = 1;
			Log.d(DEBUG_TAG, "Bottom 10 Bat:");
			for (SensorDescBattery bat : topKBatDesc) {
				Log.d(DEBUG_TAG,"Bottom 10 logged");
				Log.d(DEBUG_TAG, i++ + ": " + bat.getBatteryPercent() + " Date: " + getDate(bat.getTimestamp()));
			}
		} else {
			Log.d(DEBUG_TAG,"No Data Found");
		}
	}

	public void top10Battery() {
		SensorQueriesBattery sensorQ_Batteries = new SensorQueriesBattery(1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Batteries.containsReadings()) {
			ArrayList<SensorDescBattery> topKBatDesc = sensorQ_Batteries.getLargest(10);
			int i = 1;
			Log.d(DEBUG_TAG, "Top 10 Bat:");
			for (SensorDescBattery bat : topKBatDesc) {
				toastToScreen("Top 10 logged", false);
				Log.d(DEBUG_TAG, i++ + ": " + bat.getBatteryPercent() + " Date: " + getDate(bat.getTimestamp()));
			}
		} else {
			Log.d(DEBUG_TAG,"No Data Found");
		}
	}
	
	public void toastToScreen(String msg, boolean lengthLong) {

		int toastLength = lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
		Toast.makeText(context, msg, toastLength).show();
	}
	
	private String getDate(long time) {
		Calendar cal = Calendar.getInstance(Locale.ENGLISH);
		cal.setTimeInMillis(time);
		String date = DateFormat.format("dd-MM-yyyy HH:mm:ss", cal).toString();
		return date;
	}
}

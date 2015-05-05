package ch.ethz.soms.nervous.android;

import java.util.ArrayList;
import java.util.Calendar;

import ch.ethz.soms.nervous.android.Queries.QueryNumSingleValue;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesAccelerometer;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesBattery;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesGyroscope;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesHumidity;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesLight;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesMagnetic;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesPressure;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesProximity;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesTemperature;
import ch.ethz.soms.nervous.android.sensors.SensorDescAccelerometerNew;
import ch.ethz.soms.nervous.android.sensors.SensorDescGyroscopeNew;
import ch.ethz.soms.nervous.android.sensors.SensorDescMagneticNew;
import ch.ethz.soms.nervous.android.sensors.SensorDescSingleValue;
import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

public class ChartsWebViewActivity extends Activity {

	private WebView webView;
	private String selected_sensor;
	private boolean start,initialized = false;
	private float lastValueX,lastValueY,lastValueZ;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		 * Load the webview that shows the plot from the corresponding html file
		 * in assets
		 */

		setContentView(R.layout.charts_webview);

		// To debug webview
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		// WebView.setWebContentsDebuggingEnabled(true);
		// }

		// Get javascript variable from intent and set it into the webview
		String javascript_global_variables = getIntent().getStringExtra(
				"javascript_global_variables");
		Log.i("javascript var: ", javascript_global_variables);
		String type_of_plot = getIntent().getStringExtra("type_of_plot");
		
		// Get selected sensor from sensors statistics activity
		selected_sensor = getIntent().getStringExtra("selected_sensor");

		webView = (WebView) findViewById(R.id.webView_charts);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("javascript:" + javascript_global_variables);
		webView.loadUrl("file:///android_asset/webview_charts_" + type_of_plot
				+ ".html");
		
		start = true;

		if (type_of_plot.equals("1_line_live_data_over_time") || type_of_plot.equals("3_lines_live_data_over_time"))
			updateData();
		else findViewById(R.id.waiting_for_sensor_data_textView).setVisibility(View.GONE);
	}
	
	//1 value sensors
	private void plotLastValue(int mYear,int mMonth,int mDay,int hr,int min,int sec)
	{
		webView.loadUrl("javascript:" + "point = " + "[Date.UTC("
				+ mYear + "," + mMonth + "," + mDay + "," + hr
				+ "," + min + "," + sec + "),"
				+ lastValueX+ "];");
	}
	
	//1 value sensors
	private void plotZeroValue(int mYear,int mMonth,int mDay,int hr,int min,int sec)
	{
		webView.loadUrl("javascript:" + "point = " + "{x: Date.UTC("
				+ mYear + "," + mMonth + "," + mDay + "," + hr
				+ "," + min + "," + sec + "), y: "
				+ (float)0.0 + ", color: 'orange'};");
	}
	
	//3 values sensors
	private void plotLastValues(int mYear,int mMonth,int mDay,int hr,int min,int sec)
	{
		webView.loadUrl("javascript:" + "point0 = " + "[Date.UTC("
				+ mYear + "," + mMonth + "," + mDay + "," + hr
				+ "," + min + "," + sec + "),"
				+ lastValueX + "];" + "point1 = " + "[Date.UTC("
				+ mYear + "," + mMonth + "," + mDay + "," + hr
				+ "," + min + "," + sec + "),"
				+ lastValueY + "];" + "point2 = " + "[Date.UTC("
				+ mYear + "," + mMonth + "," + mDay + "," + hr
				+ "," + min + "," + sec + "),"
				+ lastValueZ + "];");
	}
	
	//3 values sensors
	private void plotZeroValues(int mYear,int mMonth,int mDay,int hr,int min,int sec)
	{
		webView.loadUrl("javascript:" + "point0 = " + "{x: Date.UTC("
				+ mYear + "," + mMonth + "," + mDay + "," + hr
				+ "," + min + "," + sec + "), y: "
				+ (float)0.0 + ", color: 'orange'}; point1 = " + "{x: Date.UTC("
				+ mYear + "," + mMonth + "," + mDay + "," + hr
				+ "," + min + "," + sec + "), y: "
				+ (float)0.0 + ", color: 'orange'}; point2 = " + "{x: Date.UTC("
				+ mYear + "," + mMonth + "," + mDay + "," + hr
				+ "," + min + "," + sec + "), y: "
				+ (float)0.0 + ", color: 'orange'};");
	}
	
	public <T extends SensorDescSingleValue> void displaySingleSensorValue(QueryNumSingleValue<T> ssvq,long fromTimestamp)
	{
		Calendar c = Calendar.getInstance();

		c.setTimeInMillis(fromTimestamp);
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH);
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		int hr = c.get(Calendar.HOUR_OF_DAY);
		int min = c.get(Calendar.MINUTE);
		int sec = c.get(Calendar.SECOND);
		
		if (ssvq.containsReadings()) {
			findViewById(R.id.waiting_for_sensor_data_textView).setVisibility(View.GONE);
			ArrayList<T> sensorDescs = ssvq.getSensorDescriptorList();
			
			lastValueX = sensorDescs.get(sensorDescs.size()-1).getValue();
			plotLastValue(mYear,mMonth,mDay,hr,min,sec);
			initialized = true;
		} else if(initialized)
		{
			plotLastValue(mYear,mMonth,mDay,hr,min,sec);
		} else
		{
			plotZeroValue(mYear,mMonth,mDay,hr,min,sec);
		}
	}
	
	private void updateData() {	
		new CountDownTimer(30000, 1000) {
			public void onTick(long millisUntilFinished) {
				long toTimestamp = System.currentTimeMillis();
				long pastWindow = 1000;
				if(start)
				{
					pastWindow = 60000;
					start = false;
				}
				long fromTimestamp = toTimestamp - pastWindow;
				
				if (selected_sensor.equalsIgnoreCase("Accelerometer"))
		        {
					SensorQueriesAccelerometer sensorQuery = new SensorQueriesAccelerometer(
		                    fromTimestamp, toTimestamp, getFilesDir());
					
					Calendar c = Calendar.getInstance();

					c.setTimeInMillis(fromTimestamp);
					int mYear = c.get(Calendar.YEAR);
					int mMonth = c.get(Calendar.MONTH);
					int mDay = c.get(Calendar.DAY_OF_MONTH);
					int hr = c.get(Calendar.HOUR_OF_DAY);
					int min = c.get(Calendar.MINUTE);
					int sec = c.get(Calendar.SECOND);
					
					if (sensorQuery.containsReadings()) {
						findViewById(R.id.waiting_for_sensor_data_textView).setVisibility(View.GONE);
						ArrayList<SensorDescAccelerometerNew> sensorDescs = sensorQuery.getSensorDescriptorList();

						lastValueX = sensorDescs.get(sensorDescs.size()-1).getAccX();
						lastValueY = sensorDescs.get(sensorDescs.size()-1).getAccY();
						lastValueZ = sensorDescs.get(sensorDescs.size()-1).getAccZ();
						plotLastValues(mYear,mMonth,mDay,hr,min,sec);
						initialized = true;
					} else if(initialized)
					{
						plotLastValues(mYear,mMonth,mDay,hr,min,sec);
					} else
					{
						plotZeroValues(mYear,mMonth,mDay,hr,min,sec);
					}
					
		        } else if (selected_sensor.equalsIgnoreCase("Battery"))
		        { 
		            SensorQueriesBattery sensorQuery = new SensorQueriesBattery(
		                    fromTimestamp, toTimestamp, getFilesDir());
		            displaySingleSensorValue(sensorQuery,fromTimestamp);
		        } else if (selected_sensor.equalsIgnoreCase("Gyroscope"))
		        {
		            SensorQueriesGyroscope sensorQuery = new SensorQueriesGyroscope(
		                    fromTimestamp, toTimestamp, getFilesDir());
		            
		            Calendar c = Calendar.getInstance();

					c.setTimeInMillis(fromTimestamp);
					int mYear = c.get(Calendar.YEAR);
					int mMonth = c.get(Calendar.MONTH);
					int mDay = c.get(Calendar.DAY_OF_MONTH);
					int hr = c.get(Calendar.HOUR_OF_DAY);
					int min = c.get(Calendar.MINUTE);
					int sec = c.get(Calendar.SECOND);
					
					if (sensorQuery.containsReadings()) {
						findViewById(R.id.waiting_for_sensor_data_textView).setVisibility(View.GONE);
						ArrayList<SensorDescGyroscopeNew> sensorDescs = sensorQuery.getSensorDescriptorList();
						
						lastValueX = sensorDescs.get(sensorDescs.size()-1).getGyrX();
						lastValueY = sensorDescs.get(sensorDescs.size()-1).getGyrY();
						lastValueZ = sensorDescs.get(sensorDescs.size()-1).getGyrZ();
						plotLastValues(mYear,mMonth,mDay,hr,min,sec);
						initialized = true;
					} else if(initialized)
					{
						plotLastValues(mYear,mMonth,mDay,hr,min,sec);
					} else
					{
						plotZeroValues(mYear,mMonth,mDay,hr,min,sec);
					}
					
		        } else if (selected_sensor.equalsIgnoreCase("Humidity"))
		        {
		            SensorQueriesHumidity sensorQuery = new SensorQueriesHumidity(
		                    fromTimestamp, toTimestamp, getFilesDir());
		            displaySingleSensorValue(sensorQuery,fromTimestamp);
		        } else if (selected_sensor.equalsIgnoreCase("Light"))
		        {    
		            SensorQueriesLight sensorQuery = new SensorQueriesLight(
		                    fromTimestamp, toTimestamp, getFilesDir());
		            displaySingleSensorValue(sensorQuery,fromTimestamp);
		        } else if (selected_sensor.equalsIgnoreCase("Magnetic"))
		        {
		            SensorQueriesMagnetic sensorQuery = new SensorQueriesMagnetic(
		                    fromTimestamp, toTimestamp, getFilesDir());
		            
		            Calendar c = Calendar.getInstance();

					c.setTimeInMillis(fromTimestamp);
					int mYear = c.get(Calendar.YEAR);
					int mMonth = c.get(Calendar.MONTH);
					int mDay = c.get(Calendar.DAY_OF_MONTH);
					int hr = c.get(Calendar.HOUR_OF_DAY);
					int min = c.get(Calendar.MINUTE);
					int sec = c.get(Calendar.SECOND);
					
					if (sensorQuery.containsReadings()) {
						findViewById(R.id.waiting_for_sensor_data_textView).setVisibility(View.GONE);
						ArrayList<SensorDescMagneticNew> sensorDescs = sensorQuery.getSensorDescriptorList();

						lastValueX = sensorDescs.get(sensorDescs.size()-1).getMagX();
						lastValueY = sensorDescs.get(sensorDescs.size()-1).getMagY();
						lastValueZ = sensorDescs.get(sensorDescs.size()-1).getMagZ();
						plotLastValues(mYear,mMonth,mDay,hr,min,sec);
						initialized = true;
					} else if(initialized)
					{
						plotLastValues(mYear,mMonth,mDay,hr,min,sec);
					} else
					{
						plotZeroValues(mYear,mMonth,mDay,hr,min,sec);
					}
					
		        } else if (selected_sensor.equalsIgnoreCase("Proximity"))
		        {
		            SensorQueriesProximity sensorQuery = new SensorQueriesProximity(
		                    fromTimestamp, toTimestamp, getFilesDir());
		            displaySingleSensorValue(sensorQuery,fromTimestamp);
		        } else if (selected_sensor.equalsIgnoreCase("Temperature"))
		        {
		            SensorQueriesTemperature sensorQuery = new SensorQueriesTemperature(
		                    fromTimestamp, toTimestamp, getFilesDir());
		            displaySingleSensorValue(sensorQuery,fromTimestamp);
		        } else if (selected_sensor.equalsIgnoreCase("Pressure"))
		        {
		            SensorQueriesPressure sensorQuery= new SensorQueriesPressure(
		                    fromTimestamp, toTimestamp, getFilesDir());
		            displaySingleSensorValue(sensorQuery,fromTimestamp);
		        } else if (selected_sensor.equalsIgnoreCase("Microphone"))
		        {
		        	//TODO (API not ready)
		        } else if (selected_sensor.equalsIgnoreCase("Connectivity"))
		        {
		            //TODO (API not ready)
		        } else if (selected_sensor.equalsIgnoreCase("Noise"))
		        {
		            //TODO (API not ready)
		        }
			}

			public void onFinish() {
				updateData();
			}

		}.start();
	}
}
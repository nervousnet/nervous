package ch.ethz.soms.nervous.android;

import java.util.ArrayList;
import java.util.Calendar;

import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesAccelerometer;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesBattery;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesGyroscope;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesHumidity;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesLight;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesMagnetic;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesPressure;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesProximity;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesTemperature;
import ch.ethz.soms.nervous.android.sensorQueries.SensorSingleValueQueries;
import ch.ethz.soms.nervous.android.sensors.SensorDescBattery;
import ch.ethz.soms.nervous.android.sensors.SensorDescLight;
import ch.ethz.soms.nervous.android.sensors.SensorDescSingleValue;
import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.Time;
import android.util.Log;
import android.view.Display;
import android.webkit.WebView;

public class ChartsWebViewActivity extends Activity {

	private WebView webView;
	private String selected_sensor;

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

		if (type_of_plot.equals("live_data_over_time"))
			updateData();
	}

	public void displaySingleSensorValue(SensorSingleValueQueries<SensorDescSingleValue> ssvq,long fromTimestamp) {

		if (ssvq.containsReadings()) {
			ArrayList<SensorDescSingleValue> sensorDescs = ssvq.getSensorDescriptorList();

			Calendar c = Calendar.getInstance();

			c.setTimeInMillis(fromTimestamp);
			int mYear = c.get(Calendar.YEAR);
			int mMonth = c.get(Calendar.MONTH);
			int mDay = c.get(Calendar.DAY_OF_MONTH);
			int hr = c.get(Calendar.HOUR_OF_DAY);
			int min = c.get(Calendar.MINUTE);
			int sec = c.get(Calendar.SECOND);

			webView.loadUrl("javascript:" + "point = " + "[Date.UTC("
					+ mYear + "," + mMonth + "," + mDay + "," + hr
					+ "," + min + "," + sec + "),"
					+ sensorDescs.get(sensorDescs.size()-1).getValue() + "];");
		}
	}
	
	private void updateData() {	
		new CountDownTimer(30000, 1000) {
			public void onTick(long millisUntilFinished) {
				long toTimestamp = System.currentTimeMillis();
				long fromTimestamp = toTimestamp - 60000;
				
				if (selected_sensor.equalsIgnoreCase("Accelerometer"))
		        {
					SensorQueriesAccelerometer sensorQuery = new SensorQueriesAccelerometer(
		                    fromTimestamp, toTimestamp, getFilesDir());
					//TODO display multiple value sensor
		        } else if (selected_sensor.equalsIgnoreCase("Battery"))
		        { 
		            SensorQueriesBattery sensorQuery = new SensorQueriesBattery(
		                    fromTimestamp, toTimestamp, getFilesDir());
		            displaySingleSensorValue(sensorQuery,fromTimestamp);
		        } else if (selected_sensor.equalsIgnoreCase("Gyroscope"))
		        {
		            SensorQueriesGyroscope sensorQuery = new SensorQueriesGyroscope(
		                    fromTimestamp, toTimestamp, getFilesDir());
		        } else if (selected_sensor.equalsIgnoreCase("Humidity"))
		        {
		            SensorQueriesHumidity sensorQuery = new SensorQueriesHumidity(
		                    fromTimestamp, toTimestamp, getFilesDir());
		        } else if (selected_sensor.equalsIgnoreCase("Light"))
		        {    
		            SensorQueriesLight sensorQuery = new SensorQueriesLight(
		                    fromTimestamp, toTimestamp, getFilesDir());
		        } else if (selected_sensor.equalsIgnoreCase("Magnetic"))
		        {
		            SensorQueriesMagnetic sensorQuery = new SensorQueriesMagnetic(
		                    fromTimestamp, toTimestamp, getFilesDir());
		        } else if (selected_sensor.equalsIgnoreCase("Proximity"))
		        {
		            SensorQueriesProximity sensorQuery = new SensorQueriesProximity(
		                    fromTimestamp, toTimestamp, getFilesDir());
		        } else if (selected_sensor.equalsIgnoreCase("Temperature"))
		        {
		            SensorQueriesTemperature sensorQuery = new SensorQueriesTemperature(
		                    fromTimestamp, toTimestamp, getFilesDir());
		        } else if (selected_sensor.equalsIgnoreCase("Pressure"))
		        {
		            SensorQueriesPressure sensorQuery= new SensorQueriesPressure(
		                    fromTimestamp, toTimestamp, getFilesDir());
		        } else if (selected_sensor.equalsIgnoreCase("Microphone"))
		        {
//		            SensorQueriesMicrophone sensorQ_Microphone= new SensorQueriesMicrophone(
//		                    fromTimestamp, toTimestamp, getFilesDir());
		        } else if (selected_sensor.equalsIgnoreCase("Connectivity"))
		        {
//		            //TODO
		        }
			}

			public void onFinish() {
				updateData();
			}

		}.start();
	}
}
package ch.ethz.soms.nervous.android;

import java.util.ArrayList;
import java.util.Calendar;

import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesBattery;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesLight;
import ch.ethz.soms.nervous.android.sensors.SensorDescBattery;
import ch.ethz.soms.nervous.android.sensors.SensorDescLight;
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

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* Load the webview that shows the plot from the corresponding html file in assets */
		
		setContentView(R.layout.charts_webview);

		//To debug webview
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            WebView.setWebContentsDebuggingEnabled(true);
//        }

		// Get javascript variable from intent and set it into the webview
        String javascript_global_variables = getIntent().getStringExtra("javascript_global_variables");
        Log.i("javascript var: ",javascript_global_variables);
        String type_of_plot = getIntent().getStringExtra("type_of_plot");

		webView = (WebView) findViewById(R.id.webView_charts);
		webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("javascript:" + javascript_global_variables);
        webView.loadUrl("file:///android_asset/webview_charts_"+type_of_plot+".html");
        
        if(type_of_plot.equals("live_data_over_time"))
        	updateData();
	}
	
	private void updateData()
	{
		new CountDownTimer(30000, 1000) {
		     public void onTick(long millisUntilFinished) {
		    	 	long toTimestamp = System.currentTimeMillis();
				    long fromTimestamp = toTimestamp-60000;
	                SensorQueriesBattery sensorQ_Battery = new SensorQueriesBattery(
	                        fromTimestamp, toTimestamp, getFilesDir());
	                if (sensorQ_Battery.containsReadings())
	                {
	                    ArrayList<SensorDescBattery> sensorDescs = sensorQ_Battery.getSensorDescriptorList();
	                    SensorDescBattery sensorDesc;

	                    Calendar c = Calendar.getInstance();

	                        sensorDesc = sensorDescs.get(sensorDescs.size()-1);
	                        c.setTimeInMillis(fromTimestamp);
	                        int mYear = c.get(Calendar.YEAR);
	                        int mMonth = c.get(Calendar.MONTH);
	                        int mDay = c.get(Calendar.DAY_OF_MONTH);
	                        int hr = c.get(Calendar.HOUR_OF_DAY);
	                        int min = c.get(Calendar.MINUTE);
	                        int sec = c.get(Calendar.SECOND);

	                        webView.loadUrl("javascript:" + "point = " + "[Date.UTC("+mYear+","+mMonth+","+mDay+","+hr+","+min+","+sec+"),"+sensorDesc.getBatteryPercent()+"];");
	                        Log.i("charts","inside");
	                }
		     }

		     public void onFinish() {
		        updateData();
		     }
		  }.start();
	}
}
package ch.ethz.soms.nervous.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesAccelerometer;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesBattery;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesGyroscope;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesHumidity;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesLight;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesMagnetic;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesPressure;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesProximity;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesTemperature;
import ch.ethz.soms.nervous.android.sensors.SensorDescAccelerometer;
import ch.ethz.soms.nervous.android.sensors.SensorDescAccelerometerNew;
import ch.ethz.soms.nervous.android.sensors.SensorDescBattery;
import ch.ethz.soms.nervous.android.sensors.SensorDescGyroscope;
import ch.ethz.soms.nervous.android.sensors.SensorDescGyroscopeNew;
import ch.ethz.soms.nervous.android.sensors.SensorDescHumidity;
import ch.ethz.soms.nervous.android.sensors.SensorDescLight;
import ch.ethz.soms.nervous.android.sensors.SensorDescMagnetic;
import ch.ethz.soms.nervous.android.sensors.SensorDescMagneticNew;
import ch.ethz.soms.nervous.android.sensors.SensorDescPressure;
import ch.ethz.soms.nervous.android.sensors.SensorDescProximity;
import ch.ethz.soms.nervous.android.sensors.SensorDescTemperature;
import ch.ethz.soms.nervous.utils.HighlightArrayAdapter;

public class SensorsStatisticsActivity extends Activity {

    private static float MAX_NUMBER_PLOT_POINTS = (float)30.0;
    // hours in 24hours format
    private int fromTimeHour,fromTimeMin,fromDateDayOfMonth,fromDateMonth,fromDateYear,toTimeHour,toTimeMin,toDateDayOfMonth,toDateMonth,toDateYear;
    private TimePicker fromTimePicker,toTimePicker;
    private DatePicker fromDatePicker,toDatePicker;
    private boolean serviceSwitchIsChecked = false;
    private HighlightArrayAdapter<String> arrayAdapter;
    
    private String[] getActiveSensorsArray()
    {
    	ArrayList<String> sensors = new ArrayList<String>();
    	if(SensorConfiguration.getInstance(getApplicationContext()).getInitialSensorCollectStatus(SensorDescAccelerometer.SENSOR_ID).isCollect() || SensorConfiguration.getInstance(getApplicationContext()).getInitialSensorCollectStatus(SensorDescAccelerometerNew.SENSOR_ID).isCollect())
    	{
    		sensors.add("Accelerometer");
    	}
    	if(SensorConfiguration.getInstance(getApplicationContext()).getInitialSensorCollectStatus(SensorDescBattery.SENSOR_ID).isCollect())
    	{
    		sensors.add("Battery");
    	}
    	//TODO add when API to query it is ready
//    	if(SensorConfiguration.getInstance(getApplicationContext()).getInitialSensorCollectStatus(SensorDescConnectivity.SENSOR_ID).isCollect())
//    	{
//    		sensors.add("Connectivity");
//    	}
    	if(SensorConfiguration.getInstance(getApplicationContext()).getInitialSensorCollectStatus(SensorDescGyroscope.SENSOR_ID).isCollect())
    	{
    		sensors.add("Gyroscope");
    	}
    	if(SensorConfiguration.getInstance(getApplicationContext()).getInitialSensorCollectStatus(SensorDescHumidity.SENSOR_ID).isCollect())
    	{
    		sensors.add("Humidity");
    	}
    	if(SensorConfiguration.getInstance(getApplicationContext()).getInitialSensorCollectStatus(SensorDescMagnetic.SENSOR_ID).isCollect() || SensorConfiguration.getInstance(getApplicationContext()).getInitialSensorCollectStatus(SensorDescMagneticNew.SENSOR_ID).isCollect())
    	{
    		sensors.add("Magnetic");
    	}
    	//TODO add when API to query it is ready
//    	if(SensorConfiguration.getInstance(getApplicationContext()).getInitialSensorCollectStatus(SensorDescMicrophone.SENSOR_ID).isCollect())
//    	{
//    		sensors.add("Microphone");
//    	}
    	//TODO add when API to query it is ready
//    	if(SensorConfiguration.getInstance(getApplicationContext()).getInitialSensorCollectStatus(SensorDescNoise.SENSOR_ID).isCollect())
//    	{
//    		sensors.add("Noise");
//    	}
    	if(SensorConfiguration.getInstance(getApplicationContext()).getInitialSensorCollectStatus(SensorDescPressure.SENSOR_ID).isCollect())
    	{
    		sensors.add("Pressure");
    	}
    	if(SensorConfiguration.getInstance(getApplicationContext()).getInitialSensorCollectStatus(SensorDescProximity.SENSOR_ID).isCollect())
    	{
    		sensors.add("Proximity");
    	}
    	if(SensorConfiguration.getInstance(getApplicationContext()).getInitialSensorCollectStatus(SensorDescTemperature.SENSOR_ID).isCollect())
    	{
    		sensors.add("Temperature");
    	}
    	
    	return sensors.toArray(new String[sensors.size()]);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /* Initialize view components */
        setContentView(R.layout.activity_sensors_statistics);
        
        serviceSwitchIsChecked = getIntent().getBooleanExtra("serviceSwitchIsChecked", false);

        final ListView sensorsListView = ((ListView)findViewById(R.id.sensors_list_SensStatChart));
        sensorsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        arrayAdapter = new HighlightArrayAdapter<String>(this,android.R.layout.simple_list_item_1,getActiveSensorsArray());
        sensorsListView.setAdapter(arrayAdapter);

        sensorsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	sensorsListView.setItemChecked(position, true);
                view.setBackgroundColor(Color.rgb(77, 148, 255));
                arrayAdapter.setSelectedItem(position);
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    public void toastToScreen(String msg, boolean lengthLong) {

		int toastLength = lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
		Toast.makeText(getApplicationContext(), msg, toastLength).show();
	}
    
    public void nextButtonRealTimeClicked(View view)
    {
    	/* Button to show real time plots clicked */	
    	if(!serviceSwitchIsChecked)
    	{
    		//If sensor data reading is disable
    		toastToScreen("Please, check the switch in the home view to enable sensor data collection!",true);
    		return;
    	}
    	if(arrayAdapter.getSelectedItem() == -1)
        {
            // If none of the sensors is selected
            toastToScreen("Please, select a sensor from the list.",true);
            return;
        }
        
    	generatePlot(true);
    }
    
    public void nextButtonTimeRangeClicked(View view)
    {
    	if(arrayAdapter.getSelectedItem() == -1)
        {
            // If none of the sensors is selected
            Toast.makeText(this, "Please, select a sensor from the list.",Toast.LENGTH_SHORT).show();
            return;
        }
    	
    	/* Show a dialog to make user select from/to dates and times */
    	LayoutInflater inflater = getLayoutInflater();
    	View dialoglayout = inflater.inflate(R.layout.time_range_plots_input,null);
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setView(dialoglayout);
    	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	onFromToSelected();
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Do nothing
            }
        });
    	
        fromTimePicker = ((TimePicker) dialoglayout.findViewById(R.id.fromTimePicker));
        toTimePicker = ((TimePicker) dialoglayout.findViewById(R.id.toTimePicker));
        fromDatePicker = ((DatePicker) dialoglayout.findViewById(R.id.fromDatePicker));
        toDatePicker = ((DatePicker) dialoglayout.findViewById(R.id.toDatePicker));

        fromTimePicker.setIs24HourView(true);
        toTimePicker.setIs24HourView(true);
    	
        TabHost tabHost = (TabHost)dialoglayout.findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tab1 = tabHost.newTabSpec("From");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("To");

        tab1.setIndicator("From");
        tab1.setContent(R.id.From);
        tab2.setIndicator("To");
        tab2.setContent(R.id.To);

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        
        builder.show();
    }
    
    public void onFromToSelected()
    {	
    	/* Button to show charts for time range clicked */
        fromTimeHour = fromTimePicker.getCurrentHour();
        fromTimeMin = fromTimePicker.getCurrentMinute();
        fromDateDayOfMonth = fromDatePicker.getDayOfMonth();
        fromDateMonth = fromDatePicker.getMonth()+1;
        fromDateYear = fromDatePicker.getYear();

        toTimeHour = toTimePicker.getCurrentHour();
        toTimeMin = toTimePicker.getCurrentMinute();
        toDateDayOfMonth = toDatePicker.getDayOfMonth();
        toDateMonth = toDatePicker.getMonth()+1;
        toDateYear = toDatePicker.getYear();

        Time now = new Time();
        now.setToNow();

        //Checks for selected dates (that are before current one and from is not after to)
        if(fromDateYear > now.year || (fromDateYear == now.year && fromDateMonth > (now.month+1))
                || (fromDateYear == now.year && fromDateMonth == (now.month+1) && fromDateDayOfMonth > now.monthDay)
                || (fromDateYear == now.year && fromDateMonth == (now.month+1)&& fromDateDayOfMonth == now.monthDay && fromTimeHour > now.hour)
                || (fromDateYear == now.year && fromDateMonth == (now.month+1) && fromDateDayOfMonth == now.monthDay && fromTimeHour == now.hour && fromTimeMin > now.minute)) {
            new AlertDialog.Builder(this)
                    .setTitle("Wrong date")
                    .setMessage("Please, select a past date/time in From tab.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    })
                    .show();
            return;
        }
        if(toDateYear > now.year || (toDateYear == now.year && toDateMonth > (now.month+1))
                || (toDateYear == now.year && toDateMonth == (now.month+1) && toDateDayOfMonth > now.monthDay)
                || (toDateYear == now.year && toDateMonth == (now.month+1) && toDateDayOfMonth == now.monthDay && toTimeHour > now.hour)
                || (toDateYear == now.year && toDateMonth == (now.month+1) && toDateDayOfMonth == now.monthDay && toTimeHour == now.hour && toTimeMin > now.minute)) {
            new AlertDialog.Builder(this)
                    .setTitle("Wrong date")
                    .setMessage("Please, select a past date/time in To tab.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    })
                    .show();
            return;
        }

    	// Date/time input is ok 
        generatePlot(false);
    }
    
    public void generatePlot(boolean realTime)
    {
    	/* Sensor readings and javascript variables are set and passed to chart webview */
    	//TODO put conditions and modify for real time
    	String selected_sensor = arrayAdapter.getItem(arrayAdapter.getSelectedItem());
        Intent webView = new Intent(this, ChartsWebViewActivity.class);
        
        webView.putExtra("selected_sensor",selected_sensor);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        
        String javascript_variables = "";

        //Converts dates inserted by the user in long
        long fromTimestamp,toTimestamp;
        if(!realTime)
        {
	        String date_string = ""+fromDateDayOfMonth+"."+fromDateMonth+"."+fromDateYear+" "+fromTimeHour+":"+fromTimeMin;
	        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	        Date date = null;
	        try {
	            date = format.parse(date_string);
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
	        fromTimestamp = date.getTime();
	
	        date_string = ""+toDateDayOfMonth+"."+toDateMonth+"."+toDateYear+" "+toTimeHour+":"+toTimeMin;
	        try {
	            date = format.parse(date_string);
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
	        toTimestamp = date.getTime();
        } else 
        {
		    toTimestamp = System.currentTimeMillis();
		    fromTimestamp = toTimestamp-60000;
        }

        /* Basing on different selected sensor it shows a different kind of plot and label on axis, legend and so on */
        if (selected_sensor.equalsIgnoreCase("Accelerometer"))
        {
            SensorQueriesAccelerometer sensorQ_Accel = new SensorQueriesAccelerometer(
                    fromTimestamp, toTimestamp, getFilesDir());
            javascript_variables = 
                    "var unit_of_meas = " + "'m/s^2';" +
                    "var first_curve_name = " + "'X axis';" +
                    "var second_curve_name = " +"'Y axis';" + 
                    "var third_curve_name = " + "'Z axis';" +
                    "var x_axis_title = " + "'Date';" +
                    "var y_axis_title = " + "'Acceleration (m/s^2)';" +
                    "var plot_title = " + "'Acceleration data';" +
                    "var plot_subtitle = " + "'along axes x,y,z';";
            if(!realTime)
            {
	            if (sensorQ_Accel.containsReadings())
	            {
	                ArrayList<SensorDescAccelerometerNew> sensorDescs = sensorQ_Accel.getSensorDescriptorList();
	                SensorDescAccelerometerNew sensorDesc;
	
	                String x_axis_data_arrays = "[";
	                String y_axis_data_arrays = "[";
	                String z_axis_data_arrays = "[";
	
	                Calendar c = Calendar.getInstance();
	
	                Log.i("datapoints size: ",""+sensorDescs.size());
	                int increment = (int)Math.ceil(sensorDescs.size()/MAX_NUMBER_PLOT_POINTS);
	
	                for(int i=0;i<sensorDescs.size();i+=increment)
	                {
	                    sensorDesc = sensorDescs.get(i);
	                    c.setTimeInMillis(sensorDesc.getTimestamp());
	                    int mYear = c.get(Calendar.YEAR);
	                    int mMonth = c.get(Calendar.MONTH);
	                    int mDay = c.get(Calendar.DAY_OF_MONTH);
	                    int hr = c.get(Calendar.HOUR_OF_DAY);
	                    int min = c.get(Calendar.MINUTE);
	                    int sec = c.get(Calendar.SECOND);
	
	                    x_axis_data_arrays+="[Date.UTC("+mYear+","+mMonth+","+mDay+","+hr+","+min+","+sec+"),"+sensorDesc.getAccX()+"],";
	                    y_axis_data_arrays+="[Date.UTC("+mYear+","+mMonth+","+mDay+","+hr+","+min+","+sec+"),"+sensorDesc.getAccY()+"],";
	                    z_axis_data_arrays+="[Date.UTC("+mYear+","+mMonth+","+mDay+","+hr+","+min+","+sec+"),"+sensorDesc.getAccZ()+"],";
	                }
	                x_axis_data_arrays = x_axis_data_arrays.substring(0,x_axis_data_arrays.length()-1)+"]; ";
	                y_axis_data_arrays = y_axis_data_arrays.substring(0,y_axis_data_arrays.length()-1)+"]; ";
	                z_axis_data_arrays = z_axis_data_arrays.substring(0,z_axis_data_arrays.length()-1)+"]; ";
	
	                javascript_variables+= "var x_axis_data_arrays = " + x_axis_data_arrays +
	                        "var y_axis_data_arrays = " + y_axis_data_arrays +
	                        "var z_axis_data_arrays = " + z_axis_data_arrays;
	                
	                webView.putExtra("javascript_global_variables",javascript_variables);
	                webView.putExtra("type_of_plot", "3_lines_plot_over_time");
	                startActivity(webView);
	            } else Toast.makeText(getApplicationContext(), "No data found in this range.", Toast.LENGTH_LONG).show();
            }
            else 
            {
            	webView.putExtra("javascript_global_variables",javascript_variables);
            	webView.putExtra("type_of_plot", "3_lines_live_data_over_time");
            	startActivity(webView);
            }
        } else if (selected_sensor.equalsIgnoreCase("Battery"))
        { 
        	javascript_variables = 
        			"var curve_name = " + "'Battery %';" +
                    "var unit_of_meas = " + "'%';" +
                    "var x_axis_title = " + "'Date';" +
                    "var y_axis_title = " + "'Battery percentage %';" +
                    "var plot_title = " + "'Battery data';" +
                    "var plot_subtitle = " + "'%';";
        	
        	if(!realTime)
            {
	            SensorQueriesBattery sensorQ_Battery = new SensorQueriesBattery(
	                    fromTimestamp, toTimestamp, getFilesDir());
	            if (sensorQ_Battery.containsReadings())
	            {
	                ArrayList<SensorDescBattery> sensorDescs = sensorQ_Battery.getSensorDescriptorList();
	                SensorDescBattery sensorDesc;
	
	                String data_array = "[";
	
	                Calendar c = Calendar.getInstance();
	
	                Log.i("datapoints size: ",""+sensorDescs.size());
	                int increment = (int)Math.ceil(sensorDescs.size()/MAX_NUMBER_PLOT_POINTS);
	
	                //TODO move extraction of year month day hr min sec into sensor maybe or in one unique place, don't replicate code
	                for(int i=0;i<sensorDescs.size();i+=increment)
	                {
	                    sensorDesc = sensorDescs.get(i);
	                    c.setTimeInMillis(sensorDesc.getTimestamp());
	                    int mYear = c.get(Calendar.YEAR);
	                    int mMonth = c.get(Calendar.MONTH);
	                    int mDay = c.get(Calendar.DAY_OF_MONTH);
	                    int hr = c.get(Calendar.HOUR_OF_DAY);
	                    int min = c.get(Calendar.MINUTE);
	                    int sec = c.get(Calendar.SECOND);
	
	                    data_array+="[Date.UTC("+mYear+","+mMonth+","+mDay+","+hr+","+min+","+sec+"),"+sensorDesc.getBatteryPercent()+"],";
	                }
	                data_array = data_array.substring(0,data_array.length()-1)+"]; ";
	               
	                javascript_variables+= "var data_array = " + data_array;
	                
	                webView.putExtra("javascript_global_variables",javascript_variables);
	                webView.putExtra("type_of_plot", "1_line_plot_over_time");
	                startActivity(webView);
	            } else Toast.makeText(getApplicationContext(), "No data found in this range.", Toast.LENGTH_LONG).show();
            }
        	else
        	{
        		webView.putExtra("javascript_global_variables",javascript_variables);
            	webView.putExtra("type_of_plot", "1_line_live_data_over_time");
            	startActivity(webView);
        	}
        } else if (selected_sensor.equalsIgnoreCase("Gyroscope"))
        {
        	javascript_variables =  "var unit_of_meas = " + "'��';" +
                    "var first_curve_name = " + "'�� around X axis';" +
                    "var second_curve_name = " +"'�� around Y axis';" + 
                    "var third_curve_name = " + "'�� around Z axis';" +
                    "var x_axis_title = " + "'Date';" +
                    "var y_axis_title = " + "'Angle (��)';" +
                    "var plot_title = " + "'Gyroscope data';" +
                    "var plot_subtitle = " + "'angles around axes x,y,z';";
        	
        	if(!realTime)
            {
	            SensorQueriesGyroscope sensorQ_Gyroscope = new SensorQueriesGyroscope(
	                    fromTimestamp, toTimestamp, getFilesDir());
	            if (sensorQ_Gyroscope.containsReadings())
	            {
	                ArrayList<SensorDescGyroscopeNew> sensorDescs = sensorQ_Gyroscope.getSensorDescriptorList();
	                SensorDescGyroscopeNew sensorDesc;
	
	                String x_axis_data_arrays = "[";
	                String y_axis_data_arrays = "[";
	                String z_axis_data_arrays = "[";
	
	                Calendar c = Calendar.getInstance();
	
	                int increment = (int)Math.ceil(sensorDescs.size()/MAX_NUMBER_PLOT_POINTS);
	
	                for(int i=0;i<sensorDescs.size();i+=increment)
	                {
	                    sensorDesc = sensorDescs.get(i);
	                    c.setTimeInMillis(sensorDesc.getTimestamp());
	                    int mYear = c.get(Calendar.YEAR);
	                    int mMonth = c.get(Calendar.MONTH);
	                    int mDay = c.get(Calendar.DAY_OF_MONTH);
	                    int hr = c.get(Calendar.HOUR_OF_DAY);
	                    int min = c.get(Calendar.MINUTE);
	                    int sec = c.get(Calendar.SECOND);
	
	                    x_axis_data_arrays+="[Date.UTC("+mYear+","+mMonth+","+mDay+","+hr+","+min+","+sec+"),"+sensorDesc.getGyrX()+"],";
	                    y_axis_data_arrays+="[Date.UTC("+mYear+","+mMonth+","+mDay+","+hr+","+min+","+sec+"),"+sensorDesc.getGyrY()+"],";
	                    z_axis_data_arrays+="[Date.UTC("+mYear+","+mMonth+","+mDay+","+hr+","+min+","+sec+"),"+sensorDesc.getGyrZ()+"],";
	                }
	                x_axis_data_arrays = x_axis_data_arrays.substring(0,x_axis_data_arrays.length()-1)+"]; ";
	                y_axis_data_arrays = y_axis_data_arrays.substring(0,y_axis_data_arrays.length()-1)+"]; ";
	                z_axis_data_arrays = z_axis_data_arrays.substring(0,z_axis_data_arrays.length()-1)+"]; ";
	
	
	                javascript_variables+="var x_axis_data_arrays = " + x_axis_data_arrays +
                    "var y_axis_data_arrays = " + y_axis_data_arrays +
                    "var z_axis_data_arrays = " + z_axis_data_arrays;
	                
	                webView.putExtra("javascript_global_variables",javascript_variables);
	                webView.putExtra("type_of_plot", "3_lines_plot_over_time");
	                startActivity(webView);
	            } else Toast.makeText(getApplicationContext(), "No data found in this range.", Toast.LENGTH_LONG).show();
            } else
            {
            	webView.putExtra("javascript_global_variables",javascript_variables);
            	webView.putExtra("type_of_plot", "3_lines_live_data_over_time");
            	startActivity(webView);
            }
        } else if (selected_sensor.equalsIgnoreCase("Humidity"))
        {
        	javascript_variables =  "var curve_name = " + "'Humidity %';" +
                    "var unit_of_meas = " + "'%';" +
                    "var x_axis_title = " + "'Date';" +
                    "var y_axis_title = " + "'Ambient relative humidity';" +
                    "var plot_title = " + "'Humidity data';" +
                    "var plot_subtitle = " + "'%';";
        	
        	if(!realTime)
            {
	            SensorQueriesHumidity sensorQ_Humidity = new SensorQueriesHumidity(
	                    fromTimestamp, toTimestamp, getFilesDir());
	            if (sensorQ_Humidity.containsReadings())
	            {
	                ArrayList<SensorDescHumidity> sensorDescs = sensorQ_Humidity.getSensorDescriptorList();
	                SensorDescHumidity sensorDesc;
	
	                String data_array = "[";
	
	                Calendar c = Calendar.getInstance();
	
	                int increment = (int)Math.ceil(sensorDescs.size()/MAX_NUMBER_PLOT_POINTS);
	
	                //TODO move extraction of year month day hr min sec into sensor maybe or in one unique place, don't replicate code
	                for(int i=0;i<sensorDescs.size();i+=increment)
	                {
	                    sensorDesc = sensorDescs.get(i);
	                    c.setTimeInMillis(sensorDesc.getTimestamp());
	                    int mYear = c.get(Calendar.YEAR);
	                    int mMonth = c.get(Calendar.MONTH);
	                    int mDay = c.get(Calendar.DAY_OF_MONTH);
	                    int hr = c.get(Calendar.HOUR_OF_DAY);
	                    int min = c.get(Calendar.MINUTE);
	                    int sec = c.get(Calendar.SECOND);
	
	                    data_array+="[Date.UTC("+mYear+","+mMonth+","+mDay+","+hr+","+min+","+sec+"),"+sensorDesc.getHumidity()+"],";
	                }
	                data_array = data_array.substring(0,data_array.length()-1)+"]; ";
	
	                javascript_variables+="var data_array = " + data_array;
	                
	                webView.putExtra("javascript_global_variables",javascript_variables);
	                webView.putExtra("type_of_plot", "1_line_plot_over_time");
	                startActivity(webView);
	            } else Toast.makeText(getApplicationContext(), "No data found in this range.", Toast.LENGTH_LONG).show();
            } else
            {
            	webView.putExtra("javascript_global_variables",javascript_variables);
            	webView.putExtra("type_of_plot", "1_line_live_data_over_time");
            	startActivity(webView);
            }
        } else if (selected_sensor.equalsIgnoreCase("Light"))
        {    
        	javascript_variables = "var curve_name = " + "'Illuminance';" +
                    "var unit_of_meas = " + "'lx';" +
                    "var x_axis_title = " + "'Date';" +
                    "var y_axis_title = " + "'Illuminance (lx)';" +
                    "var plot_title = " + "'Illuminance data';" +
                    "var plot_subtitle = " + "'';";
        	
        	if(!realTime)
            {
	            SensorQueriesLight sensorQ_Light = new SensorQueriesLight(
	                    fromTimestamp, toTimestamp, getFilesDir());
	            if (sensorQ_Light.containsReadings())
	            {
	                ArrayList<SensorDescLight> sensorDescs = sensorQ_Light.getSensorDescriptorList();
	                SensorDescLight sensorDesc;
	
	                String data_array = "[";
	
	                Calendar c = Calendar.getInstance();
	
	                int increment = (int)Math.ceil(sensorDescs.size()/MAX_NUMBER_PLOT_POINTS);
	
	                //TODO move extraction of year month day hr min sec into sensor maybe or in one unique place, don't replicate code
	                for(int i=0;i<sensorDescs.size();i+=increment)
	                {
	                    sensorDesc = sensorDescs.get(i);
	                    c.setTimeInMillis(sensorDesc.getTimestamp());
	                    int mYear = c.get(Calendar.YEAR);
	                    int mMonth = c.get(Calendar.MONTH);
	                    int mDay = c.get(Calendar.DAY_OF_MONTH);
	                    int hr = c.get(Calendar.HOUR_OF_DAY);
	                    int min = c.get(Calendar.MINUTE);
	                    int sec = c.get(Calendar.SECOND);
	
	                    data_array+="[Date.UTC("+mYear+","+mMonth+","+mDay+","+hr+","+min+","+sec+"),"+sensorDesc.getLight()+"],";
	                }
	                data_array = data_array.substring(0,data_array.length()-1)+"]; ";
	
	                javascript_variables+="var data_array = " + data_array;
	                
	                webView.putExtra("javascript_global_variables",javascript_variables);
	                webView.putExtra("type_of_plot", "1_line_plot_over_time");
	                startActivity(webView);
	            } else Toast.makeText(getApplicationContext(), "No data found in this range.", Toast.LENGTH_LONG).show();
            } else
            {
            	webView.putExtra("javascript_global_variables",javascript_variables);
            	webView.putExtra("type_of_plot", "1_line_live_data_over_time");
            	startActivity(webView);
            }
        } else if (selected_sensor.equalsIgnoreCase("Magnetic"))
        {
        	javascript_variables = "var unit_of_meas = " + "'��T';" +
                    "var first_curve_name = " + "'��T along X axis';" +
                    "var second_curve_name = " +"'��T along Y axis';" + 
                    "var third_curve_name = " + "'��T along Z axis';" +
                    "var x_axis_title = " + "'Date';" +
                    "var y_axis_title = " + "'Field strength (��T)';" +
                    "var plot_title = " + "'Geomagnetic field data';" +
                    "var plot_subtitle = " + "'strength along axes x,y,z';";
        	
        	if(!realTime)
            {
	            SensorQueriesMagnetic sensorQ_Magnetic = new SensorQueriesMagnetic(
	                    fromTimestamp, toTimestamp, getFilesDir());
	            if (sensorQ_Magnetic.containsReadings())
	            {
	                ArrayList<SensorDescMagneticNew> sensorDescs = sensorQ_Magnetic.getSensorDescriptorList();
	                SensorDescMagneticNew sensorDesc;
	
	                String x_axis_data_arrays = "[";
	                String y_axis_data_arrays = "[";
	                String z_axis_data_arrays = "[";
	
	                Calendar c = Calendar.getInstance();
	
	                int increment = (int)Math.ceil(sensorDescs.size()/MAX_NUMBER_PLOT_POINTS);
	
	                for(int i=0;i<sensorDescs.size();i+=increment)
	                {
	                    sensorDesc = sensorDescs.get(i);
	                    c.setTimeInMillis(sensorDesc.getTimestamp());
	                    int mYear = c.get(Calendar.YEAR);
	                    int mMonth = c.get(Calendar.MONTH);
	                    int mDay = c.get(Calendar.DAY_OF_MONTH);
	                    int hr = c.get(Calendar.HOUR_OF_DAY);
	                    int min = c.get(Calendar.MINUTE);
	                    int sec = c.get(Calendar.SECOND);
	
	                    x_axis_data_arrays+="[Date.UTC("+mYear+","+mMonth+","+mDay+","+hr+","+min+","+sec+"),"+sensorDesc.getMagX()+"],";
	                    y_axis_data_arrays+="[Date.UTC("+mYear+","+mMonth+","+mDay+","+hr+","+min+","+sec+"),"+sensorDesc.getMagY()+"],";
	                    z_axis_data_arrays+="[Date.UTC("+mYear+","+mMonth+","+mDay+","+hr+","+min+","+sec+"),"+sensorDesc.getMagZ()+"],";
	                }
	                x_axis_data_arrays = x_axis_data_arrays.substring(0,x_axis_data_arrays.length()-1)+"]; ";
	                y_axis_data_arrays = y_axis_data_arrays.substring(0,y_axis_data_arrays.length()-1)+"]; ";
	                z_axis_data_arrays = z_axis_data_arrays.substring(0,z_axis_data_arrays.length()-1)+"]; ";
	
	                javascript_variables+="var x_axis_data_arrays = " + x_axis_data_arrays +
	                        "var y_axis_data_arrays = " + y_axis_data_arrays +
	                        "var z_axis_data_arrays = " + z_axis_data_arrays;
	                
	                webView.putExtra("javascript_global_variables",javascript_variables);
	
	                webView.putExtra("type_of_plot", "3_lines_plot_over_time");
	                startActivity(webView);
	            } else Toast.makeText(getApplicationContext(), "No data found in this range.", Toast.LENGTH_LONG).show();
            } else
            {
            	webView.putExtra("javascript_global_variables",javascript_variables);
            	webView.putExtra("type_of_plot", "3_lines_live_data_over_time");
            	startActivity(webView);
            }
        } else if (selected_sensor.equalsIgnoreCase("Proximity"))
        {
        	javascript_variables = "var curve_name = " + "'Proximity';" +
                    "var unit_of_meas = " + "'cm';" +
                    "var x_axis_title = " + "'Date';" +
                    "var y_axis_title = " + "'Proximity (cm)';" +
                    "var plot_title = " + "'Proximity data';" +
                    "var plot_subtitle = " + "'';";
        	
        	if(!realTime)
            {
	            SensorQueriesProximity sensorQ_Proximity = new SensorQueriesProximity(
	                    fromTimestamp, toTimestamp, getFilesDir());
	            if (sensorQ_Proximity.containsReadings())
	            {
	                ArrayList<SensorDescProximity> sensorDescs = sensorQ_Proximity.getSensorDescriptorList();
	                SensorDescProximity sensorDesc;
	
	                String data_array = "[";
	
	                Calendar c = Calendar.getInstance();
	
	                int increment = (int)Math.ceil(sensorDescs.size()/MAX_NUMBER_PLOT_POINTS);
	
	                //TODO move extraction of year month day hr min sec into sensor maybe or in one unique place, don't replicate code
	                for(int i=0;i<sensorDescs.size();i+=increment)
	                {
	                    sensorDesc = sensorDescs.get(i);
	                    c.setTimeInMillis(sensorDesc.getTimestamp());
	                    int mYear = c.get(Calendar.YEAR);
	                    int mMonth = c.get(Calendar.MONTH);
	                    int mDay = c.get(Calendar.DAY_OF_MONTH);
	                    int hr = c.get(Calendar.HOUR_OF_DAY);
	                    int min = c.get(Calendar.MINUTE);
	                    int sec = c.get(Calendar.SECOND);
	
	                    data_array+="[Date.UTC("+mYear+","+mMonth+","+mDay+","+hr+","+min+","+sec+"),"+sensorDesc.getProximity()+"],";
	//TODO for connectivity plot
	//                    if(sensorDesc.getProximity()>966)
	//                    	data_array+="{x: Date.UTC("+mYear+","+mMonth+","+mDay+","+hr+","+min+","+sec+"), y: "+sensorDesc.getProximity()+", color: 'red'},";
	//                    else
	//                    	data_array+="{x: Date.UTC("+mYear+","+mMonth+","+mDay+","+hr+","+min+","+sec+"), y: "+sensorDesc.getProximity()+"},";
	                }
	                data_array = data_array.substring(0,data_array.length()-1)+"]; ";
	
	                javascript_variables+= "var data_array = " + data_array;
	                
	                webView.putExtra("javascript_global_variables",javascript_variables);
	                webView.putExtra("type_of_plot", "1_line_plot_over_time");
	                startActivity(webView);
	            } else Toast.makeText(getApplicationContext(), "No data found in this range.", Toast.LENGTH_LONG).show();
            } else
            {
            	webView.putExtra("javascript_global_variables",javascript_variables);
            	webView.putExtra("type_of_plot", "1_line_live_data_over_time");
            	startActivity(webView);
            }
        } else if (selected_sensor.equalsIgnoreCase("Temperature"))
        {
        	javascript_variables =  "var curve_name = " + "'Temperature';" +
                    "var unit_of_meas = " + "'��';" +
                    "var x_axis_title = " + "'Date';" +
                    "var y_axis_title = " + "'Temperature(��)';" +
                    "var plot_title = " + "'Temperature data';" +
                    "var plot_subtitle = " + "'';";
        	
        	if(!realTime)
            {
	            SensorQueriesTemperature sensorQ_Temperature= new SensorQueriesTemperature(
	                    fromTimestamp, toTimestamp, getFilesDir());
	            if (sensorQ_Temperature.containsReadings())
	            {
	                ArrayList<SensorDescTemperature> sensorDescs = sensorQ_Temperature.getSensorDescriptorList();
	                SensorDescTemperature sensorDesc;
	
	                String data_array = "[";
	
	                Calendar c = Calendar.getInstance();
	
	                int increment = (int)Math.ceil(sensorDescs.size()/MAX_NUMBER_PLOT_POINTS);
	
	                //TODO move extraction of year month day hr min sec into sensor maybe or in one unique place, don't replicate code
	                for(int i=0;i<sensorDescs.size();i+=increment)
	                {
	                    sensorDesc = sensorDescs.get(i);
	                    c.setTimeInMillis(sensorDesc.getTimestamp());
	                    int mYear = c.get(Calendar.YEAR);
	                    int mMonth = c.get(Calendar.MONTH);
	                    int mDay = c.get(Calendar.DAY_OF_MONTH);
	                    int hr = c.get(Calendar.HOUR_OF_DAY);
	                    int min = c.get(Calendar.MINUTE);
	                    int sec = c.get(Calendar.SECOND);
	
	                    data_array+="[Date.UTC("+mYear+","+mMonth+","+mDay+","+hr+","+min+","+sec+"),"+sensorDesc.getTemperature()+"],";
	                }
	                data_array = data_array.substring(0,data_array.length()-1)+"]; ";
	
	                javascript_variables+="var data_array = " + data_array;
	                
	                webView.putExtra("javascript_global_variables",javascript_variables);
	                webView.putExtra("type_of_plot", "1_line_plot_over_time");
	                startActivity(webView);
	            } else Toast.makeText(getApplicationContext(), "No data found in this range.", Toast.LENGTH_LONG).show();
            } else
            {
            	webView.putExtra("javascript_global_variables",javascript_variables);
            	webView.putExtra("type_of_plot", "1_line_live_data_over_time");
            	startActivity(webView);
            }
        } else if (selected_sensor.equalsIgnoreCase("Pressure"))
        {
        	javascript_variables =  "var curve_name = " + "'Pressure';" +
                    "var unit_of_meas = " + "'mbar';" +
                    "var x_axis_title = " + "'Date';" +
                    "var y_axis_title = " + "'Pressure (mbar)';" +
                    "var plot_title = " + "'Pressure data';" +
                    "var plot_subtitle = " + "'';";
        	if(!realTime)
            {
	            SensorQueriesPressure sensorQ_Pressure= new SensorQueriesPressure(
	                    fromTimestamp, toTimestamp, getFilesDir());
	            if (sensorQ_Pressure.containsReadings())
	            {
	                ArrayList<SensorDescPressure> sensorDescs = sensorQ_Pressure.getSensorDescriptorList();
	                SensorDescPressure sensorDesc;
	
	                String data_array = "[";
	
	                Calendar c = Calendar.getInstance();
	
	                int increment = (int)Math.ceil(sensorDescs.size()/MAX_NUMBER_PLOT_POINTS);
	
	                //TODO move extraction of year month day hr min sec into sensor maybe or in one unique place, don't replicate code
	                for(int i=0;i<sensorDescs.size();i+=increment)
	                {
	                    sensorDesc = sensorDescs.get(i);
	                    c.setTimeInMillis(sensorDesc.getTimestamp());
	                    int mYear = c.get(Calendar.YEAR);
	                    int mMonth = c.get(Calendar.MONTH);
	                    int mDay = c.get(Calendar.DAY_OF_MONTH);
	                    int hr = c.get(Calendar.HOUR_OF_DAY);
	                    int min = c.get(Calendar.MINUTE);
	                    int sec = c.get(Calendar.SECOND);
	
	                    data_array+="[Date.UTC("+mYear+","+mMonth+","+mDay+","+hr+","+min+","+sec+"),"+sensorDesc.getPressure()+"],";
	                }
	                data_array = data_array.substring(0,data_array.length()-1)+"]; ";
	
	                javascript_variables+= "var data_array = " + data_array;
	                
	                webView.putExtra("javascript_global_variables",javascript_variables);
	                webView.putExtra("type_of_plot", "1_line_plot_over_time");
	                startActivity(webView);
	            } else Toast.makeText(getApplicationContext(), "No data found in this range.", Toast.LENGTH_LONG).show();
            } else
            {
            	webView.putExtra("javascript_global_variables",javascript_variables);
            	webView.putExtra("type_of_plot", "1_line_live_data_over_time");
            	startActivity(webView);
            }
        } 
        //TODO NOISE (API not ready)
        //TODO MICROPHONE (API not ready)
        //TODO CONNECTIVITY (API not ready)
    }
}

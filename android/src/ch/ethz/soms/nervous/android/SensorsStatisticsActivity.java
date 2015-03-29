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
import ch.ethz.soms.nervous.android.sensors.SensorDescBattery;
import ch.ethz.soms.nervous.android.sensors.SensorDescGyroscope;
import ch.ethz.soms.nervous.android.sensors.SensorDescHumidity;
import ch.ethz.soms.nervous.android.sensors.SensorDescLight;
import ch.ethz.soms.nervous.android.sensors.SensorDescMagnetic;
import ch.ethz.soms.nervous.android.sensors.SensorDescPressure;
import ch.ethz.soms.nervous.android.sensors.SensorDescProximity;
import ch.ethz.soms.nervous.android.sensors.SensorDescTemperature;

public class SensorsStatisticsActivity extends Activity {

    private static float MAX_NUMBER_PLOT_POINTS = (float)30.0;
    private View selectedViewOnListView = null;
    // hours in 24hours format
    private int fromTimeHour,fromTimeMin,fromDateDayOfMonth,fromDateMonth,fromDateYear,toTimeHour,toTimeMin,toDateDayOfMonth,toDateMonth,toDateYear;
    private TimePicker fromTimePicker,toTimePicker;
    private DatePicker fromDatePicker,toDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /* Initialize view components */
        setContentView(R.layout.activity_sensors_statistics);

        fromTimePicker = ((TimePicker) findViewById(R.id.fromTimePicker));
        toTimePicker = ((TimePicker) findViewById(R.id.toTimePicker));
        fromDatePicker = ((DatePicker) findViewById(R.id.fromDatePicker));
        toDatePicker = ((DatePicker) findViewById(R.id.toDatePicker));

        fromTimePicker.setIs24HourView(true);
        toTimePicker.setIs24HourView(true);

        ListView sensorsListView = ((ListView)findViewById(R.id.sensors_list_SensStatChart));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.sensors_list));
        sensorsListView.setAdapter(arrayAdapter);

        sensorsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(selectedViewOnListView!=null)
                    selectedViewOnListView.setBackgroundColor(Color.TRANSPARENT);
                selectedViewOnListView = view;
                view.setBackgroundColor(Color.rgb(77, 148, 255));
            }
        });


        TabHost tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tab1 = tabHost.newTabSpec("From");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("To");

        tab1.setIndicator("From");
        tab1.setContent(R.id.From);
        tab2.setIndicator("To");
        tab2.setContent(R.id.To);

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
    }

    public void nextButtonClicked(View view)
    {
    	/* Button to show charts clicked */
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

        if(selectedViewOnListView == null)
        {
            // If none of the sensors is selected
            new AlertDialog.Builder(this)
                    .setTitle("One more step!")
                    .setMessage("Please, select a sensor from the list.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    })
                    .show();
            return;
        }
        else
        {
        	/* Date/time input is ok, sensor readings and javascript variables are set and passed to chart webview*/ 
            String selected_sensor = ((TextView) selectedViewOnListView).getText().toString();
            Intent webView = new Intent(this, ChartsWebViewActivity.class);

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            //Converts dates inserted by the user in long
            String date_string = ""+fromDateDayOfMonth+"."+fromDateMonth+"."+fromDateYear+" "+fromTimeHour+":"+fromTimeMin;
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Date date = null;
            try {
                date = format.parse(date_string);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long fromTimestamp = date.getTime();

            date_string = ""+toDateDayOfMonth+"."+toDateMonth+"."+toDateYear+" "+toTimeHour+":"+toTimeMin;
            try {
                date = format.parse(date_string);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long toTimestamp = date.getTime();

            /* Basing on different selected sensor it shows a different kind of plot and label on axis, legend and so on */
            if (selected_sensor.equalsIgnoreCase("Accelerometer"))
            {
                SensorQueriesAccelerometer sensorQ_Accel = new SensorQueriesAccelerometer(
                        fromTimestamp, toTimestamp, getFilesDir());
                if (sensorQ_Accel.containsReadings())
                {
                    ArrayList<SensorDescAccelerometer> sensorDescs = sensorQ_Accel.getSensorDescriptorList();
                    SensorDescAccelerometer sensorDesc;

                    String x_axis_data_arrays = "[";
                    String y_axis_data_arrays = "[";
                    String z_axis_data_arrays = "[";

                    Calendar c = Calendar.getInstance();

                    Log.i("datapoints size: ",""+sensorDescs.size());
                    int increment = Math.round(sensorDescs.size()/MAX_NUMBER_PLOT_POINTS);

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


                    webView.putExtra("javascript_global_variables",
                            "var x_axis_data_arrays = " + x_axis_data_arrays +
                            "var y_axis_data_arrays = " + y_axis_data_arrays +
                            "var z_axis_data_arrays = " + z_axis_data_arrays + 
                            "var unit_of_meas = " + "'m/s^2';" +
                            "var first_curve_name = " + "'X axis';" +
                            "var second_curve_name = " +"'Y axis';" + 
                            "var third_curve_name = " + "'Z axis';" +
                            "var x_axis_title = " + "'Date';" +
                            "var y_axis_title = " + "'Acceleration (m/s^2)';" +
                            "var plot_title = " + "'Acceleration data';" +
                            "var plot_subtitle = " + "'along axes x,y,z';");

                    webView.putExtra("type_of_plot", "3_lines_plot_over_time");
                    startActivity(webView);
                } else Toast.makeText(getApplicationContext(), "No data found in this range.", Toast.LENGTH_LONG).show();
            } else if (selected_sensor.equalsIgnoreCase("Battery"))
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
                    int increment = Math.round(sensorDescs.size()/MAX_NUMBER_PLOT_POINTS);

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

                    webView.putExtra("javascript_global_variables",
                            "var data_array = " + data_array +
                            "var curve_name = " + "'Battery %';" +
                            "var unit_of_meas = " + "'%';" +
                            "var x_axis_title = " + "'Date';" +
                            "var y_axis_title = " + "'Battery percentage %';" +
                            "var plot_title = " + "'Battery data';" +
                            "var plot_subtitle = " + "'%';");

                    webView.putExtra("type_of_plot", "1_line_plot_over_time");
                    startActivity(webView);
                } else Toast.makeText(getApplicationContext(), "No data found in this range.", Toast.LENGTH_LONG).show();
            } else if (selected_sensor.equalsIgnoreCase("Gyroscope"))
            {
                SensorQueriesGyroscope sensorQ_Gyroscope = new SensorQueriesGyroscope(
                        fromTimestamp, toTimestamp, getFilesDir());
                if (sensorQ_Gyroscope.containsReadings())
                {
                    ArrayList<SensorDescGyroscope> sensorDescs = sensorQ_Gyroscope.getSensorDescriptorList();
                    SensorDescGyroscope sensorDesc;

                    String x_axis_data_arrays = "[";
                    String y_axis_data_arrays = "[";
                    String z_axis_data_arrays = "[";

                    Calendar c = Calendar.getInstance();

                    int increment = Math.round(sensorDescs.size()/MAX_NUMBER_PLOT_POINTS);

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


                    webView.putExtra("javascript_global_variables",
                            "var x_axis_data_arrays = " + x_axis_data_arrays +
                            "var y_axis_data_arrays = " + y_axis_data_arrays +
                            "var z_axis_data_arrays = " + z_axis_data_arrays + 
                            "var unit_of_meas = " + "'°';" +
                            "var first_curve_name = " + "'° around X axis';" +
                            "var second_curve_name = " +"'° around Y axis';" + 
                            "var third_curve_name = " + "'° around Z axis';" +
                            "var x_axis_title = " + "'Date';" +
                            "var y_axis_title = " + "'Angle (°)';" +
                            "var plot_title = " + "'Gyroscope data';" +
                            "var plot_subtitle = " + "'angles around axes x,y,z';");

                    webView.putExtra("type_of_plot", "3_lines_plot_over_time");
                    startActivity(webView);
                } else Toast.makeText(getApplicationContext(), "No data found in this range.", Toast.LENGTH_LONG).show();
            } else if (selected_sensor.equalsIgnoreCase("Humidity"))
            {
                SensorQueriesHumidity sensorQ_Humidity = new SensorQueriesHumidity(
                        fromTimestamp, toTimestamp, getFilesDir());
                if (sensorQ_Humidity.containsReadings())
                {
                    ArrayList<SensorDescHumidity> sensorDescs = sensorQ_Humidity.getSensorDescriptorList();
                    SensorDescHumidity sensorDesc;

                    String data_array = "[";

                    Calendar c = Calendar.getInstance();

                    int increment = Math.round(sensorDescs.size()/MAX_NUMBER_PLOT_POINTS);

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

                    webView.putExtra("javascript_global_variables",
                            "var data_array = " + data_array +
                            "var curve_name = " + "'Humidity %';" +
                            "var unit_of_meas = " + "'%';" +
                            "var x_axis_title = " + "'Date';" +
                            "var y_axis_title = " + "'Ambient relative humidity';" +
                            "var plot_title = " + "'Humidity data';" +
                            "var plot_subtitle = " + "'%';");

                    webView.putExtra("type_of_plot", "1_line_plot_over_time");
                    startActivity(webView);
                } else Toast.makeText(getApplicationContext(), "No data found in this range.", Toast.LENGTH_LONG).show();
            } else if (selected_sensor.equalsIgnoreCase("Light"))
            {
                SensorQueriesLight sensorQ_Light = new SensorQueriesLight(
                        fromTimestamp, toTimestamp, getFilesDir());
                if (sensorQ_Light.containsReadings())
                {
                    ArrayList<SensorDescLight> sensorDescs = sensorQ_Light.getSensorDescriptorList();
                    SensorDescLight sensorDesc;

                    String data_array = "[";

                    Calendar c = Calendar.getInstance();

                    int increment = Math.round(sensorDescs.size()/MAX_NUMBER_PLOT_POINTS);

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

                    webView.putExtra("javascript_global_variables",
                            "var data_array = " + data_array +
                            "var curve_name = " + "'Illuminance';" +
                            "var unit_of_meas = " + "'lx';" +
                            "var x_axis_title = " + "'Date';" +
                            "var y_axis_title = " + "'Illuminance (lx)';" +
                            "var plot_title = " + "'Illuminance data';" +
                            "var plot_subtitle = " + "'';");

                    webView.putExtra("type_of_plot", "1_line_plot_over_time");
                    startActivity(webView);
                } else Toast.makeText(getApplicationContext(), "No data found in this range.", Toast.LENGTH_LONG).show();
            } else if (selected_sensor.equalsIgnoreCase("Magnetic"))
            {
                SensorQueriesMagnetic sensorQ_Magnetic = new SensorQueriesMagnetic(
                        fromTimestamp, toTimestamp, getFilesDir());
                if (sensorQ_Magnetic.containsReadings())
                {
                    ArrayList<SensorDescMagnetic> sensorDescs = sensorQ_Magnetic.getSensorDescriptorList();
                    SensorDescMagnetic sensorDesc;

                    String x_axis_data_arrays = "[";
                    String y_axis_data_arrays = "[";
                    String z_axis_data_arrays = "[";

                    Calendar c = Calendar.getInstance();

                    int increment = Math.round(sensorDescs.size()/MAX_NUMBER_PLOT_POINTS);

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


                    webView.putExtra("javascript_global_variables",
                            "var x_axis_data_arrays = " + x_axis_data_arrays +
                            "var y_axis_data_arrays = " + y_axis_data_arrays +
                            "var z_axis_data_arrays = " + z_axis_data_arrays + 
                            "var unit_of_meas = " + "'μT';" +
                            "var first_curve_name = " + "'μT along X axis';" +
                            "var second_curve_name = " +"'μT along Y axis';" + 
                            "var third_curve_name = " + "'μT along Z axis';" +
                            "var x_axis_title = " + "'Date';" +
                            "var y_axis_title = " + "'Field strength (μT)';" +
                            "var plot_title = " + "'Geomagnetic field data';" +
                            "var plot_subtitle = " + "'strength along axes x,y,z';");

                    webView.putExtra("type_of_plot", "3_lines_plot_over_time");
                    startActivity(webView);
                } else Toast.makeText(getApplicationContext(), "No data found in this range.", Toast.LENGTH_LONG).show();
            } else if (selected_sensor.equalsIgnoreCase("Proximity"))
            {
                SensorQueriesProximity sensorQ_Proximity = new SensorQueriesProximity(
                        fromTimestamp, toTimestamp, getFilesDir());
                if (sensorQ_Proximity.containsReadings())
                {
                    ArrayList<SensorDescProximity> sensorDescs = sensorQ_Proximity.getSensorDescriptorList();
                    SensorDescProximity sensorDesc;

                    String data_array = "[";

                    Calendar c = Calendar.getInstance();

                    int increment = Math.round(sensorDescs.size()/MAX_NUMBER_PLOT_POINTS);

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
                    }
                    data_array = data_array.substring(0,data_array.length()-1)+"]; ";

                    webView.putExtra("javascript_global_variables",
                            "var data_array = " + data_array +
                            "var curve_name = " + "'Proximity';" +
                            "var unit_of_meas = " + "'cm';" +
                            "var x_axis_title = " + "'Date';" +
                            "var y_axis_title = " + "'Proximity (cm)';" +
                            "var plot_title = " + "'Proximity data';" +
                            "var plot_subtitle = " + "'';");

                    webView.putExtra("type_of_plot", "1_line_plot_over_time");
                    startActivity(webView);
                } else Toast.makeText(getApplicationContext(), "No data found in this range.", Toast.LENGTH_LONG).show();
            } else if (selected_sensor.equalsIgnoreCase("Temperature"))
            {
                SensorQueriesTemperature sensorQ_Temperature= new SensorQueriesTemperature(
                        fromTimestamp, toTimestamp, getFilesDir());
                if (sensorQ_Temperature.containsReadings())
                {
                    ArrayList<SensorDescTemperature> sensorDescs = sensorQ_Temperature.getSensorDescriptorList();
                    SensorDescTemperature sensorDesc;

                    String data_array = "[";

                    Calendar c = Calendar.getInstance();

                    int increment = Math.round(sensorDescs.size()/MAX_NUMBER_PLOT_POINTS);

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

                    webView.putExtra("javascript_global_variables",
                            "var data_array = " + data_array +
                            "var curve_name = " + "'Temperature';" +
                            "var unit_of_meas = " + "'cm';" +
                            "var x_axis_title = " + "'Date';" +
                            "var y_axis_title = " + "'Proximity (cm)';" +
                            "var plot_title = " + "'Proximity data';" +
                            "var plot_subtitle = " + "'';");

                    webView.putExtra("type_of_plot", "1_line_plot_over_time");
                    startActivity(webView);
                } else Toast.makeText(getApplicationContext(), "No data found in this range.", Toast.LENGTH_LONG).show();
            } else if (selected_sensor.equalsIgnoreCase("Pressure"))
            {
                SensorQueriesPressure sensorQ_Pressure= new SensorQueriesPressure(
                        fromTimestamp, toTimestamp, getFilesDir());
                if (sensorQ_Pressure.containsReadings())
                {
                    ArrayList<SensorDescPressure> sensorDescs = sensorQ_Pressure.getSensorDescriptorList();
                    SensorDescPressure sensorDesc;

                    String data_array = "[";

                    Calendar c = Calendar.getInstance();

                    int increment = Math.round(sensorDescs.size()/MAX_NUMBER_PLOT_POINTS);

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

                    webView.putExtra("javascript_global_variables",
                            "var data_array = " + data_array +
                            "var curve_name = " + "'Temperature';" +
                            "var unit_of_meas = " + "'cm';" +
                            "var x_axis_title = " + "'Date';" +
                            "var y_axis_title = " + "'Proximity (cm)';" +
                            "var plot_title = " + "'Proximity data';" +
                            "var plot_subtitle = " + "'';");

                    webView.putExtra("type_of_plot", "1_line_plot_over_time");
                    startActivity(webView);
                } else Toast.makeText(getApplicationContext(), "No data found in this range.", Toast.LENGTH_LONG).show();
            } 
//                else if (selected_sensor.equalsIgnoreCase("Microphone"))
//            {
//                SensorQueriesMicrophone sensorQ_Microphone= new SensorQueriesMicrophone(
//                        fromTimestamp, toTimestamp, getFilesDir());
//                if (sensorQ_Microphone.containsReadings())
//                {
//                    ArrayList<SensorDescMagnetic> sensorDescs = sensorQ_Microphone.getSensorDescriptorList();
//                    SensorDescMagnetic sensorDesc;
//
//                    String x_axis_data_arrays = "[";
//                    String y_axis_data_arrays = "[";
//                    String z_axis_data_arrays = "[";
//
//                    Calendar c = Calendar.getInstance();
//
//                    int increment = Math.round(sensorDescs.size()/MAX_NUMBER_PLOT_POINTS);
//
//                    for(int i=0;i<sensorDescs.size();i+=increment)
//                    {
//                        sensorDesc = sensorDescs.get(i);
//                        c.setTimeInMillis(sensorDesc.getTimestamp());
//                        int mYear = c.get(Calendar.YEAR);
//                        int mMonth = c.get(Calendar.MONTH);
//                        int mDay = c.get(Calendar.DAY_OF_MONTH);
//                        int hr = c.get(Calendar.HOUR_OF_DAY);
//                        int min = c.get(Calendar.MINUTE);
//                        int sec = c.get(Calendar.SECOND);
//
//                        x_axis_data_arrays+="[Date.UTC("+mYear+","+mMonth+","+mDay+","+hr+","+min+","+sec+"),"+sensorDesc.getMagX()+"],";
//                        y_axis_data_arrays+="[Date.UTC("+mYear+","+mMonth+","+mDay+","+hr+","+min+","+sec+"),"+sensorDesc.getMagY()+"],";
//                        z_axis_data_arrays+="[Date.UTC("+mYear+","+mMonth+","+mDay+","+hr+","+min+","+sec+"),"+sensorDesc.getMagZ()+"],";
//                    }
//                    x_axis_data_arrays = x_axis_data_arrays.substring(0,x_axis_data_arrays.length()-1)+"]; ";
//                    y_axis_data_arrays = y_axis_data_arrays.substring(0,y_axis_data_arrays.length()-1)+"]; ";
//                    z_axis_data_arrays = z_axis_data_arrays.substring(0,z_axis_data_arrays.length()-1)+"]; ";
//
//
//                    webView.putExtra("javascript_global_variables",
//                            "var x_axis_data_arrays = " + x_axis_data_arrays +
//                            "var y_axis_data_arrays = " + y_axis_data_arrays +
//                            "var z_axis_data_arrays = " + z_axis_data_arrays + 
//                            "var unit_of_meas = " + "'μT';" +
//                            "var first_curve_name = " + "'μT along X axis';" +
//                            "var second_curve_name = " +"'μT along Y axis';" + 
//                            "var third_curve_name = " + "'μT along Z axis';" +
//                            "var x_axis_title = " + "'Date';" +
//                            "var y_axis_title = " + "'Field strength (μT)';" +
//                            "var plot_title = " + "'Geomagnetic field data';" +
//                            "var plot_subtitle = " + "'strength along axes x,y,z';");
//
//                    webView.putExtra("type_of_plot", "3_lines_plot_over_time");
//                    startActivity(webView);
//                } else Toast.makeText(getApplicationContext(), "No data found in this range.", Toast.LENGTH_LONG).show();
//            } 
        }
    }
}

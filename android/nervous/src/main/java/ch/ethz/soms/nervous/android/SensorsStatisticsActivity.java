package ch.ethz.soms.nervous.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesAccelerometer;
import ch.ethz.soms.nervous.android.sensors.SensorDescAccelerometer;

public class SensorsStatisticsActivity extends Activity {

    private static int numb_xaxis_labels = 6;
    private View selectedViewOnListView = null;
    // hours in 24hours format
    private int fromTimeHour,fromTimeMin,fromDateDayOfMonth,fromDateMonth,fromDateYear,toTimeHour,toTimeMin,toDateDayOfMonth,toDateMonth,toDateYear;
    private TimePicker fromTimePicker,toTimePicker;
    private DatePicker fromDatePicker,toDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                            //do things
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
        else {
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

            if (selected_sensor.equalsIgnoreCase("Accelerometer")) {

//                TODO replace with start/end timestamp entered by the user!
//                SensorQueriesAccelerometer sensorQ_Accel = new SensorQueriesAccelerometer(
//                        1, Long.MAX_VALUE, getFilesDir());
                SensorQueriesAccelerometer sensorQ_Accel = new SensorQueriesAccelerometer(
                        fromTimestamp, toTimestamp, getFilesDir());
                if (sensorQ_Accel.containsReadings()) {

                    ArrayList<SensorDescAccelerometer> sensorDescs = sensorQ_Accel.getAllReadings();
                    SensorDescAccelerometer sensorDesc;
                    String labels_array = "[\"";
//            for(int i=0; i<numb_xaxis_labels; i++)
//            {
//                date = new Date(fromTimestamp+sub_ranges_length*i);
//                labels_array=labels_array+format.format(date)+"\",\"";
//            }
//                    long sub_ranges_length = (toTimestamp-fromTimestamp)/(numb_xaxis_labels-1);

                    String x_axis_data_arrays = "[\"";
                    String y_axis_data_arrays = "[\"";
                    String z_axis_data_arrays = "[\"";
                    for(int i=0;i<sensorDescs.size();i++)
                    {
                        sensorDesc = sensorDescs.get(i);
                        date = new Date(sensorDesc.getTimestamp());
                        labels_array=labels_array+format.format(date)+"\",\"";
                        x_axis_data_arrays=x_axis_data_arrays+sensorDesc.getAccX()+"\",\"";
                        y_axis_data_arrays=y_axis_data_arrays+sensorDesc.getAccY()+"\",\"";
                        z_axis_data_arrays=z_axis_data_arrays+sensorDesc.getAccZ()+"\",\"";
                    }
                    labels_array = labels_array.substring(0,labels_array.length()-2)+"]";
                    x_axis_data_arrays = x_axis_data_arrays.substring(0,x_axis_data_arrays.length()-2)+"]";
                    y_axis_data_arrays = y_axis_data_arrays.substring(0,y_axis_data_arrays.length()-2)+"]";
                    z_axis_data_arrays = z_axis_data_arrays.substring(0,z_axis_data_arrays.length()-2)+"]";


                    webView.putExtra("javascript_global_variables", "var screen_width = " + size.x + ";" +
                            "var screen_height = " + size.y + ";" +
                            "var labels_array = "+ labels_array + ";" +
                            "var x_axis_data_arrays = " + x_axis_data_arrays + ";" +
//                            "var x_axis_data_arrays = [10,30.5,1,15.5,16.5,9,1,1,1,1,1]" + ";" +
                            "var y_axis_data_arrays = " + y_axis_data_arrays + ";" +
//                            "var y_axis_data_arrays = [-20,-4,27,3,11.2,-7,2,2,2,2,2]" + ";" +
                            "var z_axis_data_arrays = " + z_axis_data_arrays + ";");
//                            "var z_axis_data_arrays = [3,3,3,7,7,3,4,4,4,4,4];");
                }
                else
                    Toast.makeText(getApplicationContext(), "No data found in this range.", Toast.LENGTH_LONG).show();
            }

            webView.putExtra("selected_sensor", selected_sensor.toLowerCase());
            startActivity(webView);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sensors_statistics, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

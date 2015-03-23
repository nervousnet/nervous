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
import ch.ethz.soms.nervous.android.sensors.SensorDescAccelerometer;

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
        else
        {
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
                            "var z_axis_data_arrays = " + z_axis_data_arrays);

                    webView.putExtra("selected_sensor", selected_sensor.toLowerCase());
                    startActivity(webView);
                } else Toast.makeText(getApplicationContext(), "No data found in this range.", Toast.LENGTH_LONG).show();
            }
        }
    }
}

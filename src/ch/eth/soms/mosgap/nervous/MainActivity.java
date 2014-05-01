package ch.eth.soms.mosgap.nervous;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import android.content.Context;

import java.io.FileWriter;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String DEBUG_TAG = "MainActivity";

	private ToggleButton toggleButtonOnOff;
	private TextView textStatus;
	private Button buttonSettings;
	private Button buttonExport;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		toggleButtonOnOff = (ToggleButton) findViewById(R.id.togglebutton);
		textStatus = (TextView) findViewById(R.id.text_status);		
		buttonSettings = (Button) findViewById(R.id.button_settings);
		buttonExport = (Button) findViewById(R.id.button_export);
		
		toggleButtonOnOff.setChecked(serviceRunning());
		
		 buttonExport.setOnClickListener(export_handler);
	}
	
	public void startSensorService() {
		// Schedule
		AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(), SensorService.class);
		PendingIntent scheduledIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 30 seconds
		long interval = 30 * 1000;

		scheduler.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, scheduledIntent);
		
		Log.d(DEBUG_TAG, "Service started");

	}

	public void stopSensorService() {
		// Cancel
		AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(), SensorService.class);
		PendingIntent scheduledIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		scheduler.cancel(scheduledIntent);
		Log.d(DEBUG_TAG, "Service stopped");
	}
	
	public void onToggleClicked(View view) {
	    boolean on = ((ToggleButton) view).isChecked();
	    if (on) {
	    	textStatus.setText("The service is running.");
	    	startSensorService();
	    } else {
	    	textStatus.setText("The service is not running.");
	    	stopSensorService();
	    }
	}
	
	 View.OnClickListener export_handler = new View.OnClickListener() {

                    public void onClick(View v)
                    {

                        String state = Environment.getExternalStorageState();

                        if (Environment.MEDIA_MOUNTED.equals(state))
                        {
                            Log.d(DEBUG_TAG, "SD card detected");



                            stopSensorService();

                            Log.d(DEBUG_TAG, "stopSensorService for file transfer");

                            
                            long TS = System.currentTimeMillis();
                            StringBuilder stringBuilder = new StringBuilder();

                            stringBuilder.append(TS);
                            stringBuilder.append(".txt");

                            String file_name = stringBuilder.toString();
                            Log.d(DEBUG_TAG,file_name);
                            Log.d(DEBUG_TAG, "file_name assigned to time");

                            


                            String sdCard=Environment.getExternalStorageDirectory().getAbsolutePath();




                            File dir = new File (sdCard + "/nervous");
                            dir.mkdirs();
                            File file_ext = new File(dir,file_name);

                            Log.d(DEBUG_TAG,"store location of new file");

                          


                            try
                            {
                                file_ext.createNewFile();
                                Log.d(DEBUG_TAG, "Create file with file_name");

                                File file = getBaseContext().getFileStreamPath("SensorLog.txt");


                                if(file.exists())
                                {
                                    Log.d(DEBUG_TAG,"SensorLog.txt exists");
                                    FileInputStream read_file = openFileInput("SensorLog.txt");

                                    Log.d(DEBUG_TAG,"created Sensorlog.txt file obj read_file");
                                    
                                    InputStreamReader inputStreamReader = new InputStreamReader(read_file);
                                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                                    StringBuilder sb = new StringBuilder();
                                    
                                    sb.append("Timestamp of export to SD : "+TS+"\n");
                                    String line;
                                    while ((line = bufferedReader.readLine()) != null) {
                                        sb.append(line);
                                    }
                                    BufferedWriter bufWr = null;

                                    bufWr = new BufferedWriter(new FileWriter(file_ext, false));



                                    
                                    bufWr.append(sb.toString());
                                    inputStreamReader.close();
                                    bufWr.close();
                                    read_file.close();




                                    
                                    getApplicationContext().deleteFile("SensorLog.txt");
                                    Log.d(DEBUG_TAG,"deleted sensorLog.txt");

                                }
                                Log.d(DEBUG_TAG,"done with file transfer");


                            }

                            catch(Exception e){

                                e.printStackTrace();
                            }


                            startSensorService();
                            Log.d(DEBUG_TAG, "startSensorService for file transfer");



                        }
                        else

                        {
                            Log.d(DEBUG_TAG, "No external storage detected(cannot copy file)");
                            Toast.makeText(getApplicationContext(), "No external storage", Toast.LENGTH_LONG).show();

                        }
                    }
                };
	
	private boolean serviceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if ("ch.eth.soms.mosgap.nervous.SensorService".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}

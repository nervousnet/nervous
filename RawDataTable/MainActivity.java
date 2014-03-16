package com.example.rawdatatable;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {
	
	private SensorManager sm;
	TextView proximityView;
	TextView lightView;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		proximityView = (TextView)findViewById(R.id.textView12);
		lightView = (TextView)findViewById(R.id.textView22);
		
        sm =(SensorManager) getSystemService(SENSOR_SERVICE);
        sm.registerListener( this, sm.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener( this, sm.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		Sensor sensor = event.sensor;
		if(sensor.getType() == Sensor.TYPE_PROXIMITY)
			proximityView.setText("" + event.values[0]);
		else if (sensor.getType() == Sensor.TYPE_LIGHT)
			lightView.setText("" + event.values[0]);
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
	}
}

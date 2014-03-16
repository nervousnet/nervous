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
	TextView accXView;
	TextView accYView;
	TextView accZView;
	TextView magXView;
	TextView magYView;
	TextView magZView;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		proximityView = (TextView) findViewById(R.id.textView12);
		lightView = (TextView) findViewById(R.id.textView22);
		accXView = (TextView) findViewById(R.id.textView32);
		accYView = (TextView) findViewById(R.id.textView42);
		accZView = (TextView) findViewById(R.id.textView52);
		magXView = (TextView) findViewById(R.id.textView62);
		magYView = (TextView) findViewById(R.id.textView72);
		magZView = (TextView) findViewById(R.id.textView82);

		sm = (SensorManager) getSystemService(SENSOR_SERVICE);

		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);

		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);

		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
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
		switch (sensor.getType()) {
		case Sensor.TYPE_LIGHT:
			proximityView.setText(String.valueOf(event.values[0]));
			break;
		case Sensor.TYPE_PROXIMITY:
			lightView.setText(String.valueOf(event.values[0]));
			break;
		case Sensor.TYPE_ACCELEROMETER:
			accXView.setText(String.valueOf(event.values[0]));
			accYView.setText(String.valueOf(event.values[1]));
			accZView.setText(String.valueOf(event.values[2]));
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			magXView.setText(String.valueOf(event.values[0]));
			magYView.setText(String.valueOf(event.values[1]));
			magZView.setText(String.valueOf(event.values[2]));
			break;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
	}
}
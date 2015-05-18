package ch.ethz.soms.nervous.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.ethz.soms.nervous.android.sensors.SensorDescAccelerometer;
import ch.ethz.soms.nervous.android.sensors.SensorDescBLEBeacon;
import ch.ethz.soms.nervous.android.sensors.SensorDescBattery;
import ch.ethz.soms.nervous.android.sensors.SensorDescConnectivity;
import ch.ethz.soms.nervous.android.sensors.SensorDescGyroscope;
import ch.ethz.soms.nervous.android.sensors.SensorDescHumidity;
import ch.ethz.soms.nervous.android.sensors.SensorDescLight;
import ch.ethz.soms.nervous.android.sensors.SensorDescMagnetic;
import ch.ethz.soms.nervous.android.sensors.SensorDescNoise;
import ch.ethz.soms.nervous.android.sensors.SensorDescPressure;
import ch.ethz.soms.nervous.android.sensors.SensorDescProximity;
import ch.ethz.soms.nervous.android.sensors.SensorDescTemperature;
import ch.ethz.soms.nervous.utils.NervousStatics;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SensorFrequencyActivity extends Activity {

	ListView listSensorFrequency;
	String[] sensorNames = { "Accelerometer", "Battery", "BLEBeacon",
			"Connectivity", "Gyroscope", "Humidity", "Light", "Magnetic",
			"Noise", "Pressure", "Proximity", "Temperature" };
	String[] frequencyUnits = { "sec" , "min", "hours" };
	long[] sensorIds = { 
		SensorDescAccelerometer.SENSOR_ID, SensorDescBattery.SENSOR_ID, SensorDescBLEBeacon.SENSOR_ID, 
		SensorDescConnectivity.SENSOR_ID, SensorDescGyroscope.SENSOR_ID, SensorDescHumidity.SENSOR_ID, 
		SensorDescLight.SENSOR_ID, SensorDescMagnetic.SENSOR_ID, SensorDescNoise.SENSOR_ID, 
		SensorDescPressure.SENSOR_ID, SensorDescProximity.SENSOR_ID, SensorDescTemperature.SENSOR_ID };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_frequency);

		ArrayAdapter<String> freqUnitArrAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, frequencyUnits);		
		freqUnitArrAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		CustomListAdapter adapter = new CustomListAdapter(
				SensorFrequencyActivity.this, sensorNames, freqUnitArrAdapter);
		listSensorFrequency = (ListView) findViewById(R.id.list_SensorFrequency);
		listSensorFrequency.setAdapter(adapter);

	}

	public class CustomListAdapter extends ArrayAdapter<String> {
		private final Activity context;
		String[] sensorName;
		ArrayAdapter<String> freqUnitArrAdapter;

		public CustomListAdapter(Activity context, String[] sensorName,
				ArrayAdapter<String> freqUnitArrAdapter2) {
			super(context, R.layout.sensor_frequency_listitem, sensorName);
			this.context = context;
			this.sensorName = sensorName;
			this.freqUnitArrAdapter = freqUnitArrAdapter2;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView = inflater.inflate(R.layout.sensor_frequency_listitem,
					null);
			final TextView txtSensorName = (TextView) rowView
					.findViewById(R.id.txt_SensorFreq_SensorItem);

			final Spinner unitSpinner = (Spinner) rowView
					.findViewById(R.id.spinner_sensor_frequency);
			unitSpinner.setAdapter(freqUnitArrAdapter);

			final EditText txtTimeValue = (EditText) rowView
					.findViewById(R.id.txt_sensor_frequency_number);
			txtSensorName.setText(sensorName[position]);

			final SharedPreferences settings = context.getSharedPreferences(
					NervousStatics.SENSOR_FREQ, 0);
			float frequencyValue = settings.getFloat(
					Long.toHexString(sensorIds[position]) + "_freqValue", 4f);
			int freqUnitIndex = settings.getInt(
					Long.toHexString(sensorIds[position]) + "_freqUnit", 0);

			Log.d("###SensFreqAct###", position + ": " + frequencyValue + "  ("
					+ freqUnitIndex + ")");
			txtTimeValue.setText(frequencyValue + "");
			unitSpinner.setSelection(freqUnitIndex);

			txtTimeValue.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (!hasFocus) {
						float newVal;
						try {
							newVal = Float.parseFloat(((EditText) v).getText()
									.toString());
						} catch (Exception e) {
							return;
						}

						Editor edit = settings.edit();
						edit.putFloat(Long.toHexString(sensorIds[position])
								+ "_freqValue", newVal);
						edit.commit();
						//Log.d("###SensFreqAct###", "Changed to " + newVal);
					}
				}
			});

			txtTimeValue
					.setOnEditorActionListener(new OnEditorActionListener() {

						@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if (actionId == EditorInfo.IME_NULL
									&& event.getAction() == KeyEvent.ACTION_DOWN) {
								float newVal;
								try {
									newVal = Float.parseFloat(((EditText) v)
											.getText().toString());
								} catch (Exception e) {
									return true;
								}

								Editor edit = settings.edit();
								edit.putFloat(
										Long.toHexString(sensorIds[position])
												+ "_freqValue", newVal);
								edit.commit();

							}
							return true;
						}
					});

			unitSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					Editor edit = settings.edit();
					edit.putInt(Long.toHexString(sensorIds[position])
							+ "_freqUnit", position);
					edit.commit();
					// toastToScreen("saved: " + position, false);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});

			return rowView;
		}

		private int freqUnitToIndex(String freqUnit) {
			// TODO
			return 0;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sensor_frequency, menu);
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Changed settings must be applied
		if (SensorService.isServiceRunning(this)) {
			SensorService.stopService(this);
			SensorService.startService(this);
		}
		if (UploadService.isServiceRunning(this)) {
			UploadService.stopService(this);
			UploadService.startService(this);
		}
	}
	
	public void toastToScreen(String msg, boolean lengthLong) {

		int toastLength = lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
		Toast.makeText(getApplicationContext(), msg, toastLength).show();
	}
}
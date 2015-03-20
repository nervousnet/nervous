package com.example.nervousnet;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import ch.ethz.soms.nervousnet.utils.*;

public class SensorLoggingToggleActivity extends Activity {

	ListView listSensorLoggingToggle;
	String[] sensorNames = { "Accelerometer", "Battery", "BLEBeacon",
			"Connectivity", "Gyroscope", "Humidity", "Light", "Magnetic",
			"Noise", "Pressure", "Proximity", "Temperature" };
	long[] sensorIds = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_logging_toggle);

		CustomListAdapter adapter = new CustomListAdapter(
				SensorLoggingToggleActivity.this, sensorNames);
		listSensorLoggingToggle = (ListView) findViewById(R.id.list_SensorLoggingToggle);
		listSensorLoggingToggle.setAdapter(adapter);
	}

	public class CustomListAdapter extends ArrayAdapter<String> {
		private final Activity context;
		String[] sensorName;

		public CustomListAdapter(Activity context, String[] sensorName) {
			super(context, R.layout.sensor_logging_toggle_listitem, sensorName);
			this.context = context;
			this.sensorName = sensorName;

		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView = inflater.inflate(
					R.layout.sensor_logging_toggle_listitem, null);
			final TextView txtTitle = (TextView) rowView
					.findViewById(R.id.txt_SensorItem);

			final CheckBox checkBoxLog = (CheckBox) rowView
					.findViewById(R.id.checkBox_Log);
			final CheckBox checkBoxShare = (CheckBox) rowView
					.findViewById(R.id.checkBox_Share);

			txtTitle.setText(sensorName[position]);

			final SharedPreferences settings = context.getSharedPreferences(
					NervousStatics.SENSOR_PREFS, 0);
			boolean doMeasure = settings.getBoolean(
					Long.toHexString(sensorIds[position]) + "_doMeasure", true);
			boolean doShare = settings.getBoolean(
					Long.toHexString(sensorIds[position]) + "_doShare", true);

			checkBoxLog.setChecked(doMeasure);
			checkBoxShare.setChecked(doShare);

			checkBoxLog
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							Editor edit = settings.edit();
							edit.putBoolean(
									Long.toHexString(sensorIds[position])
											+ "_doMeasure", isChecked);
							edit.commit();
						}
					});

			checkBoxShare
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							Editor edit = settings.edit();
							edit.putBoolean(
									Long.toHexString(sensorIds[position])
											+ "_doShare", isChecked);
							edit.commit();
						}
					});

			return rowView;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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

package ch.ethz.soms.nervous.android;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SensorLoggingToggleActivity extends Activity {

	ListView listSensorLoggingToggle;
	String[] sensorNames = { "Accelerometer", "Battery", "BLEBeacon",
			"Connectivity", "Gyroscope", "Humidity", "Light", "Magnetic",
			"Noise", "Pressure", "Proximity", "Temperature" };
	long[] sensorIds = { SensorDescAccelerometer.SENSOR_ID,
			SensorDescBattery.SENSOR_ID, SensorDescBLEBeacon.SENSOR_ID,
			SensorDescConnectivity.SENSOR_ID, SensorDescGyroscope.SENSOR_ID,
			SensorDescHumidity.SENSOR_ID, SensorDescLight.SENSOR_ID,
			SensorDescMagnetic.SENSOR_ID, SensorDescNoise.SENSOR_ID,
			SensorDescPressure.SENSOR_ID, SensorDescProximity.SENSOR_ID,
			SensorDescTemperature.SENSOR_ID };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_logging_toggle);

		CustomListAdapter adapter = new CustomListAdapter(
				SensorLoggingToggleActivity.this, sensorNames);
		listSensorLoggingToggle = (ListView) findViewById(R.id.list_SensorLoggingToggle);
		listSensorLoggingToggle.setAdapter(adapter);
		listSensorLoggingToggle
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Toast.makeText(SensorLoggingToggleActivity.this,
								"You Clicked at " + sensorNames[position],
								Toast.LENGTH_SHORT).show();
					}
				});

	}

	public class CustomListAdapter extends ArrayAdapter<String> {
		private final Activity context;
		String[] sensorName;

		public CustomListAdapter(Activity context, String[] i_sensorName) {
			super(context, R.layout.sensor_logging_toggle_listitem,
					i_sensorName);
			this.context = context;
			this.sensorName = i_sensorName;

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

			checkBoxLog.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Editor edit = settings.edit();
					edit.putBoolean(Long.toHexString(sensorIds[position])
							+ "_doMeasure", checkBoxShare.isChecked());
					edit.commit();
				}
			});

			checkBoxShare.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Editor edit = settings.edit();
					edit.putBoolean(Long.toHexString(sensorIds[position])
							+ "_doShare", checkBoxShare.isChecked());
					edit.commit();
				}
			});
			return rowView;
		}
	}

	public void toastToScreen(String msg, boolean lengthLong) {

		int toastLength = lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
		Toast.makeText(getApplicationContext(), msg, toastLength).show();
	}

}

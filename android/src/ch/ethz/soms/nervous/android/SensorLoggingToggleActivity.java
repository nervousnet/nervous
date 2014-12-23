package ch.ethz.soms.nervous.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SensorLoggingToggleActivity extends Activity {

	ListView listSensorLoggingToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_logging_toggle);

		final String[] sensorNames = getSensorNames();
		int numOfSensors = sensorNames.length;

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

	private String[] getSensorNames() {
		String[] sensorList = { "Accelerometer", "Battery", "BLEBeacon",
				"Connectivity", "Gyroscope", "Humidity", "Light", "Magnetic",
				"Noise", "Pressure", "Proximity", "Temperature" };
		return sensorList;
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

			final Switch switchLog = (Switch) rowView
					.findViewById(R.id.switch_log);
			final Switch switchShare = (Switch) rowView
					.findViewById(R.id.switch_share);

			txtTitle.setText(sensorName[position]);
			// TODO: Set toggle on or off
			switchLog.setChecked(true);
			switchShare.setChecked(false);

			// TODO: set onClickListeners to change settings
			switchLog.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					toastToScreen("Hit Log: " + position, false);
				}
			});

			switchShare.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					toastToScreen("Hit Share: " + position, false);
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

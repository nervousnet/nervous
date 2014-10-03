package ch.ethz.soms.nervous.android;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

		Integer[] images_log = new Integer[numOfSensors];
		Integer[] images_share = new Integer[numOfSensors];
		for (int i = 0; i < numOfSensors; i++) {
			images_log[i] = R.raw.img_log_on;
			images_share[i] = R.raw.img_share_on;
		}

		CustomListAdapter adapter = new CustomListAdapter(
				SensorLoggingToggleActivity.this, sensorNames, images_log,
				images_share);
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
		String[] sensorList = { "Accelerometer", "Light", "Temperature",
				"Humidity", "Gyroscope", "Proximity", "Battery",
				"Atm. Pressure", "Magnetic", "Noise" };
		return sensorList;
	}

	public class CustomListAdapter extends ArrayAdapter<String> {
		private final Activity context;
		String[] sensorName;
		private final Integer[] image_share, image_log;

		public CustomListAdapter(Activity context, String[] i_sensorName,
				Integer[] images_log, Integer[] images_share) {
			super(context, R.layout.sensor_logging_toggle_listitem,
					i_sensorName);
			this.context = context;
			this.sensorName = i_sensorName;
			this.image_log = images_log;
			this.image_share = images_share;

		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView = inflater.inflate(
					R.layout.sensor_logging_toggle_listitem, null, true);
			TextView txtTitle = (TextView) rowView
					.findViewById(R.id.txt_SensorItem);
			final LinearLayout logLayoutButton = (LinearLayout) rowView
					.findViewById(R.id.layoutButtonLog);
			final LinearLayout shareLayoutButton = (LinearLayout) rowView
					.findViewById(R.id.layoutButtonShare);
			final TextView txtLogOnOff = (TextView) rowView
					.findViewById(R.id.txt_LogOnOff);
			final TextView txtShareOnOff = (TextView) rowView
					.findViewById(R.id.txt_ShareOnOff);

			txtTitle.setText(sensorName[position]);
			logLayoutButton.setBackgroundColor(Color.GREEN);
			txtLogOnOff.setText("ON");
			shareLayoutButton.setBackgroundColor(Color.GREEN);
			txtShareOnOff.setText("ON");

			logLayoutButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (txtLogOnOff.getText().equals("ON")) {
						txtLogOnOff.setText("OFF");
						logLayoutButton.setBackgroundColor(Color.RED);
					} else {
						txtLogOnOff.setText("ON");
						logLayoutButton.setBackgroundColor(Color.GREEN);
					}
				}
			});

			shareLayoutButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (txtShareOnOff.getText().equals("ON")) {
						txtShareOnOff.setText("OFF");
						shareLayoutButton.setBackgroundColor(Color.RED);
					} else {
						txtShareOnOff.setText("ON");
						shareLayoutButton.setBackgroundColor(Color.GREEN);
					}
				}
			});
			return rowView;
		}
	}

}

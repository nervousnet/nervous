package ch.ethz.soms.nervous.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SensorLoggingToggleActivity extends Activity {

	ListView list_SensorLoggingToggle;

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
		list_SensorLoggingToggle = (ListView) findViewById(R.id.list_SensorLoggingToggle);
		list_SensorLoggingToggle.setAdapter(adapter);
		list_SensorLoggingToggle
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
				"Humidity", "Gyroscope", "Light", "Proximity", "Battery",
				"Atm. Pressure", "Altitude", "Sound Level" };
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
			final ImageView imageView_Log = (ImageView) rowView
					.findViewById(R.id.img_log);
			final ImageView imageView_Share = (ImageView) rowView
					.findViewById(R.id.img_share);
			txtTitle.setText(sensorName[position]);
			imageView_Log.setImageResource(image_log[position]);
			imageView_Share.setImageResource(image_share[position]);

			// Anroid cannot access getImageResource. It needs to be stored in a
			// tag
			imageView_Log.setTag(image_log[position]);
			imageView_Share.setTag(image_share[position]);

			imageView_Log.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if ((Integer) imageView_Log.getTag() == R.raw.img_log_off) {
						imageView_Log.setImageResource(R.raw.img_log_on);
						imageView_Log.setTag(R.raw.img_log_on);
					} else {
						imageView_Log.setImageResource(R.raw.img_log_off);
						imageView_Log.setTag(R.raw.img_log_off);
						imageView_Share.setImageResource(R.raw.img_share_off);
						imageView_Share.setTag(R.raw.img_share_off);
					}
				}
			});

			imageView_Share.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if ((Integer) imageView_Share.getTag() == R.raw.img_share_off) {
						imageView_Share.setImageResource(R.raw.img_share_on);
						imageView_Share.setTag(R.raw.img_share_on);
						imageView_Log.setImageResource(R.raw.img_log_on);
						imageView_Log.setTag(R.raw.img_log_on);
					} else {
						imageView_Share.setImageResource(R.raw.img_share_off);
						imageView_Share.setTag(R.raw.img_share_off);
					}
				}
			});
			return rowView;
		}
	}

}

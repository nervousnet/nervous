package ch.ethz.soms.nervous.android;

import android.R.drawable;
import android.app.Activity;
import android.os.Bundle;
import android.text.AndroidCharacter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

		final String[] sensorNames = { "Accelerometer", "Gyroscope", "Battery",
				"Microphone" };

		int[] images_log = { drawable.ic_lock_lock,
				drawable.ic_lock_idle_charging, drawable.ic_lock_power_off,
				drawable.ic_lock_lock };
		int[] images_share = { drawable.ic_input_add, drawable.ic_input_add,
				drawable.ic_input_delete, drawable.ic_input_get };
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

	public class CustomListAdapter extends ArrayAdapter<String> {
		private final Activity context;
		String[] sensorName;
		private final int[] image_log, image_share;

		public CustomListAdapter(Activity context, String[] i_sensorName,
				int[] i_image_log, int[] i_image_share) {
			super(context, R.layout.sensor_logging_toggle_listitem,
					i_sensorName);
			this.context = context;
			this.sensorName = i_sensorName;
			this.image_log = i_image_log;
			this.image_share = i_image_share;

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
			ImageView imageView_Share = (ImageView) rowView
					.findViewById(R.id.img_share);
			txtTitle.setText(sensorName[position]);
			imageView_Log.setImageResource(image_log[position]);
			imageView_Share.setImageResource(image_share[position]);

			imageView_Log.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					imageView_Log.setImageResource(drawable.ic_dialog_info);
				}
			});
			return rowView;
		}
	}

}

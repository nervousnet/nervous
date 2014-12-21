package ch.ethz.soms.nervous.android;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.text.AndroidCharacter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class IfThisThenThatActivity extends Activity {

	private Spinner spinner_Sensor, spinner_Stage1, spinner_Stage1Part2,
			spinner_TimeValue, spinner_TimeUnit;
	private Spinner spinner_then_stage1, spinner_then_stage2,
			spinner_then_TimeUnit, spinner_then_TimeValue;
	private TextView txt_then_for;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_if_this_then_that);
		spinner_Sensor = (Spinner) findViewById(R.id.spinner_Sensor);
		spinner_Stage1 = (Spinner) findViewById(R.id.spinner_IfStage1);
		spinner_Stage1Part2 = (Spinner) findViewById(R.id.spinner_IfStage1_Part2);
		spinner_TimeValue = (Spinner) findViewById(R.id.spinner_TimeValue);
		spinner_TimeUnit = (Spinner) findViewById(R.id.spinner_TimeUnit);

		spinner_then_stage1 = (Spinner) findViewById(R.id.spinner_thenStage1);
		spinner_then_stage2 = (Spinner) findViewById(R.id.spinner_thenStage2);
		spinner_then_TimeUnit = (Spinner) findViewById(R.id.spinner_then_TimeUnit);
		spinner_then_TimeValue = (Spinner) findViewById(R.id.spinner_then_TimeValue);
		txt_then_for = (TextView) findViewById(R.id.txt_then_for);

		spinner_Sensor
				.setOnItemSelectedListener(new SensorSpinnerOnItemSelectedListener());
		spinner_then_stage1
				.setOnItemSelectedListener(new ThenStage1SpinnerOnItemSelectedListener());

		updateAllSpinners();
	}

	private void updateAllSpinners() {
		updateSensorSpinner();
		updateStage1Spinner();
		updateStage1Part2Spinner();
		updateTimeValueSpinner();
		updateTimeUnitSpinner();
		updateThenStage1Spinner();
		updateThenStage2Spinner();
		updateThenForTextView();
		updateThenTimeValueSpinner();
		updateThenTimeUnitSpinner();
	}

	private void updateTimeUnitSpinner() {
		ArrayList<String> timeUnitArray = getTimeUnitArray();
		ArrayAdapter<String> adapter_TimeUnit = new ArrayAdapter<String>(this,
				R.layout.if_spinners, timeUnitArray);
		spinner_TimeUnit.setAdapter(adapter_TimeUnit);
	}

	private void updateTimeValueSpinner() {
		ArrayList<String> timeValuesArray = getTimeValuesArray();
		ArrayAdapter<String> adapter_TimeValues = new ArrayAdapter<String>(
				this, R.layout.if_spinners, timeValuesArray);
		spinner_TimeValue.setAdapter(adapter_TimeValues);
	}

	private void updateStage1Part2Spinner() {
		ArrayList<String> ifStage1Part2Array = getIfStage1Part2Array();
		ArrayAdapter<String> adapter_IfStage1Part2 = new ArrayAdapter<String>(
				this, R.layout.if_spinners, ifStage1Part2Array);
		spinner_Stage1Part2.setAdapter(adapter_IfStage1Part2);
	}

	private ArrayList<String> getThenStage1Array() {
		ArrayList<String> result_list = new ArrayList<String>();
		String list[] = { "Vibrate", "Play Notification", "Flash Screen",
				"Turn off Phone", "Start App", "Call Contact" };
		result_list.addAll(Arrays.asList(list));
		return result_list;
	}

	private ArrayList<String> getThenStage2Array() {
		ArrayList<String> result_list = new ArrayList<String>();
		String chosenThenStage1 = (String) spinner_then_stage1
				.getSelectedItem();
		if (chosenThenStage1.equals("Play Notification")) {
			String list[] = { "Soft Ringing", "Heavy Ringing",
					"Sound of the Sun", "Hats off", "Candlelight",
					"Stars Align" };
			result_list.addAll(Arrays.asList(list));
		} else if (chosenThenStage1.equals("Start App")) {
			String list[] = { "Maps", "Whatsapp", "Email", "Angry Birds" };
			result_list.addAll(Arrays.asList(list));
		} else if (chosenThenStage1.equals("Call Contact")) {
			String list[] = { "Select Contact" };
			result_list.addAll(Arrays.asList(list));
		}
		return result_list;
	}

	private void updateSensorSpinner() {
		final ArrayList<String> sensorArray = getSensorArray();
		final ArrayAdapter<String> adapter_Sensor = new ArrayAdapter<String>(
				this, R.layout.if_spinners, sensorArray);
		Log.d(MainActivity.LOG_TAG, "array: " + sensorArray);
		spinner_Sensor.setAdapter(adapter_Sensor);
	}

	private void updateStage1Spinner() {
		ArrayList<String> ifStage1Array = getIfStage1Array();
		ArrayAdapter<String> adapter_IfStage1 = new ArrayAdapter<String>(this,
				R.layout.if_spinners, ifStage1Array);
		spinner_Stage1.setAdapter(adapter_IfStage1);
	}

	private void updateThenTimeUnitSpinner() {
		spinner_then_TimeUnit.setVisibility(txt_then_for.getVisibility());
		ArrayList<String> timeUnitArray = getTimeUnitArray();
		ArrayAdapter<String> adapter_TimeUnit = new ArrayAdapter<String>(this,
				R.layout.then_spinners, timeUnitArray);
		spinner_then_TimeUnit.setAdapter(adapter_TimeUnit);
	}

	private void updateThenTimeValueSpinner() {
		spinner_then_TimeValue.setVisibility(txt_then_for.getVisibility());
		ArrayList<String> timeValuesArray = getTimeValuesArray();
		ArrayAdapter<String> adapter_TimeValues = new ArrayAdapter<String>(
				this, R.layout.then_spinners, timeValuesArray);
		spinner_then_TimeValue.setAdapter(adapter_TimeValues);
	}

	private void updateThenStage1Spinner() {
		ArrayList<String> thenStage1Array = getThenStage1Array();
		ArrayAdapter<String> adapter_thenStage1 = new ArrayAdapter<String>(
				this, R.layout.then_spinners, thenStage1Array);
		spinner_then_stage1.setAdapter(adapter_thenStage1);
	}

	private void updateThenStage2Spinner() {
		ArrayList<String> thenStage2Array = getThenStage2Array();
		ArrayAdapter<String> adapter_thenStage2 = new ArrayAdapter<String>(
				this, R.layout.then_spinners, thenStage2Array);
		spinner_then_stage2.setAdapter(adapter_thenStage2);
	}

	private void updateThenForTextView() {
		// Hide if necessary
		ArrayList<String> hideThenForText_Array = new ArrayList<String>();
		String hideThenForText_lsit[] = { "Start App", "Call Contact",
				"Turn off Phone" };
		hideThenForText_Array.addAll(Arrays.asList(hideThenForText_lsit));

		String chosenThenStage1 = (String) spinner_then_stage1
				.getSelectedItem();
		if (hideThenForText_Array.contains(chosenThenStage1)) {
			txt_then_for.setVisibility(View.INVISIBLE);
		} else {
			txt_then_for.setVisibility(View.VISIBLE);
		}
	}

	private ArrayList<String> getIfStage1Array() {

		ArrayList<String> itemsArray = new ArrayList<String>();

		ArrayList<String> aboveBelowSensorsArray = new ArrayList<String>();
		String aboveBelowlist[] = { "Light", "Temperature", "Humidity",
				"Light", "Battery", "Atmospheric Pressure", "Altitude",
				"Sound Level" };
		aboveBelowSensorsArray.addAll(Arrays.asList(aboveBelowlist));

		String chosenSensor = (String) spinner_Sensor.getSelectedItem();
		if (aboveBelowSensorsArray.contains(chosenSensor)) {
			String list[] = { "is Above", "is Below" };
			itemsArray.addAll(Arrays.asList(list));
		} else if (chosenSensor.equals("Proximity")) {
			String list[] = { "is Near", "is Far" };
			itemsArray.addAll(Arrays.asList(list));
		} else if (chosenSensor.equals("Accelerometer")
				|| chosenSensor.equals("Gyroscope")) {
			String list[] = { "is resting (low sensitivity)",
					"is resting (high sensitivity)", "is shaken heavily",
					"is shaken softly" };
			itemsArray.addAll(Arrays.asList(list));
		}
		return itemsArray;
	}

	private ArrayList<String> getIfStage1Part2Array() {

		ArrayList<String> itemsArray = new ArrayList<String>();
		String chosenSensor = (String) spinner_Sensor.getSelectedItem();
		if (chosenSensor == null || chosenSensor.isEmpty()) {
			return itemsArray;
		}
		if (chosenSensor.equals("Temperature")) {
			for (int i = -20; i <= 40; i++) {
				itemsArray.add(i + " C" + (char) 0x00B0);
			}
		} else if (chosenSensor.equals("Humidity")) {
			for (int i = 0; i <= 100; i++) {
				itemsArray.add(i + "%");
			}
		} else if (chosenSensor.equals("Atmospheric Pressure")) {
			for (int i = 200; i <= 1000; i += 25) {
				itemsArray.add(i + " hPa");
			}
		} else if (chosenSensor.equals("Altitude")) {
			for (int i = 0; i <= 2000; i += 20) {
				itemsArray.add(i + " m.ü.m");
			}
		} else if (chosenSensor.equals("Sound Level")) {
			for (int i = 20; i <= 200; i += 5) {
				itemsArray.add(i + " dB");
			}
		} else if (chosenSensor.equals("Battery")) {
			for (int i = 0; i <= 100; i += 1) {
				itemsArray.add(i + "%");
			}
		} else if (chosenSensor.equals("Light")) {
			for (double i = 0; i <= 1.2; i += 0.1) {

				itemsArray.add(new StringBuilder(i + "").subSequence(0, 3)
						+ " lux");
			}
		}
		return itemsArray;
	}

	private ArrayList<String> getSensorArray() {
		ArrayList<String> sensorArray = new ArrayList<String>();
		String list[] = { "Accelerometer", "Light", "Temperature", "Humidity",
				"Gyroscope", "Light", "Proximity", "Battery",
				"Atmospheric Pressure", "Altitude", "Sound Level", "Date/Time" };

		sensorArray.addAll(Arrays.asList(list));
		return sensorArray;
	}

	private ArrayList<String> getTimeValuesArray() {
		ArrayList<String> itemsArray = new ArrayList<String>();

		String chosenSensor = (String) spinner_Sensor.getSelectedItem();
		if (!chosenSensor.equals("Date/Time")) {
			for (int i = 0; i <= 200; i++) {
				itemsArray.add(i + "");
			}
		}
		return itemsArray;
	}

	private ArrayList<String> getTimeUnitArray() {
		ArrayList<String> itemsArray = new ArrayList<String>();

		String chosenSensor = (String) spinner_Sensor.getSelectedItem();
		if (!chosenSensor.equals("Date/Time")) {
			String list[] = { "seconds", "minutes", "hours", "days", "weeks",
					"years", "centuries" };
			itemsArray.addAll(Arrays.asList(list));
		}
		return itemsArray;
	}

	private final class SensorSpinnerOnItemSelectedListener implements
			OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			updateStage1Spinner();
			updateStage1Part2Spinner();
			updateTimeValueSpinner();
			updateTimeUnitSpinner();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	}

	private final class ThenStage1SpinnerOnItemSelectedListener implements
			OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			updateThenStage2Spinner();
			updateThenForTextView();
			updateThenTimeValueSpinner();
			updateThenTimeUnitSpinner();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	}
}

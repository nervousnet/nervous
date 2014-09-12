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

public class IfThisThenThatActivity extends Activity {

	private Spinner spinner_Sensor, spinner_Stage1, spinner_Stage1Part2,
			spinner_TimeValue, spinner_TimeUnit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_if_this_then_that);
		spinner_Sensor = (Spinner) findViewById(R.id.spinner_Sensor);
		spinner_Stage1 = (Spinner) findViewById(R.id.spinner_IfStage1);
		spinner_Stage1Part2 = (Spinner) findViewById(R.id.spinner_IfStage1_Part2);
		spinner_TimeValue = (Spinner) findViewById(R.id.spinner_TimeValue);
		spinner_TimeUnit = (Spinner) findViewById(R.id.spinner_TimeUnit);

		spinner_Sensor
				.setOnItemSelectedListener(new SensorSpinnerOnItemSelectedListener());
		updateAllSpinners();
	}

	private void updateAllSpinners() {
		updateSensorSpinner();
		updateStage1Spinner();
		updateStage1Part2Spinner();
		updateTimeValueSpinner();
		updateTimeUnitSpinner();
	}

	private void updateTimeUnitSpinner() {
		ArrayList<String> timeUnitArray = getTimeUnitArray();
		ArrayAdapter<String> adapter_TimeUnit = new ArrayAdapter<String>(
				this, R.layout.if_spinners, timeUnitArray);
		spinner_TimeUnit.setAdapter(adapter_TimeUnit);
	}

	private void updateTimeValueSpinner() {
		ArrayList<String> timeValuesArray = getTimeValuesArray();
		ArrayAdapter<String> adapter_TimeValues = new ArrayAdapter<String>(
				this, R.layout.if_spinners, timeValuesArray);
		spinner_TimeValue.setAdapter(adapter_TimeValues);
	}
	
	

	private void updateStage1Part2Spinner() {
		ArrayList<String> ifStage1Part2Array = getIfStage1Part2();

		ArrayAdapter<String> adapter_IfStage1Part2 = new ArrayAdapter<String>(
				this, R.layout.if_spinners, ifStage1Part2Array);
		spinner_Stage1Part2.setAdapter(adapter_IfStage1Part2);
	}

	private void updateStage1Spinner() {
		ArrayList<String> ifStage1Array = getIfStage1();
		ArrayAdapter<String> adapter_IfStage1 = new ArrayAdapter<String>(this,
				R.layout.if_spinners, ifStage1Array);
		spinner_Stage1.setAdapter(adapter_IfStage1);
	}

	private void updateSensorSpinner() {
		final ArrayList<String> sensorArray = getSensorArray();
		final ArrayAdapter<String> adapter_Sensor = new ArrayAdapter<String>(
				this, R.layout.if_spinners, sensorArray);
		Log.d(MainActivity.DEBUG_TAG, "array: " + sensorArray);
		spinner_Sensor.setAdapter(adapter_Sensor);
	}

	private ArrayList<String> getIfStage1() {

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

	private ArrayList<String> getIfStage1Part2() {

		ArrayList<String> itemsArray = new ArrayList<String>();
		String chosenSensor = (String) spinner_Sensor.getSelectedItem();
		if (chosenSensor == null || chosenSensor.isEmpty()) {
			return itemsArray;
		}
		if (chosenSensor.equals("Temperature")) {
			for (int i = -20; i <= 40; i++) {
				itemsArray.add(i + " C");
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

}

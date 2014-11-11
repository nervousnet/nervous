package ch.ethz.soms.nervous.android;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.InputFilter.LengthFilter;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesAccelerometer;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesBattery;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesLight;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesMultipleSensors;
import ch.ethz.soms.nervous.android.sensorQueries.SensorQueriesProximity;
import ch.ethz.soms.nervous.android.sensors.SensorDescAccelerometer;
import ch.ethz.soms.nervous.android.sensors.SensorDescBattery;
import ch.ethz.soms.nervous.android.sensors.SensorDescLight;
import ch.ethz.soms.nervous.android.sensors.SensorDescProximity;
import ch.ethz.soms.nervous.android.test.PerformanceTestTask;
import ch.ethz.soms.nervous.android.test.PerformanceTestTask2;
import ch.ethz.soms.nervous.map.NervousMap;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static final String DEBUG_TAG = "MainActivity";

	private TextView textStatus;
	private ToggleButton buttonOnOff;
	private Button buttonPerfTest;

	private NervousMap nervousMap;

	private boolean serviceRunning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		nervousMap = new NervousMap(getApplicationContext());
		nervousMap.selectMapLayer(-1);

		textStatus = (TextView) findViewById(R.id.text_status);
		buttonOnOff = (ToggleButton) findViewById(R.id.togglebutton);
		buttonPerfTest = (Button) findViewById(R.id.perftestbutton);

		buttonPerfTest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new PerformanceTestTask2(getApplicationContext()).execute();
			}
		});

		setupAnimations();
	}

	private void setupAnimations() {
		final RelativeLayout layoutNodeExtraInf = setupNodeExtraInf();
		final RelativeLayout layoutMainMap = setupMainMap(layoutNodeExtraInf);

		final LinearLayout layoutExtraMenuButtonGroup = setupExtraMenuButtons();
		setupMainMenuButton(layoutMainMap, layoutExtraMenuButtonGroup);
	}

	private void setupMainMenuButton(final RelativeLayout layoutMainMap,
			final LinearLayout layoutExtraMenuButtonGroup) {
		final ImageButton mainMenuButton = (ImageButton) findViewById(R.id.btn_mainMenuButton);

		final Animation flyInFromRightAnimation = AnimationUtils.loadAnimation(
				this, R.anim.menu_button_group_animation_in);
		final Animation flyOutToRightAnimation = AnimationUtils.loadAnimation(
				this, R.anim.menu_button_group_animation_out);
		final AlphaAnimation alphaAnimFadeIn = new AlphaAnimation(0, 1);
		final AlphaAnimation alphaAnimFadeOut = new AlphaAnimation(1, 0);
		final AlphaAnimation alphaAnimSemiFadeOut = new AlphaAnimation(1, 0.5f);
		final AlphaAnimation alphaAnimSemiFadeIn = new AlphaAnimation(0.5f, 1);

		alphaAnimFadeIn.setDuration(getResources().getInteger(
				R.integer.menuButtonsGroup_animationDuration));
		alphaAnimFadeOut.setDuration(getResources().getInteger(
				R.integer.menuButtonsGroup_animationDuration));
		alphaAnimSemiFadeIn.setDuration(getResources().getInteger(
				R.integer.menuButtonsGroup_animationDuration));
		alphaAnimSemiFadeIn.setFillAfter(true);
		alphaAnimSemiFadeIn.setFillEnabled(true);
		alphaAnimSemiFadeOut.setDuration(getResources().getInteger(
				R.integer.menuButtonsGroup_animationDuration));
		alphaAnimSemiFadeOut.setFillAfter(true);
		alphaAnimSemiFadeOut.setFillEnabled(true);
		mainMenuButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mainMenuButton.setImageResource(R.raw.ic_stack);
				if (layoutExtraMenuButtonGroup.getVisibility() == View.INVISIBLE) {
					mainMenuButton.setImageResource(R.raw.ic_cross);
					layoutExtraMenuButtonGroup.setVisibility(View.VISIBLE);
					AnimationSet animSet = new AnimationSet(false);
					animSet.addAnimation(flyInFromRightAnimation);
					animSet.addAnimation(alphaAnimFadeIn);
					animSet.setFillAfter(true);
					animSet.setFillEnabled(true);
					layoutMainMap.startAnimation(alphaAnimSemiFadeOut);
					layoutExtraMenuButtonGroup.startAnimation(animSet);
					layoutMainMap.setEnabled(false);

				} else {
					AnimationSet animSet = new AnimationSet(false);
					animSet.addAnimation(flyOutToRightAnimation);
					animSet.addAnimation(alphaAnimFadeOut);
					animSet.setFillAfter(true);
					animSet.setFillEnabled(true);
					layoutMainMap.startAnimation(alphaAnimSemiFadeIn);
					layoutExtraMenuButtonGroup.startAnimation(animSet);
					layoutMainMap.setEnabled(true);
					layoutExtraMenuButtonGroup.postDelayed(
							new Runnable() {
								@Override
								public void run() {
									layoutExtraMenuButtonGroup
											.setVisibility(View.INVISIBLE);
								}
							},
							getResources()
									.getInteger(
											R.integer.menuButtonsGroup_animationDuration));
				}
			}
		});
	}

	private LinearLayout setupExtraMenuButtons() {
		final LinearLayout layoutExtraMenuButtonGroup = (LinearLayout) findViewById(R.id.layout_extraMenuButtonGroup);
		layoutExtraMenuButtonGroup.setVisibility(View.INVISIBLE);

		ImageButton btn_showRelations = (ImageButton) findViewById(R.id.btn_showRelations);
		btn_showRelations.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toastToScreen("Yolo", false);
			}
		});
		return layoutExtraMenuButtonGroup;
	}

	private RelativeLayout setupMainMap(final RelativeLayout layoutNodeExtraInf) {
		final RelativeLayout layoutMainMap = (RelativeLayout) findViewById(R.id.layout_map);
		final Animation flyInFromBottomAnimation = AnimationUtils
				.loadAnimation(this, R.anim.node_extra_information_animation_in);
		final Animation flyOutToBottomAnimation = AnimationUtils.loadAnimation(
				this, R.anim.node_extra_information_animation_out);

		layoutMainMap.addView(nervousMap.getViewSwitcher());

		/*
		 * layoutMainMap.setBackgroundResource(R.raw.mapdummy);
		 * layoutMainMap.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { if
		 * (layout_NodeExtraInf.getVisibility() == View.INVISIBLE) {
		 * layout_NodeExtraInf.setVisibility(View.VISIBLE);
		 * layout_NodeExtraInf.startAnimation(flyInFromBottomAnimation); } else
		 * { layout_NodeExtraInf.startAnimation(flyOutToBottomAnimation);
		 * layout_NodeExtraInf.postDelayed(new Runnable() {
		 * 
		 * @Override public void run() {
		 * layout_NodeExtraInf.setVisibility(View.INVISIBLE); } },
		 * getResources()
		 * .getInteger(R.integer.menuButtonsGroup_animationDuration)); } } });
		 */
		return layoutMainMap;
	}

	private RelativeLayout setupNodeExtraInf() {
		final RelativeLayout layout_NodeExtraInf = (RelativeLayout) findViewById(R.id.layout_NodeInformation);
		layout_NodeExtraInf.setVisibility(View.INVISIBLE);

		ImageButton btn_NodeOptions = (ImageButton) findViewById(R.id.btn_NodeOptions);
		btn_NodeOptions.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toastToScreen("Options", false);
			}
		});

		return layout_NodeExtraInf;
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateServiceInfo();
	}

	public void startSensorService() {

		// Schedule
		AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent sensorIntent = new Intent(getApplicationContext(),
				SensorService.class);
		PendingIntent scheduledSensorIntent = PendingIntent.getService(
				getApplicationContext(), 0, sensorIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Intent uploadIntent = new Intent(getApplicationContext(),
				UploadService.class);
		PendingIntent scheduledUploadIntent = PendingIntent.getService(
				getApplicationContext(), 0, uploadIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		// 30 seconds
		long sensorInterval = 30 * 1000;

		// 120 seconds
		long uploadInterval = 120 * 1000;

		scheduler.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), sensorInterval,
				scheduledSensorIntent);

		scheduler.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), uploadInterval,
				scheduledUploadIntent);

		serviceRunning = true;
		new ServiceInfo(getApplicationContext()).clean();
		textStatus.setText("Service started");
		Log.d(DEBUG_TAG, "Service started");
	}

	public void stopSensorService() {
		// Cancel
		AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent sensorIntent = new Intent(getApplicationContext(),
				SensorService.class);
		PendingIntent scheduledSensorIntent = PendingIntent.getService(
				getApplicationContext(), 0, sensorIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Intent uploadIntent = new Intent(getApplicationContext(),
				UploadService.class);
		PendingIntent scheduledUploadIntent = PendingIntent.getService(
				getApplicationContext(), 0, uploadIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		scheduler.cancel(scheduledSensorIntent);
		scheduler.cancel(scheduledUploadIntent);

		serviceRunning = false;
		new ServiceInfo(getApplicationContext()).clean();
		Log.d(DEBUG_TAG, "Service stopped");
	}

	public void onToggleClicked(View view) {
		boolean on = ((ToggleButton) view).isChecked();
		if (on) {
			startSensorService();

		} else {
			stopSensorService();
		}

	}

	public void updateServiceInfo() {

		final ServiceInfo info = new ServiceInfo(getApplicationContext());

		serviceRunning = info.serviceIsRunning();

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				final StringBuilder strBuf = new StringBuilder(
						"Service started. \nStarted at: "
								+ info.getTimeOfFirstFrame()
								+ " \nFrames gathered: "
								+ info.getAmountOfFrames() + "\nFile size: "
								+ info.getFileSize() + " Bytes");
				if (!serviceRunning) {
					strBuf.append("\n\nService stopped.");
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						buttonOnOff.setChecked(serviceRunning);
						textStatus.setText(strBuf); // Runs on UI Thread
					}
				});
			}
		}, 0, 1000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_IfThisThenThat:
			intent = new Intent(this, IfThisThenThatActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_SensorLoggingToggle:
			intent = new Intent(this, SensorLoggingToggleActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_TestQuery_Battery_MinBattery:
			minBattery();
			break;
		case R.id.menu_TestQuery_Battery_Bottom10:
			bottom10Battery();
			break;
		case R.id.menu_TestQuery_Battery_Top10:
			top10Battery();
			break;
		case R.id.menu_TestQuery_Battery_MaxBattery:
			maxBattery();
			break;
		case R.id.menu_TestQuery_Light_MaxLight:
			maxLight();
			break;
		case R.id.menu_TestQuery_Light_MinLight:
			minLight();
			break;
		case R.id.menu_TestQuery_Light_Top10Light:
			top10Light();
			break;
		case R.id.menu_TestQuery_Light_Bottom10Light:
			bottom10Light();
			break;
		case R.id.menu_TestQuery_Accelerometer_MaxAccAverage:
			maxAverageAccelerometer();
			break;
		case R.id.menu_TestQuery_Accelerometer_MinAccAverage:
			minAverageAccelerometer();
			break;
		case R.id.menu_TestQuery_Proximity_MaxProx:
			maxProximity();
			break;
		case R.id.menu_TestQuery_Proximity_MinProx:
			minProximity();
			break;
		case R.id.menu_TestQuery_Proximity_Top10Prox:
			top10Proximity();
			break;
		case R.id.menu_TestQuery_Proximity_Bottom10Prox:
			bottom10Proximity();
			break;
		case R.id.menu_TestQuery_Light_Prox_Kmean:
			lightProxKMean();
			break;
		case R.id.menu_settings:
			intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void lightProxKMean() {
		SensorQueriesMultipleSensors sq = new SensorQueriesMultipleSensors();
		SensorQueriesLight sensorQ_Light = new SensorQueriesLight(1,
				Long.MAX_VALUE, getFilesDir());
		SensorQueriesProximity sensorQ_Prox = new SensorQueriesProximity(1,
				Long.MAX_VALUE, getFilesDir());
		ArrayList<Vector<Float>> res = sq.getKMeans(
				sensorQ_Light.getSensorDescriptorList(),
				sensorQ_Prox.getSensorDescriptorList());
		toastToScreen(res.get(0).get(0) + "", false);
	}

	private void minLight() {
		SensorQueriesLight sensorQ_Light2 = new SensorQueriesLight(1,
				Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Light2.containsReadings()) {
			SensorDescLight minLightDesc = sensorQ_Light2.getMinValue();
			toastToScreen("Minimum Light: " + minLightDesc.getLight() + "\nat "
					+ getDate(minLightDesc.getTimestamp()), true);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	private void maxLight() {
		SensorQueriesLight sensorQ_Light = new SensorQueriesLight(1,
				Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Light.containsReadings()) {
			SensorDescLight maxLightDesc = sensorQ_Light.getMaxValue();
			toastToScreen("Maximum Light: " + maxLightDesc.getLight() + "\nat "
					+ getDate(maxLightDesc.getTimestamp()), true);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	private void bottom10Light() {
		SensorQueriesLight sensorQ_Light = new SensorQueriesLight(1,
				Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Light.containsReadings()) {
			ArrayList<SensorDescLight> topKLightDesc = sensorQ_Light
					.getBottomK(10);
			int i = 1;
			Log.d(DEBUG_TAG, "Bottom 10 Light:");
			for (SensorDescLight bat : topKLightDesc) {
				Log.d(DEBUG_TAG, i++ + ": " + bat.getLight() + " Date: "
						+ getDate(bat.getTimestamp()));
			}
			toastToScreen("Bottom 10 logged", false);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	private void top10Light() {
		SensorQueriesLight sensorQ_Light = new SensorQueriesLight(1,
				Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Light.containsReadings()) {
			ArrayList<SensorDescLight> topKLightDesc = sensorQ_Light
					.getTopK(10);
			int i = 1;
			Log.d(DEBUG_TAG, "Top 10 Light:");
			for (SensorDescLight light : topKLightDesc) {
				Log.d(DEBUG_TAG, i++ + ": " + light.getLight() + " Date: "
						+ getDate(light.getTimestamp()));
			}
			toastToScreen("Top 10 logged", false);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	private void minProximity() {
		SensorQueriesProximity sensorQ_Proximity = new SensorQueriesProximity(
				1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Proximity.containsReadings()) {
			SensorDescProximity minProxDesc = sensorQ_Proximity.getMinValue();
			toastToScreen("Minimum Proximity: " + minProxDesc.getProximity()
					+ "\nat " + getDate(minProxDesc.getTimestamp()), true);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	private void maxProximity() {
		SensorQueriesProximity sensorQ_Prox = new SensorQueriesProximity(1,
				Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Prox.containsReadings()) {
			SensorDescProximity maxProxDesc = sensorQ_Prox.getMaxValue();
			toastToScreen("Maximum Prox: " + maxProxDesc.getProximity()
					+ "\nat " + getDate(maxProxDesc.getTimestamp()), true);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	private void bottom10Proximity() {
		SensorQueriesProximity sensorQ_Prox = new SensorQueriesProximity(1,
				Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Prox.containsReadings()) {
			ArrayList<SensorDescProximity> topKProxDesc = sensorQ_Prox
					.getBottomK(10);
			int i = 1;
			Log.d(DEBUG_TAG, "Bottom 10 Prox:");
			for (SensorDescProximity proxDesc : topKProxDesc) {
				Log.d(DEBUG_TAG, i++ + ": " + proxDesc.getProximity()
						+ " Date: " + getDate(proxDesc.getTimestamp()));
			}
			toastToScreen("Bottom 10 logged", false);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	private void top10Proximity() {
		SensorQueriesProximity sensorQ_Light = new SensorQueriesProximity(1,
				Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Light.containsReadings()) {
			ArrayList<SensorDescProximity> topKProxDesc = sensorQ_Light
					.getTopK(10);
			int i = 1;
			Log.d(DEBUG_TAG, "Top 10 Prox:");
			for (SensorDescProximity proxDesc : topKProxDesc) {
				Log.d(DEBUG_TAG, i++ + ": " + proxDesc.getProximity()
						+ " Date: " + getDate(proxDesc.getTimestamp()));
			}
			toastToScreen("Top 10 logged", false);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	private void maxAverageAccelerometer() {
		SensorQueriesAccelerometer sensorQ_Accel = new SensorQueriesAccelerometer(
				1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Accel.containsReadings()) {
			SensorDescAccelerometer maxAccAverageSensDesc = sensorQ_Accel
					.getMaxAverageValue();
			toastToScreen("Maximum Accelerometer Average: \n x:"
					+ maxAccAverageSensDesc.getAccX() + "\ny: "
					+ maxAccAverageSensDesc.getAccY() + "\nz: "
					+ maxAccAverageSensDesc.getAccZ() + "\nDate: "
					+ getDate(maxAccAverageSensDesc.getTimestamp()), true);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	private void minAverageAccelerometer() {
		SensorQueriesAccelerometer sensorQ_Accel = new SensorQueriesAccelerometer(
				1, Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Accel.containsReadings()) {
			SensorDescAccelerometer minAccAverageSensDesc = sensorQ_Accel
					.getMinAverageValue();
			toastToScreen("Minimum Accelerometer Average: \n x:"
					+ minAccAverageSensDesc.getAccX() + "\ny: "
					+ minAccAverageSensDesc.getAccY() + "\nz: "
					+ minAccAverageSensDesc.getAccZ() + "\nDate: "
					+ getDate(minAccAverageSensDesc.getTimestamp()), true);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	private void maxBattery() {
		SensorQueriesBattery sensorQ_Batteries2 = new SensorQueriesBattery(1,
				Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Batteries2.containsReadings()) {
			SensorDescBattery maxBatDesc = sensorQ_Batteries2.getMaxValue();
			toastToScreen("Max Battery: " + maxBatDesc.getBatteryPercent()
					+ "\nat " + getDate(maxBatDesc.getTimestamp()), true);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	private void minBattery() {
		SensorQueriesBattery sensorQ_Batteries = new SensorQueriesBattery(1,
				Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Batteries.containsReadings()) {
			SensorDescBattery minBatDesc = sensorQ_Batteries.getMinValue();
			toastToScreen("Minimum Battery: " + minBatDesc.getBatteryPercent()
					+ "\nat " + getDate(minBatDesc.getTimestamp()), true);
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	private void bottom10Battery() {
		SensorQueriesBattery sensorQ_Batteries = new SensorQueriesBattery(1,
				Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Batteries.containsReadings()) {
			ArrayList<SensorDescBattery> topKBatDesc = sensorQ_Batteries
					.getBottomK(10);
			int i = 1;
			Log.d(DEBUG_TAG, "Bottom 10 Bat:");
			for (SensorDescBattery bat : topKBatDesc) {
				toastToScreen("Bottom 10 logged", false);
				Log.d(DEBUG_TAG, i++ + ": " + bat.getBatteryPercent()
						+ " Date: " + getDate(bat.getTimestamp()));
			}
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	private void top10Battery() {
		SensorQueriesBattery sensorQ_Batteries = new SensorQueriesBattery(1,
				Long.MAX_VALUE, getFilesDir());
		if (sensorQ_Batteries.containsReadings()) {
			ArrayList<SensorDescBattery> topKBatDesc = sensorQ_Batteries
					.getTopK(10);
			int i = 1;
			Log.d(DEBUG_TAG, "Top 10 Bat:");
			for (SensorDescBattery bat : topKBatDesc) {
				toastToScreen("Top 10 logged", false);
				Log.d(DEBUG_TAG, i++ + ": " + bat.getBatteryPercent()
						+ " Date: " + getDate(bat.getTimestamp()));
			}
		} else {
			toastToScreen("No Data Found", false);
		}
	}

	private String getDate(long time) {
		Calendar cal = Calendar.getInstance(Locale.ENGLISH);
		cal.setTimeInMillis(time);
		String date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
		return date;
	}

	private void toastToScreen(String msg, boolean lengthLong) {

		int toastLength = lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
		Toast.makeText(getApplicationContext(), msg, toastLength).show();
	}

}
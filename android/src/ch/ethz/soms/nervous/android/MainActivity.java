package ch.ethz.soms.nervous.android;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
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
import ch.ethz.soms.nervous.android.test.PerformanceTestTask2;
import ch.ethz.soms.nervous.map.AssetsMbTileSource;
import ch.ethz.soms.nervous.map.MapGraphLoader;
import ch.ethz.soms.nervous.map.NervousMap;
import android.widget.Toast;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends ActionBarActivity {

	public static final String DEBUG_TAG = "MainActivity";

	private NervousMap nervousMap;
	private boolean serviceRunning;
	private boolean menuButtonsShowing;
	private ImageButton mainMenuButton;
	private ImageView imgOverlay;
	private RelativeLayout layoutMainMap;
	private Switch serviceSwitch;
	private LinearLayout layoutExtraMenuButtonGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.fragment_toolbar);
		this.setSupportActionBar(toolbar);

		serviceSwitch = (Switch) findViewById(R.id.service_switch);

		nervousMap = new NervousMap(getApplicationContext());

		nervousMap.addMapLayer(0, new AssetsMbTileSource(getApplicationContext(), "cch0"));
		nervousMap.addMapLayer(1, new AssetsMbTileSource(getApplicationContext(), "cch1"));
		nervousMap.addMapLayer(2, new AssetsMbTileSource(getApplicationContext(), "cch2"));
		nervousMap.addMapLayer(3, new AssetsMbTileSource(getApplicationContext(), "blank"));

		new MapGraphLoader(getApplicationContext(), "http://nervous.ethz.ch/app_data/map-sn.json", nervousMap, 0, 0).execute();

		nervousMap.selectMapLayer(0);

		menuButtonsShowing = false;
		setupAnimations();

	}

	public void onServiceSwitchClick(View view) {
		if (view.getId() == R.id.service_switch) {
			startStopSensorService(((Switch) view).isChecked());
		}
	}

	@Override
	public void onBackPressed() {
		if (menuButtonsShowing) {
			moveOutButtons(this);
			menuButtonsShowing = false;
		} else {
			super.onBackPressed();
		}
	}

	private void setupAnimations() {
		final RelativeLayout layoutNodeExtraInf = setupNodeExtraInf();
		layoutMainMap = setupMainMap(layoutNodeExtraInf);
		layoutExtraMenuButtonGroup = setupExtraMenuButtons();
		setupMainMenuButton();
	}

	private void moveOutButtons(final MainActivity context) {
		final Animation flyOutToRightAnimation = AnimationUtils.loadAnimation(context, R.anim.menu_button_group_animation_out);
		final AlphaAnimation alphaAnimFadeOut = new AlphaAnimation(1, 0);
		final AlphaAnimation alphaAnimSemiFadeIn = new AlphaAnimation(0.5f, 1);

		alphaAnimFadeOut.setDuration(getResources().getInteger(R.integer.menuButtonsGroup_animationDuration));
		alphaAnimSemiFadeIn.setDuration(getResources().getInteger(R.integer.menuButtonsGroup_animationDuration));
		alphaAnimSemiFadeIn.setFillAfter(true);
		alphaAnimSemiFadeIn.setFillEnabled(true);

		mainMenuButton.setImageResource(R.raw.ic_stack);

		AnimationSet animSet = new AnimationSet(false);
		animSet.addAnimation(flyOutToRightAnimation);
		animSet.addAnimation(alphaAnimFadeOut);
		animSet.setFillAfter(true);
		animSet.setFillEnabled(true);
		layoutMainMap.startAnimation(alphaAnimSemiFadeIn);
		layoutExtraMenuButtonGroup.startAnimation(animSet);
		layoutMainMap.setEnabled(true);
		enableLayout(layoutMainMap);
		imgOverlay.setEnabled(false);
		imgOverlay.setVisibility(View.INVISIBLE);
		layoutExtraMenuButtonGroup.postDelayed(new Runnable() {
			@Override
			public void run() {
				layoutExtraMenuButtonGroup.setVisibility(View.INVISIBLE);
			}
		}, getResources().getInteger(R.integer.menuButtonsGroup_animationDuration));
	}

	private void moveInButtons(final MainActivity context) {
		final Animation flyInFromRightAnimation = AnimationUtils.loadAnimation(context, R.anim.menu_button_group_animation_in);
		final AlphaAnimation alphaAnimFadeIn = new AlphaAnimation(0, 1);
		final AlphaAnimation alphaAnimFadeOut = new AlphaAnimation(1, 0);
		final AlphaAnimation alphaAnimSemiFadeOut = new AlphaAnimation(1, 0.5f);
		final AlphaAnimation alphaAnimSemiFadeIn = new AlphaAnimation(0.5f, 1);

		alphaAnimFadeIn.setDuration(getResources().getInteger(R.integer.menuButtonsGroup_animationDuration));
		alphaAnimFadeOut.setDuration(getResources().getInteger(R.integer.menuButtonsGroup_animationDuration));
		alphaAnimSemiFadeIn.setDuration(getResources().getInteger(R.integer.menuButtonsGroup_animationDuration));
		alphaAnimSemiFadeIn.setFillAfter(true);
		alphaAnimSemiFadeIn.setFillEnabled(true);
		alphaAnimSemiFadeOut.setDuration(getResources().getInteger(R.integer.menuButtonsGroup_animationDuration));
		alphaAnimSemiFadeOut.setFillAfter(true);
		alphaAnimSemiFadeOut.setFillEnabled(true);

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
		disableLayout(layoutMainMap);
		imgOverlay.setEnabled(true);
		imgOverlay.setVisibility(View.VISIBLE);
	}

	private void enableLayout(final ViewGroup layout) {
		for (int i = 0; i < layout.getChildCount(); i++) {
			View child = layout.getChildAt(i);
			child.setEnabled(true);
		}
	}

	private void disableLayout(final ViewGroup layout) {
		for (int i = 0; i < layout.getChildCount(); i++) {
			View child = layout.getChildAt(i);
			child.setEnabled(false);
		}
	}

	private void setupMainMenuButton() {
		mainMenuButton = (ImageButton) findViewById(R.id.btn_mainMenuButton);
		final MainActivity context = this;

		imgOverlay = (ImageView) findViewById(R.id.img_Overlay);
		imgOverlay.setEnabled(false);
		imgOverlay.setVisibility(View.INVISIBLE);
		imgOverlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (menuButtonsShowing) {
					moveOutButtons(context);
					menuButtonsShowing = false;
				}
			}
		});
		mainMenuButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!menuButtonsShowing) {
					moveInButtons(context);
					menuButtonsShowing = true;
				} else {
					moveOutButtons(context);
					menuButtonsShowing = false;
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
				toastToScreen("Test", false);
			}
		});
		return layoutExtraMenuButtonGroup;
	}

	private RelativeLayout setupMainMap(final RelativeLayout layoutNodeExtraInf) {
		final RelativeLayout layoutMainMap = (RelativeLayout) findViewById(R.id.layout_map);

		layoutMainMap.addView(nervousMap.getViewSwitcher());

		/*
		 * layoutMainMap.setBackgroundResource(R.raw.mapdummy); layoutMainMap.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { if (layout_NodeExtraInf.getVisibility() == View.INVISIBLE) { layout_NodeExtraInf.setVisibility(View.VISIBLE); layout_NodeExtraInf.startAnimation(flyInFromBottomAnimation); } else { layout_NodeExtraInf.startAnimation(flyOutToBottomAnimation); layout_NodeExtraInf.postDelayed(new Runnable() {
		 * 
		 * @Override public void run() { layout_NodeExtraInf.setVisibility(View.INVISIBLE); } }, getResources() .getInteger(R.integer.menuButtonsGroup_animationDuration)); } } });
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

	public void startStopSensorService(boolean on) {
		Intent sensorIntent = new Intent(getApplicationContext(), SensorService.class);
		Intent uploadIntent = new Intent(getApplicationContext(), UploadService.class);
		if (on) {
			startService(sensorIntent);
			startService(uploadIntent);
			serviceRunning = true;

		} else {
			stopService(sensorIntent);
			stopService(uploadIntent);
			serviceRunning = false;
		}
		updateServiceInfo();
	}

	public void updateServiceInfo() {
		serviceRunning = isServiceRunning(SensorService.class) && isServiceRunning(UploadService.class);
		serviceSwitch.setChecked(serviceRunning);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		TestQueries tq = new TestQueries(getApplicationContext(), getFilesDir());

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
			tq.minBattery();
			break;
		case R.id.menu_TestQuery_Battery_Bottom10:
			tq.bottom10Battery();
			break;
		case R.id.menu_TestQuery_Battery_Top10:
			tq.top10Battery();
			break;
		case R.id.menu_TestQuery_Battery_MaxBattery:
			tq.maxBattery();
			break;
		case R.id.menu_TestQuery_Light_MaxLight:
			tq.maxLight();
			break;
		case R.id.menu_TestQuery_Light_MinLight:
			tq.minLight();
			break;
		case R.id.menu_TestQuery_Light_Top10Light:
			tq.top10Light();
			break;
		case R.id.menu_TestQuery_Light_Bottom10Light:
			tq.bottom10Light();
			break;
		case R.id.menu_TestQuery_Accelerometer_MaxAccAverage:
			tq.maxAverageAccelerometer();
			break;
		case R.id.menu_TestQuery_Accelerometer_MinAccAverage:
			tq.minAverageAccelerometer();
			break;
		case R.id.menu_TestQuery_Proximity_MaxProx:
			tq.maxProximity();
			break;
		case R.id.menu_TestQuery_Proximity_MinProx:
			tq.minProximity();
			break;
		case R.id.menu_TestQuery_Proximity_Top10Prox:
			tq.top10Proximity();
			break;
		case R.id.menu_TestQuery_Proximity_Bottom10Prox:
			tq.bottom10Proximity();
			break;
		case R.id.menu_TestQuery_Light_Prox_Kmean:
			tq.lightProxKMean();
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

	public void toastToScreen(String msg, boolean lengthLong) {

		int toastLength = lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
		Toast.makeText(getApplicationContext(), msg, toastLength).show();
	}

	private boolean isServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

}
package ch.ethz.soms.nervous.android;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;
import ch.ethz.soms.nervous.android.sensors.SensorDescBLEBeacon;
import ch.ethz.soms.nervous.map.AssetsMbTileSource;
import ch.ethz.soms.nervous.map.MapGraph.MapGraphNode;
import ch.ethz.soms.nervous.map.MapGraphLoader;
import ch.ethz.soms.nervous.map.NervousMap;
import ch.ethz.soms.nervous.map.NervousMap.NervousMapListener;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;
import ch.ethz.soms.nervous.utils.NervousStatics;
import ch.ethz.soms.nervous.vm.NervousVM;

public class MainActivity extends ActionBarActivity implements
		NervousMapListener {

	public static final String LOG_TAG = MainActivity.class.getSimpleName();

	private static final int REQUEST_ENABLE_BT = 0;

	private NervousMap nervousMap;
	private boolean serviceRunning;
	private boolean menuButtonsShowing;
	private ImageView imgOverlay;
	private RelativeLayout layoutMainMap;
	private Switch serviceSwitch;
	private LinearLayout layoutExtraMenuButtonGroup;

	private ImageButton mainMenuButton, btnFloor2Map, btnFloor3Map,
			btnSocialMap, btnOrbitalMap, btnFloor1Map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.AppTheme);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.fragment_toolbar);
		this.setSupportActionBar(toolbar);

		serviceSwitch = (Switch) findViewById(R.id.service_switch);

		nervousMap = new NervousMap(getApplicationContext());

		nervousMap.addMapLayer(0, new AssetsMbTileSource(
				getApplicationContext(), "cch0"));
		nervousMap.addMapLayer(1, new AssetsMbTileSource(
				getApplicationContext(), "cch1"));
		nervousMap.addMapLayer(2, new AssetsMbTileSource(
				getApplicationContext(), "cch2"));
		nervousMap.addMapLayer(3, new AssetsMbTileSource(
				getApplicationContext(), "blank"));

		nervousMap.selectMapLayer(-1);

		nervousMap.addListener(this);

		menuButtonsShowing = false;
		setupAnimations();

		updateServiceInfo();
		if (!serviceRunning) {
			askServiceEnable();
		}
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
		layoutMainMap = setupMainMap();
		layoutExtraMenuButtonGroup = setupExtraMenuButtons();
		setupMainMenuButton();
	}

	private void moveOutButtons(final MainActivity context) {
		final Animation flyOutToRightAnimation = AnimationUtils.loadAnimation(
				context, R.anim.menu_button_group_animation_out);
		final AlphaAnimation alphaAnimFadeOut = new AlphaAnimation(1, 0);
		final AlphaAnimation alphaAnimSemiFadeIn = new AlphaAnimation(0.5f, 1);

		alphaAnimFadeOut.setDuration(getResources().getInteger(
				R.integer.menuButtonsGroup_animationDuration));
		alphaAnimSemiFadeIn.setDuration(getResources().getInteger(
				R.integer.menuButtonsGroup_animationDuration));
		alphaAnimSemiFadeIn.setFillAfter(true);
		alphaAnimSemiFadeIn.setFillEnabled(true);

		mainMenuButton.setImageResource(R.drawable.relations);

		AnimationSet animSetExit = new AnimationSet(false);
		animSetExit.addAnimation(flyOutToRightAnimation);
		animSetExit.addAnimation(alphaAnimFadeOut);
		animSetExit.setFillAfter(true);
		animSetExit.setFillEnabled(true);
		layoutMainMap.startAnimation(alphaAnimSemiFadeIn);
		layoutExtraMenuButtonGroup.startAnimation(animSetExit);
		layoutMainMap.setEnabled(true);
		enableLayout(layoutMainMap);
		imgOverlay.setEnabled(false);
		imgOverlay.setVisibility(View.INVISIBLE);
		layoutExtraMenuButtonGroup.postDelayed(
				new Runnable() {
					@Override
					public void run() {
						setExtraButtonsVisibility(false);
					}
				},
				getResources().getInteger(
						R.integer.menuButtonsGroup_animationDuration));
	}

	private void moveInButtons(final MainActivity context) {
		setExtraButtonsVisibility(true);

		final Animation flyInFromRightAnimation = AnimationUtils.loadAnimation(
				context, R.anim.menu_button_group_animation_in);
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

		mainMenuButton.setImageResource(R.drawable.close);
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

	private void setExtraButtonsVisibility(boolean visible) {
		int visibleID;
		if (visible) {
			visibleID = View.VISIBLE;
		} else {
			visibleID = View.INVISIBLE;
		}
		btnFloor1Map.setVisibility(visibleID);
		btnFloor2Map.setVisibility(visibleID);
		btnFloor3Map.setVisibility(visibleID);
		btnSocialMap.setVisibility(visibleID);
		btnOrbitalMap.setVisibility(visibleID);
		layoutExtraMenuButtonGroup.setVisibility(visibleID);
	}

	private void enableLayout(final ViewGroup layout) {
		for (int i = 0; i < layout.getChildCount(); i++) {
			View child = layout.getChildAt(i);
			child.setEnabled(true);
			child.setClickable(true);
		}
	}

	private void disableLayout(final ViewGroup layout) {
		for (int i = 0; i < layout.getChildCount(); i++) {
			View child = layout.getChildAt(i);
			child.setEnabled(false);
			child.setClickable(false);
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

		ImageButton btnCenterMap = (ImageButton) findViewById(R.id.btn_centermap);
		btnOrbitalMap = (ImageButton) findViewById(R.id.btn_orbitalmap);
		btnSocialMap = (ImageButton) findViewById(R.id.btn_socialmap);
		btnFloor3Map = (ImageButton) findViewById(R.id.btn_floor3map);
		btnFloor2Map = (ImageButton) findViewById(R.id.btn_floor2map);
		btnFloor1Map = (ImageButton) findViewById(R.id.btn_floor1map);

		btnCenterMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int layer = nervousMap.getSelectedMapLayer();
				loadMapGraph(layer);
				nervousMap.focusYouAndZoom();
			}
		});

		btnOrbitalMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				nervousMap.selectMapLayer(-1);
				loadMapGraph(-1);
			}
		});

		btnSocialMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				nervousMap.selectMapLayer(3);
				loadMapGraph(3);
			}
		});

		btnFloor3Map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				nervousMap.selectMapLayer(2);
				loadMapGraph(2);
			}
		});

		btnFloor2Map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				nervousMap.selectMapLayer(1);
				loadMapGraph(1);
			}
		});

		btnFloor1Map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				nervousMap.selectMapLayer(0);
				loadMapGraph(0);
			}
		});

		return layoutExtraMenuButtonGroup;
	}

	private void loadMapGraph(int level) {
		String youUuid = NervousVM.getInstance(getFilesDir()).getUUID()
				.toString().replace("-", "");
		switch (level) {
		case -1:
			// Update with BLE encounters from the last minute
			List<SensorDescBLEBeacon> beacons = new ArrayList<SensorDescBLEBeacon>();
			List<SensorData> datas = NervousVM.getInstance(getFilesDir())
					.retrieve(SensorDescBLEBeacon.SENSOR_ID,
							System.currentTimeMillis() - 20 * 1000,
							System.currentTimeMillis());
			if (datas != null) {
				for (SensorData data : datas) {
					beacons.add(new SensorDescBLEBeacon(data));
				}
				nervousMap.updateOrbitView(beacons);
			}
			break;
		case 0:
			new MapGraphLoader(getApplicationContext(),
					"http://nervous.ethz.ch/app_data/map-0.json", nervousMap,
					0, 0, youUuid, true).execute();
			new MapGraphLoader(getApplicationContext(),
					"http://nervous.ethz.ch/app_data/poi.json", nervousMap, 0,
					1, 1, true).execute();
			break;
		case 1:
			new MapGraphLoader(getApplicationContext(),
					"http://nervous.ethz.ch/app_data/map-1.json", nervousMap,
					1, 0, youUuid, true).execute();
			new MapGraphLoader(getApplicationContext(),
					"http://nervous.ethz.ch/app_data/poi.json", nervousMap, 1,
					1, 2, true).execute();
			break;
		case 2:
			new MapGraphLoader(getApplicationContext(),
					"http://nervous.ethz.ch/app_data/map-2.json", nervousMap,
					2, 0, youUuid, true).execute();
			new MapGraphLoader(getApplicationContext(),
					"http://nervous.ethz.ch/app_data/poi.json", nervousMap, 2,
					1, 3, true).execute();
			break;
		case 3:
			new MapGraphLoader(getApplicationContext(),
					"http://nervous.ethz.ch/app_data/map-sn.json", nervousMap,
					3, 0, youUuid, true).execute();
			break;
		}
	}

	private RelativeLayout setupMainMap() {
		final RelativeLayout layoutMainMap = (RelativeLayout) findViewById(R.id.layout_map);

		layoutMainMap.addView(nervousMap.getViewSwitcher());
		return layoutMainMap;
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateServiceInfo();
	}

	private void askServiceEnable() {
		final SharedPreferences prefs = getSharedPreferences(
				NervousStatics.SERVICE_PREFS, 0);
		boolean showServiceDialog = prefs.getBoolean("ShowServiceDialog", true);
		if (showServiceDialog) {
			View checkBoxView = View.inflate(this, R.layout.checkbox, null);
			CheckBox checkBox = (CheckBox) checkBoxView
					.findViewById(R.id.checkbox);
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					Editor edit = prefs.edit();
					edit.putBoolean("ShowServiceDialog", !isChecked);
					edit.commit();
				}
			});
			checkBox.setText(getString(R.string.dont_show_again));

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(true);
			builder.setTitle(getString(R.string.contribute));
			builder.setView(checkBoxView);
			builder.setMessage(getString(R.string.contribute_long));
			builder.setPositiveButton(getString(R.string.yes),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							startStopSensorService(true);
						}
					});
			builder.setNegativeButton(getString(R.string.no),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			builder.create().show();
		}
	}

	@TargetApi(18)
	private void initializeBluetooth() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

			if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
	}

	public void startStopSensorService(boolean on) {
		Intent sensorIntent = new Intent(getApplicationContext(),
				SensorService.class);
		Intent uploadIntent = new Intent(getApplicationContext(),
				UploadService.class);
		if (on) {
			startService(sensorIntent);
			startService(uploadIntent);
			serviceRunning = true;

			// If the user wants to collect BT/BLE data, ask to enable bluetooth
			// if disabled
			SensorConfiguration sc = SensorConfiguration
					.getInstance(getApplicationContext());
			SensorCollectStatus scs = sc
					.getInitialSensorCollectStatus(SensorDescBLEBeacon.SENSOR_ID);
			if (scs.isCollect()) {
				// This will only work on API level 18 or higher
				initializeBluetooth();
			}

		} else {
			stopService(sensorIntent);
			stopService(uploadIntent);
			serviceRunning = false;
		}
		updateServiceInfo();
	}

	public void updateServiceInfo() {
		serviceRunning = isServiceRunning(SensorService.class)
				&& isServiceRunning(UploadService.class);
		serviceSwitch.setChecked(serviceRunning);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.ccc_menu, menu);
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
		case R.id.menu_PrivacySettings:
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
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onTouchEvent(MapGraphNode oi) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(oi.getTitle() + ": " + oi.getSnippet());
		Dialog dialog = builder.create();
		dialog.setCancelable(true);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.y += 100;
		dialog.getWindow().setAttributes(params);
		dialog.getWindow().setGravity(Gravity.TOP);
		dialog.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		dialog.show();
	}

}
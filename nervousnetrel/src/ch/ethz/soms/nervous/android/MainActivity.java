package ch.ethz.soms.nervous.android;

import java.util.Random;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import ch.ethz.soms.nervous.android.sensors.SensorDescBLEBeacon;
import ch.ethz.soms.nervous.utils.NervousStatics;

public class MainActivity extends Activity {

	public static final String LOG_TAG = MainActivity.class.getSimpleName();

	private static final int REQUEST_ENABLE_BT = 0;
	
	private boolean serviceRunning;
	
	private static final int vibDuration = 50;
	int selectedActivity;
	private ImageButton btnMain, btnPrivacy, btnDataVis, btnColFreq, btnOn,
			btnOff,btnServerInfo;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);				

		final Vibrator vibrator = (Vibrator) this
				.getSystemService(VIBRATOR_SERVICE);

		btnMain = (ImageButton) findViewById(R.id.btn_main);
		btnPrivacy = (ImageButton) findViewById(R.id.btn_privacy);
		btnDataVis = (ImageButton) findViewById(R.id.btn_DataVisualizer);
		btnColFreq = (ImageButton) findViewById(R.id.btn_collectionFrequency);
		btnOn = (ImageButton) findViewById(R.id.btn_on);
		btnOff = (ImageButton) findViewById(R.id.btn_off);
		btnServerInfo = (ImageButton) findViewById(R.id.btn_serverInfo);

		btnMain.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				vibrator.vibrate(vibDuration);
				selectedActivity = 0;
				animateAllButtonsOut(btnMain);
			}

		});

		btnPrivacy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vibrator.vibrate(vibDuration);
				selectedActivity = 1;
				animateAllButtonsOut(btnPrivacy);
			}
		});

		btnDataVis.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vibrator.vibrate(vibDuration);
				selectedActivity = 2;
				animateAllButtonsOut(btnDataVis);
			}
		});

		btnColFreq.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vibrator.vibrate(vibDuration);
				selectedActivity = 3;
				animateAllButtonsOut(btnColFreq);
			}

		});

		btnOn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vibrator.vibrate(vibDuration);
				switchServiceOnOff();
			}
		});

		btnOff.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vibrator.vibrate(vibDuration);
				switchServiceOnOff();
			}
		});

		btnServerInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vibrator.vibrate(vibDuration);
				selectedActivity = 4;
				animateAllButtonsOut(btnServerInfo);
			}
		});
		
		updateServiceInfo();
		if (!serviceRunning) {
			askServiceEnable();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateServiceInfo();
	}

	private void askServiceEnable() {
		final SharedPreferences prefs = getSharedPreferences(NervousStatics.SERVICE_PREFS, 0);
		boolean showServiceDialog = prefs.getBoolean("ShowServiceDialog", true);
		if (showServiceDialog) {
			View checkBoxView = View.inflate(this, R.layout.checkbox, null);
			CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
			builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					startStopSensorService(true);
				}
			});
			builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {

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
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
	}

	public void startStopSensorService(boolean on) {		
		if (on) {
			SensorService.startService(this);
			UploadService.startService(this);
			serviceRunning = true;

			// If the user wants to collect BT/BLE data, ask to enable bluetooth if disabled
			SensorConfiguration sc = SensorConfiguration.getInstance(getApplicationContext());
			SensorCollectStatus scs = sc.getInitialSensorCollectStatus(SensorDescBLEBeacon.SENSOR_ID);
			if (scs.isCollect()) {
				// This will only work on API level 18 or higher
				initializeBluetooth();
			}

		} else {
			SensorService.stopService(this);
			UploadService.stopService(this);
			serviceRunning = false;
		}
		updateServiceInfo();
	}

	protected void switchServiceOnOff() {
		serviceRunning = !serviceRunning;

		startStopSensorService(serviceRunning);
		
		RotateAnimation rotAnim;
		rotAnim = new RotateAnimation(0, 360, btnOff.getX()
				+ (btnOff.getWidth() / 2), btnOff.getY()
				+ (btnOff.getHeight() / 2));
		rotAnim.setDuration(500);

		AnimationSet sAll = new AnimationSet(false);
		sAll.addAnimation(rotAnim);

		if (serviceRunning) {
			AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
			fadeOut.setDuration(500);
			sAll.addAnimation(fadeOut);
			sAll.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					btnOff.setVisibility(Button.INVISIBLE);
					btnOff.setEnabled(false);
					btnOn.setVisibility(Button.VISIBLE);
					btnOn.setEnabled(true);
				}
			});
		} else {
			AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
			fadeIn.setDuration(500);
			sAll.addAnimation(fadeIn);
			sAll.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					btnOff.setVisibility(Button.VISIBLE);
					btnOff.setEnabled(true);
					btnOn.setVisibility(Button.INVISIBLE);
					btnOn.setEnabled(false);
				}
			});
		}

		btnOff.startAnimation(sAll);
		btnOn.startAnimation(rotAnim);
	}
	
	public void updateServiceInfo() {
		serviceRunning = SensorService.isServiceRunning(this) && UploadService.isServiceRunning(this);		
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
			// TODO @Priya this method does not exist
//			tq.lightProxKMean();
			break;		
		case R.id.menu_SensorsStatistics:
	            intent = new Intent(this, SensorsStatisticsActivity.class);
	            intent.putExtra("serviceSwitchIsChecked", serviceRunning);
	            startActivity(intent);
	            break;
		default:
			break;

		}
		return super.onOptionsItemSelected(item);
	}
	
	public void resetButtons(int maxX, int maxY) {
		// Main Button
		float scale = 0.4f;
		float w = maxX * scale;
		float h = maxY * scale;
		int newW = (int) Math.min(w, h);
		int newH = (int) Math.min(w, h);

		float newX = (maxX * 0.5f) - (newW / 2);
		float newY = (maxY * 0.5f) - (newH / 2);

		resetButtonAnimateIn(btnMain, newW, newH, newX, newY);

		// privacy Button
		scale = 0.2f;
		w = maxX * scale;
		h = maxY * scale;
		newW = (int) Math.min(w, h);
		newH = (int) Math.min(w, h);

		newX = (maxX * 0.8f) - (newW / 2);
		newY = (maxY * 0.7f) - (newH / 2);

		resetButtonAnimateIn(btnPrivacy, newW, newH, newX, newY);

		// Data Visualizer button
		scale = 0.15f;
		w = maxX * scale;
		h = maxY * scale;
		newW = (int) Math.min(w, h);
		newH = (int) Math.min(w, h);

		newX = (maxX * 0.22f) - (newW / 2);
		newY = (maxY * 0.75f) - (newH / 2);

		resetButtonAnimateIn(btnDataVis, newW, newH, newX, newY);

		// Collection Frequency button
		scale = 0.2f;
		w = maxX * scale;
		h = maxY * scale;
		newW = (int) Math.min(w, h);
		newH = (int) Math.min(w, h);

		newX = (maxX * 0.7f) - (newW / 2);
		newY = (maxY * 0.2f) - (newH / 2);

		resetButtonAnimateIn(btnColFreq, newW, newH, newX, newY);

		//Server Info Button
		scale = 0.2f;
		w = maxX * scale;
		h = maxY * scale;
		newW = (int) Math.min(w, h);
		newH = (int) Math.min(w, h);

		newX = (maxX * 0.6f) - (newW / 2);
		newY = (maxY * 0.9f) - (newH / 2);

		resetButtonAnimateIn(btnServerInfo, newW, newH, newX, newY);
		
		// On-Off button
		scale = 0.13f;
		w = maxX * scale;
		h = maxY * scale;
		newW = (int) Math.min(w, h);
		newH = (int) Math.min(w, h);

		newX = (maxX * 0.2f) - (newW / 2);
		newY = (maxY * 0.1f) - (newH / 2);

		if (serviceRunning) {
			btnOn.setVisibility(Button.VISIBLE);
			btnOn.setEnabled(true);
			btnOff.setVisibility(Button.INVISIBLE);
			btnOff.setEnabled(false);
			resetButtonAnimateIn(btnOn, newW, newH, newX, newY);
			resetButtonPos(btnOff, newW, newH, newX, newY);
		} else {
			btnOff.setVisibility(Button.VISIBLE);
			btnOff.setEnabled(true);
			btnOn.setVisibility(Button.INVISIBLE);
			btnOn.setEnabled(false);
			resetButtonAnimateIn(btnOff, newW, newH, newX, newY);
			resetButtonPos(btnOn, newW, newH, newX, newY);
		}		
	}

	private void resetButtonAnimateIn(ImageButton btn, int newW, int newH,
			float newX, float newY) {
		btn.setX(newX);
		btn.setY(newY);

		android.view.ViewGroup.LayoutParams params = btn.getLayoutParams();
		params.height = newW;
		params.width = newH;
		btn.setLayoutParams(params);

		// animate in
		Random r = new Random();
		ScaleAnimation s1, s2;
		s1 = new ScaleAnimation(0, 1f, 0, 1f, newX + (newW / 2), newY
				+ (newH / 2));
		s1.setDuration(300);
		s1.setStartOffset(250 + r.nextInt(250));

		s2 = new ScaleAnimation(1.2f, 1, 1.2f, 1, newX + (newW / 2), newY
				+ (newH / 2));
		s2.setDuration(150);
		s2.setStartOffset(s1.getStartOffset() + s1.getDuration());

		AnimationSet sAll = new AnimationSet(false);
		sAll.addAnimation(s1);
		sAll.addAnimation(s2);
		btn.startAnimation(sAll);
	}

	private void resetButtonPos(ImageButton btn, int newW, int newH,
			float newX, float newY) {
		btn.setX(newX);
		btn.setY(newY);

		android.view.ViewGroup.LayoutParams params = btn.getLayoutParams();
		params.height = newW;
		params.width = newH;
		btn.setLayoutParams(params);
	}

	private void animateButtonOutSelected(ImageButton btn) {
		ScaleAnimation s1, s2;
		s1 = new ScaleAnimation(1, 2, 1, 2, btn.getX() + (btn.getWidth() / 2),
				btn.getY() + (btn.getHeight() / 2));
		s1.setDuration(200);

		s2 = new ScaleAnimation(1, 0, 1, 0, btn.getX() + (btn.getWidth() / 2),
				btn.getY() + (btn.getHeight() / 2));
		s2.setDuration(400);
		s2.setStartOffset(s1.getStartOffset() + s1.getDuration());

		AnimationSet s = new AnimationSet(false);
		s.addAnimation(s1);
		s.addAnimation(s2);
		s.setFillAfter(true);
		s.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				Intent intent = null;
				switch (selectedActivity) {
				case 0:
//					intent = new Intent(MainActivity.this,
//							ServerDetailsActivity.class);
//					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					// TODO nothing yet
					break;
				case 1:
					intent = new Intent(MainActivity.this,
							SensorLoggingToggleActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					break;
				case 2:
					intent = new Intent(MainActivity.this,
							SensorsStatisticsActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					break;
				case 3:
					intent = new Intent(MainActivity.this,
							SensorFrequencyActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					break;
				case 4:
					intent = new Intent(MainActivity.this,
							ServerDetailsActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					break;
				default:
					break;
				}
				if (intent != null) {
					startActivity(intent);
				}
				// toastToScreen(msg, false);
			}
		});
		btn.startAnimation(s);
	}

	private void animateButtonOut(ImageButton btn) {
		ScaleAnimation scaleAnimMinus;
		scaleAnimMinus = new ScaleAnimation(1, 0, 1, 0, btn.getX()
				+ (btn.getWidth() / 2), btn.getY() + (btn.getHeight() / 2));
		scaleAnimMinus.setDuration(500);
		scaleAnimMinus.setFillAfter(true);
		btn.startAnimation(scaleAnimMinus);
	}

	private void animateAllButtonsOut(ImageButton selectedButton) {
		animateButtonOutSelected(selectedButton);
		if (!selectedButton.equals(btnMain)) {
			animateButtonOut(btnMain);
		}
		if (!selectedButton.equals(btnDataVis)) {
			animateButtonOut(btnDataVis);
		}
		if (!selectedButton.equals(btnPrivacy)) {
			animateButtonOut(btnPrivacy);
		}
		if (!selectedButton.equals(btnColFreq)) {
			animateButtonOut(btnColFreq);
		}
		if (!selectedButton.equals(btnServerInfo)) {
			animateButtonOut(btnServerInfo);
		}
		animateButtonOut(btnOn);
		animateButtonOut(btnOff);
	}

	public void toastToScreen(String msg, boolean lengthLong) {

		int toastLength = lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
		Toast.makeText(getApplicationContext(), msg, toastLength).show();
	}	
}
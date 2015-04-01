package com.example.nervousnet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int vibDuration = 50;
	int selectedActivity;
	private ImageButton btnMain, btnPrivacy, btnDataVis, btnColFreq;
	DrawView drawView;
	private Button b1;
	private DrawView dw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final Animation animIn = AnimationUtils.loadAnimation(this,
				R.anim.anim_scale_zoom_in);
		final Animation animOutSel = AnimationUtils.loadAnimation(this,
				R.anim.anim_scale_zoom_out_selected);
		final Animation animOut = AnimationUtils.loadAnimation(this,
				R.anim.anim_scale_zoom_out);

		b1 = (Button)findViewById(R.id.button1);
		b1.setBackgroundColor(Color.GREEN);
		dw = (DrawView) findViewById(R.id.signature_canvas);

		
		
		animOutSel.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				String msg = "";
				Intent intent = null;
				switch (selectedActivity) {
				case 0:
					msg = "Selected Main Apps";
					break;
				case 1:
					msg = "Selected Privacy Settings";
					intent = new Intent(MainActivity.this,
							SensorLoggingToggleActivity.class);
					break;
				case 2:
					msg = "Selected Data Visualization";
					break;
				case 3:
					msg = "Selected Collection Frequency";
					break;

				default:
					break;
				}
				if (intent == null) {
					intent = new Intent(MainActivity.this,
							SensorLoggingToggleActivity.class);
				}
				toastToScreen(msg, false);
				startActivity(intent);
			}
		});
//
//		// Setup Buttons-------------------------------------------------------
		final Vibrator vibrator = (Vibrator) this
				.getSystemService(VIBRATOR_SERVICE);

		btnMain = (ImageButton) findViewById(R.id.btn_main);
//		btnPrivacy = (ImageButton) findViewById(R.id.btn_privacy);
//		btnDataVis = (ImageButton) findViewById(R.id.btn_DataVisualizer);
//		btnColFreq = (ImageButton) findViewById(R.id.btn_collectionFrequency);
//
		btnMain.startAnimation(animIn);
		b1.startAnimation(animIn);
//		btnPrivacy.startAnimation(animIn);
//		btnDataVis.startAnimation(animIn);
//		btnColFreq.startAnimation(animIn);
//
		btnMain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vibrator.vibrate(vibDuration);
				selectedActivity = 0;
				btnMain.startAnimation(animOutSel);
//				btnPrivacy.startAnimation(animOut);
//				btnDataVis.startAnimation(animOut);
//				btnColFreq.startAnimation(animOut);
			}
		});
//
//		btnPrivacy.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				vibrator.vibrate(vibDuration);
//				selectedActivity = 1;
//				btnMain.startAnimation(animOut);
//				btnPrivacy.startAnimation(animOutSel);
//				btnDataVis.startAnimation(animOut);
//				btnColFreq.startAnimation(animOut);
//			}
//		});
//
//		btnDataVis.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				vibrator.vibrate(vibDuration);
//				selectedActivity = 2;
//				btnMain.startAnimation(animOut);
//				btnPrivacy.startAnimation(animOut);
//				btnDataVis.startAnimation(animOutSel);
//				btnColFreq.startAnimation(animOut);
//			}
//		});
//
//		btnColFreq.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				vibrator.vibrate(vibDuration);
//				selectedActivity = 3;
//				btnMain.startAnimation(animOut);
//				btnPrivacy.startAnimation(animOut);
//				btnDataVis.startAnimation(animOut);
//				btnColFreq.startAnimation(animOutSel);
//			}
//		});
//		// -----------------------------------------------------------
//
//		// //-------------Load background image
//		final ImageView img_background = (ImageView) findViewById(R.id.img_backgroundMain);
//		new AsyncTask<Void, Void, Void>() {
//			protected Void doInBackground(Void... params) {
//				return null;
//			};
//
//			protected void onPostExecute(Void result) {
//				// img_background
//				// .setImageResource(R.drawable.background_graph_mockup);
//			};
//		}.execute();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		//TODO: Connect Activities here
		int id = item.getItemId();
		switch (id) {
		case R.id.menu_main:
			toastToScreen("Start Main Apps", false);
			break;
		case R.id.menu_visualizer:
			toastToScreen("Start Visualizer", false);
			break;
		case R.id.menu_privacy:
			toastToScreen("Start Privacy", false);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		final Animation animIn = AnimationUtils.loadAnimation(this,
				R.anim.anim_scale_zoom_in);
		btnMain.startAnimation(animIn);
//		btnPrivacy.startAnimation(animIn);
//		btnDataVis.startAnimation(animIn);
//		btnColFreq.startAnimation(animIn);
		super.onResume();
	}

	public void resetButtons(int maxW, int maxH){
		int newW = maxW/2;
		int newH = maxH/2;
		b1.setX(newW);
		b1.setY(newH);
		b1.setWidth(maxW/4);
		b1.setHeight(maxH/4);
		
		btnMain.setX(newW);
//		toastToScreen("adjusted buttons" + newW + ", " + newH, false);
	}
	
	public void toastToScreen(String msg, boolean lengthLong) {

		int toastLength = lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
		Toast.makeText(getApplicationContext(), msg, toastLength).show();
	}

}

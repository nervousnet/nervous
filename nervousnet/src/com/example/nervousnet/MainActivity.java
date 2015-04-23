package com.example.nervousnet;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int vibDuration = 50;
	int selectedActivity;
	private ImageButton btnMain, btnPrivacy, btnDataVis, btnColFreq;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// // Setup
		// Buttons-------------------------------------------------------
		final Vibrator vibrator = (Vibrator) this
				.getSystemService(VIBRATOR_SERVICE);

		btnMain = (ImageButton) findViewById(R.id.btn_main);
		btnPrivacy = (ImageButton) findViewById(R.id.btn_privacy);
		btnDataVis = (ImageButton) findViewById(R.id.btn_DataVisualizer);
		btnColFreq = (ImageButton) findViewById(R.id.btn_collectionFrequency);

		btnMain.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				vibrator.vibrate(vibDuration);
				selectedActivity = 0;
				animateButtonOutSelected(btnMain);
				animateButtonOut(btnColFreq);
				animateButtonOut(btnDataVis);
				animateButtonOut(btnPrivacy);
			}

		});

		btnPrivacy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vibrator.vibrate(vibDuration);
				selectedActivity = 1;
				animateButtonOutSelected(btnPrivacy);
				animateButtonOut(btnColFreq);
				animateButtonOut(btnDataVis);
				animateButtonOut(btnMain);
			}
		});

		btnDataVis.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vibrator.vibrate(vibDuration);
				selectedActivity = 2;
				animateButtonOutSelected(btnDataVis);
				animateButtonOut(btnColFreq);
				animateButtonOut(btnMain);
				animateButtonOut(btnPrivacy);
			}
		});

		btnColFreq.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vibrator.vibrate(vibDuration);
				selectedActivity = 3;
				animateButtonOutSelected(btnColFreq);
				animateButtonOut(btnMain);
				animateButtonOut(btnDataVis);
				animateButtonOut(btnPrivacy);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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

		resetButton(btnMain, newW, newH, newX, newY);

		// privacy Button
		scale = 0.2f;
		w = maxX * scale;
		h = maxY * scale;
		newW = (int) Math.min(w, h);
		newH = (int) Math.min(w, h);

		newX = (maxX * 0.8f) - (newW / 2);
		newY = (maxY * 0.7f) - (newH / 2);

		resetButton(btnPrivacy, newW, newH, newX, newY);

		// Data Visualizer button
		scale = 0.15f;
		w = maxX * scale;
		h = maxY * scale;
		newW = (int) Math.min(w, h);
		newH = (int) Math.min(w, h);

		newX = (maxX * 0.2f) - (newW / 2);
		newY = (maxY * 0.8f) - (newH / 2);

		resetButton(btnDataVis, newW, newH, newX, newY);

		// Collection Frequency button
		scale = 0.2f;
		w = maxX * scale;
		h = maxY * scale;
		newW = (int) Math.min(w, h);
		newH = (int) Math.min(w, h);

		newX = (maxX * 0.7f) - (newW / 2);
		newY = (maxY * 0.2f) - (newH / 2);

		resetButton(btnColFreq, newW, newH, newX, newY);

	}

	private void resetButton(ImageButton btn, int newW, int newH, float newX,
			float newY) {
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
					intent = new Intent(MainActivity.this,
							SensorLoggingToggleActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					break;
				case 1:
					intent = new Intent(MainActivity.this,
							SensorLoggingToggleActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					break;
				case 2:
					intent = new Intent(MainActivity.this,
							SensorLoggingToggleActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					break;
				case 3:
					intent = new Intent(MainActivity.this,
							SensorLoggingToggleActivity.class);
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

	public void toastToScreen(String msg, boolean lengthLong) {

		int toastLength = lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
		Toast.makeText(getApplicationContext(), msg, toastLength).show();
	}

}

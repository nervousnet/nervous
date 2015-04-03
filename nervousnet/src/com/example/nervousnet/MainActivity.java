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
	private DrawView dw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dw = (DrawView) findViewById(R.id.signature_canvas);

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
			}
		});

		btnPrivacy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vibrator.vibrate(vibDuration);
				selectedActivity = 1;
			}
		});

		btnDataVis.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vibrator.vibrate(vibDuration);
				selectedActivity = 2;
			}
		});

		btnColFreq.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vibrator.vibrate(vibDuration);
				selectedActivity = 3;
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// TODO: Connect Activities here
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
	}

	public void toastToScreen(String msg, boolean lengthLong) {

		int toastLength = lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
		Toast.makeText(getApplicationContext(), msg, toastLength).show();
	}

}

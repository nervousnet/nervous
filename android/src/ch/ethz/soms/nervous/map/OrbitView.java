package ch.ethz.soms.nervous.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import ch.ethz.soms.nervous.android.R;
import ch.ethz.soms.nervous.android.sensors.SensorDescBLEBeacon;

public class OrbitView extends View {

	private static final float VELOCITY_SCALE = 0.15f;
	private static final float TEXT_SIZE_YOU = 18.f;
	private static final float TEXT_SIZE_ORBITER = 14.f;
	private static final float VELOCITY_MIN = 0.05f;

	private float alpha = 255;
	private Paint textPaintYou;
	private Paint textPaintOrbiter;
	private Paint orbitPaint;
	private Paint circlePaintYou;
	private Paint circlePaint[];
	private List<Orbiter> orbits;
	private TextShapeDrawable youDrawable;
	private float scaleFactor = 1.0f;
	private OrbitView selfReference;

	private Handler viewHandler;
	private Runnable viewUpdate;

	private class Orbiter {
		private TextShapeDrawable drawable;
		private float velocity;
		private float ratio;
		private float angularPosition;

		public Orbiter(TextShapeDrawable drawable, float velocity, float ratio) {
			this.drawable = drawable;
			this.velocity = velocity;
			this.ratio = ratio;
			this.angularPosition = (float) (Math.random() * 2 * Math.PI);
		}

		public void draw(Canvas canvas, boolean mode) {
			float height = canvas.getHeight();
			float width = canvas.getWidth();
			float baseRadius = Math.min(height / 2.f, width / 2.f);
			float minRadius = 1.3f * (float) youDrawable.getRadius();
			float radius = ratio * 0.9f * (baseRadius - minRadius) + minRadius;

			float x = width / 2.f + (float) Math.sin(angularPosition) * radius;
			float y = height / 2.f + (float) Math.cos(angularPosition) * radius;

			if (mode) {
				drawable.draw(canvas, (int) x, (int) y);
			} else {
				canvas.drawCircle(width / 2.f, height / 2.f, radius, orbitPaint);
			}
		}

		public void updatePosition() {
			angularPosition += velocity;
			while (angularPosition >= 2 * Math.PI) {
				angularPosition -= 2 * Math.PI;
			}
		}
	}


	public OrbitView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setup();
	}

	public OrbitView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup();
	}

	public OrbitView(Context context) {
		super(context);
		setup();
	}

	private void setup() {
		setWillNotDraw(false);

		selfReference = this;

		DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
		scaleFactor = metrics.density;

		circlePaintYou = new Paint();
		circlePaintYou.setColor(getContext().getResources().getColor(R.color.orange_nervous));
		circlePaintYou.setStyle(Paint.Style.FILL);
		circlePaintYou.setAntiAlias(true);

		circlePaint = new Paint[2];

		circlePaint[0] = new Paint();
		circlePaint[0].setColor(getContext().getResources().getColor(R.color.blue_nervous));
		circlePaint[0].setStyle(Paint.Style.FILL);
		circlePaint[0].setAntiAlias(true);

		circlePaint[1] = new Paint();
		circlePaint[1].setColor(getContext().getResources().getColor(R.color.green_nervous));
		circlePaint[1].setStyle(Paint.Style.FILL);
		circlePaint[1].setAntiAlias(true);

		orbitPaint = new Paint();
		orbitPaint.setColor(getContext().getResources().getColor(R.color.gray_nervous_dark));
		orbitPaint.setStyle(Paint.Style.STROKE);
		orbitPaint.setStrokeWidth(scaleFactor * 3f);
		orbitPaint.setAntiAlias(true);

		textPaintYou = new Paint();
		textPaintYou.setColor(Color.WHITE);
		textPaintYou.setTextSize(TEXT_SIZE_YOU * scaleFactor);
		textPaintYou.setAntiAlias(true);
		textPaintYou.setFakeBoldText(true);
		textPaintYou.setShadowLayer(6f * scaleFactor, 0, 0, Color.BLACK);
		textPaintYou.setStyle(Paint.Style.FILL);
		textPaintYou.setTextAlign(Paint.Align.CENTER);

		textPaintOrbiter = new Paint();
		textPaintOrbiter.setColor(Color.WHITE);
		textPaintOrbiter.setTextSize(TEXT_SIZE_ORBITER * scaleFactor);
		textPaintOrbiter.setAntiAlias(true);
		textPaintOrbiter.setFakeBoldText(true);
		textPaintOrbiter.setShadowLayer(6f * scaleFactor, 0, 0, Color.BLACK);
		textPaintOrbiter.setStyle(Paint.Style.FILL);
		textPaintOrbiter.setTextAlign(Paint.Align.CENTER);

		youDrawable = new TextShapeDrawable(new String[] { getContext().getResources().getString(R.string.you) }, scaleFactor, circlePaintYou, textPaintYou);
		orbits = new ArrayList<OrbitView.Orbiter>();

		viewHandler = new Handler();
		viewUpdate = new Runnable() {
			@Override
			public void run() {
				for (Orbiter orbit : orbits) {
					orbit.updatePosition();
				}
				selfReference.invalidate();
				viewHandler.postDelayed(this, 40);
			}
		};
	}

	public void startAnimation() {
		viewHandler.removeCallbacks(viewUpdate);
		viewHandler.postDelayed(viewUpdate, 40);
	}

	void stopAnimation() {
		viewHandler.removeCallbacks(viewUpdate);
	}

	public void setBeacons(List<SensorDescBLEBeacon> bleBeacons) {

		// Reverse the list (new to new)
		Collections.reverse(bleBeacons);

		// Remove duplicates in time
		HashSet<String> duplicateSet = new HashSet<String>();
		Iterator<SensorDescBLEBeacon> it = bleBeacons.iterator();
		while (it.hasNext()) {
			SensorDescBLEBeacon beacon = it.next();
			String id = String.valueOf(beacon.getMajor()) + "_" + String.valueOf(beacon.getMinor());
			if (duplicateSet.contains(id)) {
				it.remove();
			} else {
				duplicateSet.add(id);
			}
		}

		// Remove the old ones
		orbits.clear();

		// Add new ones, without duplicates

		float maxDistance = 0.0f;

		for (SensorDescBLEBeacon beacon : bleBeacons) {
			float txpower = beacon.getTxpower();
			float rssi = beacon.getRssi();
			float distance = calculateDistance(txpower, rssi);
			maxDistance = Math.max(maxDistance, distance);
		}

		for (SensorDescBLEBeacon beacon : bleBeacons) {
			int paintSelect = beacon.getMajor() == 0x8037 ? 0 : 1;
			float txpower = beacon.getTxpower();
			float rssi = beacon.getRssi();
			float distance = calculateDistance(txpower, rssi);
			float ratio = distance / (0.000001f + maxDistance);
			float velocity = (float) Math.random() * VELOCITY_SCALE - VELOCITY_SCALE / 2.f;
			velocity = Math.signum(velocity) * VELOCITY_MIN + velocity;
			orbits.add(new Orbiter(new TextShapeDrawable(new String[] { String.valueOf(beacon.getMinor()) }, scaleFactor, circlePaint[paintSelect], textPaintOrbiter), velocity, ratio));
		}
	}

	protected static float calculateDistance(float txpower, float rssi) {
		if (rssi == 0) {
			return 0.0f;
		}
		float ratio = rssi / (float) txpower;
		if (ratio < 1.0) {
			return (float) Math.pow(ratio, 10.f);
		} else {
			float distance = (0.89976f) * (float) Math.pow(ratio, 7.7095f) + 0.111f;
			return distance;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.saveLayerAlpha(0.f, 0.f, (float) canvas.getWidth(), (float) canvas.getHeight(), (int) alpha, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
		super.onDraw(canvas);
		canvas.drawColor(getContext().getResources().getColor(R.color.gray_nervous_light));
		for (Orbiter orbiter : orbits) {
			orbiter.draw(canvas, false);
		}
		for (Orbiter orbiter : orbits) {
			orbiter.draw(canvas, true);
		}
		youDrawable.draw(canvas, canvas.getWidth() / 2, canvas.getHeight() / 2);
		invalidate();
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	@Override
	public float getAlpha() {
		return alpha;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_UP:
			this.performClick();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean performClick() {
		super.performClick();
		return true;
	}

}
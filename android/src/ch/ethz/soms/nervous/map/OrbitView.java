package ch.ethz.soms.nervous.map;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import ch.ethz.soms.nervous.android.R;
import ch.ethz.soms.nervous.android.sensors.SensorDescBLEBeacon;

public class OrbitView extends View {

	private static final float VELOCITY_SCALE = 10.f;
	private static final float TEXT_SIZE_YOU = 18.f;
	private static final float TEXT_SIZE_ORBITER = 14.f;

	private float alpha = 255;

	private Paint textPaintYou;
	private Paint textPaintOrbiter;
	private Paint circlePaintYou;
	private Paint circlePaint[];

	private TextShapeDrawable youDrawable;

	private float scaleFactor = 1.0f;

	private class Orbiter {
		private TextShapeDrawable drawable;
		private float velocity;
		private float radius;

		public Orbiter(TextShapeDrawable drawable, float velocity, float radius) {
			this.drawable = drawable;
			this.velocity = velocity;
			this.radius = radius;
		}

		public void draw(Canvas canvas) {
			float height = canvas.getHeight();
			float width = canvas.getWidth();
			float maxRadius = 0.8f * Math.min(height, width);

			float x = (float) Math.sin(0.0) * maxRadius;
			float y = (float) Math.cos(0.0) * maxRadius;

			drawable.draw(canvas, (int) x, (int) y);
		}

	}

	private class TextShapeDrawable extends Drawable {

		private String[] text;
		private int x;
		private int y;
		private float scaleFactor;
		private Paint paint;
		private Paint textPaint;

		public TextShapeDrawable(String[] text, float scaleFactor, Paint paint, Paint textPaint) {
			this.text = text;

			this.scaleFactor = scaleFactor;
			this.paint = paint;
			this.textPaint = textPaint;
		}

		public void draw(Canvas canvas, int x, int y) {
			this.x = x;
			this.y = y;
			this.draw(canvas);
		}

		@Override
		public void draw(Canvas canvas) {

			Rect bounds = new Rect();

			float height = 0;
			float width = 0;

			for (int i = 0; i < text.length; ++i) {
				textPaint.getTextBounds(text[i], 0, text[i].length(), bounds);
				height += 1.2f * (float) Math.abs(bounds.bottom - bounds.top);
				width = Math.max(width, Math.abs(bounds.right - bounds.left));
			}

			int radius = (int) (0.6 * Math.sqrt(height * height + width * width));
			canvas.drawCircle(x, y, radius, paint);

			for (int i = 0; i < text.length; ++i) {
				canvas.drawText(text[i], x, y + (int) ((0.7 + i - (float) text.length / 2.0) * (1.2f * Math.abs((float) height / (float) text.length))), textPaint);
			}

		}

		@Override
		public int getOpacity() {
			return 0;
		}

		@Override
		public void setAlpha(int alpha) {

		}

		@Override
		public void setColorFilter(ColorFilter cf) {
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
	}

	private List<Orbiter> orbits;

	public void setBeacons(List<SensorDescBLEBeacon> bleBeacons) {
		Random random = new Random();
		for (SensorDescBLEBeacon beacon : bleBeacons) {
			int paintSelect = random.nextInt(circlePaint.length);
			orbits.add(new Orbiter(new TextShapeDrawable(new String[] { String.valueOf(beacon.getMinor()) }, scaleFactor, circlePaint[paintSelect], textPaintOrbiter), (float) Math.random() * VELOCITY_SCALE - VELOCITY_SCALE / 2.f, 50.f));
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.saveLayerAlpha(0.f, 0.f, (float) canvas.getWidth(), (float) canvas.getHeight(), (int) alpha, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
		super.onDraw(canvas);
		canvas.drawColor(getContext().getResources().getColor(R.color.gray_nervous_light));
		for (Orbiter orbiter : orbits) {
			orbiter.draw(canvas);
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
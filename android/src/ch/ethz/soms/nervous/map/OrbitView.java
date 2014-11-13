package ch.ethz.soms.nervous.map;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import ch.ethz.soms.nervous.android.sensors.SensorDescBLEBeacon;

public class OrbitView extends View {

	private float alpha = 255;

	private Paint textPaint;
	private Paint circlePaint;

	private class TextShapeDrawable extends Drawable {

		private String[] text;
		private int x;
		private int y;

		public TextShapeDrawable(String[] text, int x, int y) {
			this.text = text;
			this.x = x;
			this.y = y;
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
			canvas.drawCircle(x, y, radius, circlePaint);

			for (int i = 0; i < text.length; ++i) {
				canvas.drawText(text[i], x, y + (int) ((1.0 / 2.0 + 0.2 + i - (float) text.length / 2.0) * (1.2f * Math.abs((float) height / (float) text.length))), textPaint);
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

		circlePaint = new Paint();
		circlePaint.setColor(Color.argb(200, 200, 130, 90));
		circlePaint.setStyle(Paint.Style.FILL);
		circlePaint.setAntiAlias(true);

		textPaint = new Paint();
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(22f);
		textPaint.setAntiAlias(true);
		textPaint.setFakeBoldText(true);
		// textPaint.setShadowLayer(6f, 0, 0, Color.BLACK);
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setTextAlign(Paint.Align.CENTER);
	}

	private List<SensorDescBLEBeacon> bleBeacons;

	public void setBeacons(List<SensorDescBLEBeacon> bleBeacons) {
		this.bleBeacons = bleBeacons;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.saveLayerAlpha(0.f, 0.f, (float) canvas.getWidth(), (float) canvas.getHeight(), (int) alpha, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);
		TextShapeDrawable youDrawable = new TextShapeDrawable(new String[] { "YOU", "LOL", "TEXT" }, canvas.getWidth() / 2, canvas.getHeight() / 2);
		youDrawable.draw(canvas);
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
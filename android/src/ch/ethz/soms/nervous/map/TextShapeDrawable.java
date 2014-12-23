package ch.ethz.soms.nervous.map;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

class TextShapeDrawable extends Drawable {

	private String[] text;
	private int x;
	private int y;
	private Paint paint;
	private Paint textPaint;
	private int width;
	private int height;
	private int radius;

	public TextShapeDrawable(String[] text, Paint paint, Paint textPaint) {
		this.text = text;
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

		height = 0;
		width = 0;

		for (int i = 0; i < text.length; ++i) {
			textPaint.getTextBounds(text[i], 0, text[i].length(), bounds);
			height += 1.2f * (float) Math.abs(bounds.bottom - bounds.top);
			width = Math.max(width, Math.abs(bounds.right - bounds.left));
		}

		radius = (int) (0.6 * Math.sqrt(height * height + width * width));
		canvas.drawCircle(x, y, radius, paint);

		for (int i = 0; i < text.length; ++i) {
			canvas.drawText(text[i], x, y + (int) ((0.8 + i - (float) text.length / 2.0) * (1.2f * Math.abs((float) height / (float) text.length))), textPaint);
		}
	}

	public int getRadius() {
		return radius;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		super.setBounds(left, top, right, bottom);
		x = (left + right) / 2;
		y = (top + bottom) / 2;
	}

	@Override
	public void setBounds(android.graphics.Rect bounds) {
		setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
	}

	@Override
	public int getIntrinsicHeight() {
		return 2 * radius;
	}

	@Override
	public int getIntrinsicWidth() {
		return 2 * radius;
	}

	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
	}

}
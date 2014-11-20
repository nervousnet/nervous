package ch.ethz.soms.nervous.map;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;

public class MapGraphMarker extends Drawable {

	public static final int COLOR_ORANGE = 0xFFF15F18;
	public static final int COLOR_GREY = 0xFFC8C8C8;
	public static final int COLOR_BLACK = 0xFF000000;

	public static final int TYPE_EMPTY_CIRCLE_GREY = 0;
	public static final int TYPE_FULL_CIRCLE_GREY = 1;
	public static final int TYPE_DOUBLE_CIRCLE_ORANGE = 2;
	
	private static MapGraphMarker markers[] = new MapGraphMarker[3];

	public static MapGraphMarker getMapGrahpMarker(int type) {
		if (type < markers.length) {
			if (markers[type] == null) {
				markers[type] = new MapGraphMarker(type);
			}
			return markers[type];
		} else {
			return null;
		}
	}

	private Paint paint;
	private int type;
	private float x = 0;
	private float y = 0;

	private MapGraphMarker(int type) {
		this.type = type;
		int color;
		switch (type) {
		case TYPE_EMPTY_CIRCLE_GREY:
		case TYPE_FULL_CIRCLE_GREY:
			color = COLOR_GREY;
			break;
		case TYPE_DOUBLE_CIRCLE_ORANGE:
			color = COLOR_ORANGE;
			break;
		default:
			color = COLOR_BLACK;
		}
		this.paint = new Paint();
		paint.setColor(color);
	}

	@Override
	public void draw(Canvas canv) {
		switch (type) {
		case TYPE_DOUBLE_CIRCLE_ORANGE:
		case TYPE_EMPTY_CIRCLE_GREY:
		case TYPE_FULL_CIRCLE_GREY:
		default:
			canv.drawCircle(x + 7, y + 7, 15, paint);
		}
	}

	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		x = left;
		y = top;
	}

	@Override
	public void setBounds(android.graphics.Rect bounds) {
		x = bounds.left;
		y = bounds.top;
	}

	@Override
	public int getIntrinsicHeight() {
		// TODO
		return 15;
	}

	@Override
	public int getIntrinsicWidth() {
		// TODO
		return 15;
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {
		paint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter colorFilter) {
		paint.setColorFilter(colorFilter);
	}

}

package ch.ethz.soms.nervous.map;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;

public class MapGraphMarker extends Drawable {

	public static final int COLOR_ORANGE = 0xFFF15F18;
	public static final int COLOR_GREY = 0xFFC8C8C8;
	public static final int COLOR_BLACK = 0xFF000000;

	public static final int TYPE_EMPTY_CIRCLE_GREY = 0;
	public static final int TYPE_FULL_CIRCLE_GREY = 1;
	public static final int TYPE_FULL_CIRCLE_ORANGE = 2;

	public static final float STROKE_WIDTH = 7.f;
	public static final float CIRCLE_DIAMETER = 15.f;
	public static final float CIRCLE_RADIUS = CIRCLE_DIAMETER / 2.f;

	private static MapGraphMarker markers[] = new MapGraphMarker[3];

	public static MapGraphMarker getMapGrahpMarker(int type) {

		if (!paintInit) {
			initializePaints();
			paintInit = true;
		}

		if (type < markers.length) {
			if (markers[type] == null) {
				markers[type] = new MapGraphMarker(type);
			}
			return markers[type];
		} else {
			return null;
		}
	}

	private static boolean paintInit;
	private static Paint paintGreyStroke;
	private static Paint paintOrangeFull;
	private static Paint paintGreyFull;

	private static void initializePaints() {
		paintGreyFull = new Paint();
		paintGreyFull.setColor(COLOR_GREY);
		paintGreyFull.setStyle(Style.FILL_AND_STROKE);
		paintGreyFull.setStrokeWidth(STROKE_WIDTH);

		paintGreyStroke = new Paint();
		paintGreyStroke.setColor(COLOR_GREY);
		paintGreyStroke.setStyle(Style.STROKE);
		paintGreyStroke.setStrokeWidth(STROKE_WIDTH);

		paintOrangeFull = new Paint();
		paintOrangeFull.setColor(COLOR_ORANGE);
		paintOrangeFull.setStyle(Style.FILL_AND_STROKE);
		paintOrangeFull.setStrokeWidth(STROKE_WIDTH);
	}

	private int type;
	private float x = 0;
	private float y = 0;

	private MapGraphMarker(int type) {
		this.type = type;
	}

	@Override
	public void draw(Canvas canv) {
		Paint paint = getPaint();
		canv.drawCircle(x, y, CIRCLE_DIAMETER, paint);
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
		return (int) CIRCLE_DIAMETER;
	}

	@Override
	public int getIntrinsicWidth() {
		return (int) CIRCLE_DIAMETER;
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {
		// paint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter colorFilter) {
		// paint.setColorFilter(colorFilter);
	}

	public Paint getPaint() {
		switch (type) {
		case TYPE_FULL_CIRCLE_ORANGE:
			return paintOrangeFull;
		case TYPE_EMPTY_CIRCLE_GREY:
			return paintGreyStroke;
		case TYPE_FULL_CIRCLE_GREY:
			return paintGreyFull;
		default:
			return paintGreyFull;
		}
	}

}

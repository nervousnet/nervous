package ch.ethz.soms.nervous.map;

import ch.ethz.soms.nervous.android.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

public class MapGraphMarker extends Drawable {


	public static final int TYPE_EMPTY_CIRCLE_GRAY = 0;
	public static final int TYPE_FULL_CIRCLE_GRAY = 1;
	public static final int TYPE_FULL_CIRCLE_ORANGE = 2;

	public static final float STROKE_WIDTH_DPI = 5.f;
	public static final float CIRCLE_DIAMETER_DPI = 10.f;

	private static int colorOrange = 0;
	private static int colorGray = 0;
	private static int colorBlack = 0;
	
	private static float stroke_width_scaled;
	private static float circle_diameter_scaled;
	private static float circle_radius_scaled;

	private static MapGraphMarker markers[] = new MapGraphMarker[3];
	private static float scaleFactor = 1.0f;

	public static MapGraphMarker getMapGrahpMarker(Context context, int type) {

		if (!paintInit) {
			
			colorOrange = context.getResources().getColor(R.color.orange_nervous);
			colorGray = context.getResources().getColor(R.color.gray_nervous);
			colorBlack = 0xFF000000;
			
			DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			scaleFactor = metrics.density;

			stroke_width_scaled = STROKE_WIDTH_DPI * scaleFactor;
			circle_diameter_scaled = CIRCLE_DIAMETER_DPI * scaleFactor;
			circle_radius_scaled = circle_diameter_scaled / 2.f;

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
		paintGreyFull.setColor(colorGray);
		paintGreyFull.setStyle(Style.FILL_AND_STROKE);
		paintGreyFull.setStrokeWidth(stroke_width_scaled);

		paintGreyStroke = new Paint();
		paintGreyStroke.setColor(colorGray);
		paintGreyStroke.setStyle(Style.STROKE);
		paintGreyStroke.setStrokeWidth(stroke_width_scaled);

		paintOrangeFull = new Paint();
		paintOrangeFull.setColor(colorOrange);
		paintOrangeFull.setStyle(Style.FILL_AND_STROKE);
		paintOrangeFull.setStrokeWidth(stroke_width_scaled);
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
		canv.drawCircle(x, y, circle_diameter_scaled, paint);
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
		return (int) circle_diameter_scaled;
	}

	@Override
	public int getIntrinsicWidth() {
		return (int) circle_diameter_scaled;
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
		case TYPE_EMPTY_CIRCLE_GRAY:
			return paintGreyStroke;
		case TYPE_FULL_CIRCLE_GRAY:
			return paintGreyFull;
		default:
			return paintGreyFull;
		}
	}

}

package ch.ethz.soms.nervous.map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import ch.ethz.soms.nervous.android.R;

public class PaintCollection {

	public static final float TEXT_SIZE_YOU = 18.f;
	public static final float TEXT_SIZE_ORBITER = 14.f;

	private static PaintCollection paintCollection;

	private Paint circlePaintYou;
	private Paint[] circlePaint;
	private Paint orbitPaint;
	private Paint textPaintYou;
	private Paint textPaintOrbiter;
	private Paint circlePaintPeer;
	private Paint circlePaintPoi;

	private float scaleFactor;

	public static PaintCollection getInstance(Context context) {
		if (paintCollection == null) {
			paintCollection = new PaintCollection(context);
		}
		return paintCollection;
	}

	private PaintCollection(Context context) {

		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		scaleFactor = metrics.density;

		circlePaintYou = new Paint();
		circlePaintYou.setColor(context.getResources().getColor(R.color.orange_nervous));
		circlePaintYou.setStyle(Paint.Style.FILL);
		circlePaintYou.setAntiAlias(true);

		circlePaintPeer = new Paint();
		circlePaintPeer.setColor(context.getResources().getColor(R.color.gray_nervous));
		circlePaintPeer.setStyle(Paint.Style.FILL);
		circlePaintPeer.setAntiAlias(true);

		circlePaintPoi = new Paint();
		circlePaintPoi.setColor(context.getResources().getColor(R.color.gray_nervous_dark));
		circlePaintPoi.setStyle(Paint.Style.FILL);
		circlePaintPoi.setAntiAlias(true);
		
		circlePaint = new Paint[2];

		circlePaint[0] = new Paint();
		circlePaint[0].setColor(context.getResources().getColor(R.color.blue_nervous));
		circlePaint[0].setStyle(Paint.Style.FILL);
		circlePaint[0].setAntiAlias(true);

		circlePaint[1] = new Paint();
		circlePaint[1].setColor(context.getResources().getColor(R.color.green_nervous));
		circlePaint[1].setStyle(Paint.Style.FILL);
		circlePaint[1].setAntiAlias(true);

		orbitPaint = new Paint();
		orbitPaint.setColor(context.getResources().getColor(R.color.gray_nervous_dark));
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

	}

	public static PaintCollection getPaintCollection() {
		return paintCollection;
	}

	public Paint getCirclePaintYou() {
		return circlePaintYou;
	}

	public Paint getOrbitPaint() {
		return orbitPaint;
	}

	public Paint getTextPaintYou() {
		return textPaintYou;
	}

	public Paint getTextPaintOrbiter() {
		return textPaintOrbiter;
	}

	public Paint getCirclePaint(int index) {
		return circlePaint[index % circlePaint.length];
	}

	public float getScaleFactor() {
		return scaleFactor;
	}

	public Paint getCirclePaintPeer() {
		return circlePaintPeer;
	}

	public Paint getCirclePaintPoi() {
		return circlePaintPoi;
	}

}

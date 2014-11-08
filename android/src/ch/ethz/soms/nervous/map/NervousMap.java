package ch.ethz.soms.nervous.map;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

import ch.ethz.soms.nervous.android.sensors.SensorDescBLEBeacon;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ViewSwitcher;

public class NervousMap {

	private HashMap<Integer, ITileSource> tileSources;

	private Context context;
	private MapView mapView;
	private OrbitView orbitView;
	private ViewSwitcher switcher;

	public abstract class NervousMapEvent {

	}

	public class NervousMapBeaconEvent extends NervousMapEvent {
		private int majorId;
		private int minorId;

		NervousMapBeaconEvent(int majorId, int minorId) {
			this.majorId = majorId;
			this.minorId = minorId;
		}

		public int getMajorId() {
			return majorId;
		}

		public int getMinorId() {
			return minorId;
		}
	}

	public interface NervousMapListener {
		public void onTouchEvent(NervousMapEvent event);
	}

	private LinkedList<NervousMapListener> listenerList;

	public void addListener(NervousMapListener listener) {
		listenerList.add(listener);
	}

	public void removeListener(NervousMapListener listener) {
		listenerList.remove(listener);
	}

	private void onTouchEvent(NervousMapEvent event) {
		for (NervousMapListener listener : listenerList) {
			listener.onTouchEvent(event);
		}
	}

	public NervousMap(Context context) {
		this.context = context;

		tileSources = new HashMap<Integer, ITileSource>();

		switcher = new ViewSwitcher(context);

		mapView = new MapView(context, null);
		mapView.setBuiltInZoomControls(true);
		mapView.setMultiTouchControls(true);

		orbitView = new OrbitView(context);

		switcher.addView(orbitView, 0);
		switcher.addView(mapView, 1);
	}

	public ViewSwitcher getViewSwitcher() {
		return switcher;
	}

	/*
	 * public OrbitView getOrbitView() { return orbitView; }
	 * 
	 * public MapView getMap() { return mapView; }
	 */

	public void selectMapLayer(int mapLayer) {
		if (mapLayer == -1) {
			if (switcher.getCurrentView() != orbitView) {
				switcher.showNext();
			}
		} else {
			if (switcher.getCurrentView() != mapView) {
				switcher.showNext();
			}
			ITileSource its = tileSources.get(mapLayer);
			if (its != null) {
				mapView.setTileSource(its);
			} else {
				mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

			}
		}
	}

	public void addMapLayer(int mapLayer, ITileSource tileSource) {
		tileSources.put(mapLayer, tileSource);
	}

	public void updateOrbitView(List<SensorDescBLEBeacon> bleBeacons) {
		orbitView.setBeacons(bleBeacons);
	}

	private class OrbitView extends View {

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
					canvas.drawText(text[i], x, y + (int) ((1.0/2.0 + 0.2 + i - (float) text.length / 2.0) * (1.2f * Math.abs((float) height / (float) text.length))), textPaint);
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
}

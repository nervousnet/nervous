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
		private Paint textPaint;
		private Paint circlePaint;

		private class TextShapeDrawable extends Drawable {

			private String text;
			private int x;
			private int y;

			public TextShapeDrawable(String text, int x, int y) {
				this.text = text;
				this.x = x;
				this.y = y;
			}

			@Override
			public void draw(Canvas canvas) {
				canvas.drawText(text, x, y, textPaint);
				Rect bounds = new Rect();
				textPaint.getTextBounds(text, 0, text.length(), bounds);

				int radius = Math.max(bounds.right, bounds.bottom);

				canvas.drawCircle(x, y, radius, circlePaint);
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
			circlePaint = new Paint();
			circlePaint.setColor(Color.argb(200, 127, 100, 50));
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
			TextShapeDrawable youDrawable = new TextShapeDrawable("YOU", canvas.getWidth() / 2, canvas.getHeight() / 2);
			youDrawable.draw(canvas);
			invalidate();
		}
	}
}

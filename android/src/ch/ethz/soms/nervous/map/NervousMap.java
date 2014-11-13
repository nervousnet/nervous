package ch.ethz.soms.nervous.map;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.TilesOverlay;

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

	private HashMap<Integer, MapTilesCustomSource> tileSources;

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

		tileSources = new HashMap<Integer, MapTilesCustomSource>();

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
			MapTilesCustomSource mtcs = tileSources.get(mapLayer);
			if (mtcs != null) {
				TilesOverlay tilesOverlay = new TilesOverlay(mtcs.getProviderArray(), mtcs.getContext());
				tilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
				mapView.getOverlays().clear();
				mapView.getOverlays().add(tilesOverlay);
			} else {
				mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
			}
			mapView.getController().setZoom(17);
			GeoPoint mapCenter = new GeoPoint(53.5622f, 9.9853f);
			mapView.getController().setCenter(mapCenter);
		}
	}

	public void addMapLayer(int mapLayer, MapTilesCustomSource tileSource) {
		tileSources.put(mapLayer, tileSource);
	}

	public void updateOrbitView(List<SensorDescBLEBeacon> bleBeacons) {
		orbitView.setBeacons(bleBeacons);
	}

}

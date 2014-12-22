package ch.ethz.soms.nervous.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.TilesOverlay;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.widget.ViewSwitcher;
import ch.ethz.soms.nervous.android.sensors.SensorDescBLEBeacon;
import ch.ethz.soms.nervous.map.MapGraph.MapGraphEdge;
import ch.ethz.soms.nervous.map.MapGraph.MapGraphNode;

@SuppressLint("UseSparseArrays")
public class NervousMap {

	private HashMap<Integer, MapTilesCustomSource> tileSources;
	private HashMap<Integer, MapGraphContainer> mapGraphContainers;
	
	private int selectedMapLayer = -1;

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

		mapGraphContainers = new HashMap<Integer, MapGraphContainer>();

		listenerList = new LinkedList<NervousMapListener>();

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
		selectedMapLayer = mapLayer;
		if (mapLayer == -1) {
			orbitView.startAnimation();
			if (switcher.getCurrentView() != orbitView) {
				switcher.showNext();
			}
		} else {
			orbitView.stopAnimation();
			if (switcher.getCurrentView() != mapView) {
				switcher.showNext();
			}
			MapTilesCustomSource mtcs = tileSources.get(mapLayer);
			if (mtcs != null) {
				mapView.setTileSource(new XYTileSource("mbtiles", ResourceProxy.string.offline_mode, mtcs.getMinZoom(), mtcs.getMaxZoom(), 256, ".png", new String[] { "http://" }));
				mapView.setUseDataConnection(false);
				TilesOverlay tilesOverlay = new TilesOverlay(mtcs.getProviderArray(), mtcs.getContext());
				tilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
				mapView.getOverlays().clear();
				mapView.getOverlays().add(tilesOverlay);
				mapView.setMinZoomLevel(mtcs.getMinZoom());
				mapView.setMaxZoomLevel(mtcs.getMaxZoom());
				mapView.getController().setZoom(mtcs.getDefaultZoom());
				mapView.getController().setCenter(mtcs.getCenter());
				loadOverlays(mapLayer);
			} else {
				mapView.setUseDataConnection(true);
				mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
			}
		}
	}

	public void addMapLayer(int mapLayer, MapTilesCustomSource tileSource) {
		tileSources.put(mapLayer, tileSource);
	}

	public void updateOrbitView(List<SensorDescBLEBeacon> bleBeacons) {
		orbitView.setBeacons(bleBeacons);
	}

	public void addMapGraph(int mapLayer, MapGraph mapGraph) {
		MapGraphContainer mapGraphContainer;
		if (mapGraphContainers.containsKey(mapLayer)) {
			mapGraphContainer = mapGraphContainers.get(mapLayer);
		} else {
			mapGraphContainer = new MapGraphContainer();
			mapGraphContainers.put(mapLayer, mapGraphContainer);
		}
		mapGraphContainer.add(mapGraph);
		selectMapLayer(mapLayer);
	}

	public void clearMapGraph(int mapLayer) {
		mapGraphContainers.remove(mapLayer);
	}

	private void loadOverlays(int mapLayer) {
		MapGraphContainer mgc = mapGraphContainers.get(mapLayer);
		if (mgc != null) {
			for (MapGraph mg : mgc.getMapGraphs()) {
				ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();

				// Add graph edges to map
				for (MapGraphEdge mge : mg.getEdges()) {
					mapView.getOverlays().add(mge);
				}
				
				// Add graph nodes to map
				for (MapGraphNode mgn : mg.getNodes()) {
					overlayItems.add(mgn);
				}

				Overlay overlay = new ItemizedIconOverlay<OverlayItem>(overlayItems, new OnItemGestureListener<OverlayItem>() {

					@Override
					public boolean onItemLongPress(int arg, OverlayItem oi) {
						return false;
					}

					@Override
					public boolean onItemSingleTapUp(int arg, OverlayItem oi) {
						// TODO
						if(oi instanceof MapGraphNode) {
							onTouchEvent(null);
						}
						return true;
					}
				}, new DefaultResourceProxyImpl(context));

				mapView.getOverlays().add(overlay);

			}
		}
	}

	public void removeMapGraph(int mapLayer, MapGraph mapGraph) {
		mapGraphContainers.get(mapLayer).remove(mapGraph);
	}

	public int getSelectedMapLayer() {
		return selectedMapLayer;
	}

	public void focusYouAndZoom() {
		if(selectedMapLayer >= 0) {
			
		}
	}

}

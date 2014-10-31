package ch.ethz.soms.nervous.android;

import java.util.HashMap;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

import android.content.Context;

public class NervousMap {

	private HashMap<Integer, ITileSource> tileSources;

	private Context context;
	private MapView mapView;

	public NervousMap(Context context) {
		this.context = context;
		mapView = new MapView(context, null);
		mapView.setBuiltInZoomControls(true);
		mapView.setMultiTouchControls(true);
	}

	public MapView getMap() {
		return mapView;

	}

	public void selectMapLayer(int mapLayer) {
		if (mapLayer == -1) {
			mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
		} else {
			mapView.setTileSource(tileSources.get(mapLayer));
		}
	}

	public void addMapLayer(int mapLayer, ITileSource tileSource) {
		tileSources.put(mapLayer, tileSource);
	}
}

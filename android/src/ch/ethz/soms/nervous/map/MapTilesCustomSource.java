package ch.ethz.soms.nervous.map;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.util.GeoPoint;

import android.content.Context;

public abstract class MapTilesCustomSource {

	protected Context context;
	protected MapTileProviderArray providerArray;
	protected int minZoom;
	protected int maxZoom;
	protected GeoPoint center;
	protected int defaultZoom;


	public MapTilesCustomSource(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	public MapTileProviderArray getProviderArray() {
		return providerArray;
	}

	public GeoPoint getCenter() {
		return center;
	}

	public int getMinZoom() {
		return minZoom;
	}

	public int getMaxZoom() {
		return maxZoom;
	}

	public int getDefaultZoom() {
		return defaultZoom;
	}

	
}

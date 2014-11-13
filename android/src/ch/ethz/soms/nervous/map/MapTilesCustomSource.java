package ch.ethz.soms.nervous.map;

import org.osmdroid.tileprovider.MapTileProviderArray;

import android.content.Context;

public abstract class MapTilesCustomSource {

	protected Context context;
	protected MapTileProviderArray providerArray;


	public MapTilesCustomSource(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	public MapTileProviderArray getProviderArray() {
		return providerArray;
	}

}

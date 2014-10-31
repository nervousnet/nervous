package ch.ethz.soms.nervous.android;

import org.osmdroid.views.MapView;

import android.content.Context;

public class NervousMap {

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
}

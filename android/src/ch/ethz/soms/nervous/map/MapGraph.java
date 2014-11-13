package ch.ethz.soms.nervous.map;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;

import android.content.Context;

public class MapGraph {
	
	private ArrayList<MapGraphNode> nodes;
	private ArrayList<MapGraphEdge> edges;
	

	MapGraph(JSONArray jsonArray) {
		HashMap<String, GeoPoint> mapGraphPoints = new HashMap<String, GeoPoint>();
		// TODO
	}

	public class MapGraphNode extends OverlayItem {
		GeoPoint pos;
		
		MapGraphNode(String label, String description, GeoPoint pos) {
			super(label, description, pos);
			this.pos = pos;
		}
	}

	public class MapGraphEdge extends PathOverlay {

		GeoPoint start;
		GeoPoint stop;

		public MapGraphEdge(Context context, int color) {
			super(color, context);
			this.addPoint(start);
			this.addPoint(stop);
		}
	}

	public ArrayList<MapGraphNode> getNodes() {
		return nodes;
	}

	public ArrayList<MapGraphEdge> getEdges() {
		return edges;
	}

}

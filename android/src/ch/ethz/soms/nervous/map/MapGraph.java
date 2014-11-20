package ch.ethz.soms.nervous.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;

import android.content.Context;

public class MapGraph {

	private ArrayList<MapGraphNode> nodes;
	private ArrayList<MapGraphEdge> edges;

	MapGraph() {
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

	public void addFromJson(JSONObject jo) {
		Iterator<String> it = jo.keys();
		try {
			while (it.hasNext()) {
				String key = it.next();
				if (key.equals("cn")) {
					addNodeFromJson(jo.getJSONObject(key));
				} else if (key.equals("ce")) {

				} else if (key.equals("an")) {

				} else if (key.equals("ae")) {

				}
			}
		} catch (JSONException e) {
		}
	}

	private void addNodeFromJson(JSONObject jo) {
		Iterator<String> it = jo.keys();
		try {
			while (it.hasNext()) {
				String id = it.next();
				JSONObject attributes = jo.getJSONObject(id);
				// TODO
			}
		} catch (JSONException e) {
		}
	}

	public ArrayList<MapGraphNode> getNodes() {
		return nodes;
	}

	public ArrayList<MapGraphEdge> getEdges() {
		return edges;
	}

}

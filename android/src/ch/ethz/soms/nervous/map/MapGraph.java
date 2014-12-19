package ch.ethz.soms.nervous.map;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;

import android.content.Context;

public class MapGraph {

	private ArrayList<MapGraphNode> nodes;
	private ArrayList<MapGraphEdge> edges;

	public MapGraph() {
		this.nodes = new ArrayList<MapGraph.MapGraphNode>();
		this.edges = new ArrayList<MapGraph.MapGraphEdge>();
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
		GeoPoint pos = new GeoPoint(0, 0);
		String label = "";
		String description = "";
		Iterator<String> it = jo.keys();
		try {
			while (it.hasNext()) {
				String id = it.next();
				JSONObject attributes = jo.getJSONObject(id);
				Iterator<String> jt = attributes.keys();
				while (jt.hasNext()) {
					String attrName = jt.next();
					if (attrName.equals("lon")) {
						double lon = (Double) attributes.get(attrName);
						pos.setLongitudeE6((int) (lon * 10e6));
					} else if (attrName.equals("lat")) {
						double lat = (Double) attributes.get(attrName);
						pos.setLatitudeE6((int) (lat * 10e6));
					} else if (attrName.equals("label")) {
						label = (String) attributes.get(attrName);
					}
				}
			}
		} catch (JSONException e) {
		}
		MapGraphNode mgn = new MapGraphNode(label, description, pos);
		nodes.add(mgn);
	}

	public ArrayList<MapGraphNode> getNodes() {
		return nodes;
	}

	public ArrayList<MapGraphEdge> getEdges() {
		return edges;
	}

}

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
import org.osmdroid.views.overlay.OverlayItem.HotspotPlace;

import ch.ethz.soms.nervous.vm.NervousVM;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;

public class MapGraph {

	public static final int TYPE_BEACON = 0;
	public static final int TYPE_POI = 1;

	private Context context;

	private MapGraphNode youNode;
	private ArrayList<MapGraphNode> nodes;
	private ArrayList<MapGraphEdge> edges;
	private HashMap<String, GeoPoint> positionMap;
	private String youUuid;
	private int identifier;
	private PaintCollection paintCollection;
	private int type;
	private int poiLayerSelect;

	public MapGraph(Context context, int identifier, String youUuid) {
		this.paintCollection = PaintCollection.getInstance(context);
		this.context = context;
		this.nodes = new ArrayList<MapGraph.MapGraphNode>();
		this.edges = new ArrayList<MapGraph.MapGraphEdge>();
		this.positionMap = new HashMap<String, GeoPoint>();
		this.youUuid = youUuid;
		this.identifier = identifier;
		this.type = TYPE_BEACON;
		this.poiLayerSelect = -1;
	}

	public MapGraph(Context context, int identifier, int poiLayerSelect) {
		this.paintCollection = PaintCollection.getInstance(context);
		this.context = context;
		this.nodes = new ArrayList<MapGraph.MapGraphNode>();
		this.edges = new ArrayList<MapGraph.MapGraphEdge>();
		this.positionMap = new HashMap<String, GeoPoint>();
		this.youUuid = "";
		this.identifier = identifier;
		this.type = TYPE_POI;
		this.poiLayerSelect = poiLayerSelect;
	}

	public int getIdentifier() {
		return identifier;
	}

	public class MapGraphNode extends OverlayItem {
		private GeoPoint pos;

		MapGraphNode(String label, String description, GeoPoint pos) {
			super(label, description, pos);
			this.pos = pos;
		}

		public GeoPoint getPos() {
			return pos;
		}

	}

	public class MapGraphEdge extends PathOverlay {

		private GeoPoint start;
		private GeoPoint stop;

		public MapGraphEdge(Context context, GeoPoint start, GeoPoint stop) {
			super(0, context);
			Paint paint = paintCollection.getOrbitPaint();
			this.setPaint(paint);
			this.start = start;
			this.stop = stop;
			this.addPoint(start);
			this.addPoint(stop);
		}

		public GeoPoint getStart() {
			return start;
		}

		public GeoPoint getStop() {
			return stop;
		}

	}

	public void addFromJson(JSONArray ja) {
		switch (type) {
		case TYPE_BEACON:
			break;
		case TYPE_POI:
			for (int i = 0; i < ja.length(); ++i) {
				try {
					JSONObject jo = ja.getJSONObject(i);
					Iterator<String> it = jo.keys();
					String key = it.next();
					if (Integer.parseInt(key) == poiLayerSelect) {
						JSONArray pois = jo.getJSONArray(key);
						for (int j = 0; j < pois.length(); ++j) {
							JSONObject poi = pois.getJSONObject(j);
							addPoiFromJson(poi);
						}
					}
				} catch (JSONException e) {
				}
			}
			break;
		}
	}

	public void addFromJson(JSONObject jo) {
		Iterator<String> it = jo.keys();
		switch (type) {
		case TYPE_BEACON:
			try {
				while (it.hasNext()) {
					String key = it.next();
					if (key.equals("cn")) {
						addNodeFromJson(jo.getJSONObject(key));
					} else if (key.equals("ce")) {
						addEdgeFromJson(jo.getJSONObject(key));
					} else if (key.equals("an")) {
						addNodeFromJson(jo.getJSONObject(key));
					} else if (key.equals("ae")) {
						addEdgeFromJson(jo.getJSONObject(key));
					}
				}
			} catch (JSONException e) {
			}
			break;
		case TYPE_POI:
			break;
		}
	}

	private void addEdgeFromJson(JSONObject jo) {
		GeoPoint pos0 = new GeoPoint(0, 0);
		GeoPoint pos1 = new GeoPoint(0, 0);
		Iterator<String> it = jo.keys();
		try {
			while (it.hasNext()) {
				String id = it.next();
				JSONObject attributes = jo.getJSONObject(id);
				Iterator<String> jt = attributes.keys();
				while (jt.hasNext()) {
					String attrName = jt.next();
					if (attrName.equals("target")) {
						pos0 = positionMap.get(attributes.get(attrName));
					} else if (attrName.equals("source")) {
						pos1 = positionMap.get(attributes.get(attrName));
					}
				}
			}
		} catch (JSONException e) {
		}
		MapGraphEdge mge = new MapGraphEdge(context, pos0, pos1);
		edges.add(mge);
	}

	private void addPoiFromJson(JSONObject jo) {
		GeoPoint pos = new GeoPoint(0, 0);
		String label = "";
		String description = "POI";
		Iterator<String> it = jo.keys();
		try {
			while (it.hasNext()) {
				String attrName = it.next();
				if (attrName.equals("lon")) {
					double lon = (Double) jo.get(attrName);
					pos.setLongitudeE6((int) (lon * 1e6));
				} else if (attrName.equals("lat")) {
					double lat = (Double) jo.get(attrName);
					pos.setLatitudeE6((int) (lat * 1e6));
				} else if (attrName.equals("title")) {
					label = (String) jo.get(attrName);
				}
			}
		} catch (JSONException e) {
		}
		
		MapGraphNode mgn = new MapGraphNode(label, description, pos);

		TextShapeDrawable marker = new TextShapeDrawable(new String[] { description }, paintCollection.getCirclePaintPoi(), paintCollection.getTextPaintOrbiter());

		mgn.setMarker(marker);

		nodes.add(mgn);
	}

	private void addNodeFromJson(JSONObject jo) {
		GeoPoint pos = new GeoPoint(0, 0);
		String label = "";
		String description = "";
		String id = "";
		Iterator<String> it = jo.keys();
		try {
			while (it.hasNext()) {
				id = it.next();
				description = String.valueOf(id);
				JSONObject attributes = jo.getJSONObject(id);
				Iterator<String> jt = attributes.keys();
				while (jt.hasNext()) {
					String attrName = jt.next();
					if (attrName.equals("lon")) {
						double lon = (Double) attributes.get(attrName);
						pos.setLongitudeE6((int) (lon * 1e6));
					} else if (attrName.equals("lat")) {
						double lat = (Double) attributes.get(attrName);
						pos.setLatitudeE6((int) (lat * 1e6));
					} else if (attrName.equals("label")) {
						label = (String) attributes.get(attrName);
					}
				}
			}
		} catch (JSONException e) {
		}

		positionMap.put(id, pos);

		boolean yourNodeFlag = false;

		TextShapeDrawable mgm = null;

		String labelSplit[] = label.split(" ");

		if (labelSplit[0].equalsIgnoreCase("Phone")) {
			if (description.equalsIgnoreCase(youUuid)) {
				description = "YOU";
				yourNodeFlag = true;
				mgm = new TextShapeDrawable(new String[] { description }, paintCollection.getCirclePaintYou(), paintCollection.getTextPaintOrbiter());
			} else {
				description = "SP";
				mgm = new TextShapeDrawable(new String[] { description }, paintCollection.getCirclePaintPeer(), paintCollection.getTextPaintOrbiter());
			}
		} else {
			int minorId = Integer.parseInt(description);
			// TODO: This is hacky code, to be changed after 31c3
			int paintSelect = minorId > 100 && minorId < 122 ? 0 : 1;
			mgm = new TextShapeDrawable(new String[] { description }, paintCollection.getCirclePaint(paintSelect), paintCollection.getTextPaintOrbiter());
		}

		MapGraphNode mgn = new MapGraphNode(label, description, pos);
		mgn.setMarker(mgm);
		mgn.setMarkerHotspot(HotspotPlace.CENTER);

		if (yourNodeFlag) {
			youNode = mgn;
		}

		nodes.add(mgn);
	}

	public ArrayList<MapGraphNode> getNodes() {
		return nodes;
	}

	public ArrayList<MapGraphEdge> getEdges() {
		return edges;
	}

	public MapGraphNode getYouNode() {
		return youNode;
	}

}

package ch.ethz.soms.nervous.map;

import java.util.LinkedList;

public class MapGraphContainer {
	private LinkedList<MapGraph> mapGraphs;

	public MapGraphContainer() {
		this.mapGraphs = new LinkedList<MapGraph>();
	}

	public void remove(MapGraph mapGraph) {
		mapGraphs.remove(mapGraph);
	}

	public void remove(int index) {
		mapGraphs.remove(index);
	}

	public LinkedList<MapGraph> getMapGraphs() {
		return mapGraphs;
	}

	public void add(MapGraph mapGraph) {
		mapGraphs.add(mapGraph);
	}
}

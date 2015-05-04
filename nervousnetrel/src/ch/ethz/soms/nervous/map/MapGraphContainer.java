package ch.ethz.soms.nervous.map;

import java.util.Iterator;
import java.util.LinkedList;

public class MapGraphContainer {
	private LinkedList<MapGraph> mapGraphs;

	public MapGraphContainer() {
		this.mapGraphs = new LinkedList<MapGraph>();
	}

	public void remove(MapGraph mapGraph) {
		mapGraphs.remove(mapGraph);
	}

	public void removeByIndex(int index) {
		mapGraphs.remove(index);
	}
	
	public void removeByIdentifier(int identifier) {
		Iterator<MapGraph> it = mapGraphs.iterator();
		while(it.hasNext()) {
			MapGraph mapGraph = it.next();
			if(mapGraph.getIdentifier() == identifier) {
				it.remove();
			}
		}
	}

	public LinkedList<MapGraph> getMapGraphs() {
		return mapGraphs;
	}

	public void add(MapGraph mapGraph) {
		mapGraphs.add(mapGraph);
	}
}

package ch.eth.soms.mosgap.sensorservice;

import java.util.LinkedList;

public class SensorFrame {

	public SensorHeader sensorHeader;
	public LinkedList<SensorData> sensorData;

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("");
		
		
		return sb.toString();
	}
	
	public boolean isComplete()
	{
		// TODO
		return false;
	}

}

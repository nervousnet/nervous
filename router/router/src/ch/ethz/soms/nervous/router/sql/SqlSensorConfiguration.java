package ch.ethz.soms.nervous.router.sql;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "sensor")
public class SqlSensorConfiguration {

	String sensorName;
	String sensorID;
	
	
	public String getSensorName() {
		return sensorName;
	}
	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}
	public String getSensorID() {
		return sensorID;
	}
	public void setSensorID(String sensorID) {
		this.sensorID = sensorID;
	}
	
}

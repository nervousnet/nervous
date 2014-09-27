package ch.ethz.soms.nervous.router.sql;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.ethz.soms.nervous.xml.HexIdAdapter;

public class SqlSensorConfiguration {

	private String sensorName;
	
	private Long sensorID;

	public SqlSensorConfiguration() {

	}

	@XmlElementWrapper(name = "attributes")
	@XmlElement(name = "attribute")
	public List<SqlSensorAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<SqlSensorAttribute> attributes) {
		this.attributes = attributes;
	}

	private List<SqlSensorAttribute> attributes = new ArrayList<SqlSensorAttribute>();


	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	@XmlJavaTypeAdapter(type=long.class, value=HexIdAdapter.class)
	public Long getSensorID() {
		return sensorID;
	}

	public void setSensorID(Long sensorID) {
		this.sensorID = sensorID;
	}

}

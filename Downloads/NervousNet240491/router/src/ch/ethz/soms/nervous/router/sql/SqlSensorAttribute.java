package ch.ethz.soms.nervous.router.sql;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.ethz.soms.nervous.xml.TypeAdapter;

class SqlSensorAttribute {
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlJavaTypeAdapter(type=int.class, value=TypeAdapter.class)
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public SqlSensorAttribute() {
	}

	private String name;
	
	private int type;
}


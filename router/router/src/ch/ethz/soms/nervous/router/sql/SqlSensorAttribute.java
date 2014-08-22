package ch.ethz.soms.nervous.router.sql;

class SqlSensorAttribute {
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public SqlSensorAttribute() {
	}

	private String name;
	private String type;
}


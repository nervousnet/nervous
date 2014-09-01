package ch.ethz.soms.nervous.router.sql;

class SqlSensorAttribute {
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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


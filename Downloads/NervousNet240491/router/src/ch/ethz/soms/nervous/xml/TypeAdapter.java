package ch.ethz.soms.nervous.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import ch.ethz.soms.nervous.router.sql.SqlSetup;

public class TypeAdapter extends XmlAdapter<String, Integer> {

	@Override
	public String marshal(Integer v) throws Exception {
		switch (v) {
		case SqlSetup.TYPE_BOOL:
			return "BOOL";
		case SqlSetup.TYPE_INT32:
			return "INT32";
		case SqlSetup.TYPE_INT64:
			return "INT64";
		case SqlSetup.TYPE_FLOAT:
			return "FLOAT";
		case SqlSetup.TYPE_DOUBLE:
			return "DOUBLE";
		case SqlSetup.TYPE_STRING:
			return "STRING";
		default:
			return "ERROR!";
		}
	}

	@Override
	public Integer unmarshal(String v) throws Exception {
		String type = v.toUpperCase();
		if (type.equals("BOOL")) {
			return SqlSetup.TYPE_BOOL;
		} else if (type.equals("INT32")) {
			return SqlSetup.TYPE_INT32;
		} else if (type.equals("INT64")) {
			return SqlSetup.TYPE_INT64;
		} else if (type.equals("FLOAT")) {
			return SqlSetup.TYPE_FLOAT;
		} else if (type.equals("DOUBLE")) {
			return SqlSetup.TYPE_DOUBLE;
		} else if (type.equals("STRING")) {
			return SqlSetup.TYPE_STRING;
		} else {
			return -1;
		}
	}
}

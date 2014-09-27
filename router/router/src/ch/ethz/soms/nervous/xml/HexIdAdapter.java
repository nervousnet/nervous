package ch.ethz.soms.nervous.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class HexIdAdapter extends XmlAdapter<String, Long> {

	@Override
	public String marshal(Long v) throws Exception {
		return Long.toHexString(v);
	}

	@Override
	public Long unmarshal(String v) throws Exception {
		return Long.parseUnsignedLong(v, 16);
	}

}

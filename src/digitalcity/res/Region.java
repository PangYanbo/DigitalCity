package digitalcity.res;

import java.util.List;

import jp.ac.ut.csis.pflow.routing4.res.Node;

@SuppressWarnings("serial")
public class Region extends Node{
	private List<Double> attrs;

	public Region(String code, double lon, double lat, List<Double> attrs) {
		super(code, lon, lat);
		this.attrs = attrs;
	}

	public List<Double> getAttrs() {
		return attrs;
	}
}

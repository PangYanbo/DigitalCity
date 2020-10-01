package digitalcity.res;

import jp.ac.ut.csis.pflow.geom2.LonLat;
import jp.ac.ut.csis.pflow.routing4.res.Node;

@SuppressWarnings("serial")
public class Facility extends Node{
	private double area;
	private EFacility type;
	
	public Facility(int id, double lon, double lat, EFacility type, double area) {
		super(String.valueOf(id), lon, lat);
		this.type = type;
		this.area = area;
	}

	public double getArea() {
		return area;
	}

	public EFacility getType() {
		return type;
	}
	
	public LonLat getLonLat() {
		return new LonLat(this.getLon(), this.getLat());
	}
}

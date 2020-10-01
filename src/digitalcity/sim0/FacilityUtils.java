package digitalcity.sim0;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import digitalcity.res.EFacility;
import digitalcity.res.Facility;
import jp.ac.ut.csis.pflow.geom2.Mesh;
import jp.ac.ut.csis.pflow.geom2.MeshUtils;
import jp.ac.ut.csis.pflow.routing4.res.Network;

public class FacilityUtils {
	
	public static Map<EFacility, List<Facility>> getListMapFacilities(List<Facility> listFacilities){
		// map: facilities type-facilities list, categorization
		Map<EFacility, List<Facility>> map = new HashMap<EFacility, List<Facility>>();
		for (Facility facility : listFacilities) {
			EFacility type = facility.getType();
			List<Facility> list = map.containsKey(type) ? map.get(type) : new ArrayList<>();
			list.add(facility);
			map.put(type, list);
		}
		return map;
	}
	
	public static Map<EFacility, Network> getNetworkMapFacilities(List<Facility> listFacilities){
		Map<EFacility, Network> map = new HashMap<EFacility, Network>();
		for (Facility facility : listFacilities) {
			EFacility type = facility.getType();
			Network network = map.containsKey(type) ? map.get(type) : new Network();
			network.addNode(facility);
			map.put(type, network);
		}
		return map;
	}
	
	public static Map<String, List<Facility>> getMeshMapFacilities(List<Facility> listFacilities){
		Map<String, List<Facility>> map = new HashMap<String, List<Facility>>();
		for (Facility facility : listFacilities) {
			Mesh mesh = MeshUtils.createMesh(3, facility.getLon(), facility.getLat());
			String meshCode = mesh.getCode();
			List<Facility> list = map.containsKey(meshCode) ? map.get(meshCode) : new ArrayList<>();
			list.add(facility);
			map.put(meshCode, list);
		}
		return map;
	}
}

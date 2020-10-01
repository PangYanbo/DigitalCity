package digitalcity.sim0;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import digitalcity.res.Region;
import jp.ac.ut.csis.pflow.geom2.DistanceUtils;

public class DestChoice {
	
	private List<Region> regions;
	private Map<String, List<Double>> mapProbability;
	private Map<String, List<String>> mapRegionName;	
	
	public DestChoice(List<Region> regions) {
		super();
		this.regions = regions;
	}
	
	private static double utility(List<Double> values, List<Double> params) {
		double value = 0;
		// System.out.println(values.size());
		// values are population/maxPopulation, numOfOffice/Max numOfOffice, numOfStaff/max numOfStaff) and distance
		for (int i = 0; i < values.size(); i++) {
			//value += Math.log(variables.get(i))* params.get(i);
			value += values.get(i) * params.get(i);
		}	
		return Math.exp(value);
	}
		
	private Map<String, Double> update(Region oriArea, List<Double> params) {
		Map<String, Double> probs = new HashMap<>();
		double deno = 0;
		for (Region dstArea : regions) {
			// desArea is in regions, which is in the DestChoice class
			double distance = DistanceUtils.distance(oriArea, dstArea)/1000;
			List<Double> values = new ArrayList<>(dstArea.getAttrs());
			//System.out.println(values);
			// the utility is decided by distance and three key factors
			values.add(normalize(distance));
			deno += utility(values, params);
		}
		for (Region dstArea : regions) {
			// unit: km
			double distance = DistanceUtils.distance(oriArea, dstArea)/1000;
			List<Double> values = new ArrayList<>(dstArea.getAttrs());
			values.add(normalize(distance));
			double value = utility(values, params);
			double prob = value / deno;
			// probs: map of areaID-probability decided by utility function
			probs.put(dstArea.getNodeID(), prob);
		}
		//System.out.println(probs);
		return probs;
	}
    // parameters here are from sublist, the number is four
	public void update(List<Double> params) {
		Map<String, Map<String, Double>> probs = new HashMap<>();
		
		for (Region region : regions) {
			// map stores the probability for one area
			Map<String, Double> map = update(region, params);
			probs.put(region.getNodeID(), map);
		}
		
		// Convert one map of code-map to two maps of code-list
		mapProbability = new HashMap<>();
		mapRegionName = new HashMap<>();
		// map.entry: get key-value pairs
		for (Map.Entry<String, Map<String, Double>> e :  probs.entrySet()) {
			List<Double> values = new ArrayList<>();
			List<String> names = new ArrayList<>();
			for (Map.Entry<String, Double> e2 : e.getValue().entrySet()) {
				values.add(e2.getValue());
				names.add(e2.getKey());
			}
			mapProbability.put(e.getKey(), values);
			mapRegionName.put(e.getKey(), names);
		}
	}
	
	public List<Double> getProbability(String code){
		return mapProbability.get(code);
	}
	
	public String getDestinationName(String ori, int index) {
		if (mapRegionName.containsKey(ori)) {
			return mapRegionName.get(ori).get(index);
		}
		return null;
	}

	private double normalize(double value) {
		return 1.0 / Math.exp(value);
	}
}

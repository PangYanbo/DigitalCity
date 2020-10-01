package digitalcity.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import digitalcity.res.Region;

public class DataLoader {
	public static Map<Integer,Map<String, Double>> loadObservation(File file){
		Random random = new Random(1000);
		Map<Integer,Map<String, Double>> map = new HashMap<>();
		try(BufferedReader br = new BufferedReader(new FileReader(file));) {
          String record;
          while ((record = br.readLine()) != null) {
          	String[] items = record.split(",");
        	int hour = Integer.valueOf(items[0]);
        	String code = String.valueOf(items[1]);
        	Double value = Double.valueOf(items[2]);
        	
        	Map<String, Double> sub = map.containsKey(hour) ? map.get(hour) : new HashMap<>();
        	sub.put(code, value + random.nextInt(100));
        	map.put(hour, sub);
          }
        } catch (Exception e) {
        	e.printStackTrace();
        }
		return map;
	}
	
	public static List<Region> loadRegions(File file) {
		List<Region> regions = new ArrayList<Region>();
		try(BufferedReader br = new BufferedReader(new FileReader(file));) {
          String record;
          while ((record = br.readLine()) != null) {
          	String[] items = record.split(",");
        	String id = String.valueOf(items[0]);
        	double lon = Double.valueOf(items[1]);
        	double lat = Double.valueOf(items[2]); 	
        	
        	List<Double> values = new ArrayList<>();
        	for (int i = 3; i < items.length; i++) {
        		values.add(Double.valueOf(items[i]));		
        	}
        	Region area = new Region(id, lon, lat, values);
        	regions.add(area);
          }
        } catch (Exception e) {
        	e.printStackTrace();
        }
		return regions;
	}
}

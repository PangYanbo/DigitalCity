package digitalcity.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import digitalcity.res.Facility;
import digitalcity.res.EFacility;
import jp.ac.ut.csis.pflow.geom2.Mesh;
import jp.ac.ut.csis.pflow.geom2.MeshUtils;

public class ZDCTelepointLoader {

	private static EFacility getType(int atype2, String business) {
			
		if (business.contains("3340000") 
				|| business.contains("3701000")) {
			return EFacility.KINDERGARTEN;
		}else if (business.contains("3702000")) {
			return EFacility.PRIMARY_SCHOOL;
		}else if (business.contains("3703000")) {
			return EFacility.JUNIOR_HIGH_SCHOOL;
		}else if (business.contains("3704000") 
				|| business.contains("3704000")) {
			return EFacility.HIGH_SCHOOL;
		}
		
		switch(atype2) {
			case 1364: return EFacility.RESIDENCE;
			case 1365: return EFacility.OFFICE;
			case 13634: return EFacility.RESIDENCE;
			case 13632: return EFacility.RESIDENCE;
			case 13635: return EFacility.OFFICE;
			case 13633: return EFacility.OFFICE;
			case 13631: return EFacility.OFFICE;
			default: return EFacility.OTHER;
		}
	}

	
	public static List<Facility> load(File file) {	
		List<Facility> list = new ArrayList<>();
		int pindex = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(file));){
            String line;            
            while ((line = br.readLine()) != null) {
            	String[] items = line.split(",");
            	EFacility type = getType(Integer.valueOf(items[16]), String.valueOf(items[19]));
            	//String code = String.valueOf(items[1]);
            	double lon = Double.valueOf(items[7]);
            	double lat = Double.valueOf(items[8]);
            	double area = Double.valueOf(items[17]);
            	
             	if (type != EFacility.OTHER) {
            		Facility facility = new Facility(++pindex, lon, lat, type, area);
           		list.add(facility);
            	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return list;
	}

	public static List<Facility> filterFacility(List<Facility> allFacilities) {
		List<Facility> areaFacilities = new ArrayList<>();
		for (Facility f : allFacilities) {
			//5436,5437
			Mesh mesh = MeshUtils.createMesh(3, f.getLon(), f.getLat());
			String meshCode = mesh.getCode();
			//System.out.println(meshCode);
			if (meshCode.substring(0, 4).equals("5436") || meshCode.substring(0, 3).equals("5437")) {
				areaFacilities.add(f);
			}
		}
		return areaFacilities;
	}
}

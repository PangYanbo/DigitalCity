package digitalcity.utils;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.ac.ut.csis.pflow.geom2.Mesh;
import jp.ac.ut.csis.pflow.geom2.MeshUtils;

public class MeshLocationGenerator {

	private static SimpleDateFormat dtf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	
	private static Date convertString(String text) {
		try {
			return dtf.parse(text);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public class InnerPerson{
		private int id;
		private String[] locations;
		
		public InnerPerson(int id) {
			super();
			this.id = id;
			locations = new String[24];
			for (int i = 0; i < locations.length; i++) {
				locations[i] = "";
			}
		}
		
		public int getId() {
			return id;
		}
		
		public String getLocations(int index) {
			return locations[index];
		}
		
		public void setLocation(int index, String location) {
			locations[index] = location;
		}
		
		public int getSize() {
			return locations.length;
		}
	}
	
	
	private List<InnerPerson> load(File input){
		List<InnerPerson> data = new ArrayList<InnerPerson>();
		Calendar calendar = Calendar.getInstance();
		try (BufferedReader br = new BufferedReader(new FileReader(input));){
            String line;
            int preid = -1;
            InnerPerson person = null;
            while ((line = br.readLine()) != null) {
            	String[] items = line.split(",");
            	int id = Integer.valueOf(items[0]);
            	Date date = convertString(items[1]);
            	double lon = Double.valueOf(items[2]);
            	double lat = Double.valueOf(items[3]);
            	
            	calendar.setTime(date);
            	int hour = calendar.get(Calendar.HOUR_OF_DAY);
            	
            	Mesh mesh = MeshUtils.createMesh(4, lon, lat);
            	String meshCode = mesh.getCode();
            	
            	if (preid != id) {
            		person = new InnerPerson(id);
            		data.add(person);
            	}
            	
            	if (person.getLocations(hour).equals("")) {
            		person.setLocation(hour, meshCode);
            	}
            	preid = id;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return data;
	}
	
	private List<InnerPerson> process(File input) {
		List<InnerPerson> data = load(input);
		for (InnerPerson person : data) {
			String location = "";
			for (int i = 0; i < person.getSize(); i++) {
				// System.out.println(String.format("%d,%d,%s", person.getId(), i, person.getLocations(i)));
				String storedValue = person.getLocations(i);
				if (storedValue.equals("")) {
					person.setLocation(i, location);
				}else {
					location = storedValue;
				}	
			}
		}
		return data;
	}
	
	private void write(File file, List<InnerPerson> data) {
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(file));){
			for (InnerPerson e : data) {
				int id = e.getId();
				for (int i = 0; i < e.getSize(); i++) {
					bw.write(String.format("%d,%d,%s", id, i, e.getLocations(i)));
					bw.newLine();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {

		String src_path = "/home/ubuntu/Projects/DigitalCityProject/Output/20201001.2/";

		File src_dir = new File(src_path+"results/");
		File[] files = src_dir.listFiles();
		File mesh_dir = new File(src_path + "mesh/");

		if (mesh_dir.mkdir()){
			System.out.println("Create mesh dir");
		}

		for(File input : files) {

			String[] args1 = new String[] {"python3",
					"/home/ubuntu/Projects/DigitalCityProject/PflowforDigitalCity/src/digitalcity/utils/SortById.py", src_path + "results/" + input.getName()};
			Process proc = Runtime.getRuntime().exec(args1);
			proc.waitFor();

			System.out.println(input.getPath());
			File output = new File(mesh_dir +"/"+ input.getName()+"_mesh.csv");

			File sorted_in = new File(src_dir+"/"+input.getName()+"_sorted.csv");

			MeshLocationGenerator generator = new MeshLocationGenerator();
			List<InnerPerson> data = generator.process(sorted_in);
			generator.write(output, data);
			System.out.println("end");
		}
	}
}

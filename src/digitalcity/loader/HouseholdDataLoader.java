package digitalcity.loader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import digitalcity.res.Person;
import digitalcity.res.EGender;
import digitalcity.res.EPerson;
import jp.ac.ut.csis.pflow.geom2.LonLat;
public class HouseholdDataLoader{

	private static int getAge(int code) {
		switch(code) {
		case 200:return 4;
		case 201:return 9;
		case 202:return 14;
		case 203:return 18;
		case 204:return 24;
		case 205:return 29;
		case 206:return 34;
		case 207:return 39;
		case 208:return 44;
		case 209:return 49;
		case 210:return 54;
		case 211:return 59;
		case 212:return 64;
		case 213:return 69;
		case 214:return 74;
		case 215:return 79;
		case 216:return 84;
		case 585:return 89;
		}
		return 100;
	}
	
	public static EGender getGender(int gender) {
		switch (gender) {
		case 1: return EGender.MAN;
		default:return EGender.WOMAN;
		}
	}
	
	public static List<Person> load2(File file){
		List<Person> list = new ArrayList<Person>();
		int pindex = 1;
		try (BufferedReader br = new BufferedReader(new FileReader(file));){
            String line;
            
            br.readLine();
            while ((line = br.readLine()) != null) {
            	String[] items = line.split(",");
            	String gyousei = String.valueOf(items[1]);
            	double lon = Double.valueOf(items[73]);
            	double lat = Double.valueOf(items[74]);
            	LonLat home = new LonLat(lon, lat);
              	int floor = Integer.valueOf(items[5]);
            	int residence = !items[6].equals("") ? Integer.valueOf(items[6]) : 0;
            	int family = !items[7].equals("") ? Integer.valueOf(items[7]) : 0;
          
            	// person
            	if (items[12].equals("") != true && Integer.valueOf(items[12]) >= 0) {
            		EGender gender = getGender(Integer.valueOf(items[11]));
            		int age = getAge(Integer.valueOf(items[12]));
            		Person person = new Person(pindex++, age, home, EPerson.UNDEFINED, gender, gyousei,
            				floor, residence, family);
            		list.add(person);
            	}
    			
    			// wife
            	if (items[14].equals("") != true && Integer.valueOf(items[14]) >= 0) {
            		EGender gender = getGender(Integer.valueOf(items[13]));
            		int age = getAge(Integer.valueOf(items[14]));
            		Person person =  new Person(pindex++, age, home, EPerson.UNDEFINED, gender, gyousei,
            				floor, residence, family);
            		list.add(person);
            	}           	
            	
            	// father
            	if (items[16].equals("") != true && Integer.valueOf(items[16]) >= 0) {
            		int age = getAge(Integer.valueOf(items[16]));
            		Person person =  new Person(pindex++, age, home, EPerson.UNDEFINED, EGender.MAN, gyousei,
            				floor, residence, family);
            		list.add(person);
            	}
    			
    			// mother
            	if (items[18].equals("") != true && Integer.valueOf(items[18]) >= 0) {
            		int age = getAge(Integer.valueOf(items[18]));
            		Person person =  new Person(pindex++, age, home, EPerson.UNDEFINED, EGender.WOMAN, gyousei,
            				floor, residence, family);
            		list.add(person);
            	}
            	
    			// child, grandchild, other
            	for (int i = 20; i <= 72; i+=2) {
            		if (items[i].equals("") != true && Integer.valueOf(items[i]) >= 0) {
            			EGender gender = getGender(Integer.valueOf(items[i-1]));
            			int age = getAge(Integer.valueOf(items[i]));
                		Person person =  new Person(pindex++, age, home, EPerson.UNDEFINED, gender, gyousei,
                				floor, residence, family);
                		list.add(person);
            		}
            	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return list;
	}
	
	public static List<Person> load(File file) {
		List<Person> list = new ArrayList<Person>();
		try (BufferedReader br = new BufferedReader(new FileReader(file));){
            String line;
            while ((line = br.readLine()) != null) {
            	String[] items = line.split(",");
            	int id = Integer.valueOf(items[0]);
            	int age = Integer.valueOf(items[1]);
            	int gender = Integer.valueOf(items[2]);
            	double lon = Double.valueOf(items[3]);
            	double lat = Double.valueOf(items[4]);
            	String gyousei = String.valueOf(items[5]).substring(0,5);
              	int floor = Integer.valueOf(items[6]);
            	int residence = Integer.valueOf(items[7]);
            	int family = Integer.valueOf(items[8]);
            	
            	Person person = new Person(id, age,new LonLat(lon,lat), 
            			EPerson.UNDEFINED,  EGender.getType(gender), gyousei,
        				floor, residence, family);
            	list.add(person);
            }
		
		}catch (Exception e) {
            e.printStackTrace();
        }
		return list;
	}

	public static void main(String[] args) {
		System.out.println("start");

		String[] filename = {
				"16201_household_reallocation_result_pattern01.csv",
				"16202_household_reallocation_result_pattern01.csv",
				"16204_household_reallocation_result_pattern01.csv",
				"16205_household_reallocation_result_pattern01.csv",
				"16206_household_reallocation_result_pattern01.csv",
				"16207_household_reallocation_result_pattern01.csv",
				"16208_household_reallocation_result_pattern01.csv",
				"16209_household_reallocation_result_pattern01.csv",
				"16210_household_reallocation_result_pattern01.csv",
				"16211_household_reallocation_result_pattern01.csv",
				"16322_household_reallocation_result_pattern01.csv",
				"16323_household_reallocation_result_pattern01.csv",
				"16342_household_reallocation_result_pattern01.csv",
				"16343_household_reallocation_result_pattern01.csv"
		};
		
//		String[] filename = {
//				"22101_household_reallocation_result_pattern01.csv",
//				"22102_household_reallocation_result_pattern01.csv",
//				"22103_household_reallocation_result_pattern01.csv",
//				"22131_household_reallocation_result_pattern01.csv",
//				"22132_household_reallocation_result_pattern01.csv",
//				"22133_household_reallocation_result_pattern01.csv",
//				"22134_household_reallocation_result_pattern01.csv",
//				"22135_household_reallocation_result_pattern01.csv",
//				"22136_household_reallocation_result_pattern01.csv",
//				"22137_household_reallocation_result_pattern01.csv",
//				"22203_household_reallocation_result_pattern01.csv",
//				"22205_household_reallocation_result_pattern01.csv",
//				"22206_household_reallocation_result_pattern01.csv",
//				"22207_household_reallocation_result_pattern01.csv",
//				"22208_household_reallocation_result_pattern01.csv",
//				"22209_household_reallocation_result_pattern01.csv",
//				"22210_household_reallocation_result_pattern01.csv",
//				"22211_household_reallocation_result_pattern01.csv",
//				"22212_household_reallocation_result_pattern01.csv",
//				"22213_household_reallocation_result_pattern01.csv",
//				"22214_household_reallocation_result_pattern01.csv",
//				"22215_household_reallocation_result_pattern01.csv",
//				"22216_household_reallocation_result_pattern01.csv",
//				"22219_household_reallocation_result_pattern01.csv",
//				"22220_household_reallocation_result_pattern01.csv",
//				"22221_household_reallocation_result_pattern01.csv",
//				"22222_household_reallocation_result_pattern01.csv",
//				"22223_household_reallocation_result_pattern01.csv",
//				"22224_household_reallocation_result_pattern01.csv",
//				"22225_household_reallocation_result_pattern01.csv",
//				"22226_household_reallocation_result_pattern01.csv",
//				"22301_household_reallocation_result_pattern01.csv",
//				"22302_household_reallocation_result_pattern01.csv",
//				"22304_household_reallocation_result_pattern01.csv",
//				"22305_household_reallocation_result_pattern01.csv",
//				"22306_household_reallocation_result_pattern01.csv",
//				"22325_household_reallocation_result_pattern01.csv",
//				"22341_household_reallocation_result_pattern01.csv",
//				"22342_household_reallocation_result_pattern01.csv",
//				"22344_household_reallocation_result_pattern01.csv",
//				"22361_household_reallocation_result_pattern01.csv",
//				"22424_household_reallocation_result_pattern01.csv",
//				"22429_household_reallocation_result_pattern01.csv",
//				"22461_household_reallocation_result_pattern01.csv",
//				//"22503_household_reallocation_result_pattern01.csv"
//				};
		
		
		String dir = "C:/Users/ksym2/Desktop/digitalcity/household/";
		File outfile = new File("C:/Users/ksym2/Desktop/person16.csv");
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));){
			List<Person> listPersons = new ArrayList<>();
			for (String e : filename) {
				System.out.println(e);
				File file = new File(dir, e);
				listPersons.addAll(load2(file));
			}	
			for (int i = 0; i < listPersons.size(); i++) {
				Person p = listPersons.get(i);
				bw.write(String.format("%d,%d,%d,%f,%f,%s,%d,%d,%d", 
						i, p.getAge(), p.getGender().getId(),
						p.getHome().getLon(),p.getHome().getLat(),p.getGyousei(),
						p.getFloor(),p.getResidence(),p.getFamiliy()
						));
				bw.newLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("end");
	}
	
}

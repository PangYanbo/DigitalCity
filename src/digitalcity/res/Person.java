package digitalcity.res;

import jp.ac.ut.csis.pflow.geom2.ILonLat;

public class Person {
	private int id;
	private ILonLat home;
	private int age;
	private EGender gender;
	private String gyousei;
	private int floor;
	private int residence;
	private int familiy;

	public Person(int id, int age, ILonLat home, EPerson type, EGender gender, String gyousei,
			int floor, int residence, int familiy
			) {
		this.id = id;
		this.home = home;
		this.age = age;
		this.gender = gender;
		this.gyousei = gyousei;
		this.floor = floor;
		this.residence = residence;
		this.familiy = familiy;
	}
	
	public int getId() {
		return this.id;
	}
	
	public ILonLat getHome() {
		return this.home;
	}
	
	public int getAge() {
		return age;
	}
	
	public EGender getGender() {
		return gender;
	}
	
	public String getGyousei() {
		return this.gyousei;
	}
	
	public int getFloor() {
		return floor;
	}

	public int getResidence() {
		return residence;
	}

	public int getFamiliy() {
		return familiy;
	}	
}

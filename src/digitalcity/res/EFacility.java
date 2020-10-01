package digitalcity.res;


public enum EFacility {
	KINDERGARTEN(1),
	PRIMARY_SCHOOL(2),
	JUNIOR_HIGH_SCHOOL(3),
	HIGH_SCHOOL(4),
	RESIDENCE(5),
	OFFICE(6),
	OTHER(7);
	
	private final int id;
	
	private EFacility(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}	
}

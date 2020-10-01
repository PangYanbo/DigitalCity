package digitalcity.res;

public enum EPerson {
	UNDEFINED(1),
	KINDERGARTEN(2),
	PRIMARY_SCHOOL(3),
	JUNIOR_HIGH_SCHOOL(4),
	HIGH_SCHOOL(5),
	BUSINESS(6);
	
	private final int code;
	
	EPerson(int code){
		this.code = code;
	}
	
	public int getId() {
		return code;
	}
}

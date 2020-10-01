package digitalcity.res;

public enum EActivity {
	HOME("home"),
	SCHOOL("school"),
	WORK("work"),
	OTHER("other");
	
	private final String code;
	
	EActivity(String code){
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
}

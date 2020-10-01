package digitalcity.res;

public class Pair<T extends Object>{
	private T object1;
	private T object2;
	
	public Pair(T object1, T object2) {
		super();
		this.object1 = object1;
		this.object2 = object2;
	}

	public T getObject1() {
		return object1;
	}

	public void setObject1(T object1) {
		this.object1 = object1;
	}

	public T getObject2() {
		return object2;
	}

	public void setObject2(T object2) {
		this.object2 = object2;
	}
}

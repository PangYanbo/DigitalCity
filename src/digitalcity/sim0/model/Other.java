package digitalcity.sim0.model;

import digitalcity.res.Person;

public class Other extends AModel{

	public Other(Person person) {
		super(person);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean update(long time) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Other clone() throws CloneNotSupportedException {
		return (Other)super.clone();
	}
}

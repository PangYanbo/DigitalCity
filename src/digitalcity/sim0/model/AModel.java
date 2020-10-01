package digitalcity.sim0.model;

import digitalcity.res.Person;
import digitalcity.sim0.DestChoice;
import sim.sim4.res.Agent;

public abstract class AModel extends Agent implements Cloneable{
	protected Person person;
	protected DestChoice choice;
	
	public AModel(Person person) {
		super(person.getId(), person.getHome());
		this.person = person;
	}

	public Person getPerson() {
		return person;
	}
	
	public abstract boolean update(long time);

	@Override
	public AModel clone() throws CloneNotSupportedException {
		return (AModel)super.clone();
	}
	
	public void setDestChoice(DestChoice choice) {
		this.choice = choice;
	}
}

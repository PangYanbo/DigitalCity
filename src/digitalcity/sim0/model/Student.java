package digitalcity.sim0.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import digitalcity.res.Person;
import digitalcity.res.Facility;
import jp.ac.ut.csis.pflow.geom2.DistanceUtils;
import jp.ac.ut.csis.pflow.geom2.ILonLat;
import jp.ac.ut.csis.pflow.routing4.logic.Dijkstra;
import jp.ac.ut.csis.pflow.routing4.res.Network;
import jp.ac.ut.csis.pflow.routing4.res.Node;
import sim.sim4.res.ETrip;
import sim.sim4.res.Trip;

public class Student extends AModel{
	
	private final static double MAX_DISTANCE = 10000;
	
	private final static Random RANDOM1 = new Random(100);
	private final static Random RANDOM2 = new Random(100);
	
	private final static Dijkstra ROUTING = new Dijkstra();
	private final static double MAX_WALK_DISTANCE = 500;
	private final static double MAX_BICYCLE_DISTANCE = 5000;

	private final static Map<Long, Double> START_RATE = new HashMap<>();
	private final static Map<Long, Double> END_RATE = new HashMap<>();
	static {
		START_RATE.put((long)(6.0*3600), 0.2);
		START_RATE.put((long)(7.0*3600), 0.8);
		START_RATE.put((long)(8.0*3600), 1.0);
		
		END_RATE.put((long)(15.0*3600), 0.1);
		END_RATE.put((long)(16.0*3600), 0.5);
		END_RATE.put((long)(17.0*3600), 0.7);
		END_RATE.put((long)(18.0*3600), 1.0);
	}
	
	private Network facilities;
	private Facility destination;
	
	public Student(Person person, Network facilities) {
		super(person);
		this.facilities = facilities;
		this.destination = null;
	}
		
	protected ETrip getTransport(double distance) {
		if (distance < MAX_WALK_DISTANCE) {
			return ETrip.WALK;
		}else if (distance < MAX_BICYCLE_DISTANCE) {
			return ETrip.BICYCLE;
		}
		return ETrip.CAR;
	}
	
	private int generate(long time, ILonLat destination) {
		ILonLat curLoc = this.getLocation();
		if (curLoc != null) {
			double distance = DistanceUtils.distance(destination, curLoc);
			ETrip transport = getTransport(distance);
				
			long timeNoise = (long) (3600*1000*RANDOM2.nextDouble());
			
			// add trips
			List<Trip> listTrips = getListTrips();

			Trip trip0 = new Trip(curLoc, time+timeNoise, ETrip.STAY);
			listTrips.add(trip0);
			
			Trip trip1 = new Trip(destination, 0, transport);
			listTrips.add(trip1);
			
			Trip trip2 = new Trip(destination, Long.MAX_VALUE, ETrip.STAY);
			listTrips.add(trip2);
		}
		return 0;
	}
	
	private boolean gotoSchool(long time) {
				ILonLat home = person.getHome();
		Node node = ROUTING.getNearestNode(
				facilities, home.getLon(),home.getLat(),MAX_DISTANCE);
		if (node != null) {
			Facility facility = (Facility)node;
			generate(time, facility);
			destination = facility;
		}
		return true;
	}
	
	private boolean gotoHome(long time) {
		generate(time, person.getHome());
		return true;
	}
	
	private boolean update(long time, long hour) {
		if (destination == null) {
			double rate = START_RATE.containsKey(hour) ? START_RATE.get(hour) : 0;
			if (rate > 0) {
				double rndValue = RANDOM1.nextDouble();
				if (rate > rndValue) {
					return gotoSchool(time);
				}
			}
		}else {
			double rate = END_RATE.containsKey(hour) ? END_RATE.get(hour) : 0;
			if (rate > 0) {
				double rndValue = RANDOM1.nextDouble();
				if (rate > rndValue) {
					return gotoHome(time);
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean update(long time) {
		long hourMs = 3600 * 1000;
		long rest = time % hourMs;
		if (rest == 0) {
			int hour = (int) (time/1000);
			return update(time, hour);
		}
		return false;
	}
	
	@Override
	public Student clone() throws CloneNotSupportedException {
		return (Student)super.clone();
	}
}

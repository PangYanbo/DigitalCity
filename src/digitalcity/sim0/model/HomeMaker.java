package digitalcity.sim0.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import digitalcity.res.Facility;
import digitalcity.res.Person;
import jp.ac.ut.csis.pflow.geom2.DistanceUtils;
import jp.ac.ut.csis.pflow.geom2.ILonLat;
import jp.ac.ut.csis.pflow.geom2.Mesh;
import jp.ac.ut.csis.pflow.geom2.MeshUtils;
import sim.sim4.res.ETrip;
import sim.sim4.res.Trip;

public class HomeMaker extends AModel{

	private final static Random RANDOM1 = new Random(100);
	private final static Random RANDOM2 = new Random(100);
	
	private final static double MAX_WALK_DISTANCE = 500;
	private final static double MAX_BICYCLE_DISTANCE = 1000;

	private final static Map<Long, Double> START_RATE = new HashMap<>();
	static {
		START_RATE.put((long)(6.0*3600),   0.015);
		START_RATE.put((long)(7.0*3600),   0.02);
		START_RATE.put((long)(8.0*3600),   0.05);
		START_RATE.put((long)(9.0*3600),   0.09);
		START_RATE.put((long)(10.0*3600),  0.14);
		START_RATE.put((long)(11.0*3600),  0.14);
		START_RATE.put((long)(12.0*3600),  0.11);
		START_RATE.put((long)(13.0*3600),  0.16);
		START_RATE.put((long)(14.0*3600),  0.19);
		START_RATE.put((long)(15.0*3600),  0.22);
		START_RATE.put((long)(16.0*3600),  0.28);
		START_RATE.put((long)(17.0*3600),  0.37);
		START_RATE.put((long)(18.0*3600),  0.42);
		START_RATE.put((long)(19.0*3600),  0.40);
		START_RATE.put((long)(20.0*3600),  0.45);
		START_RATE.put((long)(21.0*3600),  0.60);
		START_RATE.put((long)(22.0*3600),  1.00);
	}
	
	private Map<String, List<Facility>> meshFacilities;
	private Facility destination;
	
	public HomeMaker(Person person, Map<String, List<Facility>> meshFacilities) {
		super(person);
		this.meshFacilities = meshFacilities;
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
			
			long stayTime = (long) (7200*1000*RANDOM2.nextDouble());
			Trip trip2 = new Trip(destination, time+timeNoise+stayTime, ETrip.STAY);
			listTrips.add(trip2);
			
			Trip trip3 = new Trip(curLoc, 0, transport);
			listTrips.add(trip3);
			
			Trip trip4 = new Trip(curLoc, Long.MAX_VALUE, ETrip.STAY);
			listTrips.add(trip4);
		}
		return 0;
	}
	
	private int choice(List<Double> probability, double random) {
		int index = 0;
		double sum = 0;
		for (int i = 0; i < probability.size(); i++) {
			sum += probability.get(i);
			if (random < sum) {
				return i;
			}
		}
		return index;
	}
	
	private Facility choiceFacility(String meshCode) {
		if (meshFacilities.containsKey(meshCode)) {
			List<Facility> listFacilities = meshFacilities.get(meshCode);
			int size = listFacilities.size();
			if (size > 0) {
				int index = RANDOM2.nextInt(size);
				return listFacilities.get(index);
			}
		}
		return null;
	}
	
	private boolean gotoDestination(long time) {
		ILonLat home = person.getHome();
		Mesh mesh = MeshUtils.createMesh(3, home.getLon(), home.getLat());
		List<Double> probs = choice.getProbability(mesh.getCode());
		if (probs != null) {
			int index = choice(probs, RANDOM2.nextDouble());
			String dstCode = choice.getDestinationName(mesh.getCode(), index);
			if (dstCode != null) {
				Facility facility = choiceFacility(dstCode);
				if (facility != null) {
					generate(time, facility);
					destination = facility;
				}
			}
		}
		return true;
	}
	
	private boolean update(long time, long hour) {
		if (destination == null) {
			double rate = START_RATE.containsKey(hour) ? START_RATE.get(hour) : 0;
			if (rate > 0) {
				double rndValue = RANDOM1.nextDouble();
				if (rate > rndValue) {
					return gotoDestination(time);
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
	public HomeMaker clone() throws CloneNotSupportedException {
		return (HomeMaker)super.clone();
	}
}

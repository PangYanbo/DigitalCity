package digitalcity.sim0;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import digitalcity.res.EFacility;
import digitalcity.res.EPerson;
import digitalcity.res.Facility;
import digitalcity.res.Person;
import digitalcity.sim0.model.AModel;
import digitalcity.sim0.model.HomeMaker;
import digitalcity.sim0.model.Other;
import digitalcity.sim0.model.Student;
import digitalcity.sim0.model.Worker;
import jp.ac.ut.csis.pflow.routing4.res.Network;

public class JobAssigner {
	
	private static EPerson getPersonType(int age) {
		if (age < 6) {
			return EPerson.KINDERGARTEN;
		}else if (age < 10) {
			return EPerson.PRIMARY_SCHOOL;
		}else if (age < 15) {
			return EPerson.JUNIOR_HIGH_SCHOOL;
		}else if (age < 19) {
			return EPerson.HIGH_SCHOOL;
		}else if (age < 65) {
			return EPerson.BUSINESS;
		}
		return null;
	}
	
	private static EFacility getSchoolType(EPerson person) {
		switch(person) {
		case KINDERGARTEN:
			return EFacility.KINDERGARTEN;
		case PRIMARY_SCHOOL:
			return EFacility.PRIMARY_SCHOOL;
		case JUNIOR_HIGH_SCHOOL:
			return EFacility.JUNIOR_HIGH_SCHOOL;
		case HIGH_SCHOOL:
			return EFacility.HIGH_SCHOOL;
		default:
			return EFacility.OTHER;
		}
	}
	
	private static final double WORKER_RATE = 0.6;
	private static final double GO_OUTSIDE_RATE = 0.95;
	private static final double HOMEMAKER_RATE = 0.6;
	
	public static List<AModel> assign(List<Person> listPerson, List<Facility> listFacilities, List<Facility> areaFacilities) {
		Random random = new Random(100);
		List<AModel> listModels = new ArrayList<>();
		Map<EFacility, Network> typeFacility = FacilityUtils.getNetworkMapFacilities(listFacilities);
		Map<String, List<Facility>> meshFacility = FacilityUtils.getMeshMapFacilities(listFacilities);
		
		for (Person p : listPerson) {
			EPerson type = getPersonType(p.getAge());
			// todo: add random walk activities to the agents
			if(type == EPerson.BUSINESS) {
				//if (random.nextDouble() < WORKER_RATE) {
				if (random.nextDouble() < GO_OUTSIDE_RATE) {
					Worker worker = new Worker(p, meshFacility, areaFacilities);
					listModels.add(worker);
				}
			}else if (type != null){
				EFacility ftype = getSchoolType(type);
				Network facilities = typeFacility.get(ftype);
				Student student = new Student(p, facilities);
				listModels.add(student);
			}else {
				if (random.nextDouble() < HOMEMAKER_RATE) {
					HomeMaker homeMaker = new HomeMaker(p, meshFacility);
					listModels.add(homeMaker);
				}else {
					listModels.add(new Other(p));
				}
			}
		}
		return listModels;
	}
}

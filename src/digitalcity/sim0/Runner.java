package digitalcity.sim0;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.sql.ClientInfoStatus;
import java.util.*;
import java.io.IOException;

import digitalcity.loader.DataLoader;
import digitalcity.loader.HouseholdDataLoader;
import digitalcity.loader.NetworkLoader;
import digitalcity.loader.ZDCTelepointLoader;
import digitalcity.res.Facility;
import digitalcity.res.Person;
import digitalcity.res.Region;
import digitalcity.sim0.model.AModel;
import jp.ac.ut.csis.pflow.routing4.res.Network;

public class Runner {
	
	public static void main(String[] args) throws Exception{
		System.out.println("start simulator");

		String src_path = "/home/ubuntu/Projects/DigitalCityProject/";

		// load facility data (zmaptown2)
		File bfile = new File(src_path + "Data/16_zmap_tel.csv");
		List<Facility> listFacilities = ZDCTelepointLoader.load(bfile);
		System.out.println("# of facilities: " + listFacilities.size());
		List<Facility> areaFacilities = ZDCTelepointLoader.filterFacility(listFacilities);
		System.out.println("# of facilities within city: " + areaFacilities.size());
		
		// load network data
		File file = new File(src_path + "Data/seidrm2017_16.txt");
		Network network = NetworkLoader.load(file);
		System.out.println("# of roads: " + network.linkCount());
		
		// load population data		
		File mfile = new File(src_path + "/Data/16210_household_reallocation_result_pattern01.csv");
		List<Person> listPerson=  HouseholdDataLoader.load2(mfile);
		System.out.println("# of household: " + listPerson.size());
		
		// load region data
		File rfile = new File(src_path + "Data/region.csv");
		List<Region> regions = DataLoader.loadRegions(rfile);
		System.out.println("# of regions: " + regions.size());
		
		// load observation data
		File ofile = new File(src_path + "Data/observation_20191013.csv");
		Map<Integer,Map<String, Double>> objMap = DataLoader.loadObservation(ofile);
		System.out.println("# of observation:" + objMap.size());

//		File dirname = new File(src_path + "Output/" + getRandomFileName());
//		if(dirname.mkdir()){
//			System.out.println("Create " + dirname.getPath());
//			File results = new File(dirname.getPath() + "/results/");
//			results.mkdir();
//		};

		String dirname = createOutputDir(src_path+"Output/");

		//writers
		File llfile = new File(dirname + "/likelihoods.csv");
		BufferedWriter likelihoodsWriter = new BufferedWriter(new FileWriter(llfile, true));

		File weightsfile = new File(dirname + "/weights.csv");
		BufferedWriter weightsWriter = new BufferedWriter(new FileWriter(weightsfile, true));

		File parameters = new File(dirname+"/parameters.csv");
		BufferedWriter wp = new BufferedWriter(new FileWriter(parameters, true));

		// initial parameters

		Double[][] params = createParams();
		
		// assign job
		List<AModel> listAgents = JobAssigner.assign(listPerson, listFacilities, areaFacilities);
		System.out.println("# of agents: " + listAgents.size());
		
		int startHour = 5;
		int endHour = 22;
		int numParticles = params.length;
		System.out.println("# of particles: "+ numParticles);

		ParticleMap particles = new ParticleMap();

		// Initialization
		DestChoice choice1 = new DestChoice(regions);
		DestChoice choice2 = new DestChoice(regions);
		for (int i = 0; i < numParticles; i++) {
			try{
				if(i%100==0){
					System.out.println("initialize particles: "+i);
				}
				List<Double> params_i = Arrays.asList(params[i]);
				Particle particle = new Particle(choice1, choice2, params_i, listAgents);
				particles.add(particle);
				ListWriter.writeLine(wp, particle.getModelParams());
			}catch (IOException e){
				System.out.println(e);
			}
		}
		wp.close();
		particles.normalization();
		for (int i = startHour; i < endHour; i++) {
			//unit: ms
			System.out.println("Simulation time is: "+i);

			long simStartTime = i * 3600 * 1000;
			long simEndTime = (i+1) * 3600 * 1000;
			int simStepTime = 600*1000;
			int index = 0;
			for (Particle p : particles.getListParticles()) {
				// main process
				p.simulate(simStartTime, simEndTime, simStepTime, network, index, dirname);
				index += 1;
			}

			List<Double>likelihoods = new ArrayList<Double>();
			List<Double>weights = new ArrayList<Double>();

			// evaluation
			double maxLikelihood = Double.MIN_VALUE;
			Particle topParticle = null;
			int maxParticleIndex = 0;
			for (int j = 0; j < particles.size(); j++) {
				Particle particle = particles.get(j);
				Map<String, Double> mapOfPrediction = Evaluation.getPredictionData(particle.getListAgents());
				double likelihood = Evaluation.calcLikelihood(mapOfPrediction, objMap.get(i+1));
				likelihoods.add(j, likelihood);
				//double likelihood = Evaluation.calculatePearson(mapOfPrediction, objMap.get(i+1));
				if (maxLikelihood < likelihood) {
					topParticle = particle;
					maxParticleIndex = j;
					maxLikelihood = likelihood;
				}
				System.out.printf("The likelihood of particle %d: %.8f%n", j, likelihood);
				particles.updateWeight(j, likelihood);
				weights.add(j, particles.getWeights().get(j));
				// updateModel(params);
			}

			// write out weights and likelihoods at each hour
			ListWriter.writeLine(likelihoodsWriter, likelihoods);
			ListWriter.writeLine(weightsWriter, weights);


			particles.normalization();
			if (particles.effectiveN() < particles.size() / 2) {
				particles.resample();
				System.out.println("Resampling!");
			}
			System.out.println(maxParticleIndex);
			// select best score
			listAgents = topParticle.getListAgents();
			List<Double>opt_params = topParticle.getModelParams();
			System.out.println(Arrays.toString(opt_params.toArray()));
		}
		likelihoodsWriter.close();
		weightsWriter.close();
		System.out.println("end");
	}

	public static String getRandomFileName() {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date =new Date();

		String str = simpleDateFormat.format(date);

		Random random = new Random();

		int rannum = (int)(random.nextDouble() * (99999 - 10000 + 1)) + 10000;

		return str + "." + rannum;

	};

	public static String createOutputDir(String source_path){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date =new Date();

		String str = simpleDateFormat.format(date);

		String filename = "";

		for(int i=0; i<1000; i++){
			File file = new File(source_path+str+'.'+i);
			if (!file.exists()){
				file.mkdir();
				File results = new File(file.getPath() + "/results/");
				results.mkdir();
				filename = file.getPath();
				break;
			}
		}
		return filename;
	}

	public static Double[][] createParams(){
		int n_params = 8;

		// commuter
		Double[] params_a = range(3,5.1,1);
		Double[] params_b = range(3,5.1,1);
		Double[] params_c = range(4,5.1,1);
		Double[] params_d = range(-2,-1,1);
		// otaku
		Double[] params_e = range(3,5.1,1);
		Double[] params_f = range(3,5.1,1);
		Double[] params_g = range(4,5.1,1);
		Double[] params_h = range(-2,-1,1);

		Double[][] sets = new Double[8][];
		sets[0] = params_a;
		sets[1] = params_b;
		sets[2] = params_c;
		sets[3] = params_d;
		sets[4] = params_e;
		sets[5] = params_f;
		sets[6] = params_g;
		sets[7] = params_h;

		int solutions = 1;
		for (Double[] doubles : sets) {
			solutions *= doubles.length;
		}

		Double[][] params = new Double[solutions][];

		for (int i = 0; i < solutions; i++){
			Double[] param = new Double[sets.length];
			int j = 1;
			int k = 0;
			for(Double[] set : sets){
				param[k] = set[(i/j)%set.length];
				// System.out.print(set[(i/j)%set.length] + " ");
				j *= set.length;
				k += 1;
			}
			// System.out.println();
			params[i] = param;
		}
		return params;
	}

	public static Double[] range(double start, double end, double step){
		int n = (int)((end-start)/step);
		Double[] params = new Double[n];

		for(int i = 0; i < n; i++){
			params[i] = start + i * step;
		}
		return params;
	}

}

package digitalcity.sim0;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import digitalcity.sim0.model.AModel;
import digitalcity.sim0.model.Worker;
import jp.ac.ut.csis.pflow.routing4.res.Network;
import sim.sim4.ctr.Controller;
import sim.sim4.ctr.Simulator;
import sim.sim4.filter.TrajectoryWriter;

public class Particle {
	
	private List<Double> modelParams;
	private DestChoice choiceLogic1;
	private DestChoice choiceLogic2;

	private double weight;

	// generated from the assignAgents
	private List<AModel> listAgents;
	
	public Particle(DestChoice choiceLogic1, DestChoice choiceLogic2, List<Double> params, List<AModel> listAgents) {
		super();
		this.choiceLogic1 = choiceLogic1;
		this.choiceLogic2 = choiceLogic2;
		updateModel(params);
		assignAgents(listAgents);
	}

	public int simulate(long simStartTime, long simEndTime, int simStepTime, Network network, int i, String path) {
		// i represents the index of particle
		File trjFile = new File(String.format(path + "/results/traj_%d.csv", i));
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(trjFile, true));){

		Controller<AModel> controller = new Controller<>(network, listAgents, true);
		// filter1
		AgentUpdater<AModel> modelUpdater = new AgentUpdater<>();
		controller.add(modelUpdater);
		// filter2
		TrajectoryWriter<AModel> writer = new TrajectoryWriter<>(bw);
		controller.add(writer);
		Simulator simulator = new Simulator(controller, simStartTime, simEndTime, simStepTime);
		simulator.run();
		
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public List<AModel> getListAgents() {
		return listAgents;
	}
	
	private DestChoice selectChoiceLogic(AModel model) {
		if (model instanceof Worker) {
			return choiceLogic1;
		}else {
			return choiceLogic2;
		}
	}

	private static List<Double> noise(List<Double> values){
		//Random random = new Random(100);
		Random random = new Random();
		List<Double> result = new ArrayList<>();
		for (int i = 0; i < values.size(); i++) {
			double value = values.get(i);
			//double noise = -0.5 + 1.0 * random.nextDouble();
			// average : 0.5 variance : 4
			// double noise = Math.sqrt(16) * random.nextGaussian() + 0.01;
			double noise = 0.1 * random.nextGaussian() + 0.01;
			result.add(i, value + noise);
		}
		return result;
	}

	// todo: revise update
	public void updateModel(List<Double> params) {
		// add noise
		this.modelParams = noise(params);
		// Go to DestChoice Class
		// commuter
		choiceLogic1.update(modelParams.subList(0, 4));
		// homemaker
		choiceLogic2.update(modelParams.subList(4, 8));
	}

	public void assignAgents(List<AModel> agentSource) {
		this.listAgents = new ArrayList<AModel>();
		for (AModel agent : agentSource) {
			AModel newAgent;
			try {
				newAgent = agent.clone();
				// choose commuter or homemaker
				DestChoice choice = selectChoiceLogic(newAgent);
				// choice: map of probability
				newAgent.setDestChoice(choice);
				listAgents.add(newAgent);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<Double> getModelParams() {
		return modelParams;
	}

}


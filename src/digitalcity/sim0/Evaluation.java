package digitalcity.sim0;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import digitalcity.sim0.model.AModel;
import jp.ac.ut.csis.pflow.geom2.ILonLat;
import jp.ac.ut.csis.pflow.geom2.Mesh;
import jp.ac.ut.csis.pflow.geom2.MeshUtils;

public class Evaluation {

	/**
	 * Calculate population of mesh area from location of agents.
	 * @param listAgents
	 * @return population of mesh area
	 */
	public static Map<String, Double> getPredictionData(List<AModel> listAgents){
		Map<String, Double> result = new HashMap<>();
		for (AModel agent : listAgents) {
			ILonLat loc = agent.getLocation();
			if (loc != null) {
				Mesh mesh = MeshUtils.createMesh(4, loc.getLon(), loc.getLat());
				String code = mesh.getCode();
				double value = result.containsKey(code) ? result.get(code) : 0;
				result.put(code, value + 1);
			}
		}
		return result;
	}
	
	public static double calcLikelihood(Map<String, Double> preds, Map<String,Double> obs){
		double likelihood = 0;
		double sumSubs = 0;
		List<Double> listSubs = new ArrayList<>();
		for (Map.Entry<String,Double> e : obs.entrySet()) {
			String obsCode = e.getKey();
			double predictionValue = preds.containsKey(obsCode) ? preds.get(obsCode) : 0;
			double sub = e.getValue() - predictionValue;
			listSubs.add(sub);
			sumSubs += sub;
		}
		
		double avg = sumSubs / listSubs.size();
		// disp
		double disp = 0, rmse = 0;
		for (Double e : listSubs) {
			disp += Math.pow(e-avg, 2);
			rmse += Math.pow(e, 2);
		}
		disp /= listSubs.size();
		rmse = Math.sqrt(rmse/listSubs.size());

		likelihood = Math.exp(-1*Math.pow(rmse,2)/(2*disp));
		likelihood /= (Math.sqrt(2*Math.PI)*disp);

		return likelihood;
	}

	public static double calculatePearson(Map<String, Double> prediction, Map<String, Double> observation) {
		double Pearson = 0;
		double sumX = 0;
		double sumY = 0;
		double sumXY = 0;
		double sumXX = 0;
		double sumYY = 0;
		int n = 0;
		for (Map.Entry<String, Double> e: observation.entrySet()) {
			String meshCode = e.getKey();
			double observe= e.getValue();
			double predict = 0;
			//double predict = prediction.containsKey(meshCode) ? prediction.get(meshCode): 0;
			if (!prediction.containsKey(meshCode)) {
				continue;
			} else {
				predict = prediction.get(meshCode);
			}
			sumX += predict;
			sumY += observe;
			sumXY += predict * observe;
			sumXX += predict * predict;
			sumYY += observe * observe;
			n += 1;
		}
		Pearson = (sumXY - sumX * sumY / n) / Math.sqrt((sumXX - Math.pow(sumX, 2) / n) * (sumYY - Math.pow(sumY, 2) / n));
		return Math.pow(Pearson, 2);
	}
}

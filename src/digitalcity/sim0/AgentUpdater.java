package digitalcity.sim0;

import java.util.ArrayList;
import java.util.List;

import digitalcity.sim0.model.AModel;
import jp.ac.ut.csis.pflow.routing4.res.Network;
import sim.sim4.filter.IFilter;
import sim.sim4.trip.Routing;

public class AgentUpdater<T extends AModel> implements IFilter<T> {

	@Override
	public void run(long time, Network network, List<T> listAgents) {
		List<AModel> updatedList = new ArrayList<>();
		for (AModel m : listAgents) {
			boolean isUpdated = m.update(time);
			if (isUpdated) {
				updatedList.add(m);
			}
		}
		try {
			Routing.route(network, updatedList, time);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}

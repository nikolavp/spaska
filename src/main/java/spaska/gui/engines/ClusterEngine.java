package spaska.gui.engines;

import java.util.Map;
import java.util.Vector;

import spaska.clusterers.FuzzyKMeans;
import spaska.clusterers.IClusterer;
import spaska.clusterers.SimpleKMeans;
import spaska.clusterers.ZeroClusterer;
import spaska.gui.InputException;
import spaska.statistics.Statistics;

public class ClusterEngine extends Engine {

	private IClusterer							clusterer;
	private Vector<Class<? extends IClusterer>>	clusterizators;

	public ClusterEngine() {
		clusterizators = new Vector<Class<? extends IClusterer>>();
		clusterizators.add(SimpleKMeans.class);
		clusterizators.add(ZeroClusterer.class);
		clusterizators.add(FuzzyKMeans.class);
	}

	public Vector<Class<? extends IClusterer>> getClusterizators() {
		return clusterizators;
	}

	public void setClusterer(IClusterer c, Map<String, String> params) throws Exception {
		if (c != null) {
			clusterer = c;
			if (params != null) {
				clusterer.setParameters(params);
			}
		}
		else {
			throw new InputException("Please choose a clusterer");
		}
	}

	@Override
	public void check() throws Exception {
		super.check();
		if (clusterer == null) throw new InputException("Choose a clusterer first"); 

		if (clusterer instanceof SimpleKMeans && (reader.getValidators() == null || reader.getValidators().size() == 0)) {
			throw new InputException("SimpleKMeans clusterer needs normalized input data.");
		}
	}

	public Statistics start() throws Exception {
		setDataset();
		if (clusterer != null) {
			clusterer.clusterize(getDataset());
			return clusterer.getStatistic();
		}
		return null;
	}

	@Override
	public void reset() {
		super.reset();
		clusterer = null;
	};
}

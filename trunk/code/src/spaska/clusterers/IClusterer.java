package spaska.clusterers;

import spaska.data.Dataset;
import spaska.gui.Parametrable;
import spaska.statistics.ClustererStatistics;

public interface IClusterer extends Parametrable {

	public void clusterize(Dataset data);

	public Dataset getClusteredDataset();

	public ClustererStatistics getStatistic();

	public String getName();
}

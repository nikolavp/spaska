package spaska.clusterers;

import spaska.data.Dataset;
import spaska.gui.Parametrable;
import spaska.statistics.ClustererStatistics;

/**
 * An interface representing all clustering algorithms in the system. If you
 * want to implement a custom clustering algorithm you should implement this
 * interface.
 */
public interface IClusterer extends Parametrable {
    /**
     * Cluster the given dataset.
     * 
     * @param data
     *            the data set that needs to be clustered
     */
    void clusterize(Dataset data);

    /**
     * Get the result from the clustering.
     * 
     * @return the resulting clustered data
     */
    Dataset getClusteredDataset();

    /**
     * Get the statistics from the clustering.
     * 
     * @return the statistic object that was populated in the clustering process
     */
    ClustererStatistics getStatistic();

    /**
     * Get the name of the clustering algorithm.
     * 
     * @return the name of the clustering algorithm
     */
    String getName();
}

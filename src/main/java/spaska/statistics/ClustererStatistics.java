package spaska.statistics;

/**
 * A simple statistics for clusterization algorithms.
 * 
 * @author Lazar Chifudov
 */

public final class ClustererStatistics extends Statistics {

    private static final double PERCENT_BASE = 100.;
    private int[] clusters;

    /**
     * Construct a statistic object by an array which contains the numbers of
     * instances per each cluster.
     * 
     * @param clusters
     *            an array containing the size of every cluster
     */
    public ClustererStatistics(int[] clusters) {
        this.clusters = clusters;
    }

    @Override
    protected void generateInfo() {
        StringBuilder result = new StringBuilder();
        result.append("================================================\n");
        if (getAlgorithmName() != null) {
            result.append("Clusterer: " + getAlgorithmName() + "\n");
        }
        setInstances(0);
        for (int i : clusters) {
            setInstances(getInstances() + i);
        }
        result.append("Total instances: " + getInstances() + "\n");
        result.append("Number of clusters: " + clusters.length + "\n");
        result.append("Test time (HH:MM:SS.MS): " + timeToString(getTestTime()));
        result.append("\n------------------------------------------------\n");

        for (int i = 0; i < clusters.length; i++) {
            result.append("Cluster " + i + ": " + clusters[i]);
            double percent = (PERCENT_BASE * clusters[i]) / (double) getInstances();
            result.append(String.format(" instances (%.2f%%)\n", percent));
        }
        result.append("================================================\n");
        setInfo(result.toString());
        setModified(false);
    }
}

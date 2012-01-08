package spaska.statistics;

/**
 * 
 * @author Lazar Chifudov
 */

/* A simple statistics for clusterization */
public final class ClustererStatistics extends Statistics {

    private int[] clusters;

    /*
     * construct by an array which contains the numbers of instances per each
     * cluster
     */
    public ClustererStatistics(int[] clusters) {
        this.clusters = clusters;
    }

    @Override
    protected void generateInfo() {
        StringBuilder result = new StringBuilder();
        result.append("================================================\n");
        if (algorithmName != null) {
            result.append("Clusterer: " + algorithmName + "\n");
        }
        instances = 0;
        for (int i : clusters) {
            instances += i;
        }
        result.append("Total instances: " + instances + "\n");
        result.append("Number of clusters: " + clusters.length + "\n");
        result.append("Test time (HH:MM:SS.MS): " + timeToString(testTime));
        result.append("\n------------------------------------------------\n");

        for (int i = 0; i < clusters.length; i++) {
            result.append("Cluster " + i + ": " + clusters[i]);
            double percent = (100. * clusters[i]) / (double) instances;
            result.append(String.format(" instances (%.2f%%)\n", percent));
        }
        result.append("================================================\n");
        info = result.toString();
        modified = false;
    }
}

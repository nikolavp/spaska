package spaska.clusterers;

import java.util.List;
import java.util.Map;

import spaska.data.Attribute;
import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.NominalValue;
import spaska.data.Value;
import spaska.statistics.ClustererStatistics;

/**
 * A zero implementation for the clustering interface. This class just clusters
 * all instances into one cluster and is mostly used to test other clustering
 * algorithms.
 */
public final class ZeroClusterer implements IClusterer {

    private Dataset clusteredData;

    private long executionTime;

    /**
     * Get the parameters for this clusterer.
     * 
     * @return the parameters for this clusterer
     */
    public static Map<String, String> getParameters() {
        return null;
    }

    @Override
    public void clusterize(Dataset inputData) {
        long start = System.currentTimeMillis();

        clusteredData = new Dataset(Long.valueOf(System.currentTimeMillis())
                .toString(), inputData.getAttributes());
        clusteredData.addAttribute(new Attribute("cluster",
                Attribute.ValueType.Nominal));
        clusteredData.setClassIndex(clusteredData.getAttributes().size() - 1);

        for (Instance e : inputData.getElements()) {
            if (Thread.interrupted()) {
                return;
            }

            List<Value> list = e.getVector();
            list.add(new NominalValue("0"));
            Instance element = new Instance(list);
            clusteredData.addElement(element);
        }

        executionTime = System.currentTimeMillis() - start;
    }

    @Override
    public ClustererStatistics getStatistic() {
        int[] matrix = { 
                clusteredData.getElements().size() 
        };
        ClustererStatistics statistic = new ClustererStatistics(matrix);

        statistic.setAlgorithmName("ZeroClusterer");
        statistic.setTestTime(executionTime);
        return statistic;
    }

    @Override
    public Dataset getClusteredDataset() {
        return clusteredData;
    }

    @Override
    public String getName() {
        return "ZeroClusterer";
    }

    @Override
    public void setParameters(Map<String, String> params) {
        // do nothing
    }

}

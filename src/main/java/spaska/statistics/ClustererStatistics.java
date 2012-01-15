package spaska.statistics;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import spaska.classifiers.util.DatasetService;
import spaska.data.Instance;
import spaska.data.NominalValue;

/**
 * A simple statistics for clusterization algorithms.
 */
public final class ClustererStatistics extends Statistics {

	private static final double PERCENT_BASE = 100.;	

    private int[] clusters;
    private int[] instancesInClass;
    private Map<Instance, Integer> clusteredInstances;
    private List<String> classNames;
    private DatasetService service;
    
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

    /* Sets value indicating which instance to which cluster has been assigned */
    public void setClusteredInstances(Map<Instance, Integer> instances) {
    	this.clusteredInstances = instances;
    }
    
    @Override
    /* generate the output string and assign it to the info field */
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
        
        if (getAlgorithmName() != "ZeroClusterer") {
        	result.append(getEvaluationInfo(result));
        }
        
        result.append("================================================\n");
        setInfo(result.toString());
        setModified(false);
    }

    /* Get the evaluation statistic string */
	private StringBuilder getEvaluationInfo(StringBuilder result) {
		StringBuilder clusterStatistics = new StringBuilder();
		
		for (int clusterIndex = 0; clusterIndex < clusters.length; clusterIndex++) {
			instancesInClass = new int[classNames.size()];
			Iterator<Entry<Instance, Integer>> iterator = clusteredInstances.entrySet().iterator();
			 
			clusterStatistics.append("================================================\n");
			
			while (iterator.hasNext()) {
				Map.Entry<Instance, Integer> pair = (Map.Entry<Instance, Integer>) iterator.next();
				
				if (pair.getValue() == clusterIndex) {
					String className = ((NominalValue) service.getClass(pair.getKey())).getValue();
					int classIndex = classNames.indexOf(className);
					instancesInClass[classIndex]++;
				}
			}
			
			clusterStatistics.append("Cluster number: " + clusterIndex + "\n");
			
			for (int classIndex = 0; classIndex < classNames.size(); classIndex++) {
				clusterStatistics.append(instancesInClass[classIndex] 
						+ " instances from class " 
						+ classNames.get(classIndex) + "\n");
			}
			
			clusterStatistics.append("================================================\n");
		}
		
		return clusterStatistics; 
	}
	
	/*
	 * set class names for current instances
	 */
	public void setClassNames(String[] classNames) {
		this.classNames = Arrays.asList(classNames);
	}
	
	/*
	 * set service instance for current data
	 */
	public void setService(DatasetService service) {
		this.service = service;
	}
}

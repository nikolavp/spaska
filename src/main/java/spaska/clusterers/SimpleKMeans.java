package spaska.clusterers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spaska.classifiers.util.DatasetService;
import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.NominalValue;
import spaska.data.NumericValue;
import spaska.data.Value;
import spaska.statistics.ClustererStatistics;

/**
 * A KMeans implementation for spaska.
 * <p>
 * Implementation for the most simple and also most common algorithm for
 * clustering.
 * </p>
 */
public final class SimpleKMeans implements IClusterer {

    private static final int DEFAULT_CLUSTERS_SIZE = 3;
    private static final String K_ALGORITHM_PARAMETER = "Number of clusters";
    private static final String MAX_ITERATIONS_ALGORITHM_PARAMETER = "Max number of iterations";
    private static final int DEFAULT_MAX_ITERATIONS = 100;

    private static final Logger LOG = LoggerFactory
            .getLogger(SimpleKMeans.class);

    /**
     * Get parameters for this clusterer.
     * 
     * @return parameters for this clusterer.
     */
    public static Map<String, String> getParameters() {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(K_ALGORITHM_PARAMETER,
                String.valueOf(DEFAULT_CLUSTERS_SIZE));
        parameters.put(MAX_ITERATIONS_ALGORITHM_PARAMETER,
                String.valueOf(DEFAULT_MAX_ITERATIONS));
        return parameters;
    }

    private int maxIterations = DEFAULT_MAX_ITERATIONS;
    private int k = DEFAULT_CLUSTERS_SIZE;
    private Cluster[] clusters;
    private int seed;
    private Dataset data;
    private ClustererStatistics algorithmResult;
    private int iterations = maxIterations;

    /**
     * Default constructor.
     */
    public SimpleKMeans() {
        clusters = new Cluster[k];
        for (int i = 0; i < clusters.length; i++) {
            clusters[i] = new Cluster();
        }
    }

    /**
     * Safely get a random positive integer.
     */
    private static int nextPositiveInt(Random r) {
        int result = r.nextInt();
        if (result == Integer.MIN_VALUE) {
            return Integer.MAX_VALUE;
        }
        return Math.abs(result);
    }

    private void initCenters(Dataset initialData) {
        Random r = new Random(seed);
        List<Instance> instances = initialData.getElements();
        Set<Integer> chosenCenters = new HashSet<Integer>();
        for (int i = 0; i < k; i++) {
            int randInd = nextPositiveInt(r);
            int index = randInd % instances.size();

            while (!chosenCenters.add(index)) {
                randInd = nextPositiveInt(r);
                index = randInd % instances.size();
            }

            clusters[i].center = instances.get(index);
        }
    }

    @Override
    public void clusterize(Dataset sourceData) {
        iterations = maxIterations;
        long startTime = System.currentTimeMillis();
        initCenters(sourceData);
        boolean isAllIterations = true;

        List<Instance> instances = sourceData.getElements();
        while (iterations-- > 0) {
            if (Thread.interrupted()) {
                return;
            }
            for (int i = 0; i < clusters.length; i++) {
                clusters[i].previousInstances.clear();
                clusters[i].previousInstances.addAll(clusters[i].instances);
                clusters[i].instances.clear();
            }

            for (int i = 0; i < instances.size(); i++) {
                assignToCluster(instances.get(i), sourceData);
            }

            // recalculate cluster centers
            for (int i = 0; i < clusters.length; i++) {
                double bestDistance = Double.MAX_VALUE;
                Instance bestInstance = null;

                List<Instance> currentClusterInstances = clusters[i].instances;
                Instance currentClusterCenter = clusters[i].center;

                for (int j = 0; j < currentClusterInstances.size(); j++) {
                    Instance currentInstance = currentClusterInstances.get(j);
                    if (currentInstance.equals(currentClusterCenter)) {
                        continue;
                    }

                    double dist = getDistanceAll(currentInstance,
                            currentClusterInstances, sourceData);
                    // System.out.println(dist);
                    if (dist < bestDistance) {
                        // System.out.println("if " + dist);
                        bestDistance = dist;
                        bestInstance = currentInstance;
                    }
                }
                if (bestInstance != null) {
                    clusters[i].center = bestInstance;
                    clusters[i].centroidDistance = bestDistance;
                }
            }

            // check if previous instances are the same as current instances in
            // every cluster
            boolean isStabilized = true;
            for (int i = 0; i < clusters.length; i++) {
                List<Instance> previousIntances = clusters[i].previousInstances;
                List<Instance> currentIntances = clusters[i].instances;
                if (previousIntances.size() != currentIntances.size()) {
                    isStabilized = false;
                    break;
                }
                if (!previousIntances.containsAll(currentIntances)
                        || !currentIntances.containsAll(previousIntances)) {
                    isStabilized = false;
                    break;
                }
            }
            if (isStabilized) {
                isAllIterations = false;
                LOG.info("Iterations " + (maxIterations - iterations));
                break;
            }

        }
        if (isAllIterations) {
            LOG.info("All iterations are passed");
        }
        int[] res = new int[clusters.length];
        for (int i = 0; i < clusters.length; i++) {
            res[i] = clusters[i].instances.size();
        }
        
        Map<Instance, Integer> clusteredInstances = new HashMap<Instance, Integer>();
        for (int i = 0; i < clusters.length; i++) {
        	List<Instance> clusterInstances = clusters[i].instances;
        	for (int j = 0; j < clusterInstances.size(); j++) {
        		clusteredInstances.put(clusterInstances.get(j), i);
        	}
        }
        
        algorithmResult = new ClustererStatistics(res);
        algorithmResult.setTestTime(System.currentTimeMillis() - startTime);
        algorithmResult.setClusteredInstances(clusteredInstances);
        algorithmResult.setClassNames(data.getAllClassNamesArray());
        algorithmResult.setService(new DatasetService(data));
        algorithmResult.setAlgorithmName("Simple K-Means");
    }

    private double getDistanceAll(Instance instance,
            List<Instance> allInstances, Dataset distanceData) {
        double dist = 0;
        for (Instance currentInstance : allInstances) {
            dist += getDistance(instance, currentInstance, distanceData);
        }
        return dist;
    }

    private void assignToCluster(Instance instance, Dataset assignData) {
        int assignedClusterIndex = -1;
        double minLen = Double.MAX_VALUE;

        // find nearest cluster center
        for (int i = 0; i < clusters.length; i++) {
            double currentDistance = getDistance(i, instance, assignData);
            if (currentDistance < minLen) {
                minLen = currentDistance;
                assignedClusterIndex = i;
            }
        }

        // add to cluster
        clusters[assignedClusterIndex].instances.add(instance);
        clusters[assignedClusterIndex].centroidDistance += minLen;
    }

    private double getDistance(int centerInstanceIndex, Instance instance,
            Dataset distanceData) {
        Instance center = clusters[centerInstanceIndex].center;
        return getDistance(center, instance, distanceData);
    }

    private double getDistance(Instance center, Instance instance,
            Dataset distanceData) {
        // List<Attribute> attributes = data.getAttributes();
        List<Value> centerAttributes = center.getVector();
        List<Value> instanceAttributes = instance.getVector();
        double distance = 0;
        for (int i = 0; i < centerAttributes.size(); i++) {
            if (distanceData.getClassIndex() == i) {
                continue;
            }

            Value centerValue = centerAttributes.get(i);
            Value instanceValue = instanceAttributes.get(i);
            // distance += distanceBetweenValues(centerValue, instanceValue,
            // centerValue.getType());

            if (centerValue.getClass().equals(NumericValue.class)) {
                double centerValDouble = ((NumericValue) centerValue)
                        .getValue();
                double instanceValDouble = ((NumericValue) instanceValue)
                        .getValue();

                // // normalize them
                // Set<Value> domain = data.getDomain(attributes.get(i));
                // double min = Double.MAX_VALUE;
                // double max = Double.MIN_VALUE;
                // for (Value value : domain) {
                // double d = ((NumericValue)value).getValue();
                // if (d > max) {
                // max = d;
                // continue;
                // }
                // if (d < min) {
                // min = d;
                // continue;
                // }
                // }

                // double normalizedCenterDoubleValue =
                // normalizeDoubleValue(centerValDouble, min, max);
                // double normalizedInstanceDoubleValue =
                // normalizeDoubleValue(instanceValDouble, min, max);
                double normalizedCenterDoubleValue = centerValDouble; // normalizeDoubleValue(centerValDouble,
                                                                      // min,
                                                                      // max);
                double normalizedInstanceDoubleValue = instanceValDouble; // normalizeDoubleValue(instanceValDouble,
                                                                          // min,
                                                                          // max);
                distance += Math.pow(normalizedCenterDoubleValue
                        - normalizedInstanceDoubleValue, 2);

            } else if (centerValue.getClass().equals(NominalValue.class)
            // add 1 if same, 0 if different
                    && centerValue.getValue().equals(instanceValue.getValue())) {
                distance += 1;
            }
        }
        return distance;
    }

    @Override
    public Dataset getClusteredDataset() {
        return data;
    }

    @Override
    public String getName() {
        return "SimpleKMeans";
    }

    @Override
    public ClustererStatistics getStatistic() {
        return algorithmResult;
    }

    /**
     * This class represents a cluster in KMeans.
     */
    private static class Cluster {
        private Instance center = new Instance(null);
        private List<Instance> instances = new ArrayList<Instance>();
        private List<Instance> previousInstances = new ArrayList<Instance>();
        @SuppressWarnings("unused")
        private double centroidDistance;
    }

    @Override
    public void setParameters(Map<String, String> params) {
        for (Entry<String, String> entry : params.entrySet()) {
            String param = entry.getKey();
            if (param.equalsIgnoreCase(K_ALGORITHM_PARAMETER)) {
                try {
                    k = Integer.parseInt(entry.getValue());
                } catch (NumberFormatException e) {
                    throw new RuntimeException(K_ALGORITHM_PARAMETER
                            + " must be an integer.");
                }
            } else if (param
                    .equalsIgnoreCase(MAX_ITERATIONS_ALGORITHM_PARAMETER)) {
                try {
                    maxIterations = Integer.parseInt(entry.getValue());
                } catch (NumberFormatException e) {
                    throw new RuntimeException(
                            MAX_ITERATIONS_ALGORITHM_PARAMETER
                                    + " must be an integer.");
                }
            }
        }
        clusters = new Cluster[k];
        for (int i = 0; i < clusters.length; i++) {
            clusters[i] = new Cluster();
        }
    }

}

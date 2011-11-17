package spaska.clusterers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.NominalValue;
import spaska.data.NumericValue;
import spaska.data.Value;
import spaska.statistics.ClustererStatistics;

public class SimpleKMeans implements IClusterer {
	
	private static final String K_ALGORITHM_PARAMETER = "Number of clusters";
	private static final String MAX_ITERATIONS_ALGORITHM_PARAMETER = "Max number of iterations";
	private static final int DEFAULT_MAX_ITERATIONS = 100;

	public static Map<String, String> getParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(K_ALGORITHM_PARAMETER, String.valueOf(3));
		parameters.put(MAX_ITERATIONS_ALGORITHM_PARAMETER, String.valueOf(DEFAULT_MAX_ITERATIONS));
		return parameters;
	}

	private int maxIterations = DEFAULT_MAX_ITERATIONS;
	private int k = 3;
	private Cluster[] clusters;
	private int seed;
	private Dataset data;
	private ClustererStatistics algorithmResult;
	private int iterations = maxIterations;
	
	public SimpleKMeans() {
		clusters = new Cluster[k];
		for (int i = 0; i < clusters.length; i++) {
			clusters[i] = new Cluster();
		}
	}
	
	private void initCenters(Dataset data) {
		Random r = new Random(seed);
		List<Instance> instances = data.getElements();
		Set<Integer> chosenCenters = new HashSet<Integer>();
		for (int i = 0; i < k; i++) {
			int randInd = Math.abs(r.nextInt());
			int index = randInd % instances.size();

			while (!chosenCenters.add(index)) {
				randInd = Math.abs(r.nextInt());
				index = randInd % instances.size();
			}
			
			clusters[i].center = instances.get(index);  
		}
	}

	@Override
	public void clusterize(Dataset data) {
		iterations = maxIterations;
		long startTime = System.currentTimeMillis();
		initCenters(data);
		boolean isAllIterations = true;
		
		List<Instance> instances = data.getElements();
		while (iterations-- > 0) {
			if (Thread.interrupted()) return;
			for (int i = 0; i < clusters.length; i++) {
				clusters[i].previousInstances.clear();
				clusters[i].previousInstances.addAll(clusters[i].instances);
				clusters[i].instances.clear();
			}

			for (int i = 0; i < instances.size(); i++) {
				assignToCluster(instances.get(i), data);
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
					
					double dist = getDistanceAll(currentInstance, currentClusterInstances, data);
//					System.out.println(dist);
					if (dist < bestDistance) {
//						System.out.println("if " + dist);
						bestDistance = dist;
						bestInstance = currentInstance;
					}
				}
				if (bestInstance != null) {
					clusters[i].center = bestInstance;
					clusters[i].centroidDistance = bestDistance;
				}
			}
			
			// check if previous instances are the same as current instances in every cluster
			boolean isStabilized = true;
			for (int i = 0; i < clusters.length; i++) {
				List<Instance> previousIntances = clusters[i].previousInstances;
				List<Instance> currentIntances = clusters[i].instances;
				if (previousIntances.size() != currentIntances.size()) {
					isStabilized = false;
					break;
				}
				if (!previousIntances.containsAll(currentIntances) || !currentIntances.containsAll(previousIntances)) {
					isStabilized = false;
					break;
				}
			}
			if (isStabilized) {
				isAllIterations = false;
				System.out.println("Iterations " + (maxIterations - iterations));
				break;
			}
			
		}
		if (isAllIterations) {
			System.out.println("all iterations are passed");
		}
		int[] res = new int[clusters.length];
		for (int i = 0; i < clusters.length; i++) {
			res[i] = clusters[i].instances.size();
		}
		algorithmResult = new ClustererStatistics(res);
		algorithmResult.setTestTime(System.currentTimeMillis() - startTime);
        algorithmResult.setAlgorithmName("Simple K-Means");
	}
	
	private double getDistanceAll(Instance instance, List<Instance> allInstances, Dataset data) {
		double dist = 0;
		for (Instance currentInstance : allInstances) {
			dist += getDistance(instance, currentInstance, data);
		}
		return dist;
	}
	
    public static double euclideanDistance(double[] a, double[] b) {
        if (a == null || b == null || a.length != b.length)
            throw new IllegalArgumentException("a: " + a + "; b: " + b);
        
        double sum = 0;
        for (int i = 0; i < a.length; ++i)
            sum += Math.pow((a[i] - b[i]), 2);
        return Math.sqrt(sum);
    }

	private void assignToCluster(Instance instance, Dataset data) {
		int assignedClusterIndex = -1;
		double minLen = Double.MAX_VALUE;
		
		// find nearest cluster center
		for (int i = 0; i < clusters.length; i++) {
			double currentDistance = getDistance(i, instance, data);
			if (currentDistance < minLen) {
				minLen = currentDistance;
				assignedClusterIndex = i;
			}
		}
		
		// add to cluster
		clusters[assignedClusterIndex].instances.add(instance);
		clusters[assignedClusterIndex].centroidDistance += minLen;
	}

	private double getDistance(int centerInstanceIndex, Instance instance, Dataset data) {
		Instance center = clusters[centerInstanceIndex].center;
		return getDistance(center, instance, data);
	}

	private double getDistance(Instance center, Instance instance, Dataset data) {
//		List<Attribute> attributes = data.getAttributes();
		List<Value> centerAttributes = center.getVector();
		List<Value> instanceAttributes = instance.getVector();
		double distance = 0;
		for (int i = 0; i < centerAttributes.size(); i++) {
			if (data.getClassIndex() == i)
				continue;
			
			Value centerValue = centerAttributes.get(i);
			Value instanceValue = instanceAttributes.get(i);
//			distance += distanceBetweenValues(centerValue, instanceValue, centerValue.getType());
			
			if (centerValue.getClass().equals(NumericValue.class)) {
				double centerValDouble = ((NumericValue)centerValue).getValue();
				double instanceValDouble = ((NumericValue)instanceValue).getValue();
				
//				// normalize them
//				Set<Value> domain = data.getDomain(attributes.get(i));
//				double min = Double.MAX_VALUE;
//				double max = Double.MIN_VALUE;
//				for (Value value : domain) {
//					double d = ((NumericValue)value).getValue();
//					if (d > max) {
//						max = d;
//						continue;
//					}
//					if (d < min) {
//						min = d;
//						continue;
//					}
//				}
				
//				double normalizedCenterDoubleValue = normalizeDoubleValue(centerValDouble, min, max);
//				double normalizedInstanceDoubleValue = normalizeDoubleValue(instanceValDouble, min, max);
				double normalizedCenterDoubleValue = centerValDouble;//normalizeDoubleValue(centerValDouble, min, max);
				double normalizedInstanceDoubleValue = instanceValDouble;//normalizeDoubleValue(instanceValDouble, min, max);
				distance += Math.pow(normalizedCenterDoubleValue - normalizedInstanceDoubleValue, 2);
				
			} else if (centerValue.getClass().equals(NominalValue.class)) {
				// add 1 if same, 0 if different
				if (centerValue.getValue().equals(instanceValue.getValue())) {
					distance += 1;
				}
			}
		}
		return distance;
	}

	private double normalizeDoubleValue(double doubleValue, double min,
			double max) {
		return (doubleValue - min) / (max - min);
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

	private class Cluster {
		private Instance center = new Instance(null);
		private List<Instance> instances = new ArrayList<Instance>();
		private List<Instance> previousInstances = new ArrayList<Instance>();
		private double centroidDistance;
	}

	@Override
	public void setParameters(Map<String, String> params) {
		for (Entry<String,String> entry : params.entrySet()) {
			String param = entry.getKey();
			if (param.equalsIgnoreCase(K_ALGORITHM_PARAMETER)) {
				try {
					k = Integer.parseInt(entry.getValue());
				} catch (NumberFormatException e) {
					throw new RuntimeException(K_ALGORITHM_PARAMETER + " must be an integer.");
				}
			} else if (param.equalsIgnoreCase(MAX_ITERATIONS_ALGORITHM_PARAMETER)) {
				try {
					maxIterations = Integer.parseInt(entry.getValue());
				} catch (NumberFormatException e) {
					throw new RuntimeException(MAX_ITERATIONS_ALGORITHM_PARAMETER + " must be an integer.");
				}
			}
		}
		clusters = new Cluster[k];
		for (int i = 0; i < clusters.length; i++) {
			clusters[i] = new Cluster();
		}
	}

}
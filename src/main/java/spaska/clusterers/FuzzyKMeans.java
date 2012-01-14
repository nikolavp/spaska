package spaska.clusterers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import spaska.classifiers.util.DatasetService;
import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.NominalValue;
import spaska.data.NumericValue;
import spaska.data.Value;
import spaska.statistics.ClustererStatistics;

/**
 * 
 * The algorithm is implemented according to the algorithm described in this
 * tutorial:
 * http://home.dei.polimi.it/matteucc/Clustering/tutorial_html/cmeans.html
 * 
 */
public class FuzzyKMeans implements IClusterer {

	private static final String NUMBER_OF_CLUSTERS_PARAMETER = "Number of clusters";
	private static final String MAX_ITERATIONS_ALGORITHM_PARAMETER = "Max number of iterations";
	private static final String FUZZIFIER_PARAMETER = "Fuzzifier";
	private static final int DEFAULT_MAX_ITERATIONS = 100;

	private ClustererStatistics algorithmResults;
	private List<Instance> instances;
	private Dataset data;
	private int numberOfClusters = 3;
	private int fuzzifier = 2;
	private int numberOfInstances;
	private int numberOfAttributes;
	private int maxIterations = DEFAULT_MAX_ITERATIONS;
	private double[][] membershipFunction;
	private double[][] newMembershipFunction;
	private double[][] center;
	private DatasetService service;

	public FuzzyKMeans() {
	}

	@Override
	public void setParameters(Map<String, String> parameters) {
		for (Entry<String, String> entry : parameters.entrySet()) {
			String key = entry.getKey();
			if (key.equalsIgnoreCase(MAX_ITERATIONS_ALGORITHM_PARAMETER)) {
				try {
					maxIterations = Integer.parseInt(entry.getValue());
				} catch (NumberFormatException e) {
					throw new RuntimeException(
							MAX_ITERATIONS_ALGORITHM_PARAMETER
									+ " must be an integer.");
				}
			}

			if (key.equalsIgnoreCase(NUMBER_OF_CLUSTERS_PARAMETER)) {
				try {
					numberOfClusters = Integer.parseInt(entry.getValue());
				} catch (NumberFormatException e) {
					throw new RuntimeException(NUMBER_OF_CLUSTERS_PARAMETER
							+ " must be an integer.");
				}
			}

			if (key.equalsIgnoreCase(FUZZIFIER_PARAMETER)) {
				try {
					fuzzifier = Integer.parseInt(entry.getValue());
				} catch (NumberFormatException e) {
					throw new RuntimeException(FUZZIFIER_PARAMETER
							+ " must be an integer.");
				}
			}
		}
	}

	public static Map<String, String> getParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(NUMBER_OF_CLUSTERS_PARAMETER, String.valueOf(3));
		parameters.put(MAX_ITERATIONS_ALGORITHM_PARAMETER,
				String.valueOf(DEFAULT_MAX_ITERATIONS));
		parameters.put(FUZZIFIER_PARAMETER, String.valueOf(2));
		return parameters;
	}

	@Override
	public void clusterize(Dataset data) {
		long startTime = System.currentTimeMillis();

		initialize(data);

		for (int i = 0; i < maxIterations; i++) {
			computeNewCenters();
			computeNewWeights();

			if (closeEnoughly()) {
				break;
			}
			CopyMembershipFunction();
		}

		int[] result = new int[numberOfClusters];
		for (int instanceIndex = 0; instanceIndex < numberOfInstances; instanceIndex++) {
			double max = 0.0;
			int cluster = 0;

			for (int clusterIndex = 0; clusterIndex < numberOfClusters; clusterIndex++) {
				if (membershipFunction[clusterIndex][instanceIndex] > max) {
					max = membershipFunction[clusterIndex][instanceIndex];
					cluster = clusterIndex;
				}
			}
			result[cluster]++;
		}

		algorithmResults = new ClustererStatistics(result);
		algorithmResults.setTestTime(System.currentTimeMillis() - startTime);
		algorithmResults.setAlgorithmName("Fuzzy K-Means");
	}

	private void CopyMembershipFunction() {
		for (int clusterIndex = 0; clusterIndex < numberOfClusters; clusterIndex++) {
			membershipFunction[clusterIndex] = newMembershipFunction[clusterIndex]
					.clone();
		}
	}

	@Override
	public ClustererStatistics getStatistic() {
		return algorithmResults;
	}

	@Override
	public String getName() {
		return "FuzzyKMeans";
	}

	/*
	 * If the difference in membership function between current iteration and
	 * previous iteration is small enough we stop computation
	 */
	private boolean closeEnoughly() {
		double max = 0.0;
		for (int clusterIndex = 0; clusterIndex < numberOfClusters; clusterIndex++) {
			for (int instanceIndex = 0; instanceIndex < numberOfInstances; instanceIndex++) {
				double membershipValue = membershipFunction[clusterIndex][instanceIndex]
						- newMembershipFunction[clusterIndex][instanceIndex];
				if (Math.abs(membershipValue) > max) {
					max = Math.abs(membershipValue);
				}
			}
		}
		if (0 < max && max < 0.1) {
			return true;
		}
		return false;
	}

	/*
	 * Computes the weight of membership for each instance and each cluster in
	 * the current iteration
	 */
	private void computeNewWeights() {
		for (int instanceIndex = 0; instanceIndex < numberOfInstances; instanceIndex++) {
			double allClusterDistance = 0.0;
			for (int clusterIndex = 0; clusterIndex < numberOfClusters; clusterIndex++) {
				allClusterDistance += distance(instances.get(instanceIndex),
						center[clusterIndex]);
			}

			for (int clusterIndex = 0; clusterIndex < numberOfClusters; clusterIndex++) {
				double currentClusterDistance = distance(
						instances.get(instanceIndex), center[clusterIndex])
						* numberOfClusters;
				double delimiter = Math.pow(currentClusterDistance
						/ allClusterDistance, 2 / (fuzzifier - 1));

				if (delimiter != 0) {
					newMembershipFunction[clusterIndex][instanceIndex] = 1 / delimiter;
				} else {
					newMembershipFunction[clusterIndex][instanceIndex] = 1;
				}
			}
		}
	}

	/*
	 * Computes the Euclidean distance from given instance to given center
	 */
	private double distance(Instance instance, double[] center) {
		double sum = 0.0;
		for (int attributeIndex = 0; attributeIndex < center.length; attributeIndex++) {
			
			if (attributeIndex == service.classIndex()) {
				continue;
			}
			
			Value attribute = instance.getVector().get(attributeIndex);
			double attributeValue = 0;
			if (attribute instanceof NumericValue) {
				attributeValue = ((NumericValue) attribute).getValue();
			} else {
				attributeValue = ((NominalValue) attribute).getValue().hashCode();
			}
			sum += Math.pow(attributeValue - center[attributeIndex], 2);
		}

		double distance = Math.sqrt(sum);
		return distance;
	}

	/*
	 * Computes the centers for all clusters in current iteration
	 */
	private void computeNewCenters() {
		for (int clusterIndex = 0; clusterIndex < numberOfClusters; clusterIndex++) {
			for (int attributeIndex = 0; attributeIndex < numberOfAttributes; attributeIndex++) {
				double sum = 0.0;

				if (attributeIndex == service.classIndex()) {
					continue;
				}
				
				for (int instanceIndex = 0; instanceIndex < numberOfInstances; instanceIndex++) {
					Value attribute = instances.get(instanceIndex).getVector()
							.get(attributeIndex);
					double attributeValue = 0;
					if (attribute instanceof NumericValue) {
						attributeValue = ((NumericValue) attribute).getValue();
					} else {
						attributeValue = ((NominalValue) attribute).getValue().hashCode();
					}
					sum += Math
							.pow(membershipFunction[clusterIndex][instanceIndex],
									fuzzifier)
							* attributeValue;
				}

				double membershipSum = 0.0;
				for (int instanceIndex = 0; instanceIndex < numberOfInstances; instanceIndex++) {
					membershipSum += Math.pow(
							membershipFunction[clusterIndex][instanceIndex],
							fuzzifier);
				}

				center[clusterIndex][attributeIndex] = sum / membershipSum;
			}
		}
	}

	/*
	 * Initializes data and sets initial values to membership funcion
	 */
	private void initialize(Dataset data) {
		instances = data.getElements();
		numberOfInstances = instances.size();
		membershipFunction = new double[numberOfClusters][numberOfInstances];
		newMembershipFunction = new double[numberOfClusters][numberOfInstances];
		service = new DatasetService(data);
		numberOfAttributes = service.numberOfAttributes();
		center = new double[numberOfClusters][numberOfAttributes];
		

		int instanceIndex = 0;
		for (int clusterIndex = 0; clusterIndex < numberOfClusters; clusterIndex++) {
			membershipFunction[clusterIndex][instanceIndex] = 1;
			instanceIndex++;
			if (instanceIndex > numberOfInstances) {
				instanceIndex = 0;
			}
		}
	}

	@Override
	public Dataset getClusteredDataset() {
		return data;
	}
}

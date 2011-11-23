package spaska.classifiers.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import spaska.data.*;
import spaska.data.Attribute.ValueType;

public class ContinuousValueService {

	// Compare 2 instances according to a numeric attribute
	private static class InstanceComparator implements Comparator<Instance> {

		private int attributeIndex;

		public InstanceComparator(int i) {
			attributeIndex = i;
		}

		public void setAttributeIndex(int i) {
			attributeIndex = i;
		}

		public int compare(Instance firstInstance, Instance secondInstance) {
			Value first = firstInstance.getVector().get(attributeIndex);
			Value second = secondInstance.getVector().get(attributeIndex);
			if (first.getType() == ValueType.Unknown) {
				return -1;
			}
			if (second.getType() == ValueType.Unknown) {
				return 1;
			}
			return ((Double) first.getValue()).compareTo((Double) second
					.getValue());
		}
	}

	private DatasetService datasetService;
	private List<Instance> instances;
	private int attributeIndex;// sorting attribute
	// distribution[0][i] = number of classes of value i in the first split
	// distribution[1][i] = number of classes of value i in the second split
	private int[][] distribution;
	private int globalSplitIndex;
	private double classesEntropy;
	private double minEntropy;
	private boolean isCalculated;
	private InstanceComparator comparator;
	private int knownValueStart;// index of first instance with known value for
	// attribute
	private boolean isEmpty; // if service is an empty one

	private ContinuousValueService() {
		this.isEmpty = true;
	}

	public ContinuousValueService(DatasetService datasetService,
			List<Instance> instances, int attributeIndex, double classesEntropy) {
		this.attributeIndex = attributeIndex;
		this.classesEntropy = classesEntropy;
		this.isCalculated = false;
		this.datasetService = datasetService;
		this.comparator = new InstanceComparator(attributeIndex);
		if (datasetService != null && isContinuous(attributeIndex)) {
			this.instances = sortInstances(instances, attributeIndex);
		}
		this.knownValueStart = getFirstIndexOfKnownValue();
		this.isEmpty = false;
	}

	private boolean isContinuous(int attributeIndex) {
		return datasetService.getAttribute(attributeIndex).getType() == ValueType.Numeric;
	}

	// sort by ith attribute
	private List<Instance> sortInstances(List<Instance> instances, int i) {
		List<Instance> result = new ArrayList<Instance>(instances.size());
		Instance[] insts = new Instance[instances.size()];
		instances.toArray(insts);
		comparator.setAttributeIndex(i);
		Arrays.sort(insts, comparator);
		for (int k = 0; k < insts.length; k++) {
			result.add(insts[k]);
		}
		return result;
	}

	// when instances are sorted, unknown values become first in the list
	// therefore the first known value index should be set for a start
	private int getFirstIndexOfKnownValue() {
		int index = 0;
		while (index < instances.size()
				&& getValueAtIndex(index).getType() == ValueType.Unknown) {
			index++;
		}
		return index;
	}

	private int initDistribution() {
		distribution = new int[2][datasetService.numberOfClasses()];
		int start = knownValueStart;
		if (start >= instances.size()) {
			return instances.size() - 1;
		}
		double leftsideValue = getContinuousValue(start);
		int splitIndex = start, k = start + 1;
		while (k < instances.size() && leftsideValue == getContinuousValue(k)) {
			k++;
		}
		splitIndex = k - 1;
		for (int i = start; i <= splitIndex; i++) {
			distribution[0][getClassIntValue(i)]++;
		}
		for (int i = splitIndex + 1; i < instances.size(); i++) {
			distribution[1][getClassIntValue(i)]++;
		}
		return splitIndex;
	}

	private void calculate() {
		int splitIndex = initDistribution();
		int k = splitIndex + 1;
		int numBefore = getNumBeforeSplitPoint(splitIndex);
		int numAfter = getNumAfterSplitPoint(splitIndex);
		double leftsideValue;
		double firstHalfEntropy = Information.entropy(distribution[0],
				numBefore);
		double secondHalfEntropy = Information.entropy(distribution[1],
				numAfter);
		double currentAverage = Information.average(new double[] {
				firstHalfEntropy, secondHalfEntropy }, new int[] { numBefore,
				numAfter }, numBefore + numAfter);
		minEntropy = currentAverage;
		globalSplitIndex = splitIndex;
		while (k < instances.size()) {
			leftsideValue = getContinuousValue(k);
			while (k < instances.size()
					&& leftsideValue == getContinuousValue(k)) {
				splitIndex++;
				k++;
				int ind = getClassIntValue(splitIndex);
				distribution[0][ind]++;
				distribution[1][ind]--;
			}
			if (k >= instances.size()) {
				break;
			}
			numBefore = getNumBeforeSplitPoint(splitIndex);
			numAfter = getNumAfterSplitPoint(splitIndex);
			firstHalfEntropy = Information.entropy(distribution[0], numBefore);
			secondHalfEntropy = Information.entropy(distribution[1], numAfter);
			currentAverage = Information.average(new double[] {
					firstHalfEntropy, secondHalfEntropy }, new int[] {
					numBefore, numAfter }, numBefore + numAfter);
			// System.out.println(currentAverage);
			if (minEntropy > currentAverage) {
				minEntropy = currentAverage;
				globalSplitIndex = splitIndex;
			}
		}
	}

	// number of instances with known value before split index
	private int getNumBeforeSplitPoint(int splitIndex) {
		return splitIndex + 1 - knownValueStart;
	}

	// number of instances with known value after split index
	private int getNumAfterSplitPoint(int splitIndex) {
		return instances.size() - splitIndex - 1;
	}

	private Value getValueAtIndex(int i) {
		return instances.get(i).getVector().get(attributeIndex);
	}

	// i - index of the instance
	private double getContinuousValue(int i) {
		return (Double) instances.get(i).getVector().get(attributeIndex)
				.getValue();
	}

	private int getClassIntValue(int i) {
		return datasetService.intValue(datasetService.classIndex(),
				datasetService.getClass(instances.get(i)));
	}

	public int getSplitIndex() {
		if (!assertCalculation()) {
			return -1;
		}
		return globalSplitIndex;
	}

	public double getGainRatio() {
		if (!assertCalculation()) {
			return 0;
		}
		int numBefore = getNumBeforeSplitPoint(globalSplitIndex);
		int numAfter = getNumAfterSplitPoint(globalSplitIndex);
		return (classesEntropy - minEntropy)
				/ Information.entropy(new int[] { numBefore, numAfter },
						numBefore + numAfter);
	}

	public double getSplitValue() {
		if (!assertCalculation()) {
			return 0;
		}
		if (globalSplitIndex + 1 < instances.size()) {
			return (getContinuousValue(globalSplitIndex) + getContinuousValue(globalSplitIndex + 1)) / 2;
		} else {
			return getContinuousValue(instances.size() - 1);
		}
	}

	public int getAttributeIndex() {
		return attributeIndex;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	private boolean assertCalculation() {
		boolean result = true;
		try {
			if (!isCalculated && !isEmpty) {
				calculate();
				isCalculated = true;
			}
		} catch (Exception e) {
			result = false;
			isEmpty = true;
		}
		return !isEmpty && result;
	}

	// static factory
	public static ContinuousValueService createEmptyService() {
		ContinuousValueService cvs = new ContinuousValueService();
		return cvs;
	}
}

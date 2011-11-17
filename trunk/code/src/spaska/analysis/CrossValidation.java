package spaska.analysis;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import spaska.classifiers.IClassifier;
import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.NominalValue;
import spaska.data.Value;
import spaska.data.Attribute.ValueType;
import spaska.statistics.ClassifierStatistics;

public class CrossValidation implements IAnalyzer {

	public static Map<String, String> getParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(FOLDS, String.valueOf(10));
		return parameters;
	}

	private static final String FOLDS = "folds";

	private int folds;
	private Dataset data;
	private IClassifier classifier;
	private int seed;

	public CrossValidation(int folds, Dataset data, IClassifier classifier) {
		this.folds = folds;
		this.data = data;
		this.classifier = classifier;
	}

	public CrossValidation() {
		this.folds = 10;
	}

	public int getFolds() {
		return folds;
	}

	public void setFolds(int folds) {
		this.folds = folds;
	}

	public Dataset getData() {
		return data;
	}

	public void setData(Dataset data) {
		this.data = data;
	}

	public IClassifier getClassifier() {
		return classifier;
	}

	public void setClassifier(IClassifier classifier) {
		this.classifier = classifier;
	}

	public int getSeed() {
		return seed;
	}

	public void setSeed(int seed) {
		this.seed = seed;
	}

	@Override
	public ClassifierStatistics analyze(Dataset dataset) {
		setData(dataset);

		long startTime = System.currentTimeMillis();

		// how many elements will have every dataset
		int[] datasetsCount = new int[folds];

		List<Instance> instances = data.getElements();
		int instancesCount = instances.size();

		int instancesInGroup = instancesCount / folds;
		Arrays.fill(datasetsCount, instancesInGroup);
		int cnt = instancesInGroup * folds;
		int tmpCnt = 0;
		while (cnt++ < instancesCount) {
			datasetsCount[tmpCnt++]++;
		}

		Dataset[] datasets = data.getSubDataSets(datasetsCount, seed);
		int classIndex = data.getClassIndex();
		ValueType type = data.getAttributes().get(classIndex).getType();
		ClassifierStatistics result = new ClassifierStatistics(type);
		if (type == ValueType.Nominal) {
			result.setClassNames(data.getAllClassNamesArray());
		}
		result.setAlgorithmName(classifier.getName());

		for (int testInd = 0; testInd < folds; testInd++) {
			// build classifier for all test datasets
			// first merge them
			Dataset[] testDatasets = new Dataset[datasets.length - 1];
			tmpCnt = 0;
			Dataset testDataset = null;
			for (int i = 0; i < folds; i++) {
				if (i == testInd) {
					testDataset = datasets[i];
					continue;
				}
				testDatasets[tmpCnt++] = datasets[i];
			}
			Dataset trainDataset = Dataset.merge(testDatasets);

			classifier.buildClassifier(trainDataset);

			// test classifier
			for (Instance currentInstance : testDataset.getElements()) {
				Value v = classifier.classifyInstance(currentInstance);

				switch (v.getType()) {
				case Nominal:
					result.addNominalInfo(data.getClassName(currentInstance),
							((NominalValue) v).getValue());
					break;
				case Numeric:
					result.addNumericInfo((Double) data.getClassValue(
							currentInstance).getValue(), (Double) v.getValue());
					break;
				default:
					throw new RuntimeException("other attribute type added");
				}
			}
		}

		result.setTestTime(System.currentTimeMillis() - startTime);
		result.setAdditionalInfo(String.format(
				"Additional info :\n------------------\n%s", classifier.toString()));
		return result;
	}

	@Override
	public void setParameters(Map<String, String> parameters) {
		boolean ex = false;
		for (Entry<String, String> entry : parameters.entrySet()) {
			String param = entry.getKey();
			if (param.equalsIgnoreCase(FOLDS)) {
				try {
					folds = Integer.parseInt(entry.getValue());
				} catch (NumberFormatException e) {
					// do nothing because parameter k has default value
				}
			} else {
				ex = true;
			}
		}
		if (ex) {
			throw new IllegalArgumentException(
					"SimpleKMeans: unknown parameter (valid: K)");
		}
	}

}

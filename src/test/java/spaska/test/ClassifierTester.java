package spaska.test;

import spaska.analysis.CrossValidation;
import spaska.classifiers.IClassifier;
import spaska.data.Dataset;
import spaska.statistics.ClassifierStatistics;

public class ClassifierTester {
	private IClassifier classifier;
	private Dataset dataset;

	public ClassifierTester(IClassifier classifier) {
		this.classifier = classifier;
	}

	public static ClassifierTester forClassifier(IClassifier classifier) {
		return new ClassifierTester(classifier);
	}

	public ClassifierTester onDataset(String datasetPath) {
		this.dataset = DataSetResources.getDataSet(datasetPath);
		return this;
	}

	public ClassifierTester onDataset(Dataset dataset) {
		this.dataset = dataset;
		return this;
	}

	public ClassifierStatistics crossValidate(int folds) {
		return new CrossValidation(folds, dataset, classifier).analyze(dataset);
	}
}

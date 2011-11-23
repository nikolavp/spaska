package spaska.test;

import java.io.File;

import spaska.data.*;
import spaska.data.readers.ARFFInputReader;
import spaska.analysis.*;
import spaska.classifiers.*;
import spaska.statistics.Statistics;

public class Test {

	public static String[] fileNames = { "iris", "segment-challenge",
			"contact-lenses", "cpu", "cpu.with.vendor", "labor",
			"segment-test", "soybean", "weather", "weather.nominal" };

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//testAll();
		System.out.println(test("iris"));
	}

	public static String test(String fileName) {
		IClassifier classifier = getClassifier();
		String ss = "data" + File.separator + fileName + ".arff";
		// ss = "data" + File.separator + "segment-challenge.arff";
		// ss = "data" + File.separator + "soybean.arff";
		ARFFInputReader r = new ARFFInputReader(ss);
		Dataset dataset = r.buildDataset();
		// dataset.setClassIndex(dataset.getClassIndex()-1);
		CrossValidation cv = new CrossValidation(5, dataset, classifier);
		Statistics s = cv.analyze(dataset);
		return s.toString();
	}

	public static void testAll() {
		for (int i = 0; i < fileNames.length; i++) {
			try {
				System.out.println(test(fileNames[i]));
				System.out.println(fileNames[i] + " : OK");
			} catch (Exception e) {
				System.out.println(fileNames[i] + " : " + e.getMessage());
			}
		}
	}

	private static IClassifier getClassifier() {
		IClassifier classifier;
		KNN knn = new KNN();
		// knn.setParameters("k", "11");
		// knn.setParameters("weighted", "true");
		classifier = knn;
		//classifier = new ZeroR();
		//classifier = new OneR();
		//classifier = new DecisionTree();
		// classifier = new TwoLayerPerceptron();
		return classifier;
	}
}

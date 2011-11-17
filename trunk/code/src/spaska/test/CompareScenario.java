package spaska.test;

import java.io.File;

import spaska.analysis.PairedTTest;
import spaska.analysis.TestType;
import spaska.classifiers.*;
import spaska.data.Dataset;
import spaska.data.readers.ARFFInputReader;

public class CompareScenario {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		PairedTTest ptt = new PairedTTest();
		ptt.setAlpha(0.05); // alpha - error type I
		ptt.setTestType(TestType.TwoSided); // test type : OneSidedLessThan/
		// OneSidedGreaterThan/ TwoSided
		ptt.setClassifier1(getFirstClassifier());
		ptt.setClassifier2(getSecondClassifier());
		System.out.println(ptt.analyze(getData()).toString());
	}

	private static Dataset getData() {
		String fileName = "iris.arff";
		// fileName = "segment-challenge.arff";
		// fileName = "soybean.arff";
		String ss = "data" + File.separator + fileName;
		ARFFInputReader r = new ARFFInputReader(ss);
		return r.buildDataset();
	}

	private static IClassifier getFirstClassifier() {
		//return new ZeroR();
		return new KNN();
	}

	private static IClassifier getSecondClassifier() {
		//return new OneR();
		 return new DecisionTree();
	}

}

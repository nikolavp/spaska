package spaska.test;

import java.util.Set;

import spaska.classifiers.*;
import spaska.classifiers.util.Node;
import spaska.data.*;
import spaska.data.Attribute.ValueType;
import spaska.data.readers.*;
import spaska.statistics.*;

public class ClassifierTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		testDecisionTree();
		// testOneR();
	}

	public static void test1() {
		Dataset data = createDataset("./data/iris.arff");
		IClassifier c = createClassifier();
		Dataset trainingSet = getTrainingSet(data);
		Dataset testingSet = getTestingSet(data);
		train(c, trainingSet);
		ClassifierStatistics cs = test(c, testingSet);
		System.out.println(cs);
	}

	public static void testOneR() {
		String fileName = ".\\data\\iris.arff";
		// fileName = ".\\data\\labor.arff";
		// fileName = ".\\data\\weather.arff";
		// fileName = ".\\data\\weather.nominal.arff";
		// fileName = ".\\data\\segment-challenge.arff";
		// fileName = ".\\data\\soybean.arff";
		ARFFInputReader r = new ARFFInputReader(fileName);
		Dataset data = r.buildDataset();
		OneR oneR = new OneR();
		oneR.buildClassifier(data);
		System.out.println(oneR.toString());
	}

	public static void testDecisionTree() {
		String fileName = ".\\data\\iris.arff";
		// fileName = ".\\data\\labor.arff";
		// fileName = ".\\data\\weather.arff";
		// fileName = ".\\data\\weather.nominal.arff";
		// fileName = ".\\data\\segment-challenge.arff";
		// fileName = ".\\data\\soybean.arff";
		ARFFInputReader r = new ARFFInputReader(fileName);
		Dataset data = r.buildDataset();
		DecisionTree decisionT = new DecisionTree();
		decisionT.buildClassifier(data);

		/*
		 * decisionT.displayContinuousStatistics(data.getElements(), 0,
		 * infoService.classesEntropy);
		 * decisionT.displayContinuousStatistics(data.getElements(), 1,
		 * infoService.classesEntropy);
		 * decisionT.displayContinuousStatistics(data.getElements(), 2,
		 * infoService.classesEntropy);
		 * decisionT.displayContinuousStatistics(data.getElements(), 3,
		 * infoService.classesEntropy);
		 */
		System.out.println(decisionT.toString());
		decisionT.equals(new Object());
	}

	/*
	 * private void displayContinuousStatistics(List<Instance> list, int
	 * attributeIndex, double classesEntropy) { ContinuousValueService cvService
	 * = new ContinuousValueService( datasetService, list, attributeIndex,
	 * classesEntropy); System.out.format("\nStatistics for attribute <%d>\n",
	 * attributeIndex); System.out.format("Split Index : %d\n",
	 * cvService.getSplitIndex()); System.out.format("Split Value : %.4f\n",
	 * cvService.getSplitValue()); System.out.format("Gain Ratio  : %.4f\n",
	 * cvService.getGainRatio()); }
	 */

	private static Dataset createDataset(String path) {
		ARFFInputReader reader = new ARFFInputReader(path);
		return reader.buildDataset();
	}

	private static IClassifier createClassifier() {
		return new ZeroR();
	}

	private static void train(IClassifier classifier, Dataset trainingSet) {
		classifier.buildClassifier(trainingSet);
	}

	private static ClassifierStatistics test(IClassifier classifier,
			Dataset testingSet) {
		Set<Value> classDomain = testingSet.getAllClassNamesSet();
		String[] classNames = new String[classDomain.size()];
		int i = 0;
		for (Value v : classDomain) {
			classNames[i++] = v.getValue().toString();
		}
		ClassifierStatistics cs = new ClassifierStatistics(ValueType.Nominal);
		for (Instance inst : testingSet.getElements()) {
			cs.addNominalInfo(testingSet.getClassName(inst), classifier
					.classifyInstance(inst).getValue().toString());
		}
		return cs;
	}

	private static Dataset getTrainingSet(Dataset data) {
		return data;
	}

	private static Dataset getTestingSet(Dataset data) {
		return data;
	}
}

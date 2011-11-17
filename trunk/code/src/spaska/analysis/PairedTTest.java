package spaska.analysis;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import spaska.classifiers.IClassifier;
import spaska.data.Dataset;
import spaska.statistics.ClassifierStatistics;
import spaska.statistics.CompareStatistics;

public class PairedTTest implements ICompareAnalyzer, IStatisticalTest {

	// Parameters' names
	private static String alphaName = "Alpha (Error of type I)";
	private static String testTypeName = "Test Type (" +  Arrays.toString(TestType.values()) + ")";
	private static String foldsName = "Cross Validation folds";

	public static Map<String, String> getParameters() {
		Map<String, String> params = new HashMap<String, String>();
		params.put(alphaName, String.valueOf(IStatisticalTest.DEFAULT_ALPHA));
		params.put(testTypeName, IStatisticalTest.DEFAULT_TEST_TYPE.toString());
		params.put(foldsName, String.valueOf(10));
		return params;
	}

	// Private members
	private IClassifier firstClassifier;
	private IClassifier secondClassifier;
	private IStatisticalTest test;
	private int folds;

	public PairedTTest() {
		test = new TStatisticsTest();
		folds = 10;
	}

	// IStatisticalTest members ----------------------------------------
	public double getAlpha() {
		return test.getAlpha();
	}

	public void setAlpha(double alpha) {
		test.setAlpha(alpha);
	}

	public TestType getTestType() {
		return test.getTestType();
	}

	public void setTestType(TestType testType) {
		test.setTestType(testType);
	}

	@Override
	public boolean shouldRejectNull(double[] fstSample, double[] sndSample) {
		return test.shouldRejectNull(fstSample, sndSample);
	}

	@Override
	public String getName() {
		return "Paired T Test";
	}

	// -----------------------------------------------------------------------

	@Override
	public void setParameters(Map<String, String> parameters) {
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			String name = entry.getKey();
			String value = entry.getValue();
			if (name.equalsIgnoreCase(alphaName)) {
				setAlpha(Double.parseDouble(value));
				continue;
			}
			if (name.equalsIgnoreCase(testTypeName)) {
				setTestType(TestType.valueOf(value));
				continue;
			}
			if (name.equalsIgnoreCase(foldsName)) {
				setFolds(Integer.parseInt(value));
			}
		}
	}

	// -----------------------------------------------------------------------

	// IAnalyzer2 members ----------------------------------------------------
	public IClassifier getClassifier1() {
		return firstClassifier;
	}

	public void setClassifier1(IClassifier classifier1) {
		firstClassifier = classifier1;
	}

	public IClassifier getClassifier2() {
		return secondClassifier;
	}

	public void setClassifier2(IClassifier classifier2) {
		secondClassifier = classifier2;
	}

	@Override
	public CompareStatistics analyze(Dataset dataset) throws Exception {

		if (dataset == null) {
			throw new NullPointerException(
					"Paired T Test : dataset cannot be null");
		}

		CrossValidation firstValidator = createValidator(folds,
				firstClassifier, dataset);
		CrossValidation secondValidator = createValidator(folds,
				secondClassifier, dataset);

		double[] firstSuccessRates = new double[seeds.length];
		double[] secondSuccessRates = new double[seeds.length];
		long[] firstClassifierTimes = new long[seeds.length];
		long[] secondClassifierTimes = new long[seeds.length];

		testAlgorithm(firstValidator, dataset, firstSuccessRates,
				firstClassifierTimes);
		testAlgorithm(secondValidator, dataset, secondSuccessRates,
				secondClassifierTimes);

		CompareStatistics statistics = new CompareStatistics();
		statistics.setFirstClassifierName(firstClassifier.getName());
		statistics.setSecondClassifierName(secondClassifier.getName());
		statistics.setFirstSamplePopulation(firstSuccessRates);
		statistics.setSecondSamplePopulation(secondSuccessRates);
		statistics.setTimes1(firstClassifierTimes);
		statistics.setTimes2(secondClassifierTimes);
		statistics.setTest(test);
		statistics.setAlgorithmName("Paired T Test");

		return statistics;
	}

	// ---------------------------------------------------------------------------

	// creates a cross validator with given parameters
	private CrossValidation createValidator(int folds, IClassifier c,
			Dataset data) {
		CrossValidation validator = new CrossValidation();
		validator.setFolds(folds);
		validator.setClassifier(c);
		validator.setData(data);
		return validator;
	}

	// tests a classifier a preset number of times and updates statistics
	private void testAlgorithm(CrossValidation cv, Dataset data,
			double[] successRates, long[] times) {
		for (int i = 0; i < successRates.length; i++) {
			cv.setSeed(seeds[i]);
			ClassifierStatistics cs = cv.analyze(data);
			successRates[i] = cs.getGeneralPrecision();
			times[i] = cs.getTestTime();
		}
	}

	//Accessors & mutators -----------------------------------------------
	public IStatisticalTest getStatisticalTest() {
		return test;
	}

	public void setStatisticalTest(IStatisticalTest test) {
		this.test = test;
	}

	public int getFolds() {
		return folds;
	}

	public void setFolds(int folds) {
		this.folds = folds;
	}
	//----------------------------------------------------------------------
}

package spaska.analysis;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jsc.datastructures.PairedData;
import jsc.onesample.WilcoxonTest;
import jsc.tests.H1;
import spaska.classifiers.IClassifier;
import spaska.data.Dataset;
import spaska.statistics.ClassifierStatistics;
import spaska.statistics.CompareStatistics;

public class Wilcoxon implements ICompareAnalyzer, IStatisticalTest {

	private static final String nameAlpha = "Alpha (error of type I)";
	private static final String nameTestType = "Test Type (" + Arrays.toString(TestType.values()) + ")";
	private static final String nameFolds = "Number of folds in Cross Validation";

	public static Map<String, String> getParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(nameAlpha, String.valueOf(0.05));
		parameters.put(nameTestType, TestType.TwoSided.toString());
		parameters.put(nameFolds, String.valueOf(10));

		return parameters;
	}

	private IClassifier classifier1;
	private IClassifier classifier2;

	/**
	 * Error of type 'I' which determines the significance level. If the pValue
	 * is below alpha, we can reject the null hypothesis. Otherwise we cannot.
	 */
	private double alpha = 0.05;
	private TestType testType = TestType.TwoSided;
	private int folds = 10;

	private CompareStatistics statistic;

	@Override
	public CompareStatistics analyze(Dataset dataSet) throws Exception {
		
		if(dataSet == null){
			throw new NullPointerException("Wilcoxon: The dataSet should be set");
		}

		statistic = new CompareStatistics();
		statistic.setTest(this);
		statistic.setFirstClassifierName(classifier1.getName());
		statistic.setSecondClassifierName(classifier2.getName());
		double[] fstSample = new double[seeds.length];
		long[] fstTime = new long[seeds.length];
		double[] sndSample = new double[seeds.length];
		long[] sndTime = new long[seeds.length];

		for (int i = 0; i < seeds.length; i++) {
			CrossValidation cv1 = new CrossValidation();
			cv1.setFolds(folds);
			cv1.setSeed(seeds[i]);
			cv1.setClassifier(classifier1);
			cv1.setData(dataSet);

			ClassifierStatistics cs1 = cv1.analyze(dataSet);
			fstSample[i] = cs1.getGeneralPrecision();
			fstTime[i] = cs1.getTestTime();

			CrossValidation cv2 = new CrossValidation();
			cv2.setFolds(folds);
			cv2.setSeed(seeds[i]);
			cv2.setClassifier(classifier2);
			cv2.setData(dataSet);

			ClassifierStatistics cs2 = cv2.analyze(dataSet);
			sndSample[i] = cs2.getGeneralPrecision();
			sndTime[i] = cs2.getTestTime();
		}

		statistic.setFirstSamplePopulation(fstSample);
		statistic.setSecondSamplePopulation(sndSample);
		statistic.setTimes1(fstTime);
		statistic.setTimes2(sndTime);

		return statistic;
	}

	// private double getMinSumRanks(double[] observ1, double[] observ2) {
	// Double[] z = new Double[observ1.length];
	//
	// for (int i = 0; i < z.length; i++) {
	// z[i] = observ1[i] - observ2[i];
	// }
	//
	// Arrays.sort(z, new AbsCompare());
	//
	// double[] R = rank(z);
	//
	// double posW = 0, negW = 0;
	//
	// for (int i = 0; i < R.length; i++) {
	// if (R[i] < 0) {
	// negW += R[i];
	// } else {
	// posW += R[i];
	// }
	// }
	//
	// return (Math.abs(negW) < posW ? Math.abs(negW) : posW);
	// }

	// private double[] rank(Double[] z) {
	// double[] Rank = new double[z.length];
	//
	// int r = 1;
	//
	// int i = 0;
	// while (z[i] == 0) {
	// Rank[i++] = 0;
	// }
	//
	// while (i < Rank.length) {
	// int startR = r;
	// int currentI = i++;
	// while (i < Rank.length && Math.abs(z[i]) == Math.abs(z[currentI])) {
	// r++;
	// i++;
	// }
	// double avg = (startR + r) / 2;
	//
	// for (int j = currentI; j < i; j++) {
	// Rank[j] = avg * (z[j] < 0 ? -1 : 1);
	// }
	// r++;
	//
	// }
	//
	// return Rank;
	// }

	@Override
	public IClassifier getClassifier1() {
		return classifier1;
	}

	@Override
	public IClassifier getClassifier2() {
		return classifier2;
	}

	@Override
	public void setClassifier1(IClassifier classifier1) {
		this.classifier1 = classifier1;

	}

	@Override
	public void setClassifier2(IClassifier classifier2) {
		this.classifier2 = classifier2;

	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public TestType getTestType() {
		return testType;
	}

	public void setTestType(TestType testType) {
		this.testType = testType;
	}

	@Override
	public boolean shouldRejectNull(double[] fstSample, double[] sndSample) {

		WilcoxonTest wTest = null;

		if (testType == TestType.OneSidedGreaterThan) {
			wTest = new WilcoxonTest(new PairedData(fstSample, sndSample),
					H1.GREATER_THAN,true);
		} else {
			if (testType == TestType.OneSidedLessThan) {
				wTest = new WilcoxonTest(new PairedData(fstSample, sndSample),
						H1.LESS_THAN,true);
			} else {
				wTest = new WilcoxonTest(new PairedData(fstSample, sndSample),
						H1.NOT_EQUAL, true);
			}
		}

		double probability = wTest.approxSP()*10;

		boolean shouldReject = false;

		if (testType == TestType.TwoSided) { // (!=) case
			if (alpha / 2 < probability) {
				shouldReject = true;
			}
		} else {

			if (alpha < probability) {
				shouldReject = true;
			}
		}
		return shouldReject;

	}

	public String getName() {
		return "Wilcoxon Test";
	}

	@Override
	public void setParameters(Map<String, String> parameters) {
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			setParameters(entry.getKey(), entry.getValue());
		}
	}

	public void setParameters(String paramName, String paramValue) {
		if (paramName.equalsIgnoreCase(nameAlpha)) {
			setAlpha(Double.parseDouble(paramValue));
			return;
		}

		if (paramName.equalsIgnoreCase(nameFolds)) {
			setFolds(Integer.parseInt(paramValue));
			return;
		}

		if (paramName.equalsIgnoreCase(nameTestType)) {
			setTestType(TestType.valueOf(paramValue));
			return;
		}
		throw new IllegalArgumentException("Wilcoxon: unknown parameter:"
				+ paramName);
	}

	public int getFolds() {
		return folds;
	}

	public void setFolds(int folds) {
		this.folds = folds;
	}

	public CompareStatistics getStatistic() {
		return statistic;
	}

	public void setStatistic(CompareStatistics statistic) {
		this.statistic = statistic;
	}
}

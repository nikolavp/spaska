package spaska.analysis;

import DistLib.t;

public class TStatisticsTest implements IStatisticalTest {

	/**
	 * Error of type 'I' which determines the significance level. If the pValue
	 * is below alpha, we can reject the null hypothesis. Otherwise we cannot.
	 */
	private double alpha;

	private TestType testType;

	// Constructors
	public TStatisticsTest() {
		this(DEFAULT_ALPHA, DEFAULT_TEST_TYPE);
	}

	public TStatisticsTest(double alpha, TestType testType) {
		this.alpha = alpha;
		this.testType = testType;
	}

	// Presentation members
	public double getAlpha() {
		return this.alpha;
	}

	public void setAlpha(double alpha) {
		if (alpha > 0 && alpha < 0.5) {
			this.alpha = alpha;
		} else {
			this.alpha = DEFAULT_ALPHA;
		}
	}

	public TestType getTestType() {
		return this.testType;
	}

	public void setTestType(TestType testType) {
		this.testType = testType;
	}

	// Private helpers
	private boolean haveEqualSize(double[] fstSample, double[] sndSample) {
		return
			fstSample != null && 
			sndSample != null &&
			fstSample.length == sndSample.length;
	}

	private double[] getDifferences(double[] fstSample, double[] sndSample) {
		double[] differences = new double[fstSample.length];
		for (int i = 0; i < fstSample.length; i++) {
			differences[i] = fstSample[i] - sndSample[i];
		}
		return differences;
	}

	// Public methods
	/**
	 * Decides if the Null hypothesis can be rejected according to the preset
	 * level of significance (1-alpha)
	 * 
	 * @param tResult
	 *            the value for the T distribution random variable (t statistic)
	 * @param df
	 *            degrees of freedom
	 * @return whether or not to reject H0
	 */
	public boolean shouldRejectNull(double tResult, int df) {
		double quantile = 0;
		boolean shouldReject = false;
		if (testType == TestType.TwoSided) { // (!=) case
			quantile = t.quantile(alpha / 2, df);
			if (Math.abs(tResult) > Math.abs(quantile)) {
				shouldReject = true;
			}
		} else {
			if (testType == TestType.OneSidedLessThan) {// (<) case
				quantile = t.quantile(alpha, df);
				if (tResult < quantile) {
					shouldReject = true;
				}
			} else {// (>) case
				quantile = t.quantile(1 - alpha, df);
				if (tResult > quantile) {
					shouldReject = true;
				}
			}
		}
		return shouldReject;
	}

	public boolean shouldRejectNull(double[] fstSample, double[] sndSample) {
		double tResult = getT(fstSample, sndSample);
		return shouldRejectNull(tResult, fstSample.length - 1);
	}

	/**
	 * The random variable (mean - a)/(dev/n) has a T distribution. The method
	 * calculates the value for this variable given the two sample populations.
	 * Since we are using a paired test, the value for a is 0.0
	 * 
	 * @param fstSample
	 *            first sample population
	 * @param sndSample
	 *            second sample population
	 * @return calculated t statistic
	 */
	public double getT(double[] fstSample, double[] sndSample) {
		if (!haveEqualSize(fstSample, sndSample)) {
			throw new IllegalArgumentException();
		}
		double[] differences = getDifferences(fstSample, sndSample);
		double mean = EmpiricalTest.getMean(differences);
		double dev = EmpiricalTest.getStandardDeviation(differences);
		double tResult = ((mean - 0.0) * Math.sqrt(fstSample.length)) / dev;
		return tResult;
	}

	/**
	 * Calculates the p-value for the current test. If the p-value is less than
	 * alpha, we can reject the null hypothesis
	 * 
	 * @param fstSample
	 *            first random sample
	 * @param sndSample
	 *            second random sample
	 * @return p-value
	 */
	public double getPValue(double[] fstSample, double[] sndSample) {
		double tResult = getT(fstSample, sndSample);
		double df = fstSample.length - 1;
		double pValue = 0;
		if (testType == TestType.TwoSided) { // (!=) case
			pValue = t.cumulative(-Math.abs(tResult), df) * 2;
		} else {
			if (testType == TestType.OneSidedLessThan) {// (<) case
				pValue = t.cumulative(tResult, df);
			} else {// (>) case
				pValue = 1 - t.cumulative(tResult, df);
			}
		}
		return pValue;
	}

    public String getName() {
        return "Paired T-Test";
    }

}

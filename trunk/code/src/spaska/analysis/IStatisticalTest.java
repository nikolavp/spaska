package spaska.analysis;

public interface IStatisticalTest {

	public static final double		DEFAULT_ALPHA		= 0.05;
	public static final TestType	DEFAULT_TEST_TYPE	= TestType.TwoSided;
	public static final int[]		seeds				= { 23, 50, 48, 17, 29, 10, 76, 49, 15, 99 };

	/** Error type 'I' */
	double getAlpha();

	/** Hypothesis test type - one-sided (<,>) or two-sided (!=) */
	TestType getTestType();

	/** Sets alpha */
	void setAlpha(double alpha);

	/** Sets test type */
	void setTestType(TestType testType);

	/** Checks if the null hypothesis should be rejected */
	boolean shouldRejectNull(double[] fstSample, double[] sndSample);

	/** for statistics purposes */
	public String getName();

}

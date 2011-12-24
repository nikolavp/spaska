package spaska.analysis;

/**
 * An interface representing statistical test analyzers that compare two
 * classifiers. The model that is used is based on the statistical hypothesis.
 * The idea is that the null hypothesis states, that the expectation and the
 * median are statistically equivalent. The alternative hypothesis can be from
 * many types @see {@link TestType}
 */
public interface IStatisticalTest {
    /**
     * The default alpha in the statistical test that should be used in the
     * implementations.
     */
    public static final double DEFAULT_ALPHA = 0.05;
    /**
     * The default test type that should be used in the implementations.
     */
    public static final TestType DEFAULT_TEST_TYPE = TestType.TwoSided;
    /**
     * Convenient seeds that can be used when building the crossvalidation
     * analyzer in implementations.
     */
    public static final int[] seeds = { 23, 50, 48, 17, 29, 10, 76, 49, 15, 99 };

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

package spaska.analysis;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    double DEFAULT_ALPHA = 0.05;
    /**
     * The default test type that should be used in the implementations.
     */
    TestType DEFAULT_TEST_TYPE = TestType.TwoSided;
    /**
     * Convenient seeds that can be used when building the crossvalidation
     * analyzer in implementations.
     */
    List<Integer> SEEDS = Collections.unmodifiableList(Arrays.asList(23, 50,
            48, 17, 29, 10, 76, 49, 15, 99));

    /**
     * Returns the value behind error type 'I'.
     * 
     * @return the alpha value that represents the error type 'I'
     */
    double getAlpha();

    /**
     * Hypothesis test type.
     * 
     * @see TestType
     * @return the TestType that will be used for the statistical hypothesis
     */
    TestType getTestType();

    /**
     * Sets alpha for error type 'I'.
     * 
     * @param alpha
     *            the alpha value that represents the error type 'I'
     */
    void setAlpha(double alpha);

    /**
     * Sets the hypothesis test type.
     * 
     * @param testType
     *            the test type to be used in the statistical test
     */
    void setTestType(TestType testType);

    /**
     * Checks if the null hypothesis should be rejected.
     * 
     * @param fstSample
     *            the results from the first classifier
     * @param sndSample
     *            the results from the second classifier
     * @return if the null hypothesis should be rejected
     */
    boolean shouldRejectNull(double[] fstSample, double[] sndSample);

    /**
     * Returns the name of the algorithm that will be used.
     * 
     * @return the name of the algorithm that will test the classifiers
     */
    String getName();

}

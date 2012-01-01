package spaska.analysis;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import spaska.classifiers.IClassifier;
import spaska.data.Dataset;
import spaska.statistics.ClassifierStatistics;
import spaska.statistics.CompareStatistics;

/**
 * A paired T test analyzer that can be used to compare to classifiers.
 * 
 * <p>
 * This compares the mean from a number of classifiers crossvalidations.
 * Depending on the test type that was set, this test can be one-sided or
 * two-sided.
 * </p>
 */
public final class PairedTTest implements ICompareAnalyzer, IStatisticalTest {

    private static final int DEFAULT_FOLD_VALUE = 10;
    // Parameters' names
    private static String alphaName = "Alpha (Error of type I)";
    private static String testTypeName = "Test Type ("
            + Arrays.toString(TestType.values()) + ")";
    private static String foldsName = "Cross Validation folds";

    /**
     * Get the parameters for this analyzer.
     * 
     * @return a map of parameters and their values
     */
    public static Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(alphaName, String.valueOf(IStatisticalTest.DEFAULT_ALPHA));
        params.put(testTypeName, IStatisticalTest.DEFAULT_TEST_TYPE.toString());
        params.put(foldsName, String.valueOf(DEFAULT_FOLD_VALUE));
        return params;
    }

    // Private members
    private IClassifier firstClassifier;
    private IClassifier secondClassifier;
    private IStatisticalTest test;
    private int folds;

    /**
     * Default constructor.
     */
    public PairedTTest() {
        test = new TStatisticsTest();
        folds = DEFAULT_FOLD_VALUE;
    }

    @Override
    public double getAlpha() {
        return test.getAlpha();
    }

    @Override
    public void setAlpha(double alpha) {
        test.setAlpha(alpha);
    }

    @Override
    public TestType getTestType() {
        return test.getTestType();
    }

    @Override
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

    @Override
    public IClassifier getClassifier1() {
        return firstClassifier;
    }

    @Override
    public void setClassifier1(IClassifier classifier1) {
        firstClassifier = classifier1;
    }

    @Override
    public IClassifier getClassifier2() {
        return secondClassifier;
    }

    @Override
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

        double[] firstSuccessRates = new double[SEEDS.length];
        double[] secondSuccessRates = new double[SEEDS.length];
        long[] firstClassifierTimes = new long[SEEDS.length];
        long[] secondClassifierTimes = new long[SEEDS.length];

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

    /**
     * Creates a cross validator with given parameters.
     * 
     * @return the new crossvalidation
     */
    private CrossValidation createValidator(int crossValidationFolds,
            IClassifier c, Dataset data) {
        CrossValidation validator = new CrossValidation();
        validator.setFolds(crossValidationFolds);
        validator.setClassifier(c);
        validator.setData(data);
        return validator;
    }

    /**
     * Tests a classifier a preset number of times and updates statistics.
     * 
     * @param cv
     *            the cross validation object
     * @param data
     *            the dataset for the test
     * @param successRates
     *            the success rates array
     * @param times
     *            the time for every run
     */
    private void testAlgorithm(CrossValidation cv, Dataset data,
            double[] successRates, long[] times) {
        for (int i = 0; i < successRates.length; i++) {
            cv.setSeed(SEEDS[i]);
            ClassifierStatistics cs = cv.analyze(data);
            successRates[i] = cs.getGeneralPrecision();
            times[i] = cs.getTestTime();
        }
    }

    /**
     * Get the statistical test behind this pairedTTest.
     * 
     * @return the statistical test behind this paredTTest
     */
    public IStatisticalTest getStatisticalTest() {
        return test;
    }

    /**
     * Set the statistical test that will be used for the pairedTTest.
     * 
     * @param test
     *            the new statistical test that will be used for pairedTTest
     */
    public void setTest(IStatisticalTest test) {
        this.test = test;
    }

    /**
     * Get the number of folds that will be used when building the
     * crossvalidation objects.
     * 
     * @return the folds number for the crossvalidation
     */
    public int getFolds() {
        return folds;
    }

    /**
     * Set the new value that will be used when building the crossvalidation
     * objects.
     * 
     * @param folds
     *            the new folds number for crossvalidation
     */
    public void setFolds(int folds) {
        this.folds = folds;
    }
}

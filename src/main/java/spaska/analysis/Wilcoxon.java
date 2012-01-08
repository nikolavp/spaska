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

/**
 * An analyzer that implements the wilcoxon test. This test should be used if we
 * know that the distribution of the results is Normal. Otherwise the
 * {@link PairedTTest} should be used.
 */
public final class Wilcoxon implements ICompareAnalyzer, IStatisticalTest {

    private static final int TEN = 10;
    private static final int DEFAULT_NUMBER_OF_FOLDS = 10;
    private static final String NAME_ALPHA = "Alpha (error of type I)";
    private static final String NAME_TEST_TYPE = "Test Type ("
            + Arrays.toString(TestType.values()) + ")";
    private static final String NAME_FOLDS = 
            "Number of folds in Cross Validation";

    /**
     * Get the parameters for this analyzer.
     * 
     * @return a map of the parameters
     */
    public static Map<String, String> getParameters() {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(NAME_ALPHA, String.valueOf(DEFAULT_ALPHA));
        parameters.put(NAME_TEST_TYPE, TestType.TwoSided.toString());
        parameters.put(NAME_FOLDS, String.valueOf(DEFAULT_NUMBER_OF_FOLDS));

        return parameters;
    }

    private IClassifier classifier1;
    private IClassifier classifier2;

    /**
     * Error of type 'I' which determines the significance level. If the pValue
     * is below alpha, we can reject the null hypothesis. Otherwise we cannot.
     */
    private double alpha = DEFAULT_ALPHA;
    private TestType testType = TestType.TwoSided;
    private int folds = DEFAULT_NUMBER_OF_FOLDS;

    private CompareStatistics statistic;

    @Override
    public CompareStatistics analyze(Dataset dataSet) throws Exception {

        if (dataSet == null) {
            throw new NullPointerException(
                    "Wilcoxon: The dataSet should be set");
        }

        statistic = new CompareStatistics();
        statistic.setTest(this);
        statistic.setFirstClassifierName(classifier1.getName());
        statistic.setSecondClassifierName(classifier2.getName());
        double[] fstSample = new double[SEEDS.length];
        long[] fstTime = new long[SEEDS.length];
        double[] sndSample = new double[SEEDS.length];
        long[] sndTime = new long[SEEDS.length];

        for (int i = 0; i < SEEDS.length; i++) {
            CrossValidation cv1 = new CrossValidation();
            cv1.setFolds(folds);
            cv1.setSeed(SEEDS[i]);
            cv1.setClassifier(classifier1);
            cv1.setData(dataSet);

            ClassifierStatistics cs1 = cv1.analyze(dataSet);
            fstSample[i] = cs1.getGeneralPrecision();
            fstTime[i] = cs1.getTestTime();

            CrossValidation cv2 = new CrossValidation();
            cv2.setFolds(folds);
            cv2.setSeed(SEEDS[i]);
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

    @Override
    public double getAlpha() {
        return alpha;
    }

    @Override
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public TestType getTestType() {
        return testType;
    }

    @Override
    public void setTestType(TestType testType) {
        this.testType = testType;
    }

    @Override
    public boolean shouldRejectNull(double[] fstSample, double[] sndSample) {

        WilcoxonTest wTest = null;

        if (testType == TestType.OneSidedGreaterThan) {
            wTest = new WilcoxonTest(new PairedData(fstSample, sndSample),
                    H1.GREATER_THAN, true);
        } else {
            if (testType == TestType.OneSidedLessThan) {
                wTest = new WilcoxonTest(new PairedData(fstSample, sndSample),
                        H1.LESS_THAN, true);
            } else {
                wTest = new WilcoxonTest(new PairedData(fstSample, sndSample),
                        H1.NOT_EQUAL, true);
            }
        }

        double probability = wTest.approxSP() * TEN;

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

    @Override
    public String getName() {
        return "Wilcoxon Test";
    }

    @Override
    public void setParameters(Map<String, String> parameters) {
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            setParameters(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Set the new values for the parameters of this analyzer.
     * 
     * @param paramName
     *            the new parameter name
     * @param paramValue
     *            the parameter value behind the name
     */
    public void setParameters(String paramName, String paramValue) {
        if (paramName.equalsIgnoreCase(NAME_ALPHA)) {
            setAlpha(Double.parseDouble(paramValue));
            return;
        }

        if (paramName.equalsIgnoreCase(NAME_FOLDS)) {
            setFolds(Integer.parseInt(paramValue));
            return;
        }

        if (paramName.equalsIgnoreCase(NAME_TEST_TYPE)) {
            setTestType(TestType.valueOf(paramValue));
            return;
        }
        throw new IllegalArgumentException("Wilcoxon: unknown parameter:"
                + paramName);
    }

    /**
     * Get the number of folds that will be used for crossvalidation objects.
     * 
     * @return the folds for the crossvalidation objects
     */
    public int getFolds() {
        return folds;
    }

    /**
     * Set the number of folds for the crossvalidation objects.
     * 
     * @param folds
     *            the new folds value for the crossvalidation objects
     */
    public void setFolds(int folds) {
        this.folds = folds;
    }

    /**
     * Get the statistics after for this analysis.
     * 
     * @return the statistics for the analysis
     */
    public CompareStatistics getStatistic() {
        return statistic;
    }

    /**
     * Set the statistic results.
     * 
     * @param statistic
     *            the resulting statistic for this analyzer
     */
    public void setStatistic(CompareStatistics statistic) {
        this.statistic = statistic;
    }
}

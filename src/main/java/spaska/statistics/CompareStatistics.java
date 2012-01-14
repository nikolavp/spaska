package spaska.statistics;

import spaska.analysis.EmpiricalTest;
import spaska.analysis.IStatisticalTest;

/**
 * Statistics for comparison between two classifiers.
 */
public final class CompareStatistics extends Statistics {

    private String firstClassifierName, secondClassifierName; // classifier
                                                              // names
    private double[] firstSamplePopulation, secondSamplePopulation; // precisions
    private long[] times1, times2; // test times

    /**
     * Set the time for the first classifier.
     * 
     * @param times1
     *            the times for every fold when running the first classifier
     */
    public void setTimes1(long[] times1) {
        this.times1 = times1;
    }

    /**
     * Set the time for the second classifier.
     * 
     * @param times2
     *            the times for every fold when running the second classifier
     */

    public void setTimes2(long[] times2) {
        this.times2 = times2;
    }

    private IStatisticalTest test; // statistical test applied

    /**
     * Set the testing method to compare the classifiers.
     * 
     * @param test
     *            the testing method to compare the classifiers
     */
    public void setTest(IStatisticalTest test) {
        this.test = test;
        setModified(true);
    }

    /**
     * Set the name of the first classifier.
     * 
     * @param firstClassifierName
     *            the name of the first classifier
     */
    public void setFirstClassifierName(String firstClassifierName) {
        this.firstClassifierName = firstClassifierName;
        setModified(true);
    }

    /**
     * Set the precision of the first classifier.
     * 
     * @param firstSamplePopulation
     *            the precision of the first classifier
     */
    public void setFirstSamplePopulation(double[] firstSamplePopulation) {
        this.firstSamplePopulation = firstSamplePopulation;
        setModified(true);
    }

    /**
     * Set the name of the second classifier.
     * 
     * @param secondClassifierName
     *            the name of the second classifier
     */
    public void setSecondClassifierName(String secondClassifierName) {
        this.secondClassifierName = secondClassifierName;
        setModified(true);
    }

    /**
     * Set the precision of the second classifier.
     * 
     * @param secondSamplePopulation
     *            the precision of the second classifier
     */
    public void setSecondSamplePopulation(double[] secondSamplePopulation) {
        this.secondSamplePopulation = secondSamplePopulation;
        setModified(true);
    }

    private void appendPrecisions(double[] sample, long[] times,
            StringBuilder result) {
        result.append("Tests data [Presision -> Test Time]:\n");
        for (int i = 0; i < sample.length; i++) {
            result.append(String.format("Test %3d:  [%.6f -> %s]\n", i + 1,
                    sample[i], timeToString(times[i])));

        }
    }

    @Override
    protected void generateInfo() {

        if (test == null || firstClassifierName == null
                || secondClassifierName == null
                || firstSamplePopulation == null
                || secondSamplePopulation == null || times1 == null
                || times2 == null) {
            throw new IllegalStateException(
                    "Set all parameters of the compare statistics!");
        }

        StringBuilder result = new StringBuilder();
        result.append("================================================\n");
        result.append("Comparison between classifiers\n");
        result.append("------------------------------------------------\n");
        result.append("Classifier 1\n");
        result.append("Name: " + firstClassifierName + "\n");
        appendPrecisions(firstSamplePopulation, times1, result);
        result.append(String.format("Mean: %.6f\n",
                EmpiricalTest.getMean(firstSamplePopulation)));
        result.append(String.format("Standard deviation: %.6f\n",
                EmpiricalTest.getStandardDeviation(firstSamplePopulation)));
        result.append("------------------------------------------------\n");
        result.append("Classifier 2\n");
        result.append("Name: " + secondClassifierName + "\n");
        appendPrecisions(secondSamplePopulation, times2, result);
        result.append(String.format("Mean: %.6f\n",
                EmpiricalTest.getMean(secondSamplePopulation)));
        result.append(String.format("Standard deviation: %.6f\n",
                EmpiricalTest.getStandardDeviation(secondSamplePopulation)));
        result.append("------------------------------------------------\n");
        result.append("Statistical test: " + test.getName() + "\n");
        result.append("Type: " + test.getTestType() + "\n");
        result.append(String.format("Alpha = %.6f\n", test.getAlpha()));
        String accept = test.shouldRejectNull(firstSamplePopulation,
                secondSamplePopulation) ? "rejected" : "accepted";
        result.append("Null hypothesis:  " + accept + "\n");
        result.append("================================================\n");
        setInfo(result.toString());
        setModified(false);
    }

}

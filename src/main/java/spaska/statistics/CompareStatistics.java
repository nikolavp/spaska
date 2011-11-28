package spaska.statistics;

import spaska.analysis.*;

/* statistics for comparison between two classifiers */
public class CompareStatistics extends Statistics {

    private String firstClassifierName, secondClassifierName; //classifier names
    private double[] firstSamplePopulation, secondSamplePopulation; //precisions
    private long[] times1, times2; //test times

    public void setTimes1(long[] times1) {
        this.times1 = times1;
    }

    public void setTimes2(long[] times2) {
        this.times2 = times2;
    }
    private IStatisticalTest test; //statistical test applied

    public void setTest(IStatisticalTest test) {
        this.test = test;
        modified = true;
    }

    public void setFirstClassifierName(String firstClassifierName) {
        this.firstClassifierName = firstClassifierName;
        modified = true;
    }

    public void setFirstSamplePopulation(double[] firstSamplePopulation) {
        this.firstSamplePopulation = firstSamplePopulation;
        modified = true;
    }

    public void setSecondClassifierName(String secondClassifierName) {
        this.secondClassifierName = secondClassifierName;
        modified = true;
    }

    public void setSecondSamplePopulation(double[] secondSamplePopulation) {
        this.secondSamplePopulation = secondSamplePopulation;
        modified = true;
    }

    private void appendPrecisions(double[] sample, long[] times, StringBuilder result) {
        result.append("Tests data [Presision -> Test Time]:\n");
        for (int i = 0; i < sample.length; i++) {
            result.append(String.format("Test %3d:  [%.6f -> %s]\n", i+1, sample[i],  timeToString(times[i])));
            
        }
    }

    @Override
    protected void generateInfo() {

        if (test == null || firstClassifierName == null || secondClassifierName == null ||
                firstSamplePopulation == null || secondSamplePopulation == null ||
                times1 == null || times2 == null) {
            throw new IllegalStateException("Set all parameters of the compare statistics!");
        }

        StringBuilder result = new StringBuilder();
        result.append("================================================\n");
        result.append("Comparison between classifiers\n");
        result.append("------------------------------------------------\n");
        result.append("Classifier 1\n");
        result.append("Name: " + firstClassifierName + "\n");
        appendPrecisions(firstSamplePopulation, times1, result);
        result.append(String.format("Mean: %.6f\n", EmpiricalTest.getMean(firstSamplePopulation)));
        result.append(String.format("Standard deviation: %.6f\n", EmpiricalTest.getStandardDeviation(firstSamplePopulation)));
        result.append("------------------------------------------------\n");
        result.append("Classifier 2\n");
        result.append("Name: " + secondClassifierName + "\n");
        appendPrecisions(secondSamplePopulation, times2, result);
        result.append(String.format("Mean: %.6f\n", EmpiricalTest.getMean(secondSamplePopulation)));
        result.append(String.format("Standard deviation: %.6f\n", EmpiricalTest.getStandardDeviation(secondSamplePopulation)));
        result.append("------------------------------------------------\n");
        result.append("Statistical test: " + test.getName() + "\n");
        result.append("Type: " + test.getTestType() + "\n");
        result.append(String.format("Alpha = %.6f\n", test.getAlpha()));
        String accept = test.shouldRejectNull(firstSamplePopulation, secondSamplePopulation) ? "rejected" : "accepted";
        result.append("Null hypothesis:  " + accept + "\n");
        result.append("================================================\n");
        info = result.toString();
        modified = false;
    }

    public static void main(String[] args) {
        CompareStatistics s = new CompareStatistics();
        double[] d1 = {1.2, 3.4, 0.54, 0.78, 1.2, 3.4, 0.54, 0.78, 1.2, 3.4, 0.54, 0.78};
        double[] d2 = {1.2, 3.4, 0.54, 0.78, 1.2, 3.4, 0.54, 0.78, 1.2, 3.4, 0.54, 0.78};
        long[] times = {123456l, 123456l, 123456l, 123456l, 123456l, 123456l, 123456l, 123456l,
        123456l, 123456l, 123456l, 123456l};
        s.setFirstSamplePopulation(d1);
        s.setSecondSamplePopulation(d2);
        s.setTimes1(times);
        s.setTimes2(times);
        s.setFirstClassifierName("Foo");
        s.setSecondClassifierName("Bar");
        s.setTest(new TStatisticsTest(0.05, TestType.OneSidedLessThan));

        System.out.println(s);
    }
}

package spaska.analysis;

/**
 * This class contains static utilities that are used in empirical test classes
 * to calculate different metrics.
 */
public final class EmpiricalTest {
    /**
     * Private ctor so we make sure that we don't have instances of this class.
     */
    private EmpiricalTest() {

    }

    /**
     * Calculates the empirical mean of a sample population.
     * 
     * @param sample
     *            a random sample
     * @return mean value
     */
    public static double getMean(double[] sample) {
        if (sample == null) {
            throw new IllegalArgumentException();
        }
        double sum = 0;
        for (double element : sample) {
            sum += element;
        }
        sum /= sample.length;
        return sum;
    }

    /**
     * Calculates the standard deviation of a random sample.
     * 
     * @param sample
     *            a random sample
     * @return standard deviation
     */
    public static double getStandardDeviation(double[] sample) {
        double mean = getMean(sample);
        double s = 0;
        double current = 0;
        for (double element : sample) {
            current = element - mean;
            s += current * current;
        }
        s /= sample.length - 1;
        return Math.sqrt(s);
    }

}

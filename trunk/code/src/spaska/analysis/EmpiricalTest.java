package spaska.analysis;

public class EmpiricalTest {

	/**
	 * Calculates the empirical mean of a sample population
	 * 
	 * @param sample
	 *            a random sample
	 * @return mean value
	 */
	public static double getMean(double[] sample) {
		checkValidity(sample);
		double sum = 0;
		for (double element : sample) {
			sum += element;
		}
		sum /= sample.length;
		return sum;
	}

	/**
	 * Calculates the standard deviation of a random sample
	 * 
	 * @param sample
	 *            a random sample
	 * @return standard deviation
	 */
	public static double getStandardDeviation(double[] sample) {
		double mean = getMean(sample);
		double S = 0, current = 0;
		for (double element : sample) {
			current = element - mean;
			S += current * current;
		}
		S /= sample.length - 1;
		return Math.sqrt(S);
	}

	private static void checkValidity(Object o) {
		if (o == null) {
			throw new IllegalArgumentException();
		}
	}

}

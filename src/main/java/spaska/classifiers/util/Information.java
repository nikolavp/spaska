package spaska.classifiers.util;

public class Information {
	private static final double LOG_10_2 = Math.log10(2);

	// Information : I(p1,p2,...,pn) = -p1*log(p1)-p2*log(p2)-...-pn*log(pn)
	// Sum(pi) = 1
	public static double entropy(double[] fractions) {
		double result = 0.0;
		for (double f : fractions) {
			if (f > 0) {
				result += -f * Math.log10(f);
			}
		}
		result /= LOG_10_2;
		return result;
	}

	// Information : I(p1,p2,...,pn) = -p1*log(p1)-p2*log(p2)-...-pn*log(pn)
	// Sum(pi) = 1
	// pi = numerator(pi) / denominator(pi)
	public static double entropy(int[] numerators, int denominator) {
		double result = 0.0;
		for (int num : numerators) {
			if (num != 0) {
				result += -num * Math.log10(num);
			}
		}
		result += denominator * Math.log10(denominator);
		result /= LOG_10_2 * denominator;
		return result;
	}

	public static double entropy(double[] numerators, double denominator) {
		double result = 0.0;
		for (double num : numerators) {
			if (num != 0) {
				result += -num * Math.log10(num);
			}
		}
		result += denominator * Math.log10(denominator);
		result /= LOG_10_2 * denominator;
		return result;
	}

	public static double average(double[] infos, int[] numerators,
			int denominator) {
		double result = 0.0;
		for (int i = 0; i < infos.length; i++) {
			result += infos[i] * numerators[i];
		}
		result /= denominator;
		return result;
	}

	public static double average(double[] infos, double[] numerators,
			double denominator) {
		double result = 0.0;
		for (int i = 0; i < infos.length; i++) {
			result += infos[i] * numerators[i];
		}
		result /= denominator;
		return result;
	}

	public static double log2(double value) {
		return Math.log10(value) / LOG_10_2;
	}

}

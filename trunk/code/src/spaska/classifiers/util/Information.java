package spaska.classifiers.util;

public class Information {
	public static double log10_2 = Math.log10(2);

	// Information : I(p1,p2,...,pn) = -p1*log(p1)-p2*log(p2)-...-pn*log(pn)
	// Sum(pi) = 1
	public static double entropy(double[] fractions) {
		double result = 0.0;
		for (double f : fractions) {
			if (f > 0) {
				result += -f * Math.log10(f);
			}
		}
		result /= log10_2;
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
		result /= log10_2 * denominator;
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
		result /= log10_2 * denominator;
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
		return Math.log10(value) / log10_2;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(entropy(new int[] { 2, 3 }, 5));
		System.out.println(entropy(new int[] { 2, 3, 4 }, 9));
		System.out.println(entropy(new int[] { 9, 5 }, 14));
		System.out.println(entropy(new int[] { 2, 2 }, 4));
		System.out.println(entropy(new double[] { 0.4, 0.6 }));
		long sum = 0;
		for (int i = 0; i < 24000000; i++) {
			sum += (Math.random() > 0.5 ? 1 : -1) * i;
		}
		System.out.println(sum);
	}

}

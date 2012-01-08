package spaska.classifiers.util;

/**
 * An utility class to calculate the entropy on a given array of values.
 */
public final class Information {
    private Information() {

    }

    private static final double LOG_10_2 = Math.log10(2);

    /**
     * A method to calculate the entropy from the given fractions.
     * <p>
     * Information : I(p1,p2,...,pn) = -p1*log(p1)-p2*log(p2)-...-pn*log(pn)<br>
     * Sum(pi) = 1
     * </p>
     * 
     * @param fractions
     *            the fractions on which to calculate the entropy
     * @return the entropy value for the fractions
     */
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

    /**
     * A method to calculate the entropy from the given arguments.
     * <p>
     * Information : I(p1,p2,...,pn) = -p1*log(p1)-p2*log(p2)-...-pn*log(pn) <br>
     * Sum(pi) = 1 <br>
     * pi = numerator(pi) / denominator(pi)
     * </p>
     * 
     * @param numerators
     *            the numerators
     * @param denominator
     *            the denominator
     * @return the entropy for the given numerators and denominator
     */

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

    /**
     * A method to calculate the entropy from the given arguments.
     * <p>
     * Information : I(p1,p2,...,pn) = -p1*log(p1)-p2*log(p2)-...-pn*log(pn) <br>
     * Sum(pi) = 1 <br>
     * pi = numerator(pi) / denominator(pi)
     * </p>
     * 
     * @param numerators
     *            the numerators
     * @param denominator
     *            the denominator
     * @return the entropy for the given numerators and denominator
     */
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

    /**
     * Get the average from the arguments.
     * 
     * @param infos
     *            the information carriers
     * @param numerators
     *            the numerators
     * @param denominator
     *            the denominator
     * @return the result from averaging the information on the infos array
     */
    public static double average(double[] infos, int[] numerators,
            int denominator) {
        double result = 0.0;
        for (int i = 0; i < infos.length; i++) {
            result += infos[i] * numerators[i];
        }
        result /= denominator;
        return result;
    }

    /**
     * Gives the average from the arguments.
     * 
     * @param infos
     *            the information carriers
     * @param numerators
     *            the numerators
     * @param denominator
     *            the denominators
     * @return the result from averaging the information on the infos array
     */
    public static double average(double[] infos, double[] numerators,
            double denominator) {
        double result = 0.0;
        for (int i = 0; i < infos.length; i++) {
            result += infos[i] * numerators[i];
        }
        result /= denominator;
        return result;
    }

    /**
     * Fast log2.
     * 
     * @param value
     *            a value to run log2 function on.
     * @return the result from log2 on the given value
     */
    public static double log2(double value) {
        return Math.log10(value) / LOG_10_2;
    }

}

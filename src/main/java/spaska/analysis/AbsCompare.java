package spaska.analysis;

import java.util.Comparator;

public class AbsCompare implements Comparator<Double> {

	@Override
	public int compare(Double arg0, Double arg1) {
		double a = (Double) arg0;
		double b = (Double) arg1;

		if (Math.abs(a) < Math.abs(b))  return -1;
		if (Math.abs(a) == Math.abs(b)) return 0;

		return 1;
	}

}

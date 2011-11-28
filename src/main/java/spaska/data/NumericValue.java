package spaska.data;

import spaska.data.Attribute.ValueType;

/**
 * 
 * @author Vesko Georgiev
 */
public class NumericValue extends Value {

	private double	value;

	public NumericValue(double value) {
		this.value = value;
	}

	@Override
	public ValueType getType() {
		return ValueType.Numeric;
	}

	@Override
	public Double getValue() {
		return value;
	}

	public Object clone() {
		return new NumericValue(value);
	}

	@Override
	public int compareTo(Value v) {
		double res = ((Double)v.getValue()).doubleValue() - value;
		if(res > 0)
			return -1;
		if (res < 0)
			return 1;
		return 0;
	}
	
}

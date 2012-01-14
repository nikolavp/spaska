package spaska.data;

import spaska.data.Attribute.ValueType;

/**
 * A numeric value object that represent a numeric attribute value in the data.
 * Example of numeric values for example can be the age of a person if the
 * dataset contain people.
 */
public final class NumericValue extends Value {

    private double value;

    /**
     * Construct a new numeric value from the double value.
     * 
     * @param value
     *            the double value for this numeric value
     */
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

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    @Override
    public int compareTo(Value v) {
        double res = ((Double) v.getValue()).doubleValue() - value;
        if (res > 0) {
            return -1;
        }
        if (res < 0) {
            return 1;
        }
        return 0;
    }

}

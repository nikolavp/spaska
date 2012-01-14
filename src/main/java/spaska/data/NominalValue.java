package spaska.data;

import spaska.data.Attribute.ValueType;

/**
 * Represents a nominal value attribute in the data.
 * 
 * Nominal values are just finite list of values that an attribute can have. A
 * simple example is the class attribute that contains the label for an
 * instance.
 */
public final class NominalValue extends Value {

    private String value;

    /**
     * Construct a new nominal value for a string value.
     * 
     * @param value
     *            the string value for this nominal value
     */
    public NominalValue(String value) {
        this.value = value;
    }

    @Override
    public ValueType getType() {
        return ValueType.Nominal;
    }

    @Override
    public String getValue() {
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
        return this.value.compareTo((String) v.getValue());
    }
}

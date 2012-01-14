package spaska.data;

import spaska.data.Attribute.ValueType;

/**
 * An abstract base class for all values in the dataset.
 */
public abstract class Value implements Cloneable, Comparable<Value> {
    /**
     * Get the value behind this value object.
     * 
     * @return the value behind this value object
     */
    public abstract Object getValue();

    /**
     * Get the type of this value object.
     * 
     * @return the type of this value object
     */
    public abstract ValueType getType();

    @Override
    public String toString() {
        return getValue() + "";
    }

    @Override
    public final boolean equals(Object o) {
        if (o == null || !(o instanceof Value)) {
            return false;
        }
        Value other = (Value) o;
        return getType() == other.getType()
                && getValue().equals(other.getValue());
    }

    @Override
    public final int hashCode() {
        return getValue().hashCode();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}

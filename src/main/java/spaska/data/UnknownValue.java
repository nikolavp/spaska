package spaska.data;

import spaska.data.Attribute.ValueType;

public final class UnknownValue extends Value {

    private static UnknownValue instance = new UnknownValue();

    public static UnknownValue getInstance() {
        return instance;
    }

    private UnknownValue() {
    }

    @Override
    public ValueType getType() {
        return ValueType.Unknown;
    }

    @Override
    public Object getValue() {
        return "?";
    }

    @Override
    public Object clone() {
        return getInstance();
    }

    @Override
    public int compareTo(Value v) {
        return 0;
    }
}

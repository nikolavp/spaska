package spaska.data;

import spaska.data.Attribute.ValueType;

public class NominalValue extends Value {

    private String value;

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

package spaska.data;

import spaska.data.Attribute.ValueType;


public class NominalValue extends Value {

	private String	value;

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

	public Object clone() {
		return new NominalValue(value);
	}

	@Override
	public int compareTo(Value v) {
		return this.value.compareTo((String)v.getValue());
	}
}

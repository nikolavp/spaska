package spaska.data;

import spaska.data.Attribute.ValueType;


public class UnknownValue extends Value {

	private static UnknownValue	instance;

	public static UnknownValue getInstance() {
		if (instance == null) {
			instance = new UnknownValue();
		}
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

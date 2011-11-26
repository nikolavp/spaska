package spaska.data;

import java.util.List;
import java.util.Vector;

import spaska.data.Attribute.ValueType;

public class Factory {

	public static Value createValue(Object val) {
		if (val instanceof String) {
			return new NominalValue((String) val);
		} else if (val instanceof Double) {
			return new NumericValue((Double) val);
		}
		return UnknownValue.getInstance();
	}

	public static Instance createElement(Object[] vector) {
		List<Value> values = new Vector<Value>();
		for (Object value : vector) {
			values.add(createValue(value));
		}
		return new Instance(values);
	}

	public static List<Value> createElementData(String[] strValues,
			Dataset dataset) {
		List<Value> element = new Vector<Value>();
		List<Attribute> attributes = dataset.getAttributes();
		int i = 0;
		for (Attribute a : attributes) {
			if (i < strValues.length) {
				if (a.getType().equals(ValueType.Numeric)) {
					try {
						Double val = Double.parseDouble(strValues[i]);
						element.add(new NumericValue(val));
					} catch (java.lang.NumberFormatException e) {
						element.add(UnknownValue.getInstance());
					}
				} else {
					if ((strValues[i].trim()).equals("?")) {
						element.add(UnknownValue.getInstance());
					} else {
						element.add(new NominalValue(strValues[i].trim()));
					}
				}
			} else {
				element.add(UnknownValue.getInstance());
			}
			i++;
		}
		return element;
	}
}

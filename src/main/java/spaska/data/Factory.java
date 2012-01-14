package spaska.data;

import java.util.List;
import java.util.Vector;

import spaska.data.Attribute.ValueType;

/**
 * A common creating methods for classes in the data package.
 */
public final class Factory {

    private Factory() {

    }

    /**
     * Creates a new value from the given object value.
     * 
     * @param val
     *            the value from which to construct a Value object
     * @return the newly constructed value object
     */
    public static Value createValue(Object val) {
        if (val instanceof String) {
            return new NominalValue((String) val);
        } else if (val instanceof Double) {
            return new NumericValue((Double) val);
        }
        return UnknownValue.getInstance();
    }

    /**
     * Create a new instance from bulk data.
     * 
     * @param vector
     *            instance data
     * @return the newly created instance
     */
    public static Instance createElement(Object[] vector) {
        List<Value> values = new Vector<Value>();
        for (Object value : vector) {
            values.add(createValue(value));
        }
        return new Instance(values);
    }

    /**
     * Convert string values to data {@link Value} objects.
     * 
     * @param strValues
     *            the string values to be converted
     * @param dataset
     *            the dataset to get the metadata for attributes. This is used
     *            to help us in the convertion
     * @return a list of data {@link Value} objects
     */
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

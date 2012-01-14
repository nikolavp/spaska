/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spaska.data.readers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spaska.data.Attribute;
import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.NumericValue;
import spaska.data.Attribute.ValueType;

/**
 * A simple preprocessor/validator that will normalize the input values to be in
 * the [downLimit, upperLimit]. This is mostly done so that every attribute has
 * the same weight compared to the others.
 * <p>
 * Consider a simple example - we have a people dataset with 3 attributes:
 * <ul>
 * <li>weight</li>
 * <li>height</li>
 * <li>foot size</li>
 * </ul>
 * Now if attribute values for weight is in kilos and for height is meters, we
 * will have a problem as the kilo attribute will be considered far more
 * important as it's value is much greater as a number.
 */
public final class NormalizeValidator implements Validator {
    /**
     * Get the parameters for this validator.
     * 
     * @return the parameters for this validator
     */
    public static Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Down Limit", "0");
        params.put("Upper Limit", "1");
        return params;
    }

    private Dataset dataset;
    private int downLimit;
    private int upperLimit;

    /**
     * Construct the validator and set it to normalize values in the interval
     * [0,1].
     */
    public NormalizeValidator() {
        downLimit = 0;
        upperLimit = 1;
    }

    /**
     * Get the down limit that will be used when normalizing.
     * 
     * @return the down limit that will be used when normalizing
     */
    public int getDownLimit() {
        return downLimit;
    }

    /**
     * Set the downlimit to a new value.
     * 
     * @param downLimit
     *            the new downlimit
     */
    public void setDownLimit(int downLimit) {
        this.downLimit = downLimit;
    }

    /**
     * Get the upper limit for this validator.
     * 
     * @return the upper limit for this validator
     */
    public int getUpperLimit() {
        return upperLimit;
    }

    /**
     * Set the upper limit for this validator.
     * 
     * @param upperLimit
     *            the upper limit for this validator
     */
    public void setUpperLimit(int upperLimit) {
        this.upperLimit = upperLimit;
    }

    @Override
    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    @Override
    public boolean validate() {
        List<Attribute> attributes = dataset.getAttributes();
        List<Instance> instances = dataset.getElements();
        int attrIndex = 0;
        for (Attribute attribute : attributes) {
            if (attribute.getType().equals(ValueType.Numeric)) {
                normalize(attrIndex, instances);
            }
            attrIndex++;
        }

        return true;
    }

    private void normalize(int attrIndex, List<Instance> instances) {
        Double maxValue = new Double(0);
        Double minValue = new Double(0);
        for (Instance element : instances) {
            Double value = ((NumericValue) element.getVector().get(attrIndex))
                    .getValue();
            if (maxValue < value) {
                maxValue = new Double(value);
            }
            if (minValue > value) {
                minValue = new Double(value);
            }
        }
        for (Instance element : instances) {
            Double currentValue = ((NumericValue) element.getVector().get(
                    attrIndex)).getValue();
            Double normalizedValue = downLimit + (currentValue - minValue)
                    * (upperLimit - downLimit) / (maxValue - minValue);
            element.getVector().set(attrIndex,
                    new NumericValue(normalizedValue));

        }

    }

    /**
     * Set the parameters for this normalizer.
     * 
     * @param params
     *            the parameters map for this validator
     */
    public void setParameters(Map<String, String> params) {
        if (params.containsKey("Down Limit")) {
            this.downLimit = Integer.parseInt(params.get("Down Limit"));
        }
        if (params.containsKey("Upper Limit")) {
            this.upperLimit = Integer.parseInt(params.get("Upper Limit"));
        }
    }
}

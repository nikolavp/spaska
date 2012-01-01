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
 * 
 * @author aplamena
 */
public final class NormalizeValidator implements Validator {


	public static Map<String, String> getParameters() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("Down Limit", "0");
		params.put("Upper Limit", "1");
		return params;
	}

	private Dataset	dataset;
	private int		downLimit;
	private int		upperLimit;

	public NormalizeValidator() {
		downLimit = 0;
		upperLimit = 1;
	}

	public int getDownLimit() {
		return downLimit;
	}

	public void setDownLimit(int A) {
		this.downLimit = A;
	}

	public int getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(int B) {
		this.upperLimit = B;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

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

	public void normalize(int attrIndex, List<Instance> instances) {
		Double maxValue = new Double(0);
		Double minValue = new Double(0);
		for (Instance element : instances) {
			Double value = ((NumericValue) element.getVector().get(attrIndex)).getValue();
			if (maxValue < value) {
				maxValue = new Double(value);
			}
			if (minValue > value) {
				minValue = new Double(value);
			}
		}
		for (Instance element : instances) {
			Double currentValue = ((NumericValue) element.getVector().get(attrIndex)).getValue();
			Double normalizedValue = downLimit + (currentValue - minValue) * (upperLimit - downLimit) / (maxValue - minValue);
			element.getVector().set(attrIndex, new NumericValue(normalizedValue));

		}

	}

	public void setParameters(Map<String, String> params) {
		if (params.containsKey("Down Limit")) {
			this.downLimit = Integer.parseInt(params.get("Down Limit"));
		}
		if (params.containsKey("Upper Limit")) {
			this.upperLimit = Integer.parseInt(params.get("Upper Limit"));
		}
	}
}

package spaska.classifiers;

import java.util.HashMap;
import java.util.Map;

import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.Value;

public class ZeroR implements IClassifier {

	public static Map<String, String> getParameters() {
		return null;
	}

	private Value commonValue;

	// set for commonValue the most frequent Value among the classes
	public void buildClassifier(Dataset instances) {
		if (commonValue == null) {
			int classIndex = instances.getClassIndex();
			Map<Value, Integer> frequency = new HashMap<Value, Integer>();
			for (Instance instance : instances.getElements()) {
				Value classValue = getAttributeValue(instance, classIndex);
				if (frequency.get(classValue) == null) {
					frequency.put(classValue, 1);
				} else {
					frequency.put(classValue, frequency.get(classValue) + 1);
				}
			}
			int max = 0;
			for (Map.Entry<Value, Integer> entry : frequency.entrySet()) {
				if (max < entry.getValue()) {
					commonValue = entry.getKey();
					max = entry.getValue();
				}
			}
		}

	}

	public Value classifyInstance(Instance instance) {
		return commonValue;
	}

	public String getName() {
		return "ZeroR";
	}

	public void setParameters(String paramName, String paramValue) {
	}

	public Value getCommonValue() {
		return commonValue;
	}
	
	private Value getAttributeValue(Instance instance, int index) {
		return instance.getVector().get(index);
	}

	@Override
	public void setParameters(Map<String, String> parameters) {
		// do nothing
	}

	public String toString() {
		return "Zero R : most common value returned";
	}
}

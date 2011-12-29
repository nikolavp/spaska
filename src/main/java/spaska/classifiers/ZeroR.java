package spaska.classifiers;

import java.util.HashMap;
import java.util.Map;

import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.Value;

/**
 * A classifier that just labels all instances to the most frequent class in the
 * training dataset.
 */
public final class ZeroR implements IClassifier {
    /**
     * Get the parameters for this classifier.
     * 
     * @return the parameters for this classifier
     */
    public static Map<String, String> getParameters() {
        return null;
    }

    private Value commonValue;

    @Override
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

    @Override
    public Value classifyInstance(Instance instance) {
        return commonValue;
    }

    @Override
    public String getName() {
        return "ZeroR";
    }

    /**
     * The the most common value in the training dataset.
     * 
     * @return the most common value class in the training dataset
     */
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

    @Override
    public String toString() {
        return "Zero R : most common value returned";
    }
}

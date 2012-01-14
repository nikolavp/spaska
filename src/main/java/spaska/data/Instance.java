package spaska.data;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents an instance in the framework.
 * 
 * Instances are the base data classes that are used throughout the frameworks.
 * They currently can be classified or clustered.
 * 
 */
public class Instance implements Cloneable {

    private List<Value> vector;
    private double weight;

    /**
     * Construct an instance from a list of values.
     * 
     * @param vector
     *            the vector of attribute values
     */
    public Instance(List<Value> vector) {
        this.vector = vector;
        this.weight = 1.0;
    }

    /**
     * Set the attribute values for this instance.
     * 
     * @param vector
     *            the new attribute values for this instance
     */
    public void setVector(List<Value> vector) {
        this.vector = vector;
    }

    /**
     * Get the attribute values of this instance.
     * 
     * @return the attribute values of this instance
     */
    public List<Value> getVector() {
        return vector;
    }

    /**
     * Get if this instance is weighted and how much is the weight.
     * 
     * @return the weight ammount for this instance
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Set if this instance is weighted and how much is the weight.
     * 
     * @param weight
     *            the weight ammount for this instance
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public Object clone() {
        Instance cloned;
        try {
            cloned = (Instance) super.clone();
            cloned.vector = new ArrayList<Value>();
            for (Value v : vector) {
                cloned.vector.add((Value) v.clone());
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(
                    "Instance is implementing clonable but cloning was not supported!");
        }
    }
}

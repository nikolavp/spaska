package spaska.data;

import java.util.ArrayList;
import java.util.List;

public class Instance implements Cloneable {

    private List<Value> vector;
    private double weight;

    public Instance(List<Value> vector) {
        this.vector = vector;
        this.weight = 1.0;
    }

    public void setVector(List<Value> vector) {
        this.vector = vector;
    }

    public List<Value> getVector() {
        return vector;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

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

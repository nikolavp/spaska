package spaska.data;

import java.util.ArrayList;
import java.util.List;

public class Instance implements Cloneable {

	private List<Value> vector;
	private double      weight;

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
		List<Value> values = createEmptyList();
		for (Value v : vector) {
			values.add((Value)v.clone());
		}
		return new Instance(values);
	}
	
	private List<Value> createEmptyList() {
		return new ArrayList<Value>();
	}
	
}

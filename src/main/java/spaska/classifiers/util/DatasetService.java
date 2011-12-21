package spaska.classifiers.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import spaska.data.Attribute;
import spaska.data.Attribute.ValueType;
import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.Value;

public class DatasetService {
	private Dataset dataset;

	// attribute i -> <value : int> pairs
	private Map<Value, Integer>[] intValues;

	private int[] nominalIndices;
	private int[] numericIndices;

	public DatasetService(Dataset dataset) {
		this.dataset = dataset;
		initIntValues();
	}

	// assign each nominal value an integer representation in the specific
	// domain
	private void initIntValues() {
		int n = numberOfAttributes();
		// indeices[begin] -> nominal; indices[end] -> numeric
		int nominalEnd = 0;
		int numericEnd = n - 1;
		int[] indices = new int[n];
		intValues = getNewHashMapArray(n);
		for (int i = 0; i < n; i++) {
			intValues[i] = new HashMap<Value, Integer>();
			if (getAttribute(i).getType() == ValueType.Nominal) {
				int k = 0;
				for (Value val : getAttributeDomain(i)) {
					intValues[i].put(val, k);
					k++;
				}
				indices[nominalEnd++] = i;
			} else {
				if (getAttribute(i).getType() == ValueType.Numeric) {
					indices[numericEnd--] = i;
				}
			}
		}
		nominalIndices = new int[nominalEnd];
		numericIndices = new int[n - nominalEnd];
		for (int i = 0; i < nominalEnd; i++) {
			nominalIndices[i] = indices[i];
		}
		for (int i = n - 1, j = 0; i > numericEnd; i--, j++) {
			numericIndices[j] = indices[i];
		}
	}

    @SuppressWarnings("unchecked")
    private Map<Value, Integer>[] getNewHashMapArray(int n) {
        return (Map<Value, Integer>[]) new HashMap[n];
    }

	public int numberOfAttributes() {
		return dataset.getAttributes().size();
	}

	public int classIndex() {
		return numberOfAttributes() - 1;
	}

	public Value getClass(Instance instance) {
		return instance.getVector().get(classIndex());
	}

	public int numberOfClasses() {
		return getAttributeDomain(classIndex()).size();
	}

	public Set<Value> getAttributeDomain(int i) {
		Attribute a = dataset.getAttributes().get(i);
		return dataset.getDomain(a);
	}

	public Attribute getAttribute(int i) {
		return dataset.getAttributes().get(i);
	}

	// ith attribute - integer representation of value
	public int intValue(int i, Value val) {
		return intValues[i].get(val);
	}

	// get Value from the int representation of an attribute
	public Value getValueFromInt(int attributeIndex, int intValue) {
		Value result = null;
		for (Map.Entry<Value, Integer> entry : intValues[attributeIndex].entrySet()) {
			if (entry.getValue() == intValue) {
				result = entry.getKey();
				break;
			}
		}
		return result;
	}
	
	// nominal attribute indices
	public int[] getNominalIndices() {
		return nominalIndices;
	}

	// numeric attribute indices
	public int[] getNumericIndices() {
		return numericIndices;
	}

	public int getAttributeIndex(Attribute a) {
		return dataset.getAttributes().indexOf(a);
	}
}

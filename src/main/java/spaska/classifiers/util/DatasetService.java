package spaska.classifiers.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import spaska.data.Attribute;
import spaska.data.Attribute.ValueType;
import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.Value;

/**
 * A convenient service over a data set object to enable some bulk operations.
 * The class also gives nominal values indices, i.e. numeric value.
 */

public final class DatasetService {
    private Dataset dataset;

    // attribute i -> <value : int> pairs
    private Map<Value, Integer>[] intValues;

    private int[] nominalIndices;
    private int[] numericIndices;

    /**
     * A constructor that wraps a given dataset.
     * 
     * @param dataset
     *            the dataset on which to do bulk operations
     */
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

    /**
     * Return the total number of attributes in the dataset representatives.
     * 
     * @return the total number of attributes in the dataset representatives
     */
    public int numberOfAttributes() {
        return dataset.getAttributes().size();
    }

    /**
     * Return the index of the class in the data set.
     * 
     * @return the index of the class in the data set
     */
    public int classIndex() {
        return numberOfAttributes() - 1;
    }

    /**
     * Get the class value of a given instace.
     * 
     * @param instance
     *            the instance from which to get the class value
     * @return the value of the class on this instace
     */
    public Value getClass(Instance instance) {
        return instance.getVector().get(classIndex());
    }

    /**
     * Get the total number of classes in the dataset.
     * 
     * @return the total number of classes in the dataset
     */
    public int numberOfClasses() {
        return getAttributeDomain(classIndex()).size();
    }

    /**
     * Get the domain of the attribute on this index.
     * 
     * @param i
     *            the index of the attribute
     * @return the domain(set of values) for this attribute
     */
    public Set<Value> getAttributeDomain(int i) {
        Attribute a = dataset.getAttributes().get(i);
        return dataset.getDomain(a);
    }

    /**
     * Get the attribute on this index.
     * 
     * @param i
     *            the index of the attribute
     * @return the attribute on the given index
     */
    public Attribute getAttribute(int i) {
        return dataset.getAttributes().get(i);
    }

    /**
     * Get the attribute index - integer representation of the given value.
     * 
     * @param i
     *            the index for the int value.
     * @param val
     *            the value
     * @return the attribute index based ont he specified intValue index and
     *         value
     */
    public int intValue(int i, Value val) {
        return intValues[i].get(val);
    }

    /**
     * Get the Value of an attribute from it's index and int value.
     * 
     * @param attributeIndex
     *            the attribute index
     * @param intValue
     *            the attribute int value.
     * @return the value of the attribute.
     */
    public Value getValueFromInt(int attributeIndex, int intValue) {
        Value result = null;
        for (Map.Entry<Value, Integer> entry : intValues[attributeIndex]
                .entrySet()) {
            if (entry.getValue() == intValue) {
                result = entry.getKey();
                break;
            }
        }
        return result;
    }

    /**
     * Get nominal attribute indices.
     * 
     * @return nominal attribute indices
     */
    public int[] getNominalIndices() {
        return nominalIndices;
    }

    /**
     * Get numeric attribute indices.
     * 
     * @return numeric attribute indices
     */
    public int[] getNumericIndices() {
        return numericIndices;
    }

    /**
     * Get the index of an attribute.
     * 
     * @param a
     *            the attribute
     * @return the index of that attribute
     */
    public int getAttributeIndex(Attribute a) {
        return dataset.getAttributes().indexOf(a);
    }
}

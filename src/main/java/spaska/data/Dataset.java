package spaska.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

/**
 * Represents a collection of data(instances).
 * 
 * <p>
 * Objects of this class keep not only the instances but also the meta data
 * behind the instances.
 * </p>
 * 
 * @author Vesko Georgiev
 */
public final class Dataset {

    private String name;
    private List<Attribute> attributes;
    private List<Instance> instances;
    private Map<Attribute, Set<Value>> attributeDomains;
    private int classIndex;

    /**
     * Constructs a new dataset with the given name and attributes.
     * 
     * @param name
     *            the name of the dataset
     * @param attributes
     *            the attributes of the dataset instances
     */
    public Dataset(String name, List<Attribute> attributes) {
        this(name, (Attribute[]) attributes.toArray(new Attribute[attributes
                .size()]));
    }

    /**
     * Constructs a new dataset with the given name and attributes.
     * 
     * @param name
     *            the name of the dataset
     * @param attributes
     *            the attributes of the dataset instances
     */
    public Dataset(String name, Attribute[] attributes) {
        this.name = name;
        this.attributes = new Vector<Attribute>();
        for (Attribute e : attributes) {
            this.attributes.add(e);
        }
        instances = new Vector<Instance>();
        attributeDomains = new HashMap<Attribute, Set<Value>>();
        classIndex = attributes.length - 1;
    }

    /**
     * Constructs a new dataset.
     */
    public Dataset() {
        instances = new Vector<Instance>();
        attributeDomains = new HashMap<Attribute, Set<Value>>();
        attributes = new LinkedList<Attribute>();
    }

    /**
     * Return the classname of the given instance as string.
     * 
     * @param instance
     *            the instance
     * @return the classname of the given instance
     */
    public String getClassName(Instance instance) {
        return ((NominalValue) instance.getVector().get(classIndex)).getValue();
    }

    /**
     * Get the class value from a given instance.
     * 
     * @param instance
     *            the instance
     * @return the class for the given instance
     */
    public Value getClassValue(Instance instance) {
        return instance.getVector().get(classIndex);
    }

    /**
     * Get the domain for the class attribute.
     * 
     * @return the domain for the class attribute
     * @see #getDomain(Attribute)
     */
    public Set<Value> getAllClassNamesSet() {
        return attributeDomains.get(attributes.get(classIndex));
    }

    /**
     * Get the names of the possible classnames.
     * 
     * @return the names of the possible classnames
     */
    public String[] getAllClassNamesArray() {
        Set<Value> classDomain = attributeDomains.get(attributes
                .get(classIndex));
        if (classDomain != null) {
            String[] result = new String[classDomain.size()];
            int i = 0;
            for (Value v : classDomain) {
                result[i++] = v.toString();
            }
            return result;
        }
        return null;
    }

    /**
     * Get the name of the dataset.
     * 
     * @return the name of the dataset
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the dataset.
     * 
     * @param name
     *            the new name for the dataset
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the list of attributes for this dataset.
     * 
     * @return list of attributes for this dataset
     */
    public List<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * Add attributes to the dataset.
     * 
     * @param newAttributes
     *            attributes to be added
     */
    public void addAttribute(Attribute... newAttributes) {
        for (Attribute attribute : newAttributes) {
            this.attributes.add(attribute);
        }
    }

    /**
     * Get all instances(elements) in this dataset.
     * 
     * @return all instances in the dataset
     */
    public List<Instance> getElements() {
        return instances;
    }

    /**
     * Get the set of possible values for an attribute.
     * 
     * @param a
     *            the attribute that will be used for the query
     * @return a set of possible values for the attribute
     */
    public Set<Value> getDomain(Attribute a) {
        return attributeDomains.get(a);
    }

    /**
     * Set a new attribute with the given finite set of possible values.
     * 
     * @param a
     *            the attribute to be added
     * @param domain
     *            the set of possible values for this attribute
     */
    public void addAttributeDomain(Attribute a, Set<Value> domain) {
        attributeDomains.put(a, domain);
    }

    /**
     * Add a single instance(element) to the dataset.
     * 
     * @param e
     *            the new element to be added
     */
    public void addElement(Instance e) {
        instances.add(e);
    }

    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append(getName() + ": ");
        for (Attribute a : attributes) {
            buff.append(a + ", ");
        }
        buff.setCharAt(buff.length() - 2, '\n');
        buff.setLength(buff.length() - 1);

        for (Instance val : instances) {
            buff.append(val.getVector() + "\n");
        }
        return buff.toString();
    }

    /**
     * Get the number of attributes for every instance in the dataset.
     * 
     * @return the number of attributes for every instance in the dataset
     */
    public int getAttributesCount() {
        return attributes.size();
    }

    /**
     * Set the class index in the dataset.
     * 
     * @param classIndex
     *            new class index value for this dataset
     */
    public void setClassIndex(int classIndex) {
        this.classIndex = classIndex;
    }

    /**
     * Get the class index in the dataset.
     * 
     * @return the class index in the dataset
     */
    public int getClassIndex() {
        return classIndex;
    }

    /**
     * Merge the given datasets into one big dataset.
     * 
     * @param datasets
     *            the datasets that need to be merged
     * @return the merged dataset
     */
    public static Dataset merge(Dataset[] datasets) {
        Dataset result = datasets[0].createCopyWithoutInstances();
        List<Instance> resultList = new ArrayList<Instance>();
        for (int i = 0; i < datasets.length; i++) {
            resultList.addAll(datasets[i].getElements());
        }
        result.setInstances(resultList);
        return result;
    }

    /**
     * Set the instances behind this dataset.
     * 
     * @param instances
     *            the new instances for this dataset
     */
    public void setInstances(List<Instance> instances) {
        this.instances = instances;
    }

    /**
     * Splits the dataset into subdatasets.
     * 
     * @param datasetCounts
     *            an array of wanted dataset sizes.
     * @param seed
     *            the seed to be used for the random object that will randomize
     *            the current dataset data.
     * @return an array of sub dataset with the wanted sizes.
     */
    public Dataset[] getSubDataSets(int[] datasetCounts, int seed) {
        Random rand = new Random(seed);
        Dataset copy = this.createCopy();
        Dataset[] datasets = new Dataset[datasetCounts.length];
        for (int i = 0; i < datasets.length; i++) {
            datasets[i] = createCopyWithoutInstances();
        }
        Comparator<Instance> c = new Comparator<Instance>() {

            @Override
            public int compare(Instance o1, Instance o2) {
                return o1.getVector().get(classIndex)
                        .compareTo(o2.getVector().get(classIndex));
            }

        };
        Collections.sort(copy.instances, c);

        // randomize equal groups of classes
        List<Instance> copyInstances = copy.instances;
        int instancesSize = copyInstances.size();
        Value previousClassValue = copyInstances.get(instancesSize - 1)
                .getVector().get(classIndex);
        int previousClassValueIndex = instancesSize - 1;
        while (instancesSize-- > 0) {
            Value currentValue = copyInstances.get(instancesSize).getVector()
                    .get(classIndex);
            if (!currentValue.equals(previousClassValue)) {
                List<Instance> instancesSubList = copyInstances.subList(
                        instancesSize, previousClassValueIndex);
                Collections.shuffle(instancesSubList, rand);
                previousClassValueIndex = instancesSize;
            }
        }

        int cnt = 0;
        List<Instance> allElements = copy.getElements();
        int cycles = allElements.size();
        while (cycles-- > 0) {
            int ind = cnt++ % datasetCounts.length;
            datasets[ind].addElement(allElements.get(cycles));
            datasetCounts[ind]--;
        }
        return datasets;
    }

    private Dataset createCopy() {
        Dataset result = new Dataset();
        result.name = name;
        result.attributes = Collections.unmodifiableList(this.attributes);

        result.instances.addAll(instances);
        result.attributeDomains = Collections
                .unmodifiableMap(this.attributeDomains);
        result.classIndex = classIndex;
        return result;
    }

    private Dataset createCopyWithoutInstances() {
        Dataset result = new Dataset();
        result.name = name;
        result.attributes = Collections.unmodifiableList(this.attributes);
        result.attributeDomains = Collections
                .unmodifiableMap(this.attributeDomains);
        result.classIndex = classIndex;
        return result;
    }

}

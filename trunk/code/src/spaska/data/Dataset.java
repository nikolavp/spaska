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
 * 
 * 
 * @author Vesko Georgiev
 */
public class Dataset {

	/**  */
	private String						name;
	/**  */
	private List<Attribute>				attributes;
	/**  */
	private List<Instance>				instances;
	/** */
	private Map<Attribute, Set<Value>>	attributeDomains;
	/** */
	private int							classIndex;

	public Dataset(String name, List<Attribute> attributes) {
		this(name, (Attribute[]) attributes.toArray(new Attribute[attributes.size()]));
	}

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

	public Dataset() {
		instances = new Vector<Instance>();
		attributeDomains = new HashMap<Attribute, Set<Value>>();
		attributes = new LinkedList<Attribute>();
	}

	public String getClassName(Instance instance) {
		return ((NominalValue) instance.getVector().get(classIndex)).getValue();
	}

	public Value getClassValue(Instance instance) {
		return instance.getVector().get(classIndex);
	}

	public Set<Value> getAllClassNamesSet() {
		return attributeDomains.get(attributes.get(classIndex));
	}

	public String[] getAllClassNamesArray() {
		Set<Value> classDomain = attributeDomains.get(attributes.get(classIndex));
		if (classDomain != null) {
			String result[] = new String[classDomain.size()];
			int i = 0;
			for (Value v : classDomain) {
				result[i++] = v.toString();
			}
			return result;
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void addAttribute(Attribute... attributes) {
		for (Attribute attribute : attributes) {
			this.attributes.add(attribute);
		}
	}

	public List<Instance> getElements() {
		return instances;
	}

	public Set<Value> getDomain(Attribute a) {
		return attributeDomains.get(a);
	}

	public void addAttributeDomain(Attribute a, Set<Value> domain) {
		attributeDomains.put(a, domain);
	}

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

	public int getAttributesCount() {
		return attributes.size();
	}

	public void setClassIndex(int classIndex) {
		this.classIndex = classIndex;
	}

	public int getClassIndex() {
		return classIndex;
	}

	public static Dataset merge(Dataset[] datasets) {
		Dataset result = datasets[0].createCopyWithoutInstances();
		List<Instance> resultList = new ArrayList<Instance>();
		for (int i = 0; i < datasets.length; i++) {
			resultList.addAll(datasets[i].getElements());
		}
		result.setInstances(resultList);
		return result;
	}

	public void setInstances(List<Instance> instances) {
		this.instances = instances;
	}

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
				return o1.getVector().get(classIndex).compareTo(o2.getVector().get(classIndex));
			}

		};
		Collections.sort(copy.instances, c);

		// randomize equal groups of classes
		List<Instance> instances = copy.instances;
		int instancesSize = instances.size();
		Value previousClassValue = instances.get(instancesSize - 1).getVector().get(classIndex);
		int previousClassValueIndex = instancesSize - 1;
		while (instancesSize-- > 0) {
			Value currentValue = instances.get(instancesSize).getVector().get(classIndex);
			if (!currentValue.equals(previousClassValue)) {
				List<Instance> instancesSubList = instances.subList(instancesSize, previousClassValueIndex);
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
		result.attributeDomains = Collections.unmodifiableMap(this.attributeDomains);
		result.classIndex = classIndex;
		return result;
	}

	private Dataset createCopyWithoutInstances() {
		Dataset result = new Dataset();
		result.name = name;
		result.attributes = Collections.unmodifiableList(this.attributes);
		result.attributeDomains = Collections.unmodifiableMap(this.attributeDomains);
		result.classIndex = classIndex;
		return result;
	}

}

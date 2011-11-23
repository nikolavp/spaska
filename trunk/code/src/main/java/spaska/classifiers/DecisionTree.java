package spaska.classifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import spaska.classifiers.util.Condition;
import spaska.classifiers.util.ContinuousValueService;
import spaska.classifiers.util.DatasetService;
import spaska.classifiers.util.Node;
import spaska.classifiers.util.NominalInfoService;
import spaska.classifiers.util.Sign;
import spaska.data.Attribute;
import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.NumericValue;
import spaska.data.UnknownValue;
import spaska.data.Value;
import spaska.data.Attribute.ValueType;

//TODO : pruning
public class DecisionTree implements IClassifier {

	public static Map<String, String> getParameters() {
		return null;
	}

	private Node tree;// actual tree after building classifier

	private DatasetService datasetService;// a helpful service

	private boolean postPrune;// whether or not to prune after building

	private static String description = "The algorithm uses elements from C4.5.\n"
			+ "Attributes are considered for splitting according to their gain ratio.\n"
			+ "The higher the gain ratio, the bigger the chance the attribute will be chosen for splitting.\n"
			+ "Numeric attributes define binary split points of the form (<= value ; > value).\n"
			+ "Instances with unknown attributes are copied and then spread among\n"
			+ "all possible values of the splitting attribute. These instances are given\n"
			+ "weight according to the popularity of the attribute value, however, at this\n"
			+ "stage this weight is not considered. New instances which encounter a test,\n"
			+ "such that their value for that attribute is unknown, are classified as the\n"
			+ "most popular class for that branch.";

	// distribute instances according to the condition they satisfy
	private List<List<Instance>> distribute(List<Instance> instances, List<Condition> conditions) {
		List<List<Instance>> result = new ArrayList<List<Instance>>();
		List<Instance> unknown = new ArrayList<Instance>();

		for (int i = 0; i < conditions.size(); i++) {
			result.add(new ArrayList<Instance>());
		}
		if (conditions.isEmpty()) {
			return result;
		}
		// all conditions test the same attribute
		Condition first = conditions.get(0);
		int attributeIndex = datasetService.getAttributeIndex(first.getAttribute());
		int totalKnown = 0;
		// distribute instances to relevant conditions
		for (Instance instance : instances) {
			int listCounter = 0;
			Value current = instance.getVector().get(attributeIndex);
			for (Condition c : conditions) {
				if (current.getType() == ValueType.Unknown) {
					unknown.add(instance);
					break;
				}
				if (c.ifTrue(current)) {
					result.get(listCounter).add(instance);
					totalKnown++;
					break;
				}
				listCounter++;
			}
		}
		// set portion of instances reaching a condition
		int listCounter = 0;
		for (Condition c : conditions) {
			c.setReach(result.get(listCounter).size() / totalKnown);
			listCounter++;
		}
		// handle unknown
		for (Instance instance : unknown) {
			int counter = 0;
			for (Condition c : conditions) {
				Instance copy = (Instance) instance.clone();
				copy.setWeight(copy.getWeight() * c.getReach());
				result.get(counter).add(copy);
				counter++;
			}
		}
		return result;
	}

	// check if all instances are of class defaultClass
	private boolean allHaveTheSameClass(List<Instance> instances, Value defaultClass) {
		for (Instance instance : instances) {
			if (!defaultClass.equals(datasetService.getClass(instance))) {
				return false;
			}
		}
		return true;
	}

	// get index of best nominal attribute (according to information theory)
	private int getBestNominal(NominalInfoService service, boolean[] used) {
		int[] nominalIndices = datasetService.getNominalIndices();
		double max = 0, currentRatio;
		int index = -1;
		for (int i = 0; i < nominalIndices.length; i++) {
			if (!used[nominalIndices[i]]) {
				currentRatio = service.getGainRatio(nominalIndices[i]);
				if (max < currentRatio) {
					max = currentRatio;
					index = nominalIndices[i];
				}
			}
		}
		return index;
	}

	// get a service for the best numeric attribute to split on
	private ContinuousValueService getBestNumeric(List<Instance> list, double classesEntropy) {
		int[] numericIndices = datasetService.getNumericIndices();
		ContinuousValueService best = ContinuousValueService.createEmptyService();
		ContinuousValueService currentService;
		double max = 0, currentRatio;
		for (int i = 0; i < numericIndices.length; i++) {
			currentService = new ContinuousValueService(datasetService, list, numericIndices[i], classesEntropy);
			currentRatio = currentService.getGainRatio();
			if (max < currentRatio) {
				max = currentRatio;
				best = currentService;
			}
		}
		return best;
	}

	// get conditions (nodes in the tree) for a nominal attribute
	private List<Condition> getNominalConditions(int attributeIndex, Value majorityClass) {
		List<Condition> children = new ArrayList<Condition>();
		Attribute a = datasetService.getAttribute(attributeIndex);
		for (Value val : datasetService.getAttributeDomain(attributeIndex)) {
			children.add(new Condition(a, val, Sign.EQ, majorityClass));
		}
		return children;
	}

	// get conditions for a numeric attribute (binary split point)
	private List<Condition> getNumericConditions(int attributeIndex, double splitValue, Value majorityClass) {
		List<Condition> children = new ArrayList<Condition>();
		Attribute a = datasetService.getAttribute(attributeIndex);
		Value doubleValue = new NumericValue(splitValue);
		children.add(new Condition(a, doubleValue, Sign.LTE, majorityClass));
		children.add(new Condition(a, doubleValue, Sign.GT, majorityClass));
		return children;
	}

	// get children of the current node
	private List<Condition> getChildrenConditions(List<Instance> list, boolean[] used) {
		List<Condition> children = new ArrayList<Condition>();
		NominalInfoService infoService = new NominalInfoService(datasetService, list, used);
		ContinuousValueService continuousService = getBestNumeric(list, infoService.getClassesEntropy());
		if (infoService.isEmpty() && continuousService.isEmpty()) {
			return children;
		}
		Value majorityClass = infoService.getSiblingsMajorityClass();
		int nominalIndex = getBestNominal(infoService, used);
		if (nominalIndex < 0 && continuousService.isEmpty()) {
			return children;
		}
		if (continuousService.isEmpty() || infoService.getGainRatio(nominalIndex) >= continuousService.getGainRatio()) {
			children = getNominalConditions(nominalIndex, majorityClass);
			used[nominalIndex] = true;
		}
		else {
			children = getNumericConditions(continuousService.getAttributeIndex(), continuousService.getSplitValue(), majorityClass);
		}
		return children;
	}

	private void buildTree(Node node, List<Instance> instances, boolean[] used) {
		// stopping criteria
		// all - the same class
		Value firstClass = datasetService.getClass(instances.get(0));
		if (allHaveTheSameClass(instances, firstClass)) {
			Condition c = getNodeCondition(node);
			c.setEffect(firstClass);
			c.setNumber(instances.size());
			return;
		}
		if (instances.isEmpty()) {
			// majority vote - parent node
			Condition c = getNodeCondition(node);
			c.setEffect(c.getMajorityClass());
			return;
		}
		// zero gain - all tests

		// for each attribute (numeric or non-used nominal) calculate gain ratio choose the best attribute
		List<Condition> children = getChildrenConditions(instances, used);
		if (children.isEmpty()) {// no patterns extracted from data => possibly
			// unknown values
			Condition c = getNodeCondition(node);
			c.setEffect(c.getMajorityClass());
			c.setNumber(instances.size());
			return;
		}
		// split on that attribute
		List<List<Instance>> distribution = distribute(instances, children);
		int counter = 0;
		for (Condition c : children) {
			Node child = new Node(c);
			node.addChild(child);
			boolean[] newUsed = new boolean[used.length];
			System.arraycopy(used, 0, newUsed, 0, used.length);
			buildTree(child, distribution.get(counter++), newUsed);
		}
	}

	private Node buildTree(List<Instance> instances) {
		Node root = new Node(new Object());
		boolean[] used = new boolean[datasetService.numberOfAttributes()];
		used[datasetService.classIndex()] = true;
		buildTree(root, instances, used);
		return root;
	}

	public void buildClassifier(Dataset instances) {
		datasetService = new DatasetService(instances);
		tree = buildTree(instances.getElements());
		// System.out.println(this.toString());
	}

	public Value classifyInstance(Instance instance) {
		return classify(instance, tree);
	}

	// recursive classification
	private Value classify(Instance instance, Node node) {
		if (isLeaf(node)) {
			return getNodeCondition(node).getEffect();
		}
		else {
			Value result = null;
			for (Node current : node.getChildren()) {
				if (Thread.interrupted()) return UnknownValue.getInstance();

				Condition c = (Condition) current.getValue();
				int aIndex = datasetService.getAttributeIndex(c.getAttribute());
				Value aValue = instance.getVector().get(aIndex);
				if (aValue.getType() == ValueType.Unknown) {
					return c.getMajorityClass();
				}
				if (c.ifTrue(aValue)) {
					result = classify(instance, current);
					break;
				}
			}
			return result;
		}
	}

	// separated cast
	private Condition getNodeCondition(Node node) {
		return (Condition) node.getValue();
	}

	// check if tree node is a leaf
	private boolean isLeaf(Node node) {
		if (!(node.getValue() instanceof Condition)) {
			return false;
		}
		Condition c = getNodeCondition(node);
		return c.getEffect() != null;
	}

	public String getName() {
		StringBuilder b = new StringBuilder("DecisionTree /elements from C4.5/");
		return b.toString();
	}

	// parameters accommodating GUI interaction
	public void setParameters(String paramName, String paramValue) {
		if (paramName.equalsIgnoreCase("postPrune")) {
			if (paramValue.equalsIgnoreCase("true")) {
				postPrune = true;
			}
			else {
				postPrune = false;
			}
		}
	}

	// algorithm's description
	public static String getDescription() {
		return description;
	}

	public String toString() {
		StringBuilder b = new StringBuilder("=== Tree ===\n");
		if (tree != null) {
			for (Node child : tree.getChildren()) {
				b.append(child.toString());
			}
		}
		return b.toString();
	}

	@Override
	public void setParameters(Map<String, String> parameters) {
		// do nothing
	}
}

package spaska.classifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import spaska.classifiers.util.Condition;
import spaska.classifiers.util.ContinuousValueService;
import spaska.classifiers.util.DatasetService;
import spaska.classifiers.util.NominalInfoService;
import spaska.classifiers.util.Sign;
import spaska.data.Attribute;
import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.NominalValue;
import spaska.data.NumericValue;
import spaska.data.Value;
import spaska.data.Attribute.ValueType;

public class OneR implements IClassifier {

	public static Map<String, String> getParameters() {
		return null;
	}

	private List<Condition>	rules;

	private DatasetService	datasetService;

	public OneR() {
		rules = new ArrayList<Condition>();
	}

	// get majority class of instances
	private Value majorityVote(List<Instance> instances) {
		int classIndex = datasetService.classIndex();
		int[] freq = new int[datasetService.numberOfClasses()];
		Value[] match = new NominalValue[datasetService.numberOfClasses()];
		for (Instance instance : instances) {
			int classInt = datasetService.intValue(classIndex, datasetService.getClass(instance));
			match[classInt] = datasetService.getClass(instance);
			freq[classInt]++;
		}
		int max = 0, maxIndex = 0;
		for (int i = 0; i < freq.length; i++) {
			if (max < freq[i]) {
				max = freq[i];
				maxIndex = i;
			}
		}
		return match[maxIndex];
	}

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
			c.setReach(((double)result.get(listCounter).size()) / totalKnown);
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

	// get index of best nominal attribute (according to information theory)
	private int getBestNominal(NominalInfoService service) {
		int[] nominalIndices = datasetService.getNominalIndices();
		double max = 0, currentRatio;
		int index = -1;
		for (int i = 0; i < nominalIndices.length; i++) {
			currentRatio = service.getGainRatio(nominalIndices[i]);
			if (max < currentRatio) {
				max = currentRatio;
				index = nominalIndices[i];
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
	private List<Condition> getConditions(List<Instance> list) {
		List<Condition> children = new ArrayList<Condition>();
		boolean[] used = new boolean[datasetService.numberOfAttributes()];
		used[datasetService.classIndex()] = true;
		NominalInfoService infoService = new NominalInfoService(datasetService, list, used);
		ContinuousValueService continuousService = getBestNumeric(list, infoService.getClassesEntropy());
		if (infoService.isEmpty() && continuousService.isEmpty()) {
			return children;
		}
		Value majorityClass = infoService.getSiblingsMajorityClass();
		int nominalIndex = getBestNominal(infoService);
		if (nominalIndex < 0 && continuousService.isEmpty()) {
			return children;
		}
		if (continuousService.isEmpty() || infoService.getGainRatio(nominalIndex) >= continuousService.getGainRatio()) {
			children = getNominalConditions(nominalIndex, majorityClass);
		}
		else {
			children = getNumericConditions(continuousService.getAttributeIndex(), continuousService.getSplitValue(), majorityClass);
		}
		return children;
	}

	private void buildRules(Dataset data) {
		List<Condition> list = getConditions(data.getElements());
		if (list.isEmpty()) {
			ZeroR z = new ZeroR();
			z.buildClassifier(data);
			Attribute a = datasetService.getAttribute(0);
			Value threshold = data.getElements().get(0).getVector().get(0);
			Condition eqCondition = new Condition(a, threshold, Sign.EQ);
			eqCondition.setMajorityClass(z.getCommonValue());
			eqCondition.setEffect(z.getCommonValue());
			Condition ineqCondition = new Condition(a, threshold, Sign.NEQ);
			ineqCondition.setEffect(z.getCommonValue());
			rules.add(eqCondition);
			rules.add(ineqCondition);
		}
		else {
			List<List<Instance>> distribution = distribute(data.getElements(), list);
			int listCounter = 0;
			for (Condition c : list) {
				List<Instance> currentList = distribution.get(listCounter);
				c.setEffect(majorityVote(currentList));
				rules.add(c);
				listCounter++;
			}
		}
		// System.out.println(this.toString());
	}

	@Override
	public void buildClassifier(Dataset instances) {
		// TODO Auto-generated method stub
		datasetService = new DatasetService(instances);
		rules.clear();
		buildRules(instances);
	}

	@Override
	public Value classifyInstance(Instance instance) {
		// TODO Auto-generated method stub
		for (Condition c : rules) {
			int aIndex = datasetService.getAttributeIndex(c.getAttribute());
			Value aValue = instance.getVector().get(aIndex);
			// System.out.println(aValue.getType() == ValueType.Unknown);
			// System.out.println(c.ifTrue(aValue));
			// System.out.println(aValue + " " + aValue.getType());
			if (aValue.getType() == ValueType.Unknown) {
				return c.getMajorityClass();
			}
			if (c.ifTrue(aValue)) {
				return c.getEffect();
			}
		}

		System.out.println("here");
		return null;
	}

	@Override
	public String getName() {
		return "OneR";
	}

	public String toString() {
		StringBuilder b = new StringBuilder("OneR rule :\n");
		for (Condition c : rules) {
			b.append(String.format("if ( %s %s %s ) then %s ;\n", c.getAttribute().getName(), c.getSign(), c.getValue(), c.getEffect()));
		}
		return b.toString();
	}

	@Override
	public void setParameters(Map<String, String> parameters) {
		// do nothing
	}
}

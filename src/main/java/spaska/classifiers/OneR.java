package spaska.classifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import spaska.classifiers.util.Condition;
import spaska.classifiers.util.ContinuousValueService;
import spaska.classifiers.util.DatasetService;
import spaska.classifiers.util.NominalInfoService;
import spaska.classifiers.util.Sign;
import spaska.classifiers.util.Trees;
import spaska.data.Attribute;
import spaska.data.Attribute.ValueType;
import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.NominalValue;
import spaska.data.Value;

/**
 * A classifier that builds a one level tree which identifies what's the
 * attribute that distinguish the instances in the training set.
 */
public final class OneR implements IClassifier {
    /**
     * Get parameters for this classifier.
     * 
     * @return the parameters for this classifier
     */
    public static Map<String, String> getParameters() {
        return null;
    }

    private List<Condition> rules;

    private DatasetService datasetService;

    /**
     * Default constructor.
     */
    public OneR() {
        rules = new ArrayList<Condition>();
    }

    // get majority class of instances
    private Value majorityVote(List<Instance> instances) {
        int classIndex = datasetService.classIndex();
        int[] freq = new int[datasetService.numberOfClasses()];
        Value[] match = new NominalValue[datasetService.numberOfClasses()];
        for (Instance instance : instances) {
            int classInt = datasetService.intValue(classIndex,
                    datasetService.getClass(instance));
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

    // get children of the current node
    private List<Condition> getConditions(List<Instance> list) {
        List<Condition> children = new ArrayList<Condition>();
        boolean[] used = new boolean[datasetService.numberOfAttributes()];
        used[datasetService.classIndex()] = true;
        NominalInfoService infoService = new NominalInfoService(datasetService,
                list, used);
        ContinuousValueService continuousService = Trees.getBestNumeric(list,
                infoService.getClassesEntropy(), datasetService);
        if (infoService.isEmpty() && continuousService.isEmpty()) {
            return children;
        }
        Value majorityClass = infoService.getSiblingsMajorityClass();
        int nominalIndex = getBestNominal(infoService);
        if (nominalIndex < 0 && continuousService.isEmpty()) {
            return children;
        }
        if (continuousService.isEmpty()
                || infoService.getGainRatio(nominalIndex) >= continuousService
                        .getGainRatio()) {
            children = Trees.getNominalConditions(nominalIndex, majorityClass,
                    datasetService);
        } else {
            children = Trees.getNumericConditions(
                    continuousService.getAttributeIndex(),
                    continuousService.getSplitValue(), majorityClass,
                    datasetService);
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
        } else {
            List<List<Instance>> distribution = Trees.distribute(
                    data.getElements(), list, datasetService);
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
        return null;
    }

    @Override
    public String getName() {
        return "OneR";
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("OneR rule :\n");
        for (Condition c : rules) {
            b.append(String.format("if ( %s %s %s ) then %s ;\n", c
                    .getAttribute().getName(), c.getSign(), c.getValue(), c
                    .getEffect()));
        }
        return b.toString();
    }

    @Override
    public void setParameters(Map<String, String> parameters) {
        // do nothing
    }
}

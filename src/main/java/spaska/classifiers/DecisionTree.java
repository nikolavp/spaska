package spaska.classifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import spaska.classifiers.util.Condition;
import spaska.classifiers.util.ContinuousValueService;
import spaska.classifiers.util.DatasetService;
import spaska.classifiers.util.Node;
import spaska.classifiers.util.NominalInfoService;
import spaska.classifiers.util.Trees;
import spaska.data.Attribute.ValueType;
import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.UnknownValue;
import spaska.data.Value;
import edu.umd.cs.findbugs.annotations.SuppressWarnings;

// TODO : pruning
/**
 * A dicision tree algorithm based on ID3 and C4.5. Attributes are considered
 * for splitting according to their gain ratio. The higher the gain ratio, the
 * bigger the chance the attribute will be chosen for splitting. Numeric
 * attributes define binary split points of the form (<= value ; > value).
 * Instances with unknown attributes are copied and then spread among all
 * possible values of the splitting attribute. These instances are given weight
 * according to the popularity of the attribute value, however, at this stage
 * this weight is not considered. New instances which encounter a test, such
 * that their value for that attribute is unknown, are classified as the most
 * popular class for that branch.
 */

public final class DecisionTree implements IClassifier {
    /**
     * Get the parameters for this classifier.
     * 
     * @return the parameters for this classifier
     */
    public static Map<String, String> getParameters() {
        return null;
    }

    private Node tree; // actual tree after building classifier

    private DatasetService datasetService; // a helpful service

    // @java.lang.SuppressWarnings("unused")
    // private boolean postPrune; // whether or not to prune after building

    // check if all instances are of class defaultClass
    private boolean allHaveTheSameClass(List<Instance> instances,
            Value defaultClass) {
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

    // get children of the current node
    private List<Condition> getChildrenConditions(List<Instance> list,
            boolean[] used) {
        List<Condition> children = new ArrayList<Condition>();
        NominalInfoService infoService = new NominalInfoService(datasetService,
                list, used);
        ContinuousValueService continuousService = Trees.getBestNumeric(list,
                infoService.getClassesEntropy(), datasetService);
        if (infoService.isEmpty() && continuousService.isEmpty()) {
            return children;
        }
        Value majorityClass = infoService.getSiblingsMajorityClass();
        int nominalIndex = getBestNominal(infoService, used);
        if (nominalIndex < 0 && continuousService.isEmpty()) {
            return children;
        }
        if (continuousService.isEmpty()
                || infoService.getGainRatio(nominalIndex) >= continuousService
                        .getGainRatio()) {
            children = Trees.getNominalConditions(nominalIndex, majorityClass,
                    datasetService);
            used[nominalIndex] = true;
        } else {
            children = Trees.getNumericConditions(
                    continuousService.getAttributeIndex(),
                    continuousService.getSplitValue(), majorityClass,
                    datasetService);
        }
        return children;
    }

    private void buildTree(Node node, List<Instance> treeInstances,
            boolean[] used) {
        // stopping criteria
        // all - the same class
        Value firstClass = datasetService.getClass(treeInstances.get(0));
        if (allHaveTheSameClass(treeInstances, firstClass)) {
            Condition c = getNodeCondition(node);
            c.setEffect(firstClass);
            c.setNumber(treeInstances.size());
            return;
        }
        if (treeInstances.isEmpty()) {
            // majority vote - parent node
            Condition c = getNodeCondition(node);
            c.setEffect(c.getMajorityClass());
            return;
        }
        // zero gain - all tests

        // for each attribute (numeric or non-used nominal) calculate gain ratio
        // choose the best attribute
        List<Condition> children = getChildrenConditions(treeInstances, used);
        if (children.isEmpty()) { // no patterns extracted from data => possibly
            // unknown values
            Condition c = getNodeCondition(node);
            c.setEffect(c.getMajorityClass());
            c.setNumber(treeInstances.size());
            return;
        }
        // split on that attribute
        List<List<Instance>> distribution = Trees.distribute(treeInstances,
                children, datasetService);
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

    @Override
    public void buildClassifier(Dataset instances) {
        datasetService = new DatasetService(instances);
        tree = buildTree(instances.getElements());
    }

    @Override
    public Value classifyInstance(Instance instance) {
        return classify(instance, tree);
    }

    // recursive classification
    private Value classify(Instance instance, Node node) {
        if (isLeaf(node)) {
            return getNodeCondition(node).getEffect();
        } else {
            Value result = null;
            for (Node current : node.getChildren()) {
                if (Thread.interrupted()) {
                    return UnknownValue.getInstance();
                }
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

    @Override
    public String getName() {
        return "DecisionTree /elements from C4.5/";
    }

    /**
     * Set the parameters for this classifier.
     * 
     * @param paramName
     *            the parameter name
     * @param paramValue
     *            the parameter value
     */
    @SuppressWarnings(value = "URF_UNREAD_FIELD", justification = "This will be used when someone implement pruning")
    public void setParameters(String paramName, String paramValue) {
        if (paramName.equalsIgnoreCase("postPrune")) {
            // if (paramValue.equalsIgnoreCase("true")) {
            // postPrune = true;
            // } else {
            // postPrune = false;
            // }
        }
    }

    @Override
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

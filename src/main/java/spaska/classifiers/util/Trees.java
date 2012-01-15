package spaska.classifiers.util;

import java.util.ArrayList;
import java.util.List;

import spaska.data.Attribute;
import spaska.data.Attribute.ValueType;
import spaska.data.Instance;
import spaska.data.NumericValue;
import spaska.data.Value;

/**
 * Contain utilities for tree based classifiers.
 * 
 * @author nikolavp
 * 
 */
public final class Trees {
    private Trees() {

    }

    /**
     * Distribute instances according to the condition they satisfy.
     * 
     * @param instances
     *            the instances to distribute
     * @param conditions
     *            the conditions on which to distribute the instances
     * @param datasetService
     *            the data service for these instances
     * @return the distribution of the instances according to the given
     *         parameters
     */
    public static List<List<Instance>> distribute(List<Instance> instances,
            List<Condition> conditions, DatasetService datasetService) {
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
        int attributeIndex = datasetService.getAttributeIndex(first
                .getAttribute());
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
            c.setReach(((double) result.get(listCounter).size()) / totalKnown);
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

    /**
     * Get a service for the best numeric attribute to split on.
     * 
     * @param list
     *            the list to split
     * @param classesEntropy
     *            the classes entropy
     * @param datasetService
     *            the data service for the instances
     * @return the service for the best numeric attribute to split on
     */
    public static ContinuousValueService getBestNumeric(List<Instance> list,
            double classesEntropy, DatasetService datasetService) {
        int[] numericIndices = datasetService.getNumericIndices();
        ContinuousValueService best = ContinuousValueService
                .createEmptyService();
        ContinuousValueService currentService;
        double max = 0, currentRatio;
        for (int i = 0; i < numericIndices.length; i++) {
            currentService = new ContinuousValueService(datasetService, list,
                    numericIndices[i], classesEntropy);
            currentRatio = currentService.getGainRatio();
            if (max < currentRatio) {
                max = currentRatio;
                best = currentService;
            }
        }
        return best;
    }

    /**
     * Get conditions (nodes in the tree) for a nominal attribute.
     * 
     * @param attributeIndex
     *            the attribute index
     * @param majorityClass
     *            the majority class
     * @param datasetService
     *            the data service to use as an utility
     * @return conditions (nodes in the tree) for a nominal attribute
     * 
     */
    public static List<Condition> getNominalConditions(int attributeIndex,
            Value majorityClass, DatasetService datasetService) {
        List<Condition> children = new ArrayList<Condition>();
        Attribute a = datasetService.getAttribute(attributeIndex);
        for (Value val : datasetService.getAttributeDomain(attributeIndex)) {
            children.add(new Condition(a, val, Sign.EQ, majorityClass));
        }
        return children;
    }

    /**
     * Get conditions for a numeric attribute (binary split point).
     * 
     * @param attributeIndex
     *            the attribute index
     * @param splitValue
     *            the split value
     * @param majorityClass
     *            the majority class
     * @param datasetService
     *            the data service to use as an utility
     * @return conditions for a numeric attribute (binary split point)
     */
    public static List<Condition> getNumericConditions(int attributeIndex,
            double splitValue, Value majorityClass,
            DatasetService datasetService) {
        List<Condition> children = new ArrayList<Condition>();
        Attribute a = datasetService.getAttribute(attributeIndex);
        Value doubleValue = new NumericValue(splitValue);
        children.add(new Condition(a, doubleValue, Sign.LTE, majorityClass));
        children.add(new Condition(a, doubleValue, Sign.GT, majorityClass));
        return children;
    }

}

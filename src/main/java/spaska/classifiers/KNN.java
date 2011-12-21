package spaska.classifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spaska.data.Attribute.ValueType;
import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.NumericValue;
import spaska.data.Value;

/**
 * 
 * @author Lazar Chifudov
 */

/* Implementation of the K-Nearest Neighbor classifier */
public class KNN implements IClassifier {

    private static final Logger LOG = LoggerFactory.getLogger(KNN.class);
    private static final int DEFAULT_K = 9;
    private int k, classIndex;
    private boolean weighted;
    private DistanceQueue distanceQueue; // queue to store distances
    private List<Instance> trainSet; // list pruned of classless instances
    private Dataset originalTrainSet; // the given train set
    // //for optimization
    private ValueType[] attrTypes;
    private Value[] queryVectorArray;

    public static Map<String, String> getParameters() {
        Map<String, String> result = new HashMap<String, String>();
        result.put("k", "9");
        result.put("weighted", "false");
        return result;
    }

    public KNN() {
        setK(DEFAULT_K);
    }

    /* A class representing a measured distance and the corresponding instance */
    private static class Pair {

        double distance;
        Instance instance;

        public Pair(double distance, Instance instance) {
            this.distance = distance;
            this.instance = instance;
        }

        public Pair() {
            this(Double.MAX_VALUE, null);
        }
    }

    /* A fixed size queue for storing distances and the corresponding instances */
    private class DistanceQueue {

        Pair[] distances; // the queue

        // construct the queue given the classifier parameter K
        DistanceQueue(int k) {
            distances = new Pair[k];
        }

        // set default values
        void clear() {
            for (int i = 0; i < distances.length; ++i) {
                distances[i] = new Pair();
            }
        }

        /*
         * insert a pair it its proper place in the queue keeping the queue
         * sorted
         */
        void push(double distance, Instance instance) {
            if (distance >= distances[0].distance) {
                return; // do not insert if value is worse than the worst in
                // queue
            }

            int index = 0;
            // find the correct index of the new pair
            while (index < distances.length
                    && distance < distances[index].distance) {
                ++index;
            }
            --index;
            // shift previous pairs and insert
            for (int i = 0; i < index; ++i) {
                distances[i] = distances[i + 1];
            }
            distances[index] = new Pair(distance, instance);
        }

        /*
         * returns the most frequent class value (if discrete) or mean of class
         * values (if numeric) in the queue
         */
        Value voteForClass() {

            ValueType type = originalTrainSet.getAttributes().get(classIndex)
                    .getType();
            // 1st case: numeric class value
            if (type == ValueType.Numeric) {
                double sum = 0.0, sumWeights = 0.0;
                for (int i = 0; i < distances.length; ++i) {
                    if (distances[i].distance == 0.0) {
                        return distances[i].instance.getVector()
                                .get(classIndex);
                    }
                    double weight = weighted ? (1 / distances[i].distance)
                            : 1.0;
                    sumWeights += weight;
                    sum += weight
                            * (Double) (distances[i].instance.getVector().get(
                                    classIndex).getValue());
                }
                return new NumericValue(sum / sumWeights);
            } // 2nd case: nominal class value
            else {
                // a map for storing frequencies/weights
                Map<Value, Double> frequencies = new HashMap<Value, Double>();
                for (int i = 0; i < distances.length; ++i) {
                    if (distances[i].distance == 0.0) {
                        return distances[i].instance.getVector()
                                .get(classIndex);
                    }
                    Value currentClass = distances[i].instance.getVector().get(
                            classIndex);
                    Double oldValue = frequencies.get(currentClass);
                    double weight = weighted ? (1 / distances[i].distance)
                            : 1.0; // set weight or frequency
                    if (Double.isNaN(weight)) {
                        weight = 1.0;
                    }
                    if (oldValue == null) {
                        frequencies.put(currentClass, weight);
                    } else {
                        frequencies.put(currentClass, oldValue + weight);
                    }
                }
                // traverse the map to find the most frequent class value
                double maxFrequency = Double.MIN_VALUE;
                Value result = null;
                for (Entry<Value, Double> entry : frequencies.entrySet()) {
                    Value currentClass = entry.getKey();
                    Double currentFrequency = entry.getValue();
                    if (currentFrequency > maxFrequency) {
                        maxFrequency = currentFrequency;
                        result = currentClass;
                    }
                }
                return result;
            }
        }
    }

    /* return the KNN parameter K */
    public int getK() {
        return k;
    }

    /* set the KNN parameter K */
    public void setK(int k) {
        if (k < 1) { // if illegal value given, set to default
            LOG.error("Invalid value for k. Setting k = " + DEFAULT_K);
            k = DEFAULT_K;
        }
        this.k = k;
        // also create the distance queue
        distanceQueue = new DistanceQueue(this.k);
    }

    /* weighted or not */
    public boolean isWeighted() {
        return weighted;
    }

    /* set weighted or not */
    public void setWeighted(boolean weighted) {
        this.weighted = weighted;
    }

    /*
     * fill the queue with distances from the query instance to its K nearest
     * neighbors
     */
    private void calculateDistances(Instance query) {

        double result = 0;
        List<Value> queryVector = query.getVector();
        int vectorSize = originalTrainSet.getAttributes().size();
        // for optimization
        queryVectorArray = new Value[queryVector.size()];
        for (int i = 0; i < queryVectorArray.length; i++) {
            queryVectorArray[i] = queryVector.get(i);
        }

        for (Instance currentInstance : trainSet) {

            List<Value> currentVector = currentInstance.getVector();
            result = 0;
            for (int i = 0; i < vectorSize; ++i) {
                if (i == classIndex) {
                    // skip distance beteween class values
                    continue;
                }
                Value currentValue = currentVector.get(i);
                result += distanceBetweenValues(queryVectorArray[i],
                        currentValue, attrTypes[i]);
            }

            result = Math.sqrt(result); // distance is Euclidean
            distanceQueue.push(result, currentInstance);
        }
    }

    /* returns the distance between two values, given their type */
    private double distanceBetweenValues(Value queryValue, Value currentValue,
            ValueType attrType) {
        // 1st case: nominal values
        if (attrType == ValueType.Nominal) {

            if (queryValue.getType() != ValueType.Unknown
                    && currentValue.getType() != ValueType.Unknown) {
                if (!queryValue.equals(currentValue)) {
                    return 1;
                }
            } else if (queryValue.getType() == ValueType.Unknown
                    && currentValue.getType() == ValueType.Unknown) {
                return 0;
            } else {
                return 1;
            }
        } // 2nd case: numeric values
        else if (attrType == ValueType.Numeric) {
            if (queryValue.getType() != ValueType.Unknown
                    && currentValue.getType() != ValueType.Unknown) {
                double dst = (Double) queryValue.getValue()
                        - (Double) currentValue.getValue();
                return dst * dst;
            } else if ((queryValue.getType() == ValueType.Unknown && currentValue
                    .getType() == ValueType.Unknown)) {
                return 0;
            } else {
                return 1;
            }
        }
        return 0;
    }

    /* train the knn classifier */
    public void buildClassifier(Dataset instances) {

        originalTrainSet = instances;
        classIndex = instances.getClassIndex();
        trainSet = new ArrayList<Instance>(instances.getElements().size());
        List<Instance> givenInstances = instances.getElements();
        Iterator<Instance> it = givenInstances.iterator();
        while (it.hasNext()) { // prune instances with missing class value
            Instance current = it.next();
            if (current.getVector().get(classIndex).getType() != ValueType.Unknown) {
                trainSet.add(current);
            }
        }
        // set attribute types array
        attrTypes = new ValueType[instances.getAttributesCount()];
        for (int i = 0; i < attrTypes.length; i++) {
            attrTypes[i] = instances.getAttributes().get(i).getType();
        }

        if (k > trainSet.size()) {
            setK(trainSet.size());
            LOG.error("KNN: k >= all neighbors! Setting k to number of neighbors.");
        }
    }

    /* classify an instance after training */
    public Value classifyInstance(Instance instance) {
        distanceQueue.clear();
        calculateDistances(instance);
        return distanceQueue.voteForClass();
    }

    /* should be called after setting parameters */
    public String getName() {
        String weights = weighted ? "; weighted)" : ")";
        return "K-Nearest Neighbor (k=" + k + weights;
    }

    @Override
    public void setParameters(Map<String, String> parameters) {
        for (Entry<String, String> entry : parameters.entrySet()) {
            setParameters(entry.getKey(), entry.getValue());
        }
    }

    private void setParameters(String paramName, String paramValue) {
        if (paramName.equalsIgnoreCase("k")) {
            int kValue;
            try {
                kValue = Integer.parseInt(paramValue);
            } catch (NumberFormatException ex) {
                kValue = DEFAULT_K;
                throw new RuntimeException("\"k\" must be an integer.");
            }
            setK(kValue);
        } else if (paramName.equalsIgnoreCase("weighted")) {
            if (paramValue.equalsIgnoreCase("true")) {
                setWeighted(true);
            } else if (paramValue.equalsIgnoreCase("false")) {
                setWeighted(false);
            } else {
                throw new RuntimeException("\"weighted\" must be boolean.");
            }
        } else {
            throw new IllegalArgumentException(
                    "KNN: unknown parameter (valid: K; Weighted)");
        }
    }

    @Override
    public String toString() {
        String formatString = "K Nearest Neighbour\n  - weighted : %s\n  - k : %d";
        return String.format(formatString, weighted, k);
    }
}

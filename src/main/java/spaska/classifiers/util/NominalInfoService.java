package spaska.classifiers.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spaska.data.Attribute.ValueType;
import spaska.data.Instance;
import spaska.data.Value;

/**
 * Calculates information theory elements for all nominal attributes to support
 * best-informative-attribute decision.
 */
public final class NominalInfoService {

    private DatasetService datasetService;

    // attribute i -> value j -> class k -> number of instances of class k for
    // value j
    private Map<Value, int[]>[] statistics;

    private boolean[] usedAttributes;

    // averaged entropy for attributes
    private double[] entropy;

    // gain ratios for attributes
    private double[] gainRatio;

    // entropy for the overall distribution of classes
    private double classesEntropy;

    // the majority value among the children of a parent in the tree
    private Value siblingsMajorityClass;

    private boolean isCalculatedInfo; // if info elements are calculated

    private boolean isEmpty; // if service is an empty one

    /**
     * A constructor that will construct a nominal info service.
     * 
     * @param datasetService
     *            the data service that will be used for communication
     * @param instances
     *            the instances which will be used in the nominal info service
     * @param usedAttributes
     *            a boolean array which indicates if an attribute was already
     *            used.
     */
    public NominalInfoService(DatasetService datasetService,
            List<Instance> instances, boolean[] usedAttributes) {
        this.usedAttributes = usedAttributes.clone();
        this.entropy = new double[usedAttributes.length];
        this.gainRatio = new double[usedAttributes.length];
        this.isCalculatedInfo = false;
        this.datasetService = datasetService;
        if (allUsed()) {
            this.isEmpty = true;
        }
        initStatisticalInfo(instances);
        calculateClassEntropy();
    }

    private void initStatisticalInfo(List<Instance> instances) {
        int n = datasetService.numberOfClasses();
        int na = datasetService.numberOfAttributes();
        statistics = getNewHashMapArray(na);
        for (int k = 0; k < na; k++) {
            statistics[k] = new HashMap<Value, int[]>();
            if (datasetService.getAttribute(k).getType() == ValueType.Nominal) {
                for (Value val : datasetService.getAttributeDomain(k)) {
                    statistics[k].put(val, new int[n]);
                }
            }
        }
        for (Instance instance : instances) {
            int i = 0;
            for (Value val : instance.getVector()) {
                if (val.getType() == ValueType.Nominal
                        && statistics[i].get(val) != null) {
                    statistics[i].get(val)[datasetService.intValue(
                            datasetService.classIndex(),
                            datasetService.getClass(instance))]++;

                }
                i++;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Map<Value, int[]>[] getNewHashMapArray(int na) {
        return (Map<Value, int[]>[]) new HashMap[na];
    }

    private void calculateEntropies() {
        for (int i = 0; i < usedAttributes.length; i++) {
            if (!usedAttributes[i]
                    && datasetService.getAttribute(i).getType() == ValueType.Nominal) {
                int numValues = statistics[i].entrySet().size();
                // number[j] = number of instances with a given value
                int[] number = new int[numValues];
                double[] attributeH = new double[numValues]; // attribute
                // entropies
                int j = 0, total = 0;
                for (Map.Entry<Value, int[]> entry : statistics[i].entrySet()) {
                    int[] numerators = entry.getValue();
                    number[j] = 0;
                    for (int k = 0; k < numerators.length; k++) {
                        number[j] += numerators[k];
                    }
                    total += number[j];
                    attributeH[j] = Information.entropy(numerators, number[j]);
                    j++;
                }
                entropy[i] = Information.average(attributeH, number, total);
                gainRatio[i] = Information.entropy(number, total);
            }
        }
    }

    // calculate classes entropy + majority value
    private void calculateClassEntropy() {
        int k = datasetService.classIndex();
        int[] numerators = new int[datasetService.numberOfClasses()];
        int i = 0, total = 0, max = 0;
        for (Map.Entry<Value, int[]> entry : statistics[k].entrySet()) {
            numerators[i] = entry.getValue()[datasetService.intValue(k,
                    entry.getKey())];
            total += numerators[i];
            if (max < numerators[i]) {
                max = numerators[i];
                siblingsMajorityClass = entry.getKey();
            }
            i++;
        }
        classesEntropy = Information.entropy(numerators, total);
        // System.out.println(classesEntropy);
    }

    private void calculateInfoGainRatios() {
        calculateEntropies();
        // calculateClassEntropy();
        for (int i = 0; i < gainRatio.length; i++) {
            if (!usedAttributes[i]
                    && datasetService.getAttribute(i).getType() == ValueType.Nominal) {
                gainRatio[i] = (classesEntropy - entropy[i]) / gainRatio[i];
            }
        }
    }

    /**
     * Gets the gain ratio from this attribute index.
     * 
     * @param attributeIndex
     *            the attribute index
     * @return the double value for the gain ration from the attribute
     */
    public double getGainRatio(int attributeIndex) {
        if (!assertCalculation() || outOfBounds(attributeIndex)) {
            return 0;
        }
        return gainRatio[attributeIndex];
    }

    /**
     * Get the class entropy.
     * 
     * @return the class entropy
     */
    public double getClassesEntropy() {
        return classesEntropy;
    }

    /**
     * Get the siblings majority class for the found class entropy.
     * 
     * @return the siblings majority class
     */
    public Value getSiblingsMajorityClass() {
        assertCalculation();
        return siblingsMajorityClass;
    }

    /**
     * Checks if the attributes provided were all already used.
     * 
     * @return true if the attributes were already used and false otherwise
     */
    public boolean isEmpty() {
        return isEmpty;
    }

    private boolean assertCalculation() {
        boolean result = true;
        try {
            if (!isCalculatedInfo && !isEmpty) {
                calculateInfoGainRatios();
                isCalculatedInfo = true;
            }
        } catch (Exception e) {
            result = false;
            isEmpty = true;
        }
        return !isEmpty && result;
    }

    private boolean outOfBounds(int index) {
        return index < 0 || index > usedAttributes.length - 1;
    }

    // if all nominal atributes have already been used
    private boolean allUsed() {
        int usedCounter = 0;
        for (int i = 0; i < usedAttributes.length; i++) {
            if (usedAttributes[i]) {
                usedCounter++;
            }
        }
        return usedCounter >= datasetService.getNominalIndices().length;
    }
}

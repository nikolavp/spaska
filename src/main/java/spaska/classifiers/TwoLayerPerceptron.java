package spaska.classifiers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import spaska.classifiers.util.DatasetService;
import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.Value;

/**
 * Classifier that is based on neural two layer networks that learns with
 * backpropagation.
 */
public final class TwoLayerPerceptron implements IClassifier {
    private static final int DEFAULT_NUMBER_OF_NODES_IN_HIDDEN_LAYER = 2;
    private static final int DEFAULT_SEED = 100;
    private static final double DEFAULT_INTERVAL_LENGTH = 0.05;
    private static final int INITIAL_VALUE = 1;
    private static final int DEFAULT_NUMBER_OF_ITERATIONS = 500;
    private static final double DEFAULT_LEARNING_RATE = 0.1;

    /**
     * Get the parameters for this classifier.
     * 
     * @return the parameters for this classifier
     */
    public static Map<String, String> getParameters() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(EPOCHS_NAME, "500");
        params.put(LEARNING_RATE_NAME, "0.1");
        params.put(HIDDEN_NODES_NAME, HIDDEN_NODES_DEFAULT);
        return params;
    }

    // Private members-------------------------------------------
    private int hiddenNodes; // hidden layer - number of nodes
    private int numEpochs; // number of epochs
    private double learningRate; // number between 0 and 1
    private int numAttributes; // number of attributes
    private int numClasses; // number of different classes
    private double[][] bNet; // first matrix - output X inner
    private double[][] eNet; // second matrix - inner X output
    private double[] firstNodes; // input
    private double[] innerNodes; // hidden nodes layer
    private double[] classNodes; // output
    private double[] delta; // error in backpropagation
    private List<Instance> data;
    private Random randomObject;
    private DatasetService datasetService;
    private int[] nonclassIndices; // indices of nonclass attributes

    /*
     * whether or not to calculate default number of hidden nodes during the
     * building phase
     */
    private boolean calculateDefault;

    private static final String EPOCHS_NAME = "Number of epochs";
    private static final String LEARNING_RATE_NAME = 
            "Learning rate (between 0 and 1)";
    private static final String HIDDEN_NODES_NAME = 
            "Number of nodes in hidden layer";
    private static final String HIDDEN_NODES_DEFAULT = 
            "(attributes + classes)/2";

    /**
     * Default constructor.
     */
    public TwoLayerPerceptron() {
        this(DEFAULT_NUMBER_OF_NODES_IN_HIDDEN_LAYER,
                DEFAULT_NUMBER_OF_ITERATIONS, DEFAULT_LEARNING_RATE,
                DEFAULT_SEED);
    }

    /**
     * Construct a two layer percepttron with the given configuration.
     * 
     * @param hiddenNodes
     *            number of hidden nodes in the neural network
     * @param epochs
     *            the maximum number of iterations that will be run while
     *            learning
     * @param learningRate
     *            the learning rate at which the neural network will accept new
     *            weights
     * @param seed
     *            the seed that will be used in the initial random generation of
     *            weights
     */
    public TwoLayerPerceptron(int hiddenNodes, int epochs, double learningRate,
            int seed) {
        initParameters(hiddenNodes, epochs, learningRate);
        this.randomObject = new Random(seed);
        this.calculateDefault = true;
    }

    // --------------------------------------------------------------

    // Initializers ------------------------------------------------
    private void initNets() {
        firstNodes = new double[numAttributes];
        innerNodes = new double[hiddenNodes];
        classNodes = new double[numClasses];
        delta = new double[numClasses];
        bNet = new double[numAttributes][hiddenNodes];
        eNet = new double[hiddenNodes][numClasses];
        for (int i = 0; i < bNet.length; i++) {
            for (int j = 0; j < bNet[0].length; j++) {
                bNet[i][j] = randomWeight();
            }
        }
        for (int i = 0; i < eNet.length; i++) {
            for (int j = 0; j < eNet[0].length; j++) {
                eNet[i][j] = randomWeight();
            }
        }
    }

    private void initParameters(int h, int epochs, double lr) {
        this.hiddenNodes = h;
        this.numEpochs = epochs;
        this.learningRate = lr;
    }

    // --------------------------------------------------------------

    /**
     * Set the number of nodes in the hidden layer of the neural network.
     * 
     * @param number
     *            the new value for the number of hidden layer nodes
     */
    public void setHiddenNodes(int number) {
        if (number <= 0) {
            this.hiddenNodes = 2;
        } else {
            this.hiddenNodes = number;
        }
    }

    /**
     * Get the number of nodes in the hidden layer of the neural network.
     * 
     * @return the number of hidden layer nodes
     */
    public int getHiddenNodes() {
        return this.hiddenNodes;
    }

    /**
     * Set the maxmimum number of iterations that will be run while learning.
     * 
     * @param number
     *            the maximum number of iterations that will be run while
     *            learning
     */
    public void setNumberOfEpochs(int number) {
        if (number <= 0) {
            this.numEpochs = DEFAULT_NUMBER_OF_ITERATIONS;
        } else {
            this.numEpochs = number;
        }
    }

    /**
     * Get the maximum number of iterations that in the learning process.
     * 
     * @return the maxmimum number of iterations in the learning process.
     */
    public int getNumberOfEpochs() {
        return this.numEpochs;
    }

    /**
     * Set the learning rate for the neural network.
     * 
     * @param rate
     *            the new learning rate for the neural network
     */
    public void setLearningRate(double rate) {
        if (rate <= 0 || rate >= 1) {
            this.learningRate = DEFAULT_LEARNING_RATE;
        } else {
            this.learningRate = rate;
        }
    }

    /**
     * Get the learning rate for the neural network.
     * 
     * @return the learning rate for the neural network
     */
    public double getLearningRate() {
        return this.learningRate;
    }

    private double randomWeight() {
        int sign = randomObject.nextInt(2) > 0 ? 1 : -1;
        return randomObject.nextDouble() * DEFAULT_INTERVAL_LENGTH * sign;
    }

    // get double representation of attribute value
    private double getDoubleValue(Instance instance, int attributeIndex) {
        Value attributeValue = instance.getVector().get(attributeIndex);
        double result = 0;
        switch (attributeValue.getType()) {
        case Numeric:
            result = (Double) attributeValue.getValue();
            break;
        case Nominal:
            result = datasetService.intValue(attributeIndex, 
                    attributeValue) + 1;
            if (attributeIndex == datasetService.classIndex()) {
                result--;
            }
            break;
        default:
            break;
        }
        return result;
    }

    // forward pass in the network
    private void propagateForward(Instance instance) {
        double sum;
        for (int i = 0; i < nonclassIndices.length; i++) {
            firstNodes[i] = getDoubleValue(instance, nonclassIndices[i]);
        }
        for (int i = 0; i < hiddenNodes; i++) {
            innerNodes[i] = 0;
            sum = 0;
            for (int j = 0; j < nonclassIndices.length; j++) {
                sum += firstNodes[j] * bNet[j][i];
            }
            innerNodes[i] = aFunc(sum);
        }
        for (int i = 0; i < numClasses; i++) {
            classNodes[i] = 0;
            sum = 0;
            for (int j = 0; j < hiddenNodes; j++) {
                sum += innerNodes[j] * eNet[j][i];
            }
            classNodes[i] = aFunc(sum);
        }
    }

    private void propagateBackward(Instance instance) {
        double t = INITIAL_VALUE;
        for (int j = 0; j < numClasses; j++) {
            t = target(j, getDoubleValue(instance, 
                    datasetService.classIndex()));
            delta[j] = derivative(classNodes[j]) * (t - classNodes[j]);
            for (int i = 0; i < hiddenNodes; i++) {
                eNet[i][j] += delta[j] * innerNodes[i] * learningRate;
            }
        }
        double deltaJ, sum;
        for (int j = 0; j < hiddenNodes; j++) {
            deltaJ = derivative(innerNodes[j]);
            sum = 0;
            for (int k = 0; k < numClasses; k++) {
                sum += delta[k] * eNet[j][k];
            }
            deltaJ *= sum;
            for (int i = 0; i < nonclassIndices.length; i++) {
                bNet[i][j] += deltaJ * firstNodes[i] * learningRate;
            }
        }
    }

    // (0, 1)
    private double sigmoid(double y) {
        return 1 / (1 + Math.exp(-y));
    }

    private double dSigmoid(double node) {
        return node * (1 - node);
    }

    // activation function
    private double aFunc(double y) {
        return sigmoid(y);
    }

    // derivative of activation function
    private double derivative(double node) {
        return dSigmoid(node);
    }

    // target
    private double target(double current, double classValue) {
        return current == classValue ? 1 : 0;
    }

    // training with a preset number of epochs
    private void train() {
        for (int i = 0; i < numEpochs; i++) {
            for (int j = 0; j < data.size(); j++) {
                propagateForward(data.get(j));
                propagateBackward(data.get(j));
            }
        }
    }

    // max number in a double array
    private int max(double[] a) {
        int index = 0;
        double max = Integer.MIN_VALUE;
        for (int i = 0; i < a.length; i++) {
            if (max < a[i]) {
                max = a[i];
                index = i;
            }
        }
        return index;
    }

    // IClassifier members -----------------------------------------------
    @Override
    public void buildClassifier(Dataset instances) {

        if (instances == null || instances.getElements().size() <= 0) {
            throw new NullPointerException(
                    "Two Layer Perceptron : Instances cannot be null");
        }

        this.datasetService = new DatasetService(instances);
        this.data = instances.getElements();
        this.numAttributes = datasetService.numberOfAttributes();
        this.numClasses = datasetService.numberOfClasses();
        if (calculateDefault) {
            this.hiddenNodes = (numAttributes + numClasses) / 2;
        }
        this.nonclassIndices = new int[numAttributes - 1];
        int j = 0;
        for (int i = 0; i < numAttributes; i++) {
            if (i != datasetService.classIndex()) {
                nonclassIndices[j++] = i;
            }
        }
        initNets();
        train();
    }

    @Override
    public Value classifyInstance(Instance instance) {
        propagateForward(instance);
        int intValue = max(classNodes);
        return datasetService.getValueFromInt(datasetService.classIndex(),
                intValue);
    }

    @Override
    public String getName() {
        return "TwoLayerPerceptron";
    }

    @Override
    public void setParameters(Map<String, String> parameters) {
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            setParameter(entry.getKey(), entry.getValue());
        }
    }

    private void setParameter(String paramName, String paramValue) {
        if (paramName.equalsIgnoreCase(EPOCHS_NAME)) {
            try {
                setNumberOfEpochs(Integer.parseInt(paramValue));
            } catch (Exception e) {
                setNumberOfEpochs(DEFAULT_NUMBER_OF_ITERATIONS);
            }
            return;
        }
        if (paramName.equalsIgnoreCase(LEARNING_RATE_NAME)) {
            try {
                setLearningRate(Double.parseDouble(paramValue));
            } catch (Exception e) {
                setLearningRate(DEFAULT_LEARNING_RATE);
            }
            return;
        }
        if (paramName.equalsIgnoreCase(HIDDEN_NODES_NAME)) {
            if (paramValue.equalsIgnoreCase(HIDDEN_NODES_DEFAULT)) {
                calculateDefault = true;
            } else {
                try {
                    setHiddenNodes(Integer.parseInt(paramValue));
                    calculateDefault = false;
                } catch (Exception e) {
                    calculateDefault = true;
                }
            }
            return;
        }
    }

    @Override
    public String toString() {
        String formatString = "Two Layer Perceptron \n  "
                + "- epochs : %d\n  - learning rate : %.3f\n"
                + "  - hidden nodes : %d\n";
        return String
                .format(formatString, numEpochs, learningRate, hiddenNodes);
    }
}

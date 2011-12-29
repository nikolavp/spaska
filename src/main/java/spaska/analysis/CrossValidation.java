package spaska.analysis;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spaska.classifiers.IClassifier;
import spaska.data.Attribute.ValueType;
import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.NominalValue;
import spaska.data.Value;
import spaska.statistics.ClassifierStatistics;

/**
 * An analyzer that implements crossvalidation - common way of evaluation the
 * performance of a single classifier.
 * 
 * Crossvalidation is mostly used when the number of examples is pretty limited
 * so we have the problem of splitting our examples into training and testing
 * sets. We want to use every example for training and for testing so we do the
 * following:
 * <ol>
 * <li>split the examples into <em>n</em> subsets called folds</li>
 * <li>for every fold, we train the classifier on the other n-1 folds and
 * evaluate on the former</li>
 * </ol>
 * 
 */
public final class CrossValidation implements IAnalyzer {

    private static final int DEFAULT_NUMBER_OF_FOLDS = 10;

    /**
     * Get the valid parameters for this analyzer.
     * 
     * @return a map of valid parameters that can be set
     */
    public static Map<String, String> getParameters() {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(FOLDS, String.valueOf(DEFAULT_NUMBER_OF_FOLDS));
        return parameters;
    }

    private static final Logger LOG = LoggerFactory
            .getLogger(CrossValidation.class);

    private static final String FOLDS = "folds";

    private int folds;
    private Dataset data;
    private IClassifier classifier;
    private int seed;

    /**
     * Constructor for the crossvalidationa analyzer.
     * 
     * @param folds
     *            the number of folds that will be used
     * @param data
     *            the data which will be used for the evaluation
     * @param classifier
     *            the classifier that needs to be evaluated
     */
    public CrossValidation(int folds, Dataset data, IClassifier classifier) {
        this.folds = folds;
        this.data = data;
        this.classifier = classifier;
    }

    /**
     * A default constructor.
     * 
     * This will set the number of folds to 10
     */
    public CrossValidation() {
        this.folds = DEFAULT_NUMBER_OF_FOLDS;
    }

    /**
     * Get the number of folds into which that data will be split.
     * 
     * @return the number of folds
     */
    public int getFolds() {
        return folds;
    }

    /**
     * Set the number of folds into which the data will be split.
     * 
     * @param folds
     *            the new number of folds
     */
    public void setFolds(int folds) {
        this.folds = folds;
    }

    /**
     * Get the dataset on which the the classifier will be evaluated.
     * 
     * @return the dataset which will be used for evaluation
     */
    public Dataset getData() {
        return data;
    }

    /**
     * Set the datase which will be used for the crossvalidation.
     * 
     * @param data
     *            the dataset which be used for crossvalidation
     */
    public void setData(Dataset data) {
        this.data = data;
    }

    /**
     * Get the classifier that will be crossvalidated.
     * 
     * @return the classifier that will be crossvalidated
     */
    public IClassifier getClassifier() {
        return classifier;
    }

    /**
     * Set the classifier that will be crossvalidated.
     * 
     * @param classifier
     *            the classifier the will be crossvalidated
     */
    public void setClassifier(IClassifier classifier) {
        this.classifier = classifier;
    }

    /**
     * Get the seed that will be used to randomize the data while buildings the
     * folds.
     * 
     * @return the seed that will be used to randomize the folds
     */
    public int getSeed() {
        return seed;
    }

    /**
     * Set the seed that will be used to randomize the data while buildings the
     * folds.
     * 
     * @param seed
     *            the new seed value that will be used to randomize the folds
     */
    public void setSeed(int seed) {
        this.seed = seed;
    }

    @Override
    public ClassifierStatistics analyze(Dataset dataset) {
        setData(dataset);

        long startTime = System.currentTimeMillis();

        // how many elements will have every dataset
        int[] datasetsCount = new int[folds];

        List<Instance> instances = data.getElements();
        int instancesCount = instances.size();

        int instancesInGroup = instancesCount / folds;
        Arrays.fill(datasetsCount, instancesInGroup);
        int cnt = instancesInGroup * folds;
        int tmpCnt = 0;
        while (cnt++ < instancesCount) {
            datasetsCount[tmpCnt++]++;
        }

        Dataset[] datasets = data.getSubDataSets(datasetsCount, seed);
        int classIndex = data.getClassIndex();
        ValueType type = data.getAttributes().get(classIndex).getType();
        ClassifierStatistics result = new ClassifierStatistics(type);
        if (type == ValueType.Nominal) {
            result.setClassNames(data.getAllClassNamesArray());
        }
        result.setAlgorithmName(classifier.getName());

        for (int testInd = 0; testInd < folds; testInd++) {
            // build classifier for all test datasets
            // first merge them
            Dataset[] testDatasets = new Dataset[datasets.length - 1];
            tmpCnt = 0;
            Dataset testDataset = null;
            for (int i = 0; i < folds; i++) {
                if (i == testInd) {
                    testDataset = datasets[i];
                    continue;
                }
                testDatasets[tmpCnt++] = datasets[i];
            }
            Dataset trainDataset = Dataset.merge(testDatasets);

            classifier.buildClassifier(trainDataset);

            // test classifier
            for (Instance currentInstance : testDataset.getElements()) {
                Value v = classifier.classifyInstance(currentInstance);

                switch (v.getType()) {
                case Nominal:
                    result.addNominalInfo(data.getClassName(currentInstance),
                            ((NominalValue) v).getValue());
                    break;
                case Numeric:
                    result.addNumericInfo(
                            (Double) data.getClassValue(currentInstance)
                                    .getValue(), (Double) v.getValue());
                    break;
                default:
                    throw new RuntimeException("other attribute type added");
                }
            }
        }

        result.setTestTime(System.currentTimeMillis() - startTime);
        result.setAdditionalInfo(String.format(
                "Additional info :\n------------------\n%s",
                classifier.toString()));
        return result;
    }

    @Override
    public void setParameters(Map<String, String> parameters) {
        boolean ex = false;
        for (Entry<String, String> entry : parameters.entrySet()) {
            String param = entry.getKey();
            if (param.equalsIgnoreCase(FOLDS)) {
                try {
                    folds = Integer.parseInt(entry.getValue());
                } catch (NumberFormatException e) {
                    LOG.warn("Value {} for folds was not set as it's "
                            + "not a number!", entry.getValue(), e);
                }
            } else {
                ex = true;
            }
        }
        if (ex) {
            throw new IllegalArgumentException(
                    "SimpleKMeans: unknown parameter (valid: K)");
        }
    }

}

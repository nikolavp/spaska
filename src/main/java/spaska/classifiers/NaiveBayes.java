package spaska.classifiers;

import static java.lang.Math.PI;
import static java.lang.Math.exp;
import static java.lang.Math.sqrt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsc.descriptive.MeanVar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DistLib.logistic;

import spaska.classifiers.util.DatasetService;
import spaska.data.Attribute;
import spaska.data.Attribute.ValueType;
import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.NumericValue;
import spaska.data.Value;

/**
 * A simple implementation of the Naive Bayes classifier. Probabilistic
 * classifier based on applying Bayes' theorem with strong (naive) independence
 * assumptions. In simple terms, a naive Bayes classifier assumes that the
 * presence (or absence) of a particular feature of a class is unrelated to the
 * presence (or absence) of any other feature, given the class variable. Note:
 * The algorithm uses density function to calculate the posterior probability of
 * every class based on the instances' features
 * 
 * @see http://en.wikipedia.org/wiki/Naive_Bayes_classifier
 * @author Gergana Dzhumerkova
 * 
 */
public class NaiveBayes implements IClassifier {
    private static final Logger LOG = LoggerFactory.getLogger(NaiveBayes.class);

    /**
     * Get the default parameters for this classifier(empty hashmap).
     * 
     * @return the default parameters for this classifier(empty hashmap)
     */
    public static Map<String, String> getParameters() {
        return new HashMap<String, String>();
    }

    /**
     * A decorator for the {@link MeanVar} class from jsc. Used with purpose to
     * override toString() method in order to ease the debugging process.
     * 
     * @author Gergana Dzhumerkova
     * 
     */
    private static class MeanVariance {
        private MeanVar meanVar;

        public MeanVariance() {
            this.meanVar = new MeanVar(0.0D);
        }

        double getMean() {
            return meanVar.getMean();
        }

        double getVariance() {
            return meanVar.getVariance();
        }

        @Override
        public String toString() {
            return meanVar.getMean() + " : " + meanVar.getVariance();
        }

        public void addValue(double attributeValue) {
            meanVar.addValue(attributeValue);
        }
    }

    private MeanVariance[][] values;
    private DatasetService dataService;

    @Override
    public void setParameters(Map<String, String> parameters) {
    }

    @Override
    public void buildClassifier(Dataset instances) {
        dataService = new DatasetService(instances);
        int numberOfClasses = dataService.numberOfClasses();
        values = new MeanVariance[numberOfClasses][dataService
                .numberOfAttributes() - 1];

        for (Attribute a : instances.getAttributes()) {
            int attributeIndex = dataService.getAttributeIndex(a);
            if (attributeIndex == dataService.classIndex()) {
                continue;
            }
            for (Instance instance : instances.getElements()) {
                int classAttributeIndex = dataService.classIndex();
                Value clazz = dataService.getClass(instance);
                int classIndex = dataService.intValue(classAttributeIndex,
                        clazz);
                MeanVariance meanVar = getMeanVar(classIndex, attributeIndex);

                Value value = instance.getVector().get(attributeIndex);
                double attributeValue = getDoubleValue(attributeIndex, value);
                meanVar.addValue(attributeValue);
            }
        }
    }

    private MeanVariance getMeanVar(int classIndex, int attributeIndex) {
        MeanVariance meanVar = values[classIndex][attributeIndex];
        if (meanVar == null) {
            values[classIndex][attributeIndex] = new MeanVariance();
        }
        return values[classIndex][attributeIndex];
    }

    private double getDoubleValue(int attributeIndex, Value value) {
        if (ValueType.Numeric.equals(value.getType())) {
            return ((NumericValue) value).getValue();
        } else if (ValueType.Nominal.equals(value.getType())) {
            return dataService.intValue(attributeIndex, value) + 1;
        } else if (ValueType.Unknown.equals(value.getType())) {
            return 0.0D;
        } else {
            throw new UnsupportedOperationException("Invalid value type!");
        }
    }

    static double normalDensityF(double mean, double variance, double x) {
        if (variance == 0.0D) {
            return 0.0D;
        }
        double exp = exp((-(x - mean) * (x - mean)) / (2 * variance));
        double d = 1 / (sqrt(2 * PI * variance));
        return d * exp;
    }

    @Override
    public Value classifyInstance(Instance instance) {

        double classProbability = 1.0D / values.length;
        int maxClassIndex = -1;
        double maxPosteriorNumerator = -Double.MAX_VALUE;

        List<Value> vector = instance.getVector();
        for (int classIndex = 0; classIndex < values.length; classIndex++) {
            double posteriorNumerator = classProbability;
            if (values[classIndex][0] == null) {
                continue;
            }
            for (int attributeIndex = 0; attributeIndex < values[0].length; attributeIndex++) {
                double mean = values[classIndex][attributeIndex].getMean();
                double variance = values[classIndex][attributeIndex]
                        .getVariance();
                double value = getDoubleValue(attributeIndex,
                        vector.get(attributeIndex));
                double density = normalDensityF(mean, variance, value);
                posteriorNumerator *= density;
            }
            if(Double.isNaN(posteriorNumerator)
                    || Double.isInfinite(posteriorNumerator)){
                LOG.warn("Ignoring class value as posterior probability is non a valid double value!");
            }
            if (posteriorNumerator > maxPosteriorNumerator) {
                maxPosteriorNumerator = posteriorNumerator;
                maxClassIndex = classIndex;
            }
        }
        Value value = dataService.getValueFromInt(dataService.classIndex(),
                maxClassIndex);
        return value;
    }

    @Override
    public String getName() {
        return null;
    }

}

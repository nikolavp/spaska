package spaska.analysis;

import spaska.classifiers.IClassifier;
import spaska.data.Dataset;
import spaska.gui.Parametrable;
import spaska.statistics.ClassifierStatistics;

/**
 * An interface that represents all analyzers that can test the performance of a
 * given classifier or other characteristics.
 */
public interface IAnalyzer extends Parametrable {
    /**
     * Get the classifier that will be analysed.
     * 
     * @return the classifier that was bound to this analyzer.
     */
    IClassifier getClassifier();

    /**
     * Set the classifier that will be analysed.
     * 
     * @param classifier
     *            the classifier that will be analysed
     */
    void setClassifier(IClassifier classifier);

    /**
     * Analyze the classifier that was set on the given dataset.
     * 
     * @param testSet
     *            the dataset on which to analyze the classifier
     * @return the result from the analysis
     */
    ClassifierStatistics analyze(Dataset testSet);

}

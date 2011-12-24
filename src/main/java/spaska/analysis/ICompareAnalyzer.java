package spaska.analysis;

import spaska.classifiers.IClassifier;
import spaska.data.Dataset;
import spaska.gui.Parametrable;
import spaska.statistics.CompareStatistics;

/**
 * An interface that represents all analyzers that operate on two classifiers
 * and compare their performance.
 * 
 * Classes that implement this interface can be used to compare two independent
 * classifiers on a testing dataset.
 */
public interface ICompareAnalyzer extends Parametrable {
    /**
     * Get the first classifier that will be analysed
     * 
     * @return the first classifier that will be analysed
     */
    public IClassifier getClassifier1();

    /**
     * Set the first classifier that will be analysed
     * 
     * @param classifier1
     *            the classifier that will be analysed.
     */
    public void setClassifier1(IClassifier classifier1);

    /**
     * Get the second classifier that will be analysed
     * 
     * @return the second classifier that will be analysed
     */
    public IClassifier getClassifier2();

    /**
     * Set the second classifier that will be analysed
     * 
     * @param classifier2
     *            the second classifier that will be analysed
     */
    public void setClassifier2(IClassifier classifier2);

    /**
     * Analyse/compare the two classifiers on the given dataset.
     * 
     * @param dataSet
     *            the dataset on which the classifiers will be compared
     * @return the result from the comparison
     * @throws Exception
     *             if there was an error while comparing the classifiers
     */
    public CompareStatistics analyze(Dataset dataSet) throws Exception;

}

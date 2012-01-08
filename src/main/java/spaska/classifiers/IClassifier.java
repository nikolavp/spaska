package spaska.classifiers;

import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.Value;
import spaska.gui.Parametrable;

/**
 * An interface representing all classifiers. Classifiers are objects that given
 * some example instances with known label/class should be able to build a model
 * that will allow them to label/classify other unknown instances.
 */
public interface IClassifier extends Parametrable {
    /**
     * A method that should be used to build the model for the classifier from
     * the given {@link Dataset} of example instances.
     * 
     * @param instances
     *            the example instances
     */
    void buildClassifier(Dataset instances);

    /**
     * Classify a single instance and return it's class/label.
     * 
     * @param instance
     *            the instance to be classifed
     * @return the class/label that this instance belongs to
     */
    Value classifyInstance(Instance instance);

    /**
     * Returns the name of the classifier algorithm.
     * 
     * @return the name of the classifier
     */
    String getName();
}

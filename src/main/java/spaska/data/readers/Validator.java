package spaska.data.readers;

import spaska.data.Dataset;
import spaska.gui.Parametrable;

/**
 * An interface that represents the validators in the framework.
 * 
 * @author Vesko Georgiev
 */
public interface Validator extends Parametrable {

    /**
     * Set the dataset on which this validator/preprocessor will operate.
     * 
     * @param dataset
     *            the dataset the will be normalised
     */
    void setDataset(Dataset dataset);

    /**
     * Run the validator on the dataset to normalise the attributes in the
     * instances.
     * 
     * @return true if the dataset is valida and false otherwise
     */
    boolean validate();
}

package spaska.data.readers;

import java.util.LinkedList;
import java.util.List;

import spaska.data.Dataset;

/**
 * This is a convenient base class for {@link InputReader}. InputReaders can
 * extend this class to get some of the functionality.
 * 
 */
public abstract class AbstractInputReader implements InputReader {
    /**
     * A list of preprocessors/validators for this input reader.
     */
    private List<Validator> validators;
    /**
     * The resulting dataset from reading the input.
     */
    private Dataset dataset;

    protected AbstractInputReader() {
        setValidators(new LinkedList<Validator>());
    }

    @Override
    public final void addValidator(Validator validator) {
        getValidators().add(validator);
    }

    /**
     * Get the list of preprocessors/validators for this input reader.
     * 
     * @return the list of preprocessors/validators for this input reader
     */
    public final List<Validator> getValidators() {
        return validators;
    }

    protected Dataset getDataset() {
        return dataset;
    }

    protected void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    protected void setValidators(List<Validator> validators) {
        this.validators = validators;
    }
}

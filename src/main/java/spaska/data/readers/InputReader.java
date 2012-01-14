package spaska.data.readers;

import java.util.List;

import spaska.data.Dataset;

/**
 * An input reader represents classes that will be able to read datasets from
 * different sources.
 * 
 * Currently we support arff files and a database source is under development.
 */
public interface InputReader {
    /**
     * Add validators/preprocessors that will be used while reading the data
     * from the source.
     * 
     * @param validator
     *            a new validator to be added to the preprocessing pipeline
     */
    void addValidator(Validator validator);

    /**
     * Build a new dataset from the datasource and return it.
     * 
     * @return the dataset from the datasource that was built
     */
    Dataset buildDataset();

    /**
     * Get the pipeline of validators/preprocessors.
     * 
     * @return the pipeline of validators/preprocessors
     */
    List<Validator> getValidators();

}

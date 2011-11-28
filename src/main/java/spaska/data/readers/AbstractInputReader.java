package spaska.data.readers;

import java.util.LinkedList;
import java.util.List;

import spaska.data.Dataset;

public abstract class AbstractInputReader implements InputReader {

	protected List<Validator>	validators;
	protected Dataset			dataset;

	protected AbstractInputReader() {
		validators = new LinkedList<Validator>();
	}

	@Override
	public void addValidator(Validator validator) {
		validators.add(validator);
	}

	public List<Validator> getValidators() {
		return validators;
	}
}

package spaska.data.readers;

import java.util.List;

import spaska.data.Dataset;

public interface InputReader {

	void addValidator(Validator validator);

	Dataset buildDataset();

	List<Validator> getValidators();

}

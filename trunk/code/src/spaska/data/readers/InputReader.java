package spaska.data.readers;

import java.util.List;

import spaska.data.Dataset;

public interface InputReader {

	public void addValidator(Validator validator);

	public Dataset buildDataset();

	public List<Validator> getValidators();

}

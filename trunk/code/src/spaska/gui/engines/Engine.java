package spaska.gui.engines;

import java.io.File;
import java.util.Map;
import java.util.Vector;

import spaska.data.Dataset;
import spaska.data.readers.ARFFInputReader;
import spaska.data.readers.InputReader;
import spaska.data.readers.NormalizeValidator;
import spaska.data.readers.Validator;
import spaska.gui.InputException;
import spaska.statistics.Statistics;

public abstract class Engine {

	private static final String						ARFF	= "arff";

	protected Dataset								dataset;
	protected InputReader							reader;
	protected Vector<Class<? extends Validator>>	validators;

	public Engine() {
		validators = new Vector<Class<? extends Validator>>();
		validators.add(NormalizeValidator.class);
	}

	protected Dataset getDataset() {
		return dataset;
	}

	protected void setDataset() throws Exception {
		if (reader != null) {
			dataset = reader.buildDataset();
		}
		else {
			throw new InputException("Please set the file first");
		}
	}

	public void setFile(File file) throws Exception {
		if (file == null) {
			throw new InputException("Please choose a file");
		}
		else {
			if (file.getName().endsWith(ARFF)) {
				reader = new ARFFInputReader(file);
			}
		}
	}

	public Vector<Class<? extends Validator>> getValidators() {
		return validators;
	}

	public void addValidator(Validator v, Map<String, String> params) throws Exception {
		if (v != null) {
			if (params != null) {
				v.setParameters(params);
			}
			if (reader != null) {
				reader.addValidator(v);
			}
			else {
				throw new InputException("Please set the File first");
			}
		}
		else {
			throw new InputException("Please choose a validator");
		}
	}

	public void check() throws Exception {
		if (reader == null) throw new InputException("Please set the file first"); 
	}

	public void reset() {
		dataset = null;
		reader = null;
	}

	public abstract Statistics start() throws Exception;
}

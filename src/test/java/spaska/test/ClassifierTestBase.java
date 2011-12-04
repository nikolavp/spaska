package spaska.test;

import java.net.URL;

import spaska.classifiers.KNN;
import spaska.data.Dataset;
import spaska.data.readers.ARFFInputReader;

public abstract class ClassifierTestBase {
	protected static Dataset getDataSet(String dataSetPath) {
		URL resource = KNN.class.getResource("/data/" + dataSetPath + ".arff");
		if (resource == null) {
			throw new IllegalArgumentException("Dataset from path "
					+ dataSetPath + " was not found in the tests resources! ");
		}
		String dataSetFilePath = resource.getFile();
		ARFFInputReader inputReader = new ARFFInputReader(dataSetFilePath);
		return inputReader.buildDataset();
	}
}

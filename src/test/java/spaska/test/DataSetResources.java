package spaska.test;

import java.net.URL;

import spaska.classifiers.KNN;
import spaska.data.Dataset;
import spaska.data.readers.ARFFInputReader;
import spaska.data.readers.Validator;

public final class DataSetResources {
    
    private ARFFInputReader inputReader;

    public DataSetResources(String dataSetPath){
        URL resource = KNN.class.getResource("/data/" + dataSetPath + ".arff");
        if (resource == null) {
            throw new IllegalArgumentException("Dataset from path "
                    + dataSetPath + " was not found in the tests resources! ");
        }
        String dataSetFilePath = resource.getFile();
        inputReader = new ARFFInputReader(dataSetFilePath);
    }
    
    public DataSetResources withValidator(Validator validator){
        inputReader.addValidator(validator);
        return this;
    }
    
    public Dataset getDataSet(){
        return inputReader.buildDataset();
    }

    public static Dataset getDataSet(String dataSetPath) {
        return new DataSetResources(dataSetPath).getDataSet();
    }

}

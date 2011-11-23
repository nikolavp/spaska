package spaska.test;

import java.util.Iterator;

import spaska.classifiers.KNN;
import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.readers.ARFFInputReader;

/**
 *
 * @author Lazar Chifudov
 */
public class KNNTest {

    public static void main(String[] args) {
        int k = 11;
        KNN knn = new KNN();
        knn.setK(k);
        knn.setWeighted(true);
        ARFFInputReader input = new ARFFInputReader(".\\data\\iris-train.arff");
//        input.addValidator(new TypeValidator());

        Dataset traindata = input.buildDataset();
        knn.buildClassifier(traindata);

        ARFFInputReader input2 = new ARFFInputReader(".\\data\\iris-test.arff");
//        input2.addValidator(new TypeValidator());

        Dataset testdata = input2.buildDataset();
        System.out.println(knn.getName());
        Iterator<Instance> it = testdata.getElements().iterator();
        while (it.hasNext()) {
            Instance i = it.next();
            System.out.print(i.getVector() + " classified as: ");
            System.out.println(knn.classifyInstance(i));
        }
    }
}

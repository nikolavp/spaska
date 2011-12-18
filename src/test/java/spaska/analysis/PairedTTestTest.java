package spaska.analysis;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spaska.classifiers.DecisionTree;
import spaska.classifiers.KNN;
import spaska.data.Dataset;
import spaska.test.DataSetResources;

public class PairedTTestTest {

    private static final Logger LOG = LoggerFactory
            .getLogger(PairedTTestTest.class);

    @Test
    public void shouldBeAbleToBeUsed() throws Exception {
        PairedTTest ptt = new PairedTTest();
        ptt.setAlpha(0.05); // alpha - error type I
        ptt.setTestType(TestType.TwoSided); // test type : OneSidedLessThan/
        // OneSidedGreaterThan/ TwoSided
        ptt.setClassifier1(new KNN());
        ptt.setClassifier2(new DecisionTree());
        Dataset dataSet = DataSetResources.getDataSet("iris");
        LOG.info(ptt.analyze(dataSet).toString());
    }

}

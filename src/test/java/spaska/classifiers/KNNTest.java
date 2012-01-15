package spaska.classifiers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spaska.test.ClassifierTestBase;

public class KNNTest extends ClassifierTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(KNNTest.class);

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected TestDescriptor getDescriptor() {
        return new TestDescriptor().iris(0.966, 0.966);
    }

    @Override
    protected IClassifier getClassifier() {
        KNN knn = new KNN();
        knn.setK(11);
        knn.setWeighted(true);
        return knn;
    }

}

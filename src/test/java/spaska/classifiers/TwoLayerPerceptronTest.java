package spaska.classifiers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spaska.test.ClassifierTestBase;

public class TwoLayerPerceptronTest extends ClassifierTestBase {
    private static final Logger LOG = LoggerFactory
            .getLogger(TwoLayerPerceptronTest.class);

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected TestDescriptor getDescriptor() {
        return new TestDescriptor().iris(0.946, 0.946)
                .vote(0.947, 0.945)
                .soybean(0.909, 0.899);
    }

    @Override
    protected IClassifier getClassifier() {
        return new TwoLayerPerceptron();
    }
}

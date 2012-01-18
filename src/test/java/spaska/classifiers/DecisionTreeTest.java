package spaska.classifiers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spaska.test.ClassifierTestBase;

public class DecisionTreeTest extends ClassifierTestBase {
    private static final Logger LOG = LoggerFactory
            .getLogger(DecisionTreeTest.class);

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected TestDescriptor getDescriptor() {
        return new TestDescriptor().iris(0.94, 0.94)
                .vote(0.89D, 0.915D)
                .soybean(0.703, 0.683D);
    }

    @Override
    protected IClassifier getClassifier() {
        return new DecisionTree();
    }
}

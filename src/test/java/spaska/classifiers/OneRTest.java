package spaska.classifiers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spaska.test.ClassifierTestBase;

public class OneRTest extends ClassifierTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(OneRTest.class);

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected TestDescriptor getDescriptor() {
        return new TestDescriptor().iris(0.62, 0.65)
                .soybean(0.0139D, 0.0516D)
                .vote(0.9507D, 0.958D);
        
    }

    @Override
    protected IClassifier getClassifier() {
        return new OneR();
    }
}

package spaska.classifiers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spaska.test.ClassifierTestBase;

public class ZeroRTest extends ClassifierTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(ZeroRTest.class);

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected TestDescriptor getDescriptor() {
        return new TestDescriptor().iris(0.11, 0.33)
                .soybean(0.007D, 0.05263D)
                .vote(0.306D, 0.5D);
    }

    @Override
    protected IClassifier getClassifier() {
        return new ZeroR();
    }
}

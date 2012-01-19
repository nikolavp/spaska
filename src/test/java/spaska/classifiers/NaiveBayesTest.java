package spaska.classifiers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spaska.test.ClassifierTestBase;

public class NaiveBayesTest extends ClassifierTestBase {
    private static final Logger LOG = LoggerFactory
            .getLogger(NaiveBayesTest.class);

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected TestDescriptor getDescriptor() {
        return new TestDescriptor().iris(0.95, 0.95).vote(0.907, 0.918)
                .soybean(0.954, 0.921).glass(0.225, 0.250);
    }

    @Override
    protected IClassifier getClassifier() {
        return new NaiveBayes();
    }

    @Test
    public void shouldCalculateNormalDensityFunctionProperly() throws Exception {
        double normalDensityF = NaiveBayes
                .normalDensityF(5.855, 3.5033e-02D, 6);
        assertEquals(1.57888D, normalDensityF, 0.001);
    }

    @Test
    public void shouldReturnNonNanValueOnZeroVariance() throws Exception {
        double normalDensityF = NaiveBayes.normalDensityF(5.855, 0, 6);
        assertThat(Double.isNaN(normalDensityF), is(false));
    }
}

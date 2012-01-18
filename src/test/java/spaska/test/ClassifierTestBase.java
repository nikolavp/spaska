package spaska.test;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;

import DistLib.logistic;

import spaska.classifiers.IClassifier;
import spaska.statistics.ClassifierStatistics;
import spaska.test.ClassifierTestBase.TestDescriptor.DatasetRule;

public abstract class ClassifierTestBase {
    /**
     * A descriptor that "describes" the test cases for a given class.
     * Subclasses of the classifier test base class should provide description
     * of how good they are handling a given data set. Let's say I am building a
     * new classifier and I want to state that it can handle iris with general
     * precision of 0.89 and general recall 0.90:
     * 
     * <pre>
     * new TestDescriptor().iris(0.89, 0.90);
     * </pre>
     * 
     * @author nikolavp
     * 
     */
    protected static class TestDescriptor {
        protected static final class DatasetRule {
            private final double recall;
            private final double precision;

            public DatasetRule(double precision, double recall) {
                this.precision = precision;
                this.recall = recall;
            }
        }

        private Map<String, DatasetRule> datasetRules = new HashMap<String, DatasetRule>();

        public TestDescriptor() {

        }

        public DatasetRule getRule(String name) {
            return datasetRules.get(name);
        }

        public TestDescriptor iris(double precision, double recall) {
            return addDataset("iris", precision, recall);
        }

        private TestDescriptor addDataset(String name, double precision,
                double recall) {
            datasetRules.put(name, new DatasetRule(precision, recall));
            return this;
        }

        public TestDescriptor vote(double precision, double recall) {
            return addDataset("vote", precision, recall);
        }

        public TestDescriptor soybean(double precision, double recall) {
            return addDataset("soybean", precision, recall);
        }
    }

    protected abstract Logger getLogger();

    protected abstract TestDescriptor getDescriptor();

    protected abstract IClassifier getClassifier();

    @Test
    public void shouldGiveGoodResultsOnIrisDataSet() throws Exception {
        shouldGiveGoodResultsOnDataSet("iris");
    }

    @Test
    public void shouldGiveGoodResultsOnSoyBeanDataSet() throws Exception {
        shouldGiveGoodResultsOnDataSet("soybean");
    }

    @Test
    public void shouldGiveGoodResultsOnVoteDataSet() throws Exception {
        shouldGiveGoodResultsOnDataSet("vote");
    }

    /**
     * This constant is used to report if there is a big difference between the
     * results we are stating and the one we are getting. Most of the time we
     * want to strength the results as much as possible.
     */
    private static final double REPORT_DIFFERENCE = 0.01;

    private void shouldGiveGoodResultsOnDataSet(String datasetName)
            throws URISyntaxException {
        DatasetRule rule = getDescriptor().getRule(datasetName);
        Assume.assumeTrue(rule != null);
        ClassifierStatistics statistics = runOnDataSet(datasetName);
        assertThat("General precision is worse!",
                statistics.getGeneralPrecision(),
                greaterThanOrEqualTo(rule.precision));
        if (statistics.getGeneralPrecision() - rule.precision > REPORT_DIFFERENCE) {
            getLogger().warn(
                    "Consider strengthening the precision for dataset {}",
                    datasetName);
        }
        
        assertThat("General recall is worse!", statistics.getGeneralRecall(),
                greaterThanOrEqualTo(rule.recall));
        
        if (statistics.getGeneralRecall() - rule.recall > REPORT_DIFFERENCE) {
            getLogger().warn(
                    "Consider strengthening the recall for dataset {}",
                    datasetName);
        }
    }

    private ClassifierStatistics runOnDataSet(String dataset)
            throws URISyntaxException {
        ClassifierStatistics statistics = new ClassifierTester(getClassifier())
                .onDataset(dataset).crossValidate(10);
        getLogger().info("General precision for {} dataset is {}", dataset,
                statistics.getGeneralPrecision());
        getLogger().info("General recall for {} dataset is {}", dataset,
                statistics.getGeneralRecall());
        return statistics;
    }
}

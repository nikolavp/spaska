package spaska.classifiers;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import spaska.statistics.ClassifierStatistics;
import spaska.test.ClassifierTestBase;
import spaska.test.ClassifierTester;

public class DecisionTreeTest extends ClassifierTestBase {
	@Test
	public void shouldGiveGoodResultsOnIrisiDataset() throws Exception {
		ClassifierStatistics statistics = new ClassifierTester(
				new DecisionTree()).onDataset("iris").crossValidate(10);
		assertThat(statistics.getGeneralPrecision(), greaterThanOrEqualTo(0.94));
	}
}

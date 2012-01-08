package spaska.classifiers;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import spaska.statistics.ClassifierStatistics;
import spaska.test.ClassifierTestBase;
import spaska.test.ClassifierTester;

public class ZeroRTest extends ClassifierTestBase {
	@Test
	public void shouldGiveGoodResultsOnIrisDataset() throws Exception {
		ClassifierStatistics statistics = new ClassifierTester(new ZeroR())
				.onDataset("iris").crossValidate(10);
		assertThat(statistics.getGeneralPrecision(), greaterThan(0.33));
	}
}

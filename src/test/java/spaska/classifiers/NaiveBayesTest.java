package spaska.classifiers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;

import org.junit.Test;

import spaska.statistics.ClassifierStatistics;
import spaska.test.ClassifierTester;

public class NaiveBayesTest {
	@Test
	public void shouldGiveGoodResultsOnIrisiDataset() throws Exception {
		IClassifier classifier = new NaiveBayes();
		double generalPrecision = new ClassifierTester(classifier)
				.onDataset("iris").crossValidate(10).getGeneralPrecision();
		assertThat(generalPrecision, is(greaterThan(0.95)));
	}

	@Test
	public void shouldGiveGoodResultsOnVoteDataset() throws Exception {
		IClassifier classifier = new NaiveBayes();
		ClassifierStatistics crossValidate = new ClassifierTester(classifier)
				.onDataset("vote").crossValidate(10);
		assertThat(crossValidate.getGeneralPrecision(), is(greaterThan(0.935)));
	}

	@Test
	public void shouldCalculateNormalDensityFunctionProperly() throws Exception {
		double normalDensityF = NaiveBayes
				.normalDensityF(5.855, 3.5033e-02D, 6);
		assertEquals(1.57888D, normalDensityF, 0.001);
	}
}

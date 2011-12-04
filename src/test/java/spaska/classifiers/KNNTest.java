package spaska.classifiers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import spaska.test.ClassifierTestBase;
import spaska.test.ClassifierTester;

public class KNNTest extends ClassifierTestBase {

	@Test
	public void shouldGiveGoodResultsOnIrisiDataset() {
		KNN knn = new KNN();
		knn.setK(11);
		knn.setWeighted(true);
		
		double generalPrecision = new ClassifierTester(knn).onDataset("iris")
				.crossValidate(10).getGeneralPrecision();

		assertThat(generalPrecision, is(greaterThan(0.96)));
	}

}

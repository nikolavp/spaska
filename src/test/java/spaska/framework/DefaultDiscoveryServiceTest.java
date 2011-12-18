package spaska.framework;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import spaska.classifiers.DummyClassifier;
import spaska.classifiers.IClassifier;
import spaska.classifiers.KNN;
import spaska.classifiers.subpackage.DummyClassifierSubPackage;

public class DefaultDiscoveryServiceTest {
    DiscoveryService discoveryService = DefaultDiscoveryService.getInstance();

    @Test
    public void shouldDiscoverAtLeastKNNInItsPackageAutomatically()
            throws Exception {
        String knnPackageName = KNN.class.getPackage().getName();
        List<Class<? extends IClassifier>> classes = new PackageClassesDiscovery(
                knnPackageName).findSubclassesOf(IClassifier.class);

        assertThat(classes.contains(KNN.class), is(true));
    }

    @Test
    public void shouldDiscoverAtLeastKNNAsClassifierAlgorithm() {
        List<Class<? extends IClassifier>> discoverClasses = discoveryService
                .discoverClassifiers();
        assertThat(discoverClasses.size(), greaterThan(0));
        assertThat(discoverClasses.contains(KNN.class), is(true));
    }

    @Test
    public void shouldDiscoverTheDummyClassInClassifiersPackageInTheTests() {
        List<Class<? extends IClassifier>> discoverClasses = discoveryService
                .discoverClassifiers();
        assertThat(discoverClasses.size(), greaterThan(0));
        assertThat(discoverClasses.contains(DummyClassifier.class), is(true));
    }

    @Test
    public void shouldDiscoverTheDummyClassInSubpackageOfClassifiersPackageInTheTests() {
        List<Class<? extends IClassifier>> discoverClasses = discoveryService
                .discoverClassifiers();
        assertThat(discoverClasses.size(), greaterThan(0));
        assertThat(discoverClasses.contains(DummyClassifierSubPackage.class),
                is(true));
    }

}

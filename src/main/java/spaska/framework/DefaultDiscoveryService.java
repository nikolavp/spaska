package spaska.framework;

import java.util.List;

import spaska.analysis.IAnalyzer;
import spaska.analysis.ICompareAnalyzer;
import spaska.analysis.IStatisticalTest;
import spaska.classifiers.IClassifier;
import spaska.clusterers.IClusterer;

/**
 * The default implementation for the discovery service.
 * 
 * @author nikolavp
 * 
 */
public final class DefaultDiscoveryService implements DiscoveryService {
    private static final DefaultDiscoveryService INSTANCE = new DefaultDiscoveryService();

    private DefaultDiscoveryService() {

    }

    /**
     * Get the default discovery service.
     * 
     * @return the default discovery service
     */
    public static DefaultDiscoveryService getInstance() {
        return INSTANCE;
    }

    @Override
    public List<Class<? extends IAnalyzer>> discoverGeneralAnalyzers() {
        return new PackageClassesDiscovery("spaska.analysis")
                .findSubclassesOf(IAnalyzer.class);
    }

    @Override
    public List<Class<? extends ICompareAnalyzer>> discoverCompareAnalyzers() {
        return new PackageClassesDiscovery("spaska.analysis")
                .findSubclassesOf(ICompareAnalyzer.class);
    }

    @Override
    public List<Class<? extends IStatisticalTest>> discoverIStatisticalTests() {
        return new PackageClassesDiscovery("spaska.analysis")
                .findSubclassesOf(IStatisticalTest.class);
    }

    @Override
    public List<Class<? extends IClassifier>> discoverClassifiers() {
        return new PackageClassesDiscovery("spaska.classifiers")
                .findSubclassesOf(IClassifier.class);
    }

    @Override
    public List<Class<? extends IClusterer>> discoverClusterers() {
        return new PackageClassesDiscovery("spaska.clusterers")
                .findSubclassesOf(IClusterer.class);
    }

}

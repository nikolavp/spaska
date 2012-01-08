package spaska.framework;

import java.util.List;

import spaska.analysis.IAnalyzer;
import spaska.analysis.ICompareAnalyzer;
import spaska.analysis.IStatisticalTest;
import spaska.classifiers.IClassifier;
import spaska.clusterers.IClusterer;

/**
 * An interface representing services that discover different algorithms at
 * runtime.
 * <p>
 * This is needed if we want to extend spaska from the outside at runtime. Like
 * if you want to register a new classifier that is not bundled with spaska.
 * </p>
 * 
 * @author nikolavp
 * 
 */
public interface DiscoveryService {
    /**
     * @return general analysers found in the classpath
     */
    List<Class<? extends IAnalyzer>> discoverGeneralAnalyzers();

    /**
     * 
     * @return compare analysers found in the classpath
     */
    List<Class<? extends ICompareAnalyzer>> discoverCompareAnalyzers();

    /**
     * @return statistical tests found in the classpath
     */
    List<Class<? extends IStatisticalTest>> discoverIStatisticalTests();

    /**
     * @return classifiers found the classpath
     */
    List<Class<? extends IClassifier>> discoverClassifiers();

    /**
     * @return clusterers found in the classpath
     */
    List<Class<? extends IClusterer>> discoverClusterers();
}

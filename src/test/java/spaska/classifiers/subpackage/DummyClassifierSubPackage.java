package spaska.classifiers.subpackage;

import java.util.Map;

import spaska.classifiers.IClassifier;
import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.Value;
import spaska.framework.DefaultDiscoveryServiceTest;

/**
 * Dummy class to be used in {@link DefaultDiscoveryServiceTest}
 * @author nikolavp
 *
 */
public class DummyClassifierSubPackage implements IClassifier{

    @Override
    public void setParameters(Map<String, String> parameters) {
    }

    @Override
    public void buildClassifier(Dataset instances) {
    }

    @Override
    public Value classifyInstance(Instance instance) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

}

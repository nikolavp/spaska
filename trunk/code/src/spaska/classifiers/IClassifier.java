package spaska.classifiers;

import spaska.data.Dataset;
import spaska.data.Instance;
import spaska.data.Value;
import spaska.gui.Parametrable;

public interface IClassifier extends Parametrable{

    public void buildClassifier(Dataset instances);

    public Value classifyInstance(Instance instance);

    public String getName();
}

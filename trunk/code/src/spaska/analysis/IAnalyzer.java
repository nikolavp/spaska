package spaska.analysis;

import spaska.classifiers.IClassifier;
import spaska.data.Dataset;
import spaska.gui.Parametrable;
import spaska.statistics.ClassifierStatistics;

public interface IAnalyzer extends Parametrable{

	public IClassifier getClassifier();

	public void setClassifier(IClassifier classifier);
	
	public ClassifierStatistics analyze(Dataset testSet);

}

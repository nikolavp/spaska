package spaska.analysis;

import spaska.classifiers.IClassifier;
import spaska.data.Dataset;
import spaska.gui.Parametrable;
import spaska.statistics.CompareStatistics;


public interface ICompareAnalyzer extends Parametrable {

	public IClassifier getClassifier1();

	public void setClassifier1(IClassifier classifier1);

	public IClassifier getClassifier2();

	public void setClassifier2(IClassifier classifier2);

	public CompareStatistics analyze(Dataset dataSet) throws Exception;

}

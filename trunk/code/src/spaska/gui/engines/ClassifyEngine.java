/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spaska.gui.engines;

import java.util.Map;
import java.util.Vector;

import spaska.analysis.CrossValidation;
import spaska.analysis.IAnalyzer;
import spaska.classifiers.DecisionTree;
import spaska.classifiers.IClassifier;
import spaska.classifiers.KNN;
import spaska.classifiers.OneR;
import spaska.classifiers.TwoLayerPerceptron;
import spaska.classifiers.ZeroR;
import spaska.gui.InputException;
import spaska.statistics.Statistics;

public class ClassifyEngine extends Engine {

	private IClassifier								classifier;
	private IAnalyzer								analyzer;
	private Vector<Class<? extends IClassifier>>	classifiers;
	private Vector<Class<? extends IAnalyzer>>		analyzers;

	public ClassifyEngine() {
		classifiers = new Vector<Class<? extends IClassifier>>();
		analyzers = new Vector<Class<? extends IAnalyzer>>();
		classifiers.add(KNN.class);
		classifiers.add(DecisionTree.class);
		classifiers.add(OneR.class);
		classifiers.add(ZeroR.class);
		classifiers.add(TwoLayerPerceptron.class);
		analyzers.add(CrossValidation.class);
	}

	public Vector<Class<? extends IClassifier>> getClassifiers() {
		return classifiers;
	}

	public Vector<Class<? extends IAnalyzer>> getAnalyzers() {
		return analyzers;
	}

	public void setClassifier(IClassifier c, Map<String, String> params) throws Exception {
		if (c != null) {
			classifier = c;
			if (params != null) {
				c.setParameters(params);
			}
		}
		else {
			throw new InputException("Please choose a classifier");
		}
	}

	public void setAnalyzer(IAnalyzer a, Map<String, String> params) throws Exception {
		if (a != null) {
			analyzer = a;
			if (params != null) {
				a.setParameters(params);
			}
		}
		else {
			throw new InputException("Please choose an analyzer");
		}
	}

	@Override
	public void check() throws Exception {
		super.check();
		if (classifier == null) throw new InputException("Please choose a classifier");
		if (analyzer == null) throw new InputException("Please choose an analyzer");
	}

	public Statistics start() throws Exception {
		setDataset();
		if (analyzer != null) {
			if (classifier != null) {
				analyzer.setClassifier(classifier);
				return analyzer.analyze(getDataset());
			}
			else {
				throw new InputException("Please choose a classifier");
			}
		}
		else {
			throw new InputException("Please choose an analyzer");
		}
	}

	@Override
	public void reset() {
		super.reset();
		classifier = null;
		analyzer = null;
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spaska.gui.engines;

import java.util.Map;
import java.util.Vector;

import spaska.analysis.IAnalyzer;
import spaska.classifiers.IClassifier;
import spaska.framework.DefaultDiscoveryService;
import spaska.framework.DiscoveryService;
import spaska.gui.InputException;
import spaska.statistics.Statistics;

public class ClassifyEngine extends Engine {

    private IClassifier classifier;
    private IAnalyzer analyzer;
    private Vector<Class<? extends IClassifier>> classifiers;
    private Vector<Class<? extends IAnalyzer>> analyzers;

    public ClassifyEngine() {
        DiscoveryService discoveryService = DefaultDiscoveryService
                .getInstance();
        classifiers = new Vector<Class<? extends IClassifier>>();
        analyzers = new Vector<Class<? extends IAnalyzer>>(
                discoveryService.discoverGeneralAnalyzers());
        classifiers = new Vector<Class<? extends IClassifier>>(
                discoveryService.discoverClassifiers());
    }

    public Vector<Class<? extends IClassifier>> getClassifiers() {
        return classifiers;
    }

    public Vector<Class<? extends IAnalyzer>> getAnalyzers() {
        return analyzers;
    }

    public void setClassifier(IClassifier c, Map<String, String> params)
            throws Exception {
        if (c != null) {
            classifier = c;
            if (params != null) {
                c.setParameters(params);
            }
        } else {
            throw new InputException("Please choose a classifier");
        }
    }

    public void setAnalyzer(IAnalyzer a, Map<String, String> params)
            throws Exception {
        if (a != null) {
            analyzer = a;
            if (params != null) {
                a.setParameters(params);
            }
        } else {
            throw new InputException("Please choose an analyzer");
        }
    }

    @Override
    public void check() throws Exception {
        super.check();
        if (classifier == null)
            throw new InputException("Please choose a classifier");
        if (analyzer == null)
            throw new InputException("Please choose an analyzer");
    }

    public Statistics start() throws Exception {
        setDataset();
        if (analyzer != null) {
            if (classifier != null) {
                analyzer.setClassifier(classifier);
                return analyzer.analyze(getDataset());
            } else {
                throw new InputException("Please choose a classifier");
            }
        } else {
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

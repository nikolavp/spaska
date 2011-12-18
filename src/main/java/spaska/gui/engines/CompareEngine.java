/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spaska.gui.engines;

import java.util.Map;
import java.util.Vector;

import spaska.analysis.ICompareAnalyzer;
import spaska.classifiers.IClassifier;
import spaska.framework.DefaultDiscoveryService;
import spaska.framework.DiscoveryService;
import spaska.gui.InputException;
import spaska.statistics.Statistics;

public class CompareEngine extends Engine {

    private IClassifier classifier1;
    private IClassifier classifier2;
    private ICompareAnalyzer analyzer;
    private Vector<Class<? extends IClassifier>> classifiers;
    private Vector<Class<? extends ICompareAnalyzer>> analyzers;

    public CompareEngine() {
        DiscoveryService discoveryService = DefaultDiscoveryService
                .getInstance();
        classifiers = new Vector<Class<? extends IClassifier>>(
                discoveryService.discoverClassifiers());
        analyzers = new Vector<Class<? extends ICompareAnalyzer>>(
                discoveryService.discoverCompareAnalyzers());

    }

    public Vector<Class<? extends IClassifier>> getClassifiers() {
        return classifiers;
    }

    public Vector<Class<? extends ICompareAnalyzer>> getAnalyzers() {
        return analyzers;
    }

    public void setClassifier1(IClassifier c, Map<String, String> params)
            throws Exception {
        if (c != null) {
            classifier1 = c;
            if (params != null) {
                c.setParameters(params);
            }
        } else {
            throw new InputException("Please choose two classifiers ");
        }
    }

    public void setClassifier2(IClassifier c, Map<String, String> params)
            throws Exception {
        if (c != null) {
            classifier2 = c;
            if (params != null) {
                c.setParameters(params);
            }
        } else {
            throw new InputException("Please choose two classifiers");
        }
    }

    public void setAnalyzer(ICompareAnalyzer a, Map<String, String> params)
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
        if (analyzer == null)
            throw new InputException("Please choose an analyzer");
        if ((classifier1 == null) || (classifier2 == null))
            throw new InputException("Please choose two classifiers");
    }

    public Statistics start() throws Exception {
        if (analyzer != null) {
            if ((classifier1 != null) && (classifier2 != null)) {
                analyzer.setClassifier1(classifier1);
                analyzer.setClassifier2(classifier2);
                setDataset();
                return analyzer.analyze(getDataset());
            } else {
                throw new InputException("Please choose two classifiers");
            }
        } else {
            throw new InputException("Please choose an analyzer");
        }
    }

    public void reset() {
        super.reset();
        classifier1 = null;
        classifier2 = null;
        analyzer = null;
    }

}

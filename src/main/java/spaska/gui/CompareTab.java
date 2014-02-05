package spaska.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Map;

import spaska.analysis.ICompareAnalyzer;
import spaska.classifiers.IClassifier;
import spaska.data.readers.Validator;
import spaska.gui.engines.CompareEngine;

/**
 * 
 * @author <a href="mailto:vesko.m.georgiev@gmail.com">Vesko Georgiev</a>
 */
public class CompareTab extends SpaskaTab {

    private static final String FIRST_CLASSIFIER_CONTEXT = "f";

    private static final long serialVersionUID = 1L;

    private ComboList<IClassifier> classifierCombo1;
    private ComboList<IClassifier> classifierCombo2;
    private ComboList<ICompareAnalyzer> analyzerCombo;
    private ComboList<Validator> validatorCombo;

    public CompareTab() {
        validatorCombo = new ComboList<Validator>("Choose Validators",
                getEngine().getValidators());
        classifierCombo1 = new ComboList<IClassifier>("Choose Classifier1",
                getEngine().getClassifiers(), 1);
        classifierCombo2 = new ComboList<IClassifier>("Choose Classifier2",
                getEngine().getClassifiers(), 1);
        analyzerCombo = new ComboList<ICompareAnalyzer>("Choose Analyzer",
                getEngine().getAnalyzers(), 1);

        buildGui();
    }

    private void buildGui() {
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);

        // 1 row
        c.gridy = 1;
        c.weighty = 1;

        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = 2;

        c.gridx = 0;
        c.weightx = 1;
        add(validatorCombo, c);

        c.gridx = 2;
        c.weightx = 1;
        add(analyzerCombo, c);

        c.gridx = 4;
        c.weightx = 1;
        add(classifierCombo1, c);

        c.gridx = 6;
        c.weightx = 1;
        add(classifierCombo2, c);

        // 2 row
        c.gridy = 2;
        c.weighty = 0;

        c.gridwidth = 1;
        c.anchor = GridBagConstraints.SOUTHEAST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 7;
        c.weightx = 1;

        add(run, c);
        
        // 3 row
        c.gridy = 3;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.SOUTH;

        c.gridx = 0;
        c.gridwidth = 1;
        c.weightx = 0;
        add(status, c);
    }

    @Override
    public String getTitle() {
        return "Compare";
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(Utils.FILE_DATASET)) {
            openFile();
        }
        if (e.getActionCommand().equals(Utils.START)) {
            try {
                getEngine().reset();

                //getEngine().setFile(openedFile);
                getEngine().setResource(this.resource);

                setEngineArgs(validatorCombo.getParameters(), null);
                setEngineArgs(classifierCombo1.getParameters(),
                        FIRST_CLASSIFIER_CONTEXT);
                setEngineArgs(classifierCombo2.getParameters(), null);
                setEngineArgs(analyzerCombo.getParameters(), null);

                start();
                setButtonStop();
            } catch (Exception ex) {
                showError(ex);
            }
        } else if (e.getActionCommand().equals(Utils.STOP)) {
            stop();
            setButtonStart();
        }
    }

    @Override
    protected CompareEngine getEngine() {
        if (engine == null) {
            engine = new CompareEngine();
        }
        return (CompareEngine) engine;
    }

    @Override
    protected <T> void setComponent(Class<? extends T> cls,
            Map<String, String> params, String context) throws Exception {
        if (IClassifier.class.isAssignableFrom(cls)) {
            if (FIRST_CLASSIFIER_CONTEXT.equals(context)) {
                getEngine().setClassifier1((IClassifier) cls.newInstance(),
                        params);
            } else {
                getEngine().setClassifier2((IClassifier) cls.newInstance(),
                        params);
            }
        } else if (ICompareAnalyzer.class.isAssignableFrom(cls)) {
            getEngine().setAnalyzer((ICompareAnalyzer) cls.newInstance(),
                    params);
        }
    }
}

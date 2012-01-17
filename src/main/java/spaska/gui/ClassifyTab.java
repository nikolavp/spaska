package spaska.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Map;

import spaska.analysis.IAnalyzer;
import spaska.classifiers.IClassifier;
import spaska.data.readers.Validator;
import spaska.gui.engines.ClassifyEngine;

/**
 * 
 * @author <a href="mailto:vesko.m.georgiev@gmail.com">Vesko Georgiev</a>
 */
public class ClassifyTab extends SpaskaTab {

    private static final long serialVersionUID = 1L;

    private ComboList<IClassifier> classifierCombo;
    private ComboList<IAnalyzer> analyzerCombo;
    private ComboList<Validator> validatorCombo;

    public ClassifyTab() {
        validatorCombo = new ComboList<Validator>("Choose Validators",
                getEngine().getValidators());
        analyzerCombo = new ComboList<IAnalyzer>("Choose Analyzer", getEngine()
                .getAnalyzers(), 1);
        classifierCombo = new ComboList<IClassifier>("Choose Classifier",
                getEngine().getClassifiers(), 1);

        buildGui();
    }

    private void buildGui() {
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);

        // 1 row
        c.gridy = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;

        c.gridx = 0;
        c.gridwidth = 1;
        c.weightx = 0;
        add(browse, c);

        c.gridx = 1;
        c.gridwidth = 5;
        c.weightx = 1;
        add(textField, c);

        // 2 row
        c.gridy = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;

        c.gridwidth = 2;
        c.gridx = 0;
        c.weightx = 1;
        add(validatorCombo, c);

        c.gridwidth = 2;
        c.gridx = 2;
        c.weightx = 1;
        add(analyzerCombo, c);

        c.gridwidth = 2;
        c.gridx = 4;
        c.weightx = 1;
        add(classifierCombo, c);

        // 3 row
        c.gridy = 2;
        c.weighty = 0;

        c.gridwidth = 1;
        c.anchor = GridBagConstraints.SOUTHEAST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 4;
        c.weightx = 1;

        add(run, c);
    }

    @Override
    public String getTitle() {
        return "Classify";
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(Utils.FILE_DATASET)) {
            openFile();
        }
        if (e.getActionCommand().equals(Utils.START)) {
            try {
                getEngine().reset();

                getEngine().setFile(openedFile);
                setEngineArgs(validatorCombo.getParameters(), null);
                setEngineArgs(classifierCombo.getParameters(), null);
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
    protected ClassifyEngine getEngine() {
        if (engine == null) {
            engine = new ClassifyEngine();
        }
        return (ClassifyEngine) engine;
    }

    @Override
    protected <T> void setComponent(Class<? extends T> cls,
            Map<String, String> params, String context) throws Exception {
        if (IClassifier.class.isAssignableFrom(cls)) {
            getEngine().setClassifier((IClassifier) cls.newInstance(), params);
        } else if (IAnalyzer.class.isAssignableFrom(cls)) {
            getEngine().setAnalyzer((IAnalyzer) cls.newInstance(), params);
        }
    }

}

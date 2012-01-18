package spaska.gui;

import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spaska.data.readers.Validator;
import spaska.gui.engines.Engine;
import spaska.statistics.Statistics;

/**
 * An abstract class that keeps the common logic for different tab that
 * represent possible workflows with the spaska framework.
 */
public abstract class SpaskaTab extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    /**
     * The cursor that should be used while we are processing data in one of the
     * subclasses.
     */
    protected static final Cursor WAIT_CURSOR = new Cursor(Cursor.WAIT_CURSOR);
    private static final Logger LOG = LoggerFactory.getLogger(SpaskaTab.class);

    protected Engine engine;
    protected File openedFile;
    protected DataInputResource resource;

    protected JButton browse;
    protected JTextField textField;
    protected JFileChooser fileChooser;

    protected Thread thread;
    protected JButton run;

    protected Statistics statistics;

    protected SpaskaTab() {
        browse = new JButton("Browse");
        browse.setActionCommand(Utils.FILE_DATASET);
        browse.addActionListener(this);

        textField = new JTextField();

        run = new JButton("Start");
        run.setActionCommand(Utils.START);
        run.addActionListener(this);
    }

    protected <T> void setEngineArgs(
            Map<Class<? extends T>, Map<String, String>> classToParameters,
            String context) throws Exception {
        for (Entry<Class<? extends T>, Map<String, String>> entry : classToParameters
                .entrySet()) {
            Class<? extends T> cls = entry.getKey();
            Map<String, String> params = (entry.getValue() != null) ? entry
                    .getValue() : Utils.getParamsOfClass(cls);

            LOG.info("Set " + cls + " with " + params);
            if (Validator.class.isAssignableFrom(cls)) {
                getEngine().addValidator((Validator) cls.newInstance(), params);
            } else {
                setComponent(cls, params, context);
            }
        }
    }

    /**
     * Set a component class when calling {@link #setEngineArgs(Map)}.
     * 
     * @param cls
     *            the class to be set as component
     * @param context
     *            this is used to track some context from where setEngineArgs
     *            was called
     * @throws Exception
     *             if something goes wrong while setting the component
     */
    protected abstract <T> void setComponent(Class<? extends T> cls,
            Map<String, String> params, String context) throws Exception;

    public void openFile() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
        }
        if (openedFile != null) {
            fileChooser.setCurrentDirectory(openedFile.getParentFile());
        }
        fileChooser.setCurrentDirectory(new File("."));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            openedFile = fileChooser.getSelectedFile();
            propertyChange(Utils.FILE_DATASET, openedFile);
        }
        if (openedFile != null) {
        	this.resource = new DataInputResource(openedFile);
        }
    }
    
    public void setResource(DataInputResource resource) {
    	this.resource = resource;
    }

    protected void setButtonStart() {
        run.setActionCommand(Utils.START);
        run.setText("Start");
        setCursor(Cursor.getDefaultCursor());
    }

    protected void setButtonStop() {
        run.setActionCommand(Utils.STOP);
        run.setText("Stop");
        setCursor(WAIT_CURSOR);
    }

    protected void start() throws Exception {
        stop();
        if (thread == null) {
            thread = new Thread() {
                @Override
                public void run() {
                    runAlgorithm();
                }
            };
            engine.check();
            thread.setDaemon(true);
            thread.start();
        }
    }

    protected void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    protected void showError(Exception e) {
        stop();
        setButtonStart();
        if (e instanceof InputException) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Missing Data",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            LOG.error("Error occured in the table", e);
            JOptionPane.showMessageDialog(this, e, "Algorithm error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void runAlgorithm() {
        try {
            statistics = engine.start();
            MainApp.getInstance().showStatistics(getStatistics());
        } catch (Exception e) {
            showError(e);
        } finally {
            setButtonStart();
        }
    }

    protected abstract Engine getEngine();

    public abstract String getTitle();

    public Statistics getStatistics() {
        return statistics;
    }

    public void propertyChange(String prop, Object value) {
        if (prop.equals(Utils.FILE_DATASET)) {
            textField.setText(value.toString());
        }
    }

}

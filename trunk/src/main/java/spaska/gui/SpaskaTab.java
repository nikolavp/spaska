package spaska.gui;

import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import spaska.gui.engines.Engine;
import spaska.statistics.Statistics;

public abstract class SpaskaTab extends JPanel implements ActionListener {

	private static final long	serialVersionUID	= 1L;

	protected static Cursor		waitCursor			= new Cursor(Cursor.WAIT_CURSOR);

	protected Engine			engine;
	protected File				openedFile;

	protected JButton			browse;
	protected JTextField		textField;
	protected JFileChooser		fileChooser;

	protected Thread			thread;
	protected JButton			run;

	protected Statistics		statistics;

	protected SpaskaTab() {
		browse = new JButton("Browse");
		browse.setActionCommand(Utils.FILE_DATASET);
		browse.addActionListener(this);

		textField = new JTextField();

		run = new JButton("Start");
		run.setActionCommand(Utils.START);
		run.addActionListener(this);
	}

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
	}

	protected void setButtonStart() {
		run.setActionCommand(Utils.START);
		run.setText("Start");
		setCursor(Cursor.getDefaultCursor());
	}

	protected void setButtonStop() {
		run.setActionCommand(Utils.STOP);
		run.setText("Stop");
		setCursor(waitCursor);
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
			JOptionPane.showMessageDialog(this, e.getMessage(), "Missing Data", JOptionPane.ERROR_MESSAGE);
		}
		else {
			System.err.println(e);
			JOptionPane.showMessageDialog(this, e, "Algorithm error", JOptionPane.ERROR_MESSAGE);
		}
	}

	protected void runAlgorithm() {
		try {
			statistics = engine.start();
			MainApp.getInstance().showStatistics(getStatistics());
		}
		catch (Exception e) {
			showError(e);
		}
		finally {
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

package spaska.gui;

import javax.swing.JLabel;

/**
 * 
 * @author plamen
 *
 */
public class StatusBar extends JLabel {

	private static final long serialVersionUID = 1L;

	public StatusBar() {
		super();
	}

	public StatusBar(String message) {
		this();
		this.setMessage(message);
	}

	public void setMessage(String message) {
		this.setText(message);
	}
}

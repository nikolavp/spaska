package spaska.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ParametersDialog extends JDialog implements ActionListener {

	private static final long		serialVersionUID	= 1L;

	private Map<String, String>		result;
	private JPanel					paramPanel;
	private JButton					button;
	private Map<String, JTextField>	fields;

	private ActionListener	listener;

	public ParametersDialog(ActionListener listener) {
		this.listener = listener;
		paramPanel = new JPanel();

		JPanel commitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		button = new JButton("Commit Parameters");
		button.setActionCommand(Utils.COMMIT_PARAMETERS);
		button.addActionListener(this);
		commitPanel.add(button);

		result = new HashMap<String, String>();
		fields = new HashMap<String, JTextField>();

		setLayout(new BorderLayout(5, 5));
		add(commitPanel, BorderLayout.SOUTH);
		add(paramPanel, BorderLayout.CENTER);

		getRootPane().setDefaultButton(button);
		setModal(true);
	}

	public Map<String, String> getResultParameters() {
		return result;
	}

	public void show(Map<String, String> parameters) {
		if (parameters != null) {
			paramPanel.removeAll();
			paramPanel.validate();
			paramPanel.setLayout(new GridLayout(parameters.size(), 2, 2, 2));
			fields.clear();
	
			for (Entry<String, String> e : parameters.entrySet()) {
				JLabel label = new JLabel(e.getKey());
				JTextField field = new JTextField(e.getValue());

				fields.put(e.getKey(), field);
				paramPanel.add(label);
				paramPanel.add(field);
			}
			pack();
			setLocationRelativeTo(null);
			setVisible(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(Utils.COMMIT_PARAMETERS)) {
			result.clear();
			for (Entry<String, JTextField> entry : fields.entrySet()) {
				result.put(entry.getKey(), entry.getValue().getText().trim());
			}
			setVisible(false);
			listener.actionPerformed(e);
		}
	}
}

package spaska.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * 
 * @author iva
 * 
 */
public class ConnectDbDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JTextField hostnameField = null;
	private JTextField dbnameField = null;
	private JTextField usernameField = null;
	private JPasswordField passwordField = null;

	private static String jdbcConnectionString = null;

	public ConnectDbDialog() {
		initUI();
	}

	public static String getJdbcConnectionString() {
		jdbcConnectionString = null;
		JDialog connectDbDialog = new ConnectDbDialog();
		connectDbDialog.setVisible(true);
		return jdbcConnectionString;
	}

	private void makeJdbcString() {
		String hostname = this.hostnameField.getText();
		String dbname = this.dbnameField.getText();
		String username = this.usernameField.getText();
		String password = new String(this.passwordField.getPassword());

		if (hostname.isEmpty() || dbname.isEmpty() || username.isEmpty()
				|| password.isEmpty()) {
			// TODO: Show message.
		} else {
			jdbcConnectionString = "jdbc:mysql://" + hostname + "/" + dbname
					+ "?" + "user=" + username + "&" + "password=" + password;
			this.setVisible(false);
			this.dispose();
		}
	}

	public final void initUI() {

		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		JPanel jdbcComponents = new JPanel();
		jdbcComponents.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		jdbcComponents.setLayout(new GridLayout(5, 2, 10, 5));

		JLabel hostnameLabel = new JLabel("Hostname:");
		jdbcComponents.add(hostnameLabel);
		this.hostnameField = new JTextField();
		jdbcComponents.add(this.hostnameField);
		JLabel dbnameLabel = new JLabel("Database:");
		jdbcComponents.add(dbnameLabel);
		this.dbnameField = new JTextField();
		jdbcComponents.add(this.dbnameField);
		JLabel usernameLabel = new JLabel("Username:");
		jdbcComponents.add(usernameLabel);
		this.usernameField = new JTextField();
		jdbcComponents.add(this.usernameField);
		JLabel passwordLabel = new JLabel("Password:");
		jdbcComponents.add(passwordLabel);
		this.passwordField = new JPasswordField();
		jdbcComponents.add(this.passwordField);

		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));

		JButton connect = new JButton("Connect");
		connect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				makeJdbcString();
			}
		});
		buttons.add(connect);

		JButton close = new JButton("Cancel");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				dispose();
			}
		});
		buttons.add(close);

		add(jdbcComponents);
		add(buttons);

		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Connect do the database");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(200, 200);
	}
}

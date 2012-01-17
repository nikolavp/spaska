package spaska.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

import spaska.db.ARFF2DB;
import spaska.db.sql.SpaskaSqlConnection;
import spaska.statistics.Statistics;

/**
 * SPASKA main application.
 * 
 * @author <a href="mailto:vesko.m.georgiev@gmail.com">Vesko Georgiev</a>
 */
public class MainApp extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static MainApp instance;

	public static MainApp getInstance() {
		if (instance == null) {
			instance = new MainApp();
		}
		return instance;
	}

	private JTabbedPane tabs;
	private JDialog statDialog;
	private JTextArea area;
	private JMenuBar menuBar;
	private JDialog aboutDialog;
	private AppResources bundle;

	private String jdbcConnectionString = null;
	private SpaskaSqlConnection sqlConnection = null;

	public MainApp() {
		bundle = new AppResources();
		if (System.getProperty("os.name").startsWith("Mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		setTitle(bundle.get(AppResources.APP_TITLE));
		setPreferredSize((Dimension) bundle.getObject(AppResources.APP_SIZE));

		createMenuBar();
		buildGui();

		pack();

		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setVisible(true);
		setIconImage(bundle.getIcon(bundle.get(AppResources.APP_ICON))
				.getImage());
	}

	public void showConnectDbDialog() {
		String jdbcConnectionString = ConnectDbDialog.getJdbcConnectionString();
		if (jdbcConnectionString != null) {
			System.out.println(jdbcConnectionString);
			this.jdbcConnectionString = jdbcConnectionString;
			this.sqlConnection = new SpaskaSqlConnection(
					this.jdbcConnectionString);
			this.menuBar.getMenu(1).getMenuComponent(1).setEnabled(true);
			this.menuBar.getMenu(1).getMenuComponent(2).setEnabled(true);
			this.menuBar.getMenu(1).getMenuComponent(3).setEnabled(true);
		}
	}

	public void importArff() {
		JFileChooser fileChooser = new JFileChooser();
		File openedFile = null;
		fileChooser.setCurrentDirectory(new File("."));
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			openedFile = fileChooser.getSelectedFile();
			(new ARFF2DB(openedFile, this.jdbcConnectionString)).read();
		}
	}

	public void selectTable() {
		String tableName = getTableName();
		System.out.println(tableName);
	}
	
	private String getTableName() {
		String[] tableNames = this.sqlConnection.getTables().toArray(
				new String[0]);
		return (String) JOptionPane.showInputDialog(this, null,
				"Table selection", JOptionPane.PLAIN_MESSAGE, null, tableNames,
				tableNames[0]);
	}

	public void showStatistics(Statistics st) {
		if (st != null) {
			if (!getStatisticsDialog().isVisible()) {
				getStatisticsDialog().setVisible(true);
			}
			area.setText(st.toString());
		} else {
			area.setText("No generated statistics yet.");
		}
	}

	private void addTab(JTabbedPane tabPane, SpaskaTab panel) {
		tabPane.addTab(panel.getTitle(), panel);
	}

	private void buildGui() {
		setLayout(new BorderLayout());

		area = new JTextArea();

		tabs = new JTabbedPane();
		addTab(tabs, new ClustererTab());
		addTab(tabs, new ClassifyTab());
		addTab(tabs, new CompareTab());

		add(tabs);
	}

	private JDialog getStatisticsDialog() {
		if (statDialog == null) {
			statDialog = new JDialog(this, "Result Statistics");
			statDialog.setLayout(new BorderLayout(2, 2));

			area = new JTextArea();
			area.setEditable(false);

			statDialog.add(new JScrollPane(area));

			statDialog.setPreferredSize((Dimension) bundle
					.getObject(AppResources.STAT_DIALOG_SIZE));
			statDialog.pack();

			statDialog.setLocationRelativeTo(null);
		}
		return statDialog;
	}

	private void createMenuBar() {
		menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		JMenuItem openFileItem = (JMenuItem) bundle
				.getObject("openFileMenuItem");
		openFileItem.setActionCommand(Utils.FILE_DATASET);
		openFileItem.addActionListener(this);
		fileMenu.add(openFileItem);

		JMenu dbMenu = new JMenu("Databases");
		JMenuItem connectDbMenuItem = (JMenuItem) bundle
				.getObject("connectDbMenuItem");
		connectDbMenuItem.setActionCommand(Utils.CONNECT_DB_DIALOG);
		connectDbMenuItem.addActionListener(this);
		JMenuItem chooseTableMenuItem = (JMenuItem) bundle
				.getObject("chooseTableMenuItem");
		chooseTableMenuItem.setActionCommand(Utils.CHOOSE_TABLE);
		chooseTableMenuItem.addActionListener(this);
		chooseTableMenuItem.setEnabled(false);
		JMenuItem importArffMenuItem = (JMenuItem) bundle
				.getObject("importArffMenuItem");
		importArffMenuItem.setActionCommand(Utils.IMPORT_ARFF);
		importArffMenuItem.addActionListener(this);
		importArffMenuItem.setEnabled(false);
		JMenuItem exportArffMenuItem = (JMenuItem) bundle
				.getObject("exportArffMenuItem");
		exportArffMenuItem.setActionCommand(Utils.FILE_DATASET);
		exportArffMenuItem.addActionListener(this);
		exportArffMenuItem.setEnabled(false);
		dbMenu.add(connectDbMenuItem);
		dbMenu.add(chooseTableMenuItem);
		dbMenu.add(importArffMenuItem);
		dbMenu.add(exportArffMenuItem);

		JMenu viewMenu = new JMenu("View");
		JMenuItem openStatisticsItem = (JMenuItem) bundle
				.getObject("openStatisticsMenuItem");
		openStatisticsItem.setActionCommand(Utils.OPEN_STATISTICS);
		openStatisticsItem.addActionListener(this);
		viewMenu.add(openStatisticsItem);

		JMenu helpMenu = new JMenu("Help");
		JMenuItem aboutItem = (JMenuItem) bundle.getObject("aboutMenuItem");
		aboutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getAboutDialog().setVisible(true);
			}
		});
		JMenuItem exitmenuItem = (JMenuItem) bundle.getObject("exitMenuItem");
		exitmenuItem.setActionCommand(Utils.EXIT);
		exitmenuItem.addActionListener(this);
		helpMenu.add(aboutItem);
		helpMenu.add(exitmenuItem);

		menuBar.add(fileMenu);
		menuBar.add(dbMenu);
		menuBar.add(viewMenu);
		menuBar.add(helpMenu);
		setJMenuBar(menuBar);
	}

	private JDialog getAboutDialog() {
		if (aboutDialog == null) {
			aboutDialog = new JDialog(this);
			aboutDialog.setLayout(new BorderLayout(10, 10));
			setResizable(false);

			Font bodyFont = getFont().deriveFont(10f);

			JPanel panel = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(2, 2, 2, 2);

			List<JLabel> labels = new LinkedList<JLabel>();
			labels.add(new JLabel(""));
			labels.add(new JLabel(""));
			for (String s : bundle.get(AppResources.APP_NAME).split("\n")) {
				JLabel l = new JLabel(s);
				l.setFont(bodyFont);
				labels.add(l);
			}
			c.gridx = 0;
			c.gridy = 0;
			c.gridheight = labels.size() + 1;
			c.anchor = GridBagConstraints.NORTHWEST;
			panel.add(
					new JLabel(
							bundle.getIcon(bundle.get(AppResources.APP_ICON))),
					c);
			c.gridx = 1;
			c.gridy = 0;
			c.gridheight = 1;
			c.anchor = GridBagConstraints.NORTHEAST;
			panel.add(new JLabel(bundle.get(AppResources.APP_TITLE) + " "
					+ bundle.get(AppResources.APP_VERSION)), c);

			for (JLabel l : labels) {
				c.gridy++;
				panel.add(l, c);
			}
			labels.clear();
			c.gridx = 0;
			c.gridy++;
			c.gridwidth = 2;
			c.anchor = GridBagConstraints.CENTER;

			labels.add(new JLabel(""));
			labels.add(new JLabel(""));
			labels.add(new JLabel("Credits:"));

			for (String s : bundle.get(AppResources.APP_CERDITS).split("\n")) {
				JLabel l = new JLabel(s);
				l.setFont(bodyFont);
				labels.add(l);
			}
			labels.add(new JLabel(""));
			labels.add(new JLabel(""));
			JLabel copyRight = new JLabel(
					bundle.get(AppResources.APP_COPYRIGHT));
			labels.add(copyRight);
			labels.add(new JLabel(""));
			labels.add(new JLabel(""));

			for (JLabel l : labels) {
				panel.add(l, c);
				c.gridy++;
			}
			aboutDialog.add(new JScrollPane(panel));
			aboutDialog.setResizable(false);
			aboutDialog.setPreferredSize(new Dimension(320, 290));
			aboutDialog.pack();
			aboutDialog.setLocationRelativeTo(null);
		}
		return aboutDialog;
	}

	public SpaskaTab getSelectedTab() {
		return (SpaskaTab) tabs.getSelectedComponent();
	}

	@SuppressWarnings(justification = "This is a proper action when exit is called", value = "DM_EXIT")
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(Utils.EXIT)) {
			System.exit(0);
		} else if (e.getActionCommand().equals(Utils.OPEN_STATISTICS)) {
			showStatistics(getSelectedTab().getStatistics());
		} else if (e.getActionCommand().equals(Utils.FILE_DATASET)) {
			getSelectedTab().openFile();
		} else if (e.getActionCommand().equals(Utils.CONNECT_DB_DIALOG)) {
			showConnectDbDialog();
		} else if (e.getActionCommand().equals(Utils.IMPORT_ARFF)) {
			importArff();
		} else if (e.getActionCommand().equals(Utils.CHOOSE_TABLE)) {
			selectTable();
		}
	}

	public static void main(String[] args) throws IOException {
		getInstance();
	}

}

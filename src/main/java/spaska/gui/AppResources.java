package spaska.gui;

import java.awt.Dimension;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * 
 * @author <a href="mailto:vesko.m.georgiev@gmail.com">Vesko Georgiev</a>
 */
public class AppResources extends ResourceBundle {

	private static final Logger	logger				= Logger.getLogger(AppResources.class.toString());

	public final String			APP_TITLE			= "appTitle";
	public final String			APP_NAME			= "appName";
	public final String			APP_CERDITS			= "appCredits";
	public final String			APP_VERSION			= "appVersion";
	public final String			APP_SIZE			= "appSize";
	public final String			APP_COPYRIGHT		= "appCopyright";
	public final String			APP_ICON			= "appIcon";
	public final String			STAT_DIALOG_SIZE	= "statisticsDialogSize";

	private ResourceBundle		bundle;

	public AppResources() {
		bundle = ResourceBundle.getBundle("strings", Locale.getDefault());
		if (bundle == null) {
			logger.severe("Could not load string resources.");
		}
	}

	public String get(String key) {
		try {
			return bundle.getString(key);
		}
		catch (Exception e) {
			logger.info("Bad key passed: \"" + key + "\"");
		}
		return "";
	}

	@Override
	public Enumeration<String> getKeys() {
		return bundle.getKeys();
	}

	@Override
	protected Object handleGetObject(String key) {
		if (key.endsWith("Size")) {
			int width = 300;
			int height = 200;

			try {
				String[] size = bundle.getString(key).split(",");
				width = Integer.parseInt(size[0].trim());
				height = Integer.parseInt(size[1].trim());
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.info("Bad value in key " + key + "=" + bundle.getString(key));
			}
			return new Dimension(width, height);
		}
		else if (key.endsWith("MenuItem")) {
			try {
				String[] obj = bundle.getString(key).split(",");
				JMenuItem item = new JMenuItem(obj[0]);
				if (obj.length > 1 && !obj[1].equals("")) {
					item.setIcon(getIcon(obj[1]));
				}
				if (obj.length > 2) {
					item.setAccelerator(KeyStroke.getKeyStroke(obj[2]));
				}
				return item;
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.info("Bad value in key " + key + "=" + bundle.getString(key) + ". Exception: " + e);
			}
		}
		return new JMenuItem();
	}

	public ImageIcon getIcon(String icon) {
		return new ImageIcon(getResource(icon));
	}

	public URL getResource(String name) {
		return AppResources.class.getResource("/" + name);
	}
	
}

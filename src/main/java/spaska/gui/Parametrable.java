package spaska.gui;

import java.util.Map;

/**
 * The GUI must set the properties of different kind of objects.
 * Each object that needs to set some parameters needs to implement 
 * this interface.
 * 
 * Each Parameterable class needs to implement 
 * <code>public static Map<String, String> getParameters()</code>
 * that needs to return all the properties that needs to be set and their
 * default values. If no parameters set is required return null; 
 * 
 * @author <a href="mailto:vesko.m.georgiev@gmail.com">Vesko Georgiev</a>
 */
public interface Parametrable {

	/**
	 * Configures this object with the passed parameters.
	 * 
	 * @param parameters parameters to configure with.
	 */
	void setParameters(Map<String, String> parameters);

}

package spaska.db;

import java.util.ArrayList;

import spaska.data.Pair;
import spaska.data.Value;

/**
 * 
 * @author plamen
 * 
 */
public interface SQLGetter {
	/**
	 * 
	 * @param tableName
	 *            The table, from which the information is retrieved.
	 * @param attributeName
	 *            The attribute, for which the domain is searched.
	 * @return List of values for an attribute with type "Nominal"
	 */
	ArrayList<Value> getDomain(String tableName, String attributeName);

	/**
	 * 
	 * @param tableName
	 *            The table, from which the information is retrieved.
	 * @return List of pairs (Attribute name, SQL Type Code), to get the
	 *         attribute name and the attribute type.
	 */
	ArrayList<Pair<String, Integer>> getAttributes(String tableName);

	/**
	 * 
	 * @param tableName
	 *            The table, from which the information is retrieved.
	 * @return List of arrays of Strings, each array contains information for
	 *         one instance.
	 */
	ArrayList<String[]> getData(String tableName);

	/**
	 * 
	 * @return List of the names of the tables in the database
	 */
	ArrayList<String> getTables();
}

package spaska.db.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import spaska.data.Pair;
import spaska.data.Factory;
import spaska.data.Value;
import spaska.db.SQLGetter;

/**
 * 
 * @author plamen
 * 
 */
public class SpaskaSqlConnection implements SQLGetter {
	private Connection connection;
	private Statement statement;

	/**
	 * 
	 * @param jdbcConnString
	 *            JDBC Connection string
	 */
	public SpaskaSqlConnection(String jdbcConnString) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.connection = DriverManager.getConnection(jdbcConnString);
			this.statement = this.connection.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void finalize() throws Throwable {
		try {
			this.statement.close();
			this.connection.close();
		} finally {
			super.finalize();
		}
	}

	/**
	 * 
	 * @param tableName
	 *            The table, from which the information is retrieved.
	 * @param columnName
	 *            The attribute, for which the domain is searched.
	 * @return List of values for an attribute with type "Nominal"
	 */
	public ArrayList<Value> getDomain(String tableName, String columnName) {
		String query = "SELECT DISTINCT " + columnName + " FROM " + tableName;
		ArrayList<Value> result = new ArrayList<Value>();

		try {
			ResultSet values = this.statement.executeQuery(query);
			while (values.next()) {
				result.add(Factory.createValue(values.getString(columnName)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param tableName
	 *            The table, from which the information is retrieved.
	 * @return List of pairs (Attribute name, SQL Type Code), to get the
	 *         attribute name and the attribute type.
	 */
	public ArrayList<Pair<String, Integer>> getAttributes(String tableName) {
		ArrayList<Pair<String, Integer>> result = new ArrayList<Pair<String, Integer>>();

		try {
			DatabaseMetaData md = this.connection.getMetaData();
			ResultSet columnsAndTypes = md.getColumns(null, null, tableName,
					null);

			while (columnsAndTypes.next()) {
				String name = columnsAndTypes.getString("COLUMN_NAME");
				Integer type = columnsAndTypes.getInt("DATA_TYPE");
				result.add(new Pair<String, Integer>(name, type));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @param tableName
	 *            The table, from which the information is retrieved.
	 * @return List of arrays of Strings, each array contains information for
	 *         one instance.
	 */
	public ArrayList<String[]> getData(String tableName) {
		ArrayList<String[]> result = new ArrayList<String[]>();
		String query = "SELECT * FROM " + tableName;

		try {
			ResultSet rows = this.statement.executeQuery(query);

			ResultSetMetaData md = rows.getMetaData();
			int columnCount = md.getColumnCount();

			while (rows.next()) {
				List<String> strValues = new ArrayList<String>();
				for (int i = 1; i <= columnCount; i++) {
					strValues.add(rows.getObject(i).toString());
				}
				result.add(strValues.toArray(new String[0]));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @return List of the names of the tables in the database
	 */
	public ArrayList<String> getTables() {
		ArrayList<String> result = new ArrayList<String>();

		try {
			ResultSet tableNames = this.connection.getMetaData().getTables(
					null, null, null, null);
			while (tableNames.next()) {
				result.add(tableNames.getString("TABLE_NAME"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}
}

package spaska.db;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 
 * @author iva
 * 
 */
public class ARFF2DB {

	private static final String TAG_RELATION = "@relation";
	private static final String TAG_ATTRIBUT = "@attribute";
	private static final String TAG_DATA = "@data";

	private File file;
	private Connection connection = null;
	private Statement statement = null;

	private ArrayList<String> attrContainer;
	private ArrayList<String> dataContainer;
	private String tableName;

	/**
	 * 
	 * @param file
	 *            the file path from which to read the data
	 * @param jdbcConnString
	 *            JDBC Connection string
	 */
	public ARFF2DB(String file, String jdbcConnString) {
		this(new File(file), jdbcConnString);
	}

	/**
	 * 
	 * @param file
	 *            the file object from which to read the data
	 * @param jdbcConnString
	 *            JDBC Connection string
	 */
	public ARFF2DB(File file, String jdbcConnString) {
		this.file = file;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.connection = DriverManager.getConnection(jdbcConnString);
			this.statement = connection.createStatement();
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

	private String getValueType(String type) {
		if (type.startsWith("real") || type.startsWith("numeric")) {
			return "DOUBLE";
		} else if (type.startsWith("{") || type.startsWith("text")) {
			return "VARCHAR(100)";
		}
		return "VARCHAR(1)";
	}

	private String handleAttribute(String line) {
		String[] token = line.split("\\s+");
		String name = "Unknown";
		String type = "VARCHAR(1)";
		if (token.length > 0) {
			name = token[1];
		}
		if (token.length > 1) {
			type = getValueType(token[2].toLowerCase());
		}
		return "`" + name + "`" + " " + type;
	}

	private ArrayList<String> handleData(BufferedReader input)
			throws IOException {
		String line = null;
		ArrayList<String> result = new ArrayList<String>();
		while ((line = input.readLine()) != null) {
			String trim = line.trim();
			if (!trim.equals("") && !trim.startsWith("%")) {
				String[] strValues = line.split(",");
				StringBuffer row = new StringBuffer();
				row.append("(");
				for (int i = 0; i < strValues.length; i++) {
					if (Pattern.matches("\\d+(\\.\\d*)?", strValues[i])) {
						row.append(strValues[i]);
					} else {
						row.append("\"" + strValues[i] + "\"");
					}
					row.append(",");
				}
				row.deleteCharAt(row.length() - 1);
				row.append(")");
				result.add(row.toString());
			}
		}
		return result;
	}

	private void createTable(String tableName, List<String> attributes)
			throws SQLException {
		StringBuffer query = new StringBuffer();
		query.append("CREATE TABLE `" + tableName + "` (\n");
		for (String attribute : attributes) {
			query.append(attribute + ",\n");
		}
		// Remove the last comma
		query.deleteCharAt(query.length() - 2);
		query.append(")\n");
		this.statement.executeUpdate(query.toString());
	}

	private void insertData(String tableName, List<String> data)
			throws SQLException {
		StringBuffer query = new StringBuffer();
		query.append("INSERT INTO `" + tableName + "` VALUES\n");
		for (String dataLine : data) {
			query.append(dataLine + ",\n");
		}
		query.deleteCharAt(query.length() - 2);
		this.statement.executeUpdate(query.toString());
	}

	/**
	 * Reads the file and writes it into the database.
	 */
	public void read() {
		attrContainer = new ArrayList<String>();
		dataContainer = new ArrayList<String>();
		tableName = "";
		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));

			String line = null;
			while ((line = input.readLine()) != null) {
				if (line.toLowerCase().startsWith(TAG_RELATION)) {
					tableName = line.split("\\s")[1];
				} else if (line.toLowerCase().startsWith(TAG_ATTRIBUT)) {
					attrContainer.add(handleAttribute(line));
				} else if (line.toLowerCase().startsWith(TAG_DATA)) {
					dataContainer = handleData(input);
				}
			}
			createTable(tableName, attrContainer);
			insertData(tableName, dataContainer);
		} catch (EOFException e) {
			// do nothing
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
				}
			}
		}
	}
}

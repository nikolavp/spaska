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

public class SQLRoutines implements SQLGetter {
	private Connection connection;
	private Statement statement;

	public SQLRoutines(String jdbcConnString) {
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
}

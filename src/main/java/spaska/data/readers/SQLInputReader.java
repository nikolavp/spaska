package spaska.data.readers;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spaska.data.Attribute;
import spaska.data.Attribute.ValueType;
import spaska.data.Dataset;
import spaska.data.Factory;
import spaska.data.Instance;
import spaska.data.Value;

/**
 * @author psstoev
 * 
 */
public class SQLInputReader extends AbstractInputReader {

	private Connection connection = null;
	private Statement statement = null;
	private String tableName = null;

	public SQLInputReader(String tableName, String jdbcConnString) {
		this.tableName = tableName;

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

	private ValueType getValueType(int valueCode) {
		switch (valueCode) {
		case Types.DOUBLE:
			return ValueType.Numeric;
		case Types.VARCHAR:
			return ValueType.Nominal;
		default:
			break;
		}
		return ValueType.Unknown;
	}

	private Set<Value> getDomain(String columnName)
			throws SQLException {
		Set<Value> domain = new HashSet<Value>();
		String query = "SELECT DISTINCT " + columnName + " FROM "
				+ this.tableName;
		ResultSet values = this.statement.executeQuery(query);

		while (values.next()) {
			domain.add(Factory.createValue(values.getString(columnName)));
		}
		return domain;
	}

	private void handleAttributes() throws SQLException {
		DatabaseMetaData md = this.connection.getMetaData();
		ResultSet columnsAndTypes = md.getColumns(null, null, this.tableName,
				null);

		while (columnsAndTypes.next()) {
			String name = columnsAndTypes.getString("COLUMN_NAME");
			ValueType type = this.getValueType(columnsAndTypes
					.getInt("DATA_TYPE"));
			Attribute attr = new Attribute(name, type);

			this.dataset.addAttribute(attr);
			if (type.equals(ValueType.Nominal)) {
				this.dataset.addAttributeDomain(attr, this.getDomain(name));
			}
		}
	}

	private void handleData() throws SQLException {
		String query = "SELECT * FROM " + this.tableName;
		ResultSet result = this.statement.executeQuery(query);
		ResultSetMetaData md = result.getMetaData();
		int columnCount = md.getColumnCount();

		while (result.next()) {
			List<String> strValues = new ArrayList<String>();
			for (int i = 1; i <= columnCount; i++) {
				strValues.add(result.getObject(i).toString());
			}
			List<Value> element = Factory.createElementData(
					strValues.toArray(new String[0]), dataset);
			dataset.addElement(new Instance(element));
		}

		for (Validator v : validators) {
			v.validate();
		}
	}

	@Override
	public Dataset buildDataset() {
		dataset = new Dataset();
		for (Validator v : validators) {
			v.setDataset(dataset);
		}

		dataset.setName(this.tableName);
		try {
			this.handleAttributes();
			this.handleData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dataset.setClassIndex(dataset.getAttributesCount() - 1);
		return dataset;
	}
}

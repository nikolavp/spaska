package spaska.db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Types;

import spaska.data.Pair;
import spaska.data.Value;
import spaska.db.sql.SpaskaSqlConnection;

/**
 * 
 * @author plamen
 * 
 */
public class DB2ARFF {
	private SpaskaSqlConnection sqlroutines = null;
	private String name = null;
	private File file = null;

	/**
	 * 
	 * @param tableName
	 *            The name of the table, holding the data.
	 * @param fileName
	 *            The name of the file, in which the data is written. If
	 *            fileName is null, then the default name is <table_name>.arff.
	 * @param jdbcConnectionString
	 *            JDBC Connection string
	 */
	public DB2ARFF(String tableName, String fileName,
			String jdbcConnectionString) {
		this.name = tableName;
		this.sqlroutines = new SpaskaSqlConnection(jdbcConnectionString);
		if (fileName == null) {
			this.file = new File(name + ".arff");
		} else {
			this.file = new File(fileName);
		}
	}

	private String getAttributeTypeName(int code, String attributeName) {
		switch (code) {
		case Types.DOUBLE:
			return "NUMERIC";
		case Types.VARCHAR:
			StringBuffer result = new StringBuffer("{");
			for (Value value : this.sqlroutines.getDomain(this.name, attributeName)) {
				result.append(value.toString() + " ");
			}
			// Remove the trailing space:
			result.deleteCharAt(result.length() - 1);
			result.append("}");
			return result.toString();
		default:
			break;
		}
		return "Unknown";
	}

	/**
	 * Writes the file.
	 */
	public void write() {
		try {
			BufferedWriter output = new BufferedWriter(
					new FileWriter(this.file));

			output.write("@RELATION " + this.name + "\n");
			output.write("\n");

			for (Pair<String, Integer> attribute : this.sqlroutines
					.getAttributes(this.name)) {
				String attributeName = attribute.getFirst();
				String type = this.getAttributeTypeName(attribute.getSecond(),
						name);
				output.write("@ATTRIBUTE " + attributeName + " " + type + "\n");
			}
			output.write("\n");

			output.write("@DATA\n");
			for (String[] instance : this.sqlroutines.getData(this.name)) {
				StringBuffer row = new StringBuffer();
				for (String field : instance) {
					row.append(field + ",");
				}
				// Remove the trailing comma
				row.deleteCharAt(row.length() - 1);

				output.write(row.toString() + "\n");
			}

			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	public static void main(String[] args) {
//		(new DB2ARFF("iris", null,
//				"jdbc:mysql://localhost/spaska?user=spaska&password=spaska"))
//				.write();
//	}
}

package spaska.db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Types;

import spaska.data.Pair;
import spaska.data.Value;
import spaska.db.sql.SQLRoutines;

public class DB2ARFF {
	SQLRoutines sqlroutines = null;
	String name = null;
	File file = null;

	public DB2ARFF(String tableName, String fileName, String jdbcConnectionString) {
		this.name = tableName;
		this.sqlroutines = new SQLRoutines(jdbcConnectionString);
		if (fileName == null) {
			this.file = new File(name + ".arff");
		} else {
			this.file = new File(fileName);
		}
	}

	private String getAttributeTypeName(int code, String name) {
		switch (code) {
		case Types.DOUBLE:
			return "NUMERIC";
		case Types.VARCHAR:
			StringBuffer result = new StringBuffer("{");
			for (Value value : this.sqlroutines.getDomain(this.name, name)) {
				result.append(value.toString() + " ");
			}
			// Remove the trailing space:
			result.deleteCharAt(result.length() - 1);
			result.append("}");
			return result.toString();
		}
		return "Unknown";
	}

	public void write() {
		try {
			BufferedWriter output = new BufferedWriter(
					new FileWriter(this.file));

			output.write("@RELATION " + this.name + "\n");
			output.write("\n");

			for (Pair<String, Integer> attribute : this.sqlroutines
					.getAttributes(this.name)) {
				String name = attribute.getFirst();
				String type = this.getAttributeTypeName(attribute.getSecond(),
						name);
				output.write("@ATTRIBUTE " + name + " " + type + "\n");
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
	
	public static void main(String[] args) {
		(new DB2ARFF("iris", null, "jdbc:mysql://localhost/spaska?user=spaska&password=spaska")).write();
	}
}

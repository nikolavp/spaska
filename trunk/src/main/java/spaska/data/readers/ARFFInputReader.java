package spaska.data.readers;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import spaska.data.Attribute;
import spaska.data.Dataset;
import spaska.data.Factory;
import spaska.data.Instance;
import spaska.data.Value;
import spaska.data.Attribute.ValueType;

public class ARFFInputReader extends AbstractInputReader {

	private static final String	TAG_RELATION	= "@relation";
	private static final String	TAG_ATTRIBUT	= "@attribute";
	private static final String	TAG_DATA		= "@data";

	private File				file;

	public ARFFInputReader(String file) {
		this(new File(file));
	}

	public ARFFInputReader(File file) {
		this.file = file;
	}

	public ValueType getValueType(String type) {
		if (type.startsWith("real") || type.startsWith("numeric")) {
			return ValueType.Numeric;
		}
		else if (type.startsWith("{") || type.startsWith("text")) {
			return ValueType.Nominal;
		}
		return ValueType.Unknown;
	}

	private void handleAttribute(String line) {
		String[] token = line.split("\\s+");
		String name = "Unknown";
		ValueType type = ValueType.Unknown;

		if (token.length > 0) {
			name = token[1];
		}
		if (token.length > 1) {
			type = getValueType(token[2].toLowerCase());
		}
		Attribute attr = new Attribute(name, type);
		dataset.addAttribute(attr);

		if (token.length > 2) {
			if (type == ValueType.Nominal) {
				Set<Value> domain = new HashSet<Value>();
				int start = line.indexOf("{");
				int end = line.indexOf("}", start + 1);
				String[] domains = line.substring(start + 1, end).split(",");

				for (String str : domains) {
					domain.add(Factory.createValue(str.trim()));
				}
				dataset.addAttributeDomain(attr, domain);
			}
		}
	}

	private void handleData(BufferedReader input) throws IOException {
		String line = null;
		while ((line = input.readLine()) != null) {
			String trim = line.trim();
			if (!trim.equals("") && !trim.startsWith("%")) {
				List<Value> element = new LinkedList<Value>();
				String[] strValues = line.split(",");
				element = Factory.createElementData(strValues, dataset);
				dataset.addElement(new Instance(element));
			}
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
		BufferedReader input = null;

		try {
			input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

			String line = null;
			while ((line = input.readLine()) != null) {
				if (line.toLowerCase().startsWith(TAG_RELATION)) {
					dataset.setName(line.split("\\s")[1]);
				}
				else if (line.toLowerCase().startsWith(TAG_ATTRIBUT)) {
					handleAttribute(line);
				}
				else if (line.toLowerCase().startsWith(TAG_DATA)) {
					handleData(input);
				}
			}
		}
		catch (EOFException e) {
			// do nothing
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (input != null) {
				try {
					input.close();
				}
				catch (IOException e) {
				}
			}
		}
		dataset.setClassIndex(dataset.getAttributesCount() - 1);
		return dataset;
	}

}

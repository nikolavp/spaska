package spaska.data.readers;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
 * An input reader that reads from ARFF files. ARFF files are a format that is
 * used mostly in weka. They are simple files with metadata for the instace
 * attributes and samples/data after that.
 * 
 * @see http://www.cs.waikato.ac.nz/ml/weka/arff.html
 */
public class ARFFInputReader extends AbstractInputReader {

    private static final String TAG_RELATION = "@relation";
    private static final String TAG_ATTRIBUT = "@attribute";
    private static final String TAG_DATA = "@data";

    private File file;

    /**
     * Constructs a reader that will read from the provided file path.
     * 
     * @param file
     *            the file path from which to read the data
     */
    public ARFFInputReader(String file) {
        this(new File(file));
    }

    /**
     * Constructs a reader that will read from the provided file .
     * 
     * @param file
     *            the file from which to read the data
     */
    public ARFFInputReader(File file) {
        this.file = file;
    }

    private ValueType getValueType(String type) {
        if (type.startsWith("real") || type.startsWith("numeric")) {
            return ValueType.Numeric;
        } else if (type.startsWith("{") || type.startsWith("text")) {
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
        getDataset().addAttribute(attr);

        if (token.length > 2) {
            if (type == ValueType.Nominal) {
                Set<Value> domain = new HashSet<Value>();
                int start = line.indexOf("{");
                int end = line.indexOf("}", start + 1);
                String[] domains = line.substring(start + 1, end).split(",");

                for (String str : domains) {
                    domain.add(Factory.createValue(str.trim()));
                }
                getDataset().addAttributeDomain(attr, domain);
            }
        }
    }

    private void handleData(BufferedReader input) throws IOException {
        String line = null;
        while ((line = input.readLine()) != null) {
            String trim = line.trim();
            if (!trim.equals("") && !trim.startsWith("%")) {
                String[] strValues = line.split(",");
                List<Value> element = Factory.createElementData(strValues,
                        getDataset());
                getDataset().addElement(new Instance(element));
            }
        }
        for (Validator v : getValidators()) {
            v.validate();
        }
    }

    @Override
    public Dataset buildDataset() {
        setDataset(new Dataset());
        for (Validator v : getValidators()) {
            v.setDataset(getDataset());
        }
        BufferedReader input = null;

        try {
            input = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file)));

            String line = null;
            while ((line = input.readLine()) != null) {
                if (line.toLowerCase().startsWith(TAG_RELATION)) {
                    getDataset().setName(line.split("\\s")[1]);
                } else if (line.toLowerCase().startsWith(TAG_ATTRIBUT)) {
                    handleAttribute(line);
                } else if (line.toLowerCase().startsWith(TAG_DATA)) {
                    handleData(input);
                }
            }
        } catch (EOFException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        getDataset().setClassIndex(getDataset().getAttributesCount() - 1);
        return getDataset();
    }

}

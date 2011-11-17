package spaska.data;

/**
 * 
 * @author Vesko Georgiev
 */
public class Attribute {

	public enum ValueType {
		Numeric, Nominal, Unknown
	}

	private String		name;
	private ValueType	type;

	public Attribute(String name, ValueType type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
    	return name;
    }
	
    public ValueType getType() {
        return type;
    }
    
	@Override
	public String toString() {
		return "[" + name + ":" + type + "]";
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Attribute)) {
			return false;
		}
		Attribute a = (Attribute) other;
		return this.getName().equals(a.getName())
				&& this.getType().equals(a.getType());
	}
}

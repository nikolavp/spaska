package spaska.data;

/**
 * Represents an attribute for an instance.
 * 
 * @author Vesko Georgiev
 */
public final class Attribute {
    /**
     * Represents the value type for this attribute.
     */
    public enum ValueType {
        /**
         * Numeric attributes - this includes integers and floating point
         * numbers.
         */
        Numeric,
        /**
         * Nominal attributes are a finite list of values. This is mostly used
         * for classes and labels on instances.
         */
        Nominal,
        /**
         * Unknown type.
         */
        Unknown
    }

    private String name;
    private ValueType type;

    /**
     * Constructs an attribute from the given name and type.
     * 
     * @param name
     *            the name of the attribute
     * @param type
     *            the type of the attribute
     */
    public Attribute(String name, ValueType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Get the name of the attribute.
     * 
     * @return the name of the attribute
     */
    public String getName() {
        return name;
    }

    /**
     * Get the type of the attribute.
     * 
     * @return the type of the attribute
     */
    public ValueType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "[" + name + ":" + type + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Attribute other = (Attribute) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }

}

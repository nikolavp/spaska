package spaska.data;

/**
 * 
 * @author Vesko Georgiev
 */
public class Attribute {

    public enum ValueType {
        Numeric, Nominal, Unknown
    }

    private String name;
    private ValueType type;

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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Attribute other = (Attribute) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

}

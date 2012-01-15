package spaska.classifiers.util;

/**
 * Represents condition sign.
 * 
 * This class is mostly used when constructing {@link Condition} objects
 */
public enum Sign {
    /**
     * equal to.
     */

    EQ("=="),
    /**
     * not equal to.
     */
    NEQ("!="),

    /**
     * less than.
     * 
     */
    LT("<"),
    /**
     * grater than.
     * 
     */
    GT(">"),
    /**
     * less than or equal to.
     * 
     */
    LTE("<="),
    /**
     * greater than or equal to.
     * 
     */
    GTE(">=");

    private String sgn;

    private Sign(String sgn) {
        this.sgn = sgn;
    }

    @Override
    public String toString() {
        return sgn;
    }
}

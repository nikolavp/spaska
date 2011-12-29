package spaska.analysis;

/**
 * Describes the types of tests applied to a hypothesis of the kind : H0: m == a
 * H1: m <sign> a.
 */
public enum TestType {
    /**
     * Two-sided test type (!=).
     */
    TwoSided,
    /**
     * One-sided less than (<) test type.
     */
    OneSidedLessThan,
    /**
     * One-sided greater than (>) test type.
     */
    OneSidedGreaterThan
}

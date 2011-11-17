package spaska.analysis;

/**
 * Describes the types of tests applied to a hypothesis of the kind : 
 * H0: m == a 
 * H1: m <sign> a
 */
public enum TestType {
	TwoSided, // <sign> (!=)
	OneSidedLessThan, // <sign> (<)
	OneSidedGreaterThan // <sign> (>)
}

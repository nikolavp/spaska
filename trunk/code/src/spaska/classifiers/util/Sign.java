package spaska.classifiers.util;

public enum Sign {
	EQ("=="),	// equal to
	NEQ("!="),  // not equal to
	LT("<"),    // less than
	GT(">"),    // grater than
	LTE("<="),  // less than or equal to
	GTE(">=");  // greater than or equal to
	
	private String sgn;
	
	Sign(String sgn) {
		this.sgn = sgn;
	}
	
	public String toString() {
		return sgn;
	}
}

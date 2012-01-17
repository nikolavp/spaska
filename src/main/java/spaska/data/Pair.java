package spaska.data;

/**
 * 
 * @author plamen
 * 
 * @param <T1>
 *            The type of the first member.
 * @param <T2>
 *            The type of the second member.
 */
public class Pair<T1, T2> {

	private T1 first = null;
	private T2 second = null;

	/**
	 * 
	 * @param first
	 *            The first member.
	 * @param second
	 *            The second member.
	 */
	public Pair(T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * 
	 * @return The first member.
	 */
	public T1 getFirst() {
		return this.first;
	}

	/**
	 * 
	 * @return The second member.
	 */
	public T2 getSecond() {
		return this.second;
	}

	/**
	 * @return Returns a string of the kind "Pair<Type1, Type2>(Value1, Value2).
	 */
	public String toString() {
		return "Pair<" + first.getClass().getName() + ", "
				+ second.getClass().getName() + ">(" + this.first.toString()
				+ ", " + this.second.toString() + ")";
	}

}

package com.cannontech.common.util;

/**
 * Class to represent pairs of objects.
 * @author alauinger
 */
public class Pair {
	public Object first;
	public Object second;
		
	/**
	 * Returns the first.
	 * @return Object
	 */
	public Object getFirst() {
		return first;
	}

	/**
	 * Returns the second.
	 * @return Object
	 */
	public Object getSecond() {
		return second;
	}

	/**
	 * Sets the first.
	 * @param first The first to set
	 */
	public void setFirst(Object first) {
		this.first = first;
	}

	/**
	 * Sets the second.
	 * @param second The second to set
	 */
	public void setSecond(Object second) {
		this.second = second;
	}

}

package com.cannontech.common.util;

/**
 * Class to represent pairs of objects.
 * @author alauinger
 */
public class Pair<K,V> {
	public K first;
	public V second;
	
	public Pair(K first, V second) {
	 	this.first = first;
	 	this.second = second;
	}
	
	/**
	 * Returns the first.
	 * @return Object
	 */
	public K getFirst() {
		return first;
	}

	/**
	 * Returns the second.
	 * @return Object
	 */
	public V getSecond() {
		return second;
	}

	/**
	 * Sets the first.
	 * @param first The first to set
	 */
	public void setFirst(K first) {
		this.first = first;
	}

	/**
	 * Sets the second.
	 * @param second The second to set
	 */
	public void setSecond(V second) {
		this.second = second;
	}
    
	@Override
    public boolean equals(Object obj) {
        if(obj instanceof Pair) {
        	@SuppressWarnings("unchecked") Pair p = (Pair) obj;
            return (first.equals(p.first) && second.equals(p.second));
        }
        else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return first.hashCode() ^ second.hashCode();
    }

}

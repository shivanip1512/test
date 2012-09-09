package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
	
	@SuppressWarnings("unchecked")
	public static final <E> Collection<E> removePair(Collection<?> c, Class<E> requiredType) {
	    List<E> list = new ArrayList<E>(c.size());
	    for (final Object o : c) {
	        Object element = o;
	        if (o instanceof Pair) {
	            element = ((Pair) o).getFirst();
	        }
	        
	        if (!requiredType.isAssignableFrom(element.getClass())) {
	            throw new IllegalArgumentException("Element of type: " + requiredType + " required but found: " + element.getClass());
	        }
	        list.add((E) element);
	    }
	    return list;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
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
        Pair other = (Pair) obj;
        if (first == null) {
            if (other.first != null)
                return false;
        } else if (!first.equals(other.first))
            return false;
        if (second == null) {
            if (other.second != null)
                return false;
        } else if (!second.equals(other.second))
            return false;
        return true;
    }
}
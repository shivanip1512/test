package com.cannontech.common.util;

import java.util.Map;

/**
 * @author rneuharth
 *
 * A unique key/value pair set that can be used inside lists.
 */
public class UniqueSet implements Map.Entry
{
	private final Object key;
	private Object value;


	static final Object NULL_KEY = new Object();
	
	/**
	 * Creates new entry.
	 */
	public UniqueSet(Object key_, Object val_ )
	{
		super();
		value = val_;
		key = key_;
	}
	
	 public Object getKey() {
		  return unmaskNull(key);
	 }

	/**
	 * Returns key represented by specified internal representation.
	 */
	static Object unmaskNull(Object key) {
		 return (key == NULL_KEY ? null : key);
	}
	
	 public Object getValue() {
		  return value;
	 }
 
	 public Object setValue(Object newValue) {
		  Object oldValue = value;
		  value = newValue;
		  return oldValue;
	 }
 
	 public boolean equals(Object o) 
	 {
		  if (!(o instanceof Map.Entry))
				return false;

		  Map.Entry e = (Map.Entry)o;
		  Object k1 = getKey();
		  Object k2 = e.getKey();
		  if (k1 == k2 || (k1 != null && k1.equals(k2))) 
		  {
				Object v1 = getValue();
				Object v2 = e.getValue();
				if (v1 == v2 || (v1 != null && v1.equals(v2))) 
					 return true;
		  }

		  return false;
	 }
 
	 public int hashCode() 
	 {
		  return (key==NULL_KEY ? 0 : key.hashCode()) ^
					(value==null   ? 0 : value.hashCode());
	 }
 
	 public String toString() 
	 {
		  return getKey() + "=" + getValue();
	 }


}

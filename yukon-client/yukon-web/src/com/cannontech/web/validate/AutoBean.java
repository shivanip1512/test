package com.cannontech.web.validate;

/**
 * Insert the type's description here.
 * Creation date: (6/12/2001 12:27:15 PM)
 * @author: 
 */
public interface AutoBean {
	public String get(String property);
	public Object getObject(String property);
	public void set(String property, Object value);
}

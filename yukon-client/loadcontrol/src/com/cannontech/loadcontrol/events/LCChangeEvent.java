package com.cannontech.loadcontrol.events;

/**
 * Insert the type's description here.
 * Creation date: (3/1/2001 1:35:10 PM)
 * @author: 
 */
public class LCChangeEvent extends java.awt.Event 
{
	public static final int DELETE_ALL = 10;
	public static final int UPDATE = 11;
/**
 * MACSCategoryChange constructor comment.
 * @param target java.lang.Object
 * @param id int
 * @param arg java.lang.Object
 */
public LCChangeEvent(Object target, int id, Object arg) {
	super(target, id, arg);
}
}

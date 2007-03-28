package com.cannontech.yukon.cbc;

/**
 */
public class CBCSubAreas extends com.cannontech.yukon.cbc.CBCMessage
{	
    //contains Strings
	private java.util.Vector areas;

/**
 * CBCSubAreaNames constructor comment.
 */
public CBCSubAreas() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public CBCArea getArea(int index) 
{
	return (CBCArea) getAreas().get(index);
}
/**
 * Insert the method's description here.
 * Creation date: (2/6/2001 1:17:13 PM)
 * @return java.util.Vector
 */
public java.util.Vector getAreas() {
	return areas;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumberOfAreas() {
	return areas.size();
}
/**
 * This method was created in VisualAge.
 */
public void setAreas(java.util.Vector a)
{
	areas = a;
}

}
package com.cannontech.cbc.messages;

/**
 */
public class CBCSubAreaNames extends com.cannontech.cbc.messages.CBCMessage {
	
	private java.util.Vector areaNames;
/**
 * ScheduleCommand constructor comment.
 */
public CBCSubAreaNames() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public String getAreaName(int index) 
{
	return getAreaNames().get(index).toString();
}
/**
 * Insert the method's description here.
 * Creation date: (2/6/2001 1:17:13 PM)
 * @return java.util.Vector
 */
public java.util.Vector getAreaNames() {
	return areaNames;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumberOfAreas() {
	return areaNames.size();
}
/**
 * This method was created in VisualAge.
 */
public void setAreaNames(java.util.Vector areas)
{
	areaNames = areas;
}
}

package com.cannontech.cbc.messages;

/**
 */
public class CBCSubAreaNames extends com.cannontech.cbc.messages.CBCMessage
{	
    //contains Strings
	private java.util.Vector areaNames;
    
    //contains int[] instances
    private java.util.Vector areaSubIDs;

/**
 * CBCSubAreaNames constructor comment.
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

/**
 * Gets the SubsIDs that have this Area Name string
 */
public int[] getAreaSubIDs(String areaName)
{
    int indx = getAreaNames().indexOf( areaName );
    if( indx >= 0 )        
        return (int[])getAreaSubIDs().get(indx);
    else
        return new int[0];
}

/**
 * Gets and Vector of int[]
 * 
 */
public java.util.Vector getAreaSubIDs()
{
    return areaSubIDs;
}

/**
 * This method was created in VisualAge.
 */
public void setAreaSubIDs(java.util.Vector _areasSubIds)
{
    areaSubIDs = _areasSubIds;
}

}
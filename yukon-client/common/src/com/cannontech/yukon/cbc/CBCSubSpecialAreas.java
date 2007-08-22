package com.cannontech.yukon.cbc;

/**
 */
public class CBCSubSpecialAreas extends com.cannontech.yukon.cbc.CBCMessage
{   
    //contains Strings
    private java.util.Vector areas;

/**
 * SpecialCBCSubAreaNames constructor comment.
 */
public CBCSubSpecialAreas() {
    super();
}
/**
 * This method was created in VisualAge.
 */
public CBCSpecialArea getArea(int index) 
{
    return (CBCSpecialArea) getAreas().get(index);
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
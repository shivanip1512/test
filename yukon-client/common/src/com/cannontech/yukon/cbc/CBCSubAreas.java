package com.cannontech.yukon.cbc;

import java.util.List;

public class CBCSubAreas extends com.cannontech.yukon.cbc.CBCMessage {	
    
	private List<CBCArea> areas;

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
	return getAreas().get(index);
}

/**
 * Insert the method's description here.
 * Creation date: (2/6/2001 1:17:13 PM)
 * @return java.util.Vector
 */
public List<CBCArea> getAreas() {
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
public void setAreas(List<CBCArea> a) {
	areas = a;
}

}
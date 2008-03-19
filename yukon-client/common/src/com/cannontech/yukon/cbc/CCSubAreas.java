package com.cannontech.yukon.cbc;

import java.util.List;

public class CCSubAreas extends com.cannontech.yukon.cbc.CapControlMessage {	
    
	private List<CCArea> areas;

/**
 * CBCSubAreaNames constructor comment.
 */
public CCSubAreas() {
	super();
}

/**
 * This method was created in VisualAge.
 */
public CCArea getArea(int index) 
{
	return getAreas().get(index);
}

/**
 * Insert the method's description here.
 * Creation date: (2/6/2001 1:17:13 PM)
 * @return java.util.Vector
 */
public List<CCArea> getAreas() {
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
public void setAreas(List<CCArea> a) {
	areas = a;
}

}
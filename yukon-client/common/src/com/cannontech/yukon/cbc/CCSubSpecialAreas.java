package com.cannontech.yukon.cbc;

import java.util.List;

/**
 */
public class CCSubSpecialAreas extends com.cannontech.yukon.cbc.CapControlMessage
{   
    //contains Strings
    private List<CCSpecialArea> areas;

/**
 * SpecialCBCSubAreaNames constructor comment.
 */
public CCSubSpecialAreas() {
    super();
}

/**
 * This method was created in VisualAge.
 */
public CCSpecialArea getArea(int index) {
    return getAreas().get(index);
}

/**
 * Insert the method's description here.
 * Creation date: (2/6/2001 1:17:13 PM)
 * @return java.util.Vector
 */
public List<CCSpecialArea> getAreas() {
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
public void setAreas(List<CCSpecialArea> a) {
    areas = a;
}

}
package com.cannontech.yukon.cbc;

import java.util.List;

/**
 */
public class CBCSubSpecialAreas extends com.cannontech.yukon.cbc.CBCMessage
{   
    //contains Strings
    private List<CBCSpecialArea> areas;

/**
 * SpecialCBCSubAreaNames constructor comment.
 */
public CBCSubSpecialAreas() {
    super();
}

/**
 * This method was created in VisualAge.
 */
public CBCSpecialArea getArea(int index) {
    return getAreas().get(index);
}

/**
 * Insert the method's description here.
 * Creation date: (2/6/2001 1:17:13 PM)
 * @return java.util.Vector
 */
public List<CBCSpecialArea> getAreas() {
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
public void setAreas(List<CBCSpecialArea> a) {
    areas = a;
}

}
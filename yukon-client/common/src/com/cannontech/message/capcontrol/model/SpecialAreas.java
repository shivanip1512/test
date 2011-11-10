package com.cannontech.message.capcontrol.model;

import java.util.List;

import com.cannontech.message.capcontrol.streamable.SpecialArea;

/**
 */
public class SpecialAreas extends com.cannontech.message.capcontrol.model.CapControlMessage
{   
    //contains Strings
    private List<SpecialArea> areas;

/**
 * SpecialCBCSubAreaNames constructor comment.
 */
public SpecialAreas() {
    super();
}

/**
 * This method was created in VisualAge.
 */
public SpecialArea getArea(int index) {
    return getAreas().get(index);
}

/**
 * Insert the method's description here.
 * Creation date: (2/6/2001 1:17:13 PM)
 * @return java.util.Vector
 */
public List<SpecialArea> getAreas() {
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
public void setAreas(List<SpecialArea> a) {
    areas = a;
}

}
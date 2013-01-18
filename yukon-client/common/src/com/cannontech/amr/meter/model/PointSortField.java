package com.cannontech.amr.meter.model;

/**
 * Enumeration of the fields for point sorting
 */
public enum PointSortField {
    ATTRIBUTE, 
    POINTNAME,
    POINTTYPE,
    POINTOFFSET, 
    TIMESTAMP,
    VALUE,
    QUALITY,
    ;

    /**
     * Method to get the toString() value for this PointSortField (neccessary for JSP EL) 
     * @return toString() value
     */
    public String getName() {
        return toString();
    }
}
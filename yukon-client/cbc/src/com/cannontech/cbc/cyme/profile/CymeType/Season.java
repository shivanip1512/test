package com.cannontech.cbc.cyme.profile.CymeType;

import com.cannontech.cbc.cyme.profile.CymeRepresentation;
import com.cannontech.core.dao.NotFoundException;

/**
 * WINTER,SPRING,SUMMER,FALL only valid with 4Season TimeInterval
* Months only valid with 12Month Time intervals
 */
public enum Season implements CymeRepresentation {
    WINTER,
    SPRING,
    SUMMER,
    FALL,
    JANUARY,
    FEBRUARY,
    MARCH,
    APRIL,
    MAY,
    JUNE,
    JULY,
    AUGUST,
    SEPTEMBER,
    OCTOBER,
    NOVEMBER,
    DECEMBER;

    
    static public Season getFromCymeValue(String cymeValue) {
        
        for (Season i : values()) {
            if (i.name().equals(cymeValue)) {
                return i;
            }
        }
        throw new NotFoundException("Season was not found to match: " + cymeValue);
    }

    @Override
    public String getCymeValue() {
        return this.name();
    } 
}

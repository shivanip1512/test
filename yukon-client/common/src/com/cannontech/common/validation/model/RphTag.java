/**
 * 
 */
package com.cannontech.common.validation.model;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public enum RphTag {
    PU, // leave these in an order that makes sense for "display precedence" purposes. i.e PU > UU, PD > UD > UDC
    PD, 
    UU, 
    UD, 
    UDC,
    OK;
    
    public boolean isPeak() {
        return name().startsWith("P"); // good enough for now
    }
    public boolean isUnreasonable() {
        return name().startsWith("U"); // good enough for now
    }
    public static Set<RphTag> getAllUnreasonable() {
        return ImmutableSet.of(UU, UD, UDC); // good enough for now
    }
    public static Set<RphTag> getAllValidation() {
        return ImmutableSet.of(PU, PD, UU, UD, UDC); // good enough for now
    }
    
    public String getKey() {
    	return "yukon.common.vee.rphTag." + this.name();
    }
}
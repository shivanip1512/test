/**
 * 
 */
package com.cannontech.services.validation.model;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public enum RphTag {
    PU, 
    PD, 
    UU, 
    UD, 
    UDC;
    
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
}
package com.cannontech.dr.ecobee.dao;

/**
 * Represents a quantity of queries made to the Ecobee API for a specific query type.
 */
public final class EcobeeQueryCount {
    private final EcobeeQueryType type;
    private final int count;
    
    public EcobeeQueryCount(EcobeeQueryType type, int count) {
        this.type = type;
        this.count = count;
    }
    
    public EcobeeQueryType getType() {
        return type;
    }
    
    public int getCount() {
        return count;
    }
}

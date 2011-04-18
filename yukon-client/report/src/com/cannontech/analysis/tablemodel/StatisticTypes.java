package com.cannontech.analysis.tablemodel;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum StatisticTypes implements DatabaseRepresentationSource
{
    DAILY("Daily"),
    HOURLY("Hourly"),
    LIFETIME("Lifetime");
    
    private String type;
    
    private StatisticTypes(String type) {
        this.type = type;
    }
    
    @Override
    public Object getDatabaseRepresentation() {
        return this.type;
    }
};
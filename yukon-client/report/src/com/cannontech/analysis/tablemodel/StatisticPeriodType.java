package com.cannontech.analysis.tablemodel;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum StatisticPeriodType implements DatabaseRepresentationSource
{
    DAILY("Daily"),
    HOURLY("Hourly"),
    MONTHLY("Monthly"),
    LIFETIME("Lifetime");
    
    private String type;
    
    private StatisticPeriodType(String type) {
        this.type = type;
    }
    
    @Override
    public Object getDatabaseRepresentation() {
        return this.type;
    }
};
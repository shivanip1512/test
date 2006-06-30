package com.cannontech.database.incrementer;

import javax.sql.DataSource;

public class CachingTableIncrementer extends MultiTableIncrementer {
    int currentMax = -1;
    int current = 0;
    int incrementBy = 30;

    public CachingTableIncrementer(DataSource dataSource) {
        super(dataSource);
    }
    
    @Override
    public synchronized int getNextValue() {
        if (++current > currentMax) {
            currentMax = super.getNextValue(incrementBy);
            current = currentMax - incrementBy + 1;
        }
        return current;
    }

}

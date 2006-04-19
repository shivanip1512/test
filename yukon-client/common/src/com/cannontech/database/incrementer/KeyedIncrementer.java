package com.cannontech.database.incrementer;

public interface KeyedIncrementer {
    public long getNextValue(String key);
}

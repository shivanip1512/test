package com.cannontech.database.incrementer;

public interface KeyedIncrementer {
    public int getNextValue(String key);
}

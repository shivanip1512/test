package com.cannontech.database.incrementer;


public class NextValueHelper {
    private KeyedIncrementer incrementer;
    
    public int getNextValue(String tableName) {
        return incrementer.getNextValue(tableName);
    }
    
    public KeyedIncrementer getSequenceNameLookup() {
        return incrementer;
    }
    
    public void setSequenceNameLookup(KeyedIncrementer incrementer) {
        this.incrementer = incrementer;
    }

}

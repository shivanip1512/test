package com.cannontech.database.incrementer;

import javax.persistence.Table;


public class NextValueHelper {
    private KeyedIncrementer incrementer;
    
    public int getNextValue(String tableName) {
        return incrementer.getNextValue(tableName);
    }
    
    public int getNextValue(Object bean) {
        Table annotation = bean.getClass().getAnnotation(Table.class);
        String tableName = annotation.name();
        return getNextValue(tableName);
    }
    
    public KeyedIncrementer getSequenceNameLookup() {
        return incrementer;
    }
    
    public void setSequenceNameLookup(KeyedIncrementer incrementer) {
        this.incrementer = incrementer;
    }

}

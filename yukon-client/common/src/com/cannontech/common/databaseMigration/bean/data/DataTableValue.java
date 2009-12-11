package com.cannontech.common.databaseMigration.bean.data;


public class DataTableValue implements DataTableEntity {
    private String value;

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    public String toString(){
        return this.value;
    }
}

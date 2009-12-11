package com.cannontech.common.databaseMigration.bean.data.template;


public class DataValueTemplate implements DataEntryTemplate {
    String nullId = null;

    public String getNullId() {
        return nullId;
    }
    public void setNullId(String nullId) {
        this.nullId = nullId;
    }
    
    public String toString(){
        return "Null Id =" +this.nullId;
        		
    }
}
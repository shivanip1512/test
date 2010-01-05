package com.cannontech.common.databaseMigration.bean.data.template;


public class DataValueTemplate implements DataEntryTemplate {
    String nullId = null;
    String filterValue = null;

    public String getNullId() {
        return nullId;
    }
    public void setNullId(String nullId) {
        this.nullId = nullId;
    }
    
    public String getFilterValue() {
        return filterValue;
    }
    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }
    
    public String toString(){
        String results = " => ";

        if (this.nullId != null) {
            results += "Null Id = "+this.nullId;
        }
        if (this.filterValue != null) {
            results += "Filter Value = "+this.filterValue;
        }
        
        return results;		
    }
}
package com.cannontech.common.databaseMigration.bean.config;

public class ConfigurationIncludeTable extends ConfigurationTable{
    
    private String includeReferenceColumnName;
    
    public String getIncludeReferenceColumnName() {
        return includeReferenceColumnName;
    }
    
    public void setIncludeReferenceColumnName(String includeReferenceColumnName) {
        this.includeReferenceColumnName = includeReferenceColumnName;
    }
    
    public String toString(){
        return "includeReferenceColumnName = " + includeReferenceColumnName + "\n" +
               super.toString();
        
    }
    
}
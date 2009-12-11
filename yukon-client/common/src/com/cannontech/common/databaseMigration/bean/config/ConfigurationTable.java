package com.cannontech.common.databaseMigration.bean.config;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationTable{
    private String tableName;
    private String label;
    private List<ConfigurationIncludeTable> includeElementList;
    private List<ConfigurationTable> referencesTables;

    public ConfigurationTable(){
        this.includeElementList = new ArrayList<ConfigurationIncludeTable>();
        this.referencesTables = new ArrayList<ConfigurationTable>();
    }

    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    public List<ConfigurationIncludeTable> getIncludeElementList() {
        return includeElementList;
    }
    public void setIncludeElementList(List<ConfigurationIncludeTable> includeElementList) {
        this.includeElementList = includeElementList;
    }

    public List<ConfigurationTable> getReferencesTables() {
        return referencesTables;
    }
    public void setReferencesTables(List<ConfigurationTable> referencesTables) {
        this.referencesTables = referencesTables;
    }
    
    public String toString(){
        String results = "";
        
        results += "tableName = "+tableName+"\n";
        results += "label = "+label+"\n";

        results += "includeElements = [";
        for (ConfigurationIncludeTable includeElement : includeElementList) {
            results += includeElement.toString()+"\n";
        }
        results += "]\n";
        
        results += "referenceElements = [";
        for (ConfigurationTable referencesTables : this.referencesTables) {
            results += referencesTables.toString()+"\n";
        }
        results += "]\n";

        return results;
    }
    
}
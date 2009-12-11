package com.cannontech.common.databaseMigration.bean.data.template;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.common.databaseMigration.bean.data.ElementCategoryEnum;

public class DataTableTemplate implements DataEntryTemplate {

    private ElementCategoryEnum elementCategory;
    private int elementMappingKey = 0;
    private String tableName;
    private Map<String, DataEntryTemplate> tableColumns;
    private List<DataTableTemplate> tableReferences;
  
    public DataTableTemplate(ElementCategoryEnum elementCategory,
                     int elementMappingKey,
                     String tableName){
        this.elementCategory = elementCategory;
        this.elementMappingKey = elementMappingKey;
        this.tableName = tableName;
        this.tableColumns = new LinkedHashMap<String, DataEntryTemplate>();
        this.tableReferences = new ArrayList<DataTableTemplate>();
    }
    
    public ElementCategoryEnum getElementCategory() {
        return elementCategory;
    }
    public void setElementCategory(ElementCategoryEnum elementCategory) {
        this.elementCategory = elementCategory;
    }
    
    public int getElementMappingKey() {
        return elementMappingKey;
    }
    public void setElementMappingKey(int elementMappingKey) {
        this.elementMappingKey = elementMappingKey;
    }
    
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public void putTableColumn(String columnName, DataEntryTemplate dataEntry){
        this.tableColumns.put(columnName, dataEntry);
    }
    public Map<String, DataEntryTemplate> getTableColumns() {
        return tableColumns;
    }
    public void setTableColumns(Map<String, DataEntryTemplate> tableColumns) {
        this.tableColumns = tableColumns;
    }

    public void addTableReferences(DataTableTemplate dataTable){
        this.tableReferences.add(dataTable);
    }
    public List<DataTableTemplate> getTableReferences() {
        return tableReferences;
    }
    public void setTableReferences(List<DataTableTemplate> tableReferences) {
        this.tableReferences = tableReferences;
    }

    public String toString(){
        String results = "";
        
        results += "elementCategory = "+elementCategory+"\n";
        results += "elementMappingKey = "+elementMappingKey+"\n";
        results += "tableName = "+tableName+"\n";

        results += "tableColumns = {";
        for (String key : this.tableColumns.keySet()) {
            DataEntryTemplate dataEntry = this.tableColumns.get(key);
            if (dataEntry instanceof DataTableTemplate) {
                results += key +" => ("+ dataEntry.toString()+")\n";
            } else {
                results += key +" => "+ dataEntry.toString()+"\n";
            }
        }
        results += "}\n";
        
        results += "tableReferences = {";
        for (DataTableTemplate dataTable : this.tableReferences) {
            results += "("+ dataTable.toString()+")\n";
        }
        results += "}\n";

        return results;
    }
}
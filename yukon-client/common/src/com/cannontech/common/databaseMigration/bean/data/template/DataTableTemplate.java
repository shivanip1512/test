package com.cannontech.common.databaseMigration.bean.data.template;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.common.databaseMigration.bean.data.ElementCategoryEnum;

public class DataTableTemplate implements DataEntryTemplate {

    private ElementCategoryEnum elementCategory;
    private String name;
    private String table;
    private Map<String, DataEntryTemplate> tableColumns;
    private List<DataTableTemplate> tableReferences;
  
    public DataTableTemplate(ElementCategoryEnum elementCategory,
                     String name,
                     String table){
        this.elementCategory = elementCategory;
        this.name = name;
        this.table = table;
        this.tableColumns = new LinkedHashMap<String, DataEntryTemplate>();
        this.tableReferences = new ArrayList<DataTableTemplate>();
    }
    
    public ElementCategoryEnum getElementCategory() {
        return elementCategory;
    }
    public void setElementCategory(ElementCategoryEnum elementCategory) {
        this.elementCategory = elementCategory;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getTable() {
        return table;
    }
    public void setTable(String table) {
        this.table = table;
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
        
        results += "elementCategory = " + elementCategory + "\n";
        results += "name = " + name + "\n";
        results += "table = " + table + "\n";

        results += "tableColumns = {";
        for (String key : this.tableColumns.keySet()) {
            DataEntryTemplate dataEntry = this.tableColumns.get(key);
            if (dataEntry instanceof DataTableTemplate) {
                results += key + " => (" + dataEntry.toString() + ")\n";
            } else {
                results += key + " => " + dataEntry.toString() + "\n";
            }
        }
        results += "}\n";
        
        results += "tableReferences = {";
        for (DataTableTemplate dataTable : this.tableReferences) {
            results += "(" + dataTable.toString() + ")\n";
        }
        results += "}\n";

        return results;
    }
}
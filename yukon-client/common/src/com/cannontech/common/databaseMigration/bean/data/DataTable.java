package com.cannontech.common.databaseMigration.bean.data;

import java.util.List;
import java.util.Map;

import com.cannontech.common.databaseMigration.bean.data.template.DataTableTemplate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DataTable implements DataTableEntity {
    private ElementCategoryEnum elementCategory;
    private String tableName; // could also be a reference to DataTableTemplate
    private Map<String, DataTableEntity> tableColumns = Maps.newLinkedHashMap();
    private List<DataTable> tableReferences = Lists.newArrayList();
    
    public ElementCategoryEnum getElementCategory() {
        return elementCategory;
    }
    public void setElementCategory(ElementCategoryEnum elementCategory) {
        this.elementCategory = elementCategory;
    }
    
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public Map<String, DataTableEntity> getTableColumns() {
        return tableColumns;
    }
    public void setTableColumns(Map<String, DataTableEntity> tableColumns) {
        this.tableColumns = tableColumns;
    }
    
    public List<DataTable> getTableReferences() {
        return tableReferences;
    }
    public void setTableReferences(List<DataTable> tableReferences) {
        this.tableReferences = tableReferences;
    }

    public String toString(){
        String results = "";
        
        results += "elementCategory = "+this.elementCategory+"\n";
        results += "tableName = "+this.tableName+"\n";

        results += "tableColumns = {";
        for (String key : this.tableColumns.keySet()) {
            DataTableEntity dataTableEntity = this.tableColumns.get(key);
            if (dataTableEntity instanceof DataTableTemplate) {
                results += key +" => ("+ dataTableEntity.toString()+")\n";
            } else {
                results += key +" => "+ dataTableEntity.toString()+"\n";
            }
        }
        results += "}\n";
        
        results += "tableReferences = {";
        for (DataTable dataTable : this.tableReferences) {
            results += "("+ dataTable.toString()+")\n";
        }
        results += "}\n";

        return results;
    }

}

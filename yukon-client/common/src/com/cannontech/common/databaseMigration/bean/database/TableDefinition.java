package com.cannontech.common.databaseMigration.bean.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;

public class TableDefinition {
    String name;
    String table;
    ListMultimap<String, Column> columns = ArrayListMultimap.create();
    
    public TableDefinition(String name, String table, Element tableElement){
        this.name = name;
        this.table = table;
        
        ColumnTypeEnum[] columnTypes = ColumnTypeEnum.values();
        for (ColumnTypeEnum columnType : columnTypes)
            addColumns(tableElement, columnType);
    }
    
    @SuppressWarnings("unchecked")
    private void addColumns(Element tableElement, ColumnTypeEnum columnType){
        List<Element> columnElements = tableElement.getChildren(columnType.getXMLKey());
        for (Element columnElement : columnElements) {
            Column column = new Column();
            column.setName(columnElement.getAttributeValue("name"));
            column.setTableRef(columnElement.getAttributeValue("tableRef"));
            column.setColumnType(columnType);
            if (column.getTableRef() != null) {
                String refTypeStr = columnElement.getAttributeValue("refType"); 
                if (refTypeStr != null) {
                    column.setRefType(ReferenceTypeEnum.valueOf(refTypeStr));
                } else {
                    column.setRefType(ReferenceTypeEnum.ONE_TO_ONE);
                }
            }
            column.setNullId(columnElement.getAttributeValue("nullId"));
            column.setFilterValue(columnElement.getAttributeValue("filterValue"));
            this.columns.put(columnType.toString(), column);
        }
    }
    
    public Column getColumn(String columnName){
        ColumnTypeEnum[] columnTypes = ColumnTypeEnum.values();
        for (ColumnTypeEnum columnType : columnTypes)
            for (Column column : this.columns.get(columnType.toString()))
                if(column.getName().equalsIgnoreCase(columnName));

        return null;
    }
    
    public List<Column> getColumns(ColumnTypeEnum... columnTypes){
        List<Column> columns = new ArrayList<Column>();
        
        for (ColumnTypeEnum columnType : columnTypes) 
            columns.addAll(this.columns.get(columnType.toString()));
        
        return columns;
    }
    
    public List<Column> getAllColumns(){
        List<Column> columns = new ArrayList<Column>();
        
        ColumnTypeEnum[] columnTypes = ColumnTypeEnum.values();
        for (ColumnTypeEnum columnType : columnTypes)
            columns.addAll(getColumns(columnType));
            
        return columns;
    }
    
    public Map<String, String> getDefaultColumnValueMap(){
        HashMap<String, String> columnValueMap = Maps.newLinkedHashMap();

        List<Column> allColumns = getAllColumns();
        for (Column column : allColumns) {
            if (column.getDefaultValue() != null) {
                columnValueMap.put(column.getName(), column.getDefaultValue());
            }
        }
        
        return columnValueMap;
    }
    
    public static List<String> getColumnNames(List<Column> columns) {
        List<String> columnNames = new ArrayList<String>();
        for (Column column : columns) {
            columnNames.add(column.getName());
        }
        return columnNames;
    }
    
    public String getName() {
        return name;
    }
    
    public String getTable() {
        return table;
    }

    public ListMultimap<String, Column> getColumns() {
        return columns;
    }
    
    public String toString(){
        return this.name;
    }
}
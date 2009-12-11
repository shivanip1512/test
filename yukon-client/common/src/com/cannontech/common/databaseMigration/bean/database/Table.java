package com.cannontech.common.databaseMigration.bean.database;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class Table{
    String tableName;
    ListMultimap<String, Column> columns = ArrayListMultimap.create();
    
    public Table(String tableName){
        this.tableName = tableName;
    }

    public Table(String tableName, Element tableElement){
        this.tableName = tableName;
        
        ColumnTypeEnum[] columnTypes = ColumnTypeEnum.values();
        for (ColumnTypeEnum columnType : columnTypes)
            addColumns(tableElement, columnType);
    }

    
    @SuppressWarnings("unchecked")
    private void addColumns(Element tableElement, ColumnTypeEnum columnType){
        List<Element> columnElements = tableElement.getChildren(columnType.toString());
        for (Element columnElement : columnElements) {
            Column column = new Column();
            column.setName(columnElement.getAttributeValue("name"));
            column.setTableRef(columnElement.getAttributeValue("tableRef"));
            column.setColumnType(columnType);
            if(column.getTableRef() != null &&
               !column.getColumnType().equals(ColumnTypeEnum.primaryKey)){
                column.setRefType(ReferenceTypeEnum.valueOf(columnElement.getAttributeValue("refType")));
            }
            String nullId = columnElement.getAttributeValue("nullId");
            column.setNullId(nullId);
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
    
    public static List<String> getColumnNames(List<Column> columns) {
        List<String> columnNames = new ArrayList<String>();
        for (Column column : columns) {
            columnNames.add(column.getName());
        }
        return columnNames;
    }
    
    public String getTableName() {
        return tableName;
    }

    public ListMultimap<String, Column> getColumns() {
        return columns;
    }
    
    public String toString(){
        return this.tableName;
    }
}
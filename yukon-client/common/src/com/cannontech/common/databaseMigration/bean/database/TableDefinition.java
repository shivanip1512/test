package com.cannontech.common.databaseMigration.bean.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;

public class TableDefinition {
    private String name;
    private String table;
    private ListMultimap<String, Column> columns = ArrayListMultimap.create();
    
    public TableDefinition(String name, String table, Element tableElement){
        this.name = name;
        this.table = table;
        
        ColumnTypeEnum[] columnTypes = ColumnTypeEnum.values();
        for (ColumnTypeEnum columnType : columnTypes)
            addColumns(tableElement, columnType);
    }
    
    private void addColumns(Element tableElement, ColumnTypeEnum columnType){

    	Iterable<Element> columnElements = 
    		Iterables.filter(tableElement.getChildren(columnType.getXMLKey()), Element.class);
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

            String escapeNeededStr = columnElement.getAttributeValue("escapingNeeded");
            if (!StringUtils.isBlank(escapeNeededStr)) {
            	Boolean escapeNeeded = Boolean.valueOf(escapeNeededStr);
                column.setEscapingNeeded(escapeNeeded);
            }

            String addToDisplayLabelsStr = columnElement.getAttributeValue("addToDisplayLabels");
            if (!StringUtils.isBlank(addToDisplayLabelsStr)){
            	Boolean addToDisplayLable = Boolean.valueOf(addToDisplayLabelsStr);
                column.setAddToDisplayLabels(addToDisplayLable);
            }
            
            String uniqueStr = columnElement.getAttributeValue("unique");
            if(!StringUtils.isBlank(uniqueStr)) {
            	if(!ColumnTypeEnum.IDENTIFIER.equals(columnType)) {
            		throw new IllegalArgumentException("The unique attribute is only valid for " +
            				"identifier columns");
            	}
            	Boolean unique = Boolean.valueOf(uniqueStr);
            	column.setUnique(unique);
            }
            
            this.columns.put(columnType.toString(), column);
        }
    }
    
    public Column getColumn(String columnName){
        ColumnTypeEnum[] columnTypes = ColumnTypeEnum.values();
        for (ColumnTypeEnum columnType : columnTypes) {
            for (Column column : this.columns.get(columnType.toString())) {
                if(column.getName().equalsIgnoreCase(columnName)) {
                    return column;
                }
            }
            
        }
        return null;
    }
    
    public List<Column> getColumns(ColumnTypeEnum... columnTypes){
        List<Column> columns = new ArrayList<Column>();
        
        for (ColumnTypeEnum columnType : columnTypes) 
            columns.addAll(this.columns.get(columnType.toString()));
        
        return columns;
    }
    
    public List<Column> getAddToDisplayLabelsColumns(){
        List<Column> results = new ArrayList<Column>();
        List<Column> dataColumns = this.getColumns(ColumnTypeEnum.DATA);
        
        for (Column dataColumn : dataColumns) {
            if (dataColumn.isAddToDisplayLabels()) {
                results.add(dataColumn);
            }
        }
        
        return results;
    }
    
    public List<Column> getAllColumns(){
        List<Column> columns = new ArrayList<Column>();
        
        ColumnTypeEnum[] columnTypes = ColumnTypeEnum.values();
        for (ColumnTypeEnum columnType : columnTypes)
            columns.addAll(getColumns(columnType));
            
        return columns;
    }
    
    public List<Column> getUniqueColumns() {
    	List<Column> uniqueColumns = new ArrayList<Column>();
    	for(Column column : columns.values()) {
    		if(column.isUnique()) {
    			uniqueColumns.add(column);
    		}
    	}
    	return uniqueColumns;
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
    
    /**
     * Table alias
     */
    public String getName() {
        return name;
    }
    
    /**
     * Actual database table name (for use in sql queries)
     */
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
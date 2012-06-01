package com.cannontech.common.csvImport;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Represents an individual row of data (typically a single import operation).
 */
public class ImportRow {
    private List<String> stringValues;
    private Map<String, String> lookupMap = Maps.newHashMap();
    
    public ImportRow(List<String> headers, String[] values) {
        stringValues = Lists.newArrayList(values);
        for(int i = 0; i < headers.size(); i++) {
            if(values[i].equals("")) {
                continue; //no value, leave out of map
            }
            if(values[i].equalsIgnoreCase("NULL")) {
                values[i] = "";
            }
            lookupMap.put(headers.get(i), values[i]);
        }
    }
    
    /**
     * @return A map of "column name, row value" pairs. In most cases, the getValue and getValueAt
     * methods will be more convenient.
     */
    public Map<String, String> getValueMap() {
        return lookupMap;
    }
    
    /**
     * @return The list of String values, in the order they were specified in the original file. In 
     * most cases, the getValue and getValueAt methods will be more convenient.
     */
    public List<String> getValueList() {
        return stringValues;
    }
    
    /**
     * @return The String value at the specified index in this row.
     */
    public String getValueAt(int index) {
        return stringValues.get(index);
    }
    
    /**
     * @return The String value for the specified column in this row, or null if no value is present.
     */
    public String getValue(String columnName) {
        return lookupMap.get(columnName);
    }
    
    /**
     * @return True if there is a value for the specified column in this row (including NULL).
     * Otherwise false.
     */
    public boolean hasValue(String columnName) {
        return lookupMap.containsKey(columnName);
    }
    
    /**
     * Removes the specified column's value from this row.
     */
    public void removeColumn(String columnName, int columnIndex) {
        lookupMap.remove(columnName);
        stringValues.remove(columnIndex);
    }
}

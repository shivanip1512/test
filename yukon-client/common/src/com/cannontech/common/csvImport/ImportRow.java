package com.cannontech.common.csvImport;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Represents an individual row of data (typically a single import operation).
 */
public class ImportRow {
    private ImportFileFormat format;
    private List<String> stringValues;
    private Map<String, String> lookupMap = Maps.newHashMap();
    
    public ImportRow(ImportFileFormat format, List<String> headers, String[] values) {
        this.format = format;
        
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
     * If the column is specified as "uppercased" in the file import format, the value will be uppercased
     * before being returned.
     */
    public String getValue(String columnName) {
        boolean uppercase = format.getColumnByName(columnName).isUppercaseValue();
        String value = lookupMap.get(columnName);
        if(uppercase && value != null) {
            value = value.toUpperCase();
        }
        return value;
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

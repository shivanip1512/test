package com.cannontech.amr.device.search.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class acts as a container for search result data
 * 
 * @author macourtois
 *
 */
public class DeviceSearchResultEntry {
    private LinkedHashMap<String, String> fields = new LinkedHashMap<String, String>();
    
    /**
     * Add a field to the search result
     * 
     * @param fieldName name of the field
     * @param fieldValue value of the field
     */
    public void putField(String fieldName, String fieldValue) {
        fields.put(fieldName, fieldValue);
    }

    /**
     * Get the map of the fields and their value
     * 
     * @return the map of all field/value for this entry
     */
    public Map<String, String> getMap() {
        return fields;
    }
}

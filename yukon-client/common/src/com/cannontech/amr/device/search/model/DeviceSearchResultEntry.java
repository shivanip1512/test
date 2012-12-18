package com.cannontech.amr.device.search.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class DeviceSearchResultEntry {
    private LinkedHashMap<String, String> fields = new LinkedHashMap<String, String>();
    
    public void putField(String fieldName, String fieldValue) {
        fields.put(fieldName, fieldValue);
    }

    public Map<String, String> getMap() {
        return fields;
    }
}

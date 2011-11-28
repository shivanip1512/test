package com.cannontech.web.bulk.model;

import java.util.Map;

import com.google.common.collect.Maps;

public class FdrInterfaceDisplayable {
    String name;
    Map<String, String> columnsAndDescriptions;
    
    public FdrInterfaceDisplayable(String name) {
        this.name = name;
        columnsAndDescriptions = Maps.newLinkedHashMap();
    }
    
    public void addColumnAndDescription(String columnName, String description) {
        columnsAndDescriptions.put(columnName, description);
    }
    
    public String getName() {
        return name;
    }
    
    public Map<String, String> getColumnsAndDescriptions() {
        return columnsAndDescriptions;
    }
}

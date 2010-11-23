package com.cannontech.common.bulk.collection.inventory;

public enum InventoryCollectionType {
    
    idList, 
    inventoryFilter, 
    fileUpload, 
    memory, 
    ;
    
    public String getParameterName(String parameter) {
        return name() + "." + parameter;
    }
    
}
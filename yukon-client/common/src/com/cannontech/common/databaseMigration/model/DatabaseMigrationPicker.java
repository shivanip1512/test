package com.cannontech.common.databaseMigration.model;

import java.util.Map;
import java.util.Set;

public class DatabaseMigrationPicker {

    int databaseMigrationId;
    Map<String,Object> identifierColumnValueMap = null;
    
    public DatabaseMigrationPicker(int databaseMigrationId, Map<String, Object> identifierColumnValueMap) {
        this.databaseMigrationId = databaseMigrationId;
        this.identifierColumnValueMap = identifierColumnValueMap;
    }
    
    public int getDatabaseMigrationId() {
        return databaseMigrationId;
    }

    public Map<String, Object> getIdentifierColumnValueMap() {
        return identifierColumnValueMap;
    }
    
    public String getIdentifierColumnValueMapString() {
        String results = "";
        
        Set<String> keySet = identifierColumnValueMap.keySet();
        for (String key : keySet) {
            results += key+":"+identifierColumnValueMap.get(key)+" ";
        }
        
        return results;
    }
}

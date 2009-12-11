package com.cannontech.common.databaseMigration.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

import org.springframework.core.io.Resource;

import com.cannontech.common.databaseMigration.TableChangeCallback;
import com.cannontech.user.YukonUserContext;

public interface DatabaseMigrationService {
    
    public void processImportDatabaseMigration (File importFile);
    
    public void processExportDatabaseMigration(File configurationXMLFile, 
                                               List<Integer> primaryKeyList, 
                                               YukonUserContext userContext) throws IOException;
    
    public List<Map<String, Object>> getConfigurationItems(String configurationName);
        
    
    public Map<String, Resource> getAvailableConfigurationMap();
    
    public SortedMap<String, SortedSet<String>> getAvailableConfigurationDatabaseTableMap();
    
    public void addDBTableListener(String tableName, TableChangeCallback tableChangeCallback);
    
}
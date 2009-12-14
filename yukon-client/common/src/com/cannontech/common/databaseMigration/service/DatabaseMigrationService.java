package com.cannontech.common.databaseMigration.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

import org.springframework.core.io.Resource;

import com.cannontech.common.databaseMigration.TableChangeCallback;
import com.cannontech.common.databaseMigration.bean.ExportDatabaseMigrationStatus;
import com.cannontech.common.databaseMigration.bean.ImportDatabaseMigrationStatus;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.user.YukonUserContext;

public interface DatabaseMigrationService {
    
    public ImportDatabaseMigrationStatus validateImportFile(File importFile);
    
    public ImportDatabaseMigrationStatus processImportDatabaseMigration (File importFile);
    
    public ExportDatabaseMigrationStatus processExportDatabaseMigration(File configurationXMLFile, 
                                               List<Integer> primaryKeyList, 
                                               YukonUserContext userContext) throws IOException;
    
    public List<Map<String, Object>> getConfigurationItems(String configurationName);

    /**
     * Returns a list of all the configuration identifier column names
     * 
     * @param configurationName
     * @return
     */
    public List<String> getConfigurationItemColumnIdentifiers(String configurationName);
            
    /**
     * Returns a SqlStatementBuilder that contains the base query for selecting all the 
     * items for a configuration.
     * 
     * @param configurationName
     * @return
     */
    public SqlStatementBuilder getConfigurationItemsBaseSQL(String configurationName);

    
    public Map<String, Resource> getAvailableConfigurationMap();
    
    public SortedMap<String, SortedSet<String>> getAvailableConfigurationDatabaseTableMap();
    
    public void addDBTableListener(String tableName, TableChangeCallback tableChangeCallback);
    
}
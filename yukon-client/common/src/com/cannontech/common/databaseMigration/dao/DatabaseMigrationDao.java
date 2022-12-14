package com.cannontech.common.databaseMigration.dao;

import java.util.Map;

import com.cannontech.common.databaseMigration.bean.database.TableDefinition;

public interface DatabaseMigrationDao {

    /**
     * This method will get the next primary key value for the missingPrimaryKeyName.
     * NOTE: This method should only be used when the next value helper does not give you a value.
     */
    public Integer getNextMissingPrimaryKeyValue(Map<String, String> columnValueMap,
                                                 TableDefinition tableDefinition,
                                                 String missingPrimaryKeyName);

    
    /**
     * This method will create a query that will return a row in the given table 
     * that matches the supplied primary key column and value pairs found in the 
     * columnValueMap.
     * This method will return null if no results are found.
     */
    public Map<String, Object> findResultSetForPrimaryKeyValues(Map<String, String> columnValueMap,
                                                                TableDefinition tableDefinition);

    
    /**
     * This method will create a query that will return a row in the given table 
     * that matches the supplied identifier column and value pairs found in the 
     * columnValueMap.
     * This method will return null if no results are found.
     */
    public Map<String, Object> findResultSetForIdentifierValues(Map<String, String> columnValueMap,
                                                                TableDefinition tableDefinition,
                                                                String missingPrimaryKey);
        
    /**
     * This method will use the table object and column value map to create an insert sql call
     * and use it to add the corresponding entry into the database.
     */
    public Integer insertNewTableEntry(TableDefinition tableDefinition,
                                       Map<String, String> columnValueMap);

    
    /**
     * This method will use the table object and column value map to create an update sql call
     * and use it to update the corresponding entry into the database.
     */
    public void updateTableEntry(TableDefinition tableDefinition, Map<String, String> columnValueMap);

    /**
     * Checks to see if the destination database table has already an existing value for a unique
     * column
     * @param tableName - Table to check
     * @param columnName - Unique column in question
     * @param columnValue - Value that we're checking to see if it already exists
     * @return True if the value already exists in the given table/column
     */
    public boolean uniqueColumnHasExistingValues(String tableName, String columnName, 
    		String columnValue);
    
}
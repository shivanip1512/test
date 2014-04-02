package com.cannontech.common.databaseMigration.service.impl;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.databaseMigration.bean.ExportDatabaseMigrationStatus;
import com.cannontech.common.databaseMigration.bean.SqlHolder;
import com.cannontech.common.databaseMigration.bean.data.DataTable;
import com.cannontech.common.databaseMigration.bean.data.DataTableEntity;
import com.cannontech.common.databaseMigration.bean.data.DataTableValue;
import com.cannontech.common.databaseMigration.bean.data.ElementCategoryEnum;
import com.cannontech.common.databaseMigration.bean.data.template.DataEntryTemplate;
import com.cannontech.common.databaseMigration.bean.data.template.DataTableTemplate;
import com.cannontech.common.databaseMigration.bean.data.template.DataValueTemplate;
import com.cannontech.common.databaseMigration.bean.database.Column;
import com.cannontech.common.databaseMigration.bean.database.ColumnTypeEnum;
import com.cannontech.common.databaseMigration.bean.database.DatabaseDefinition;
import com.cannontech.common.databaseMigration.bean.database.TableDefinition;
import com.cannontech.common.databaseMigration.model.DatabaseMigrationContainer;
import com.cannontech.common.databaseMigration.service.ConfigurationParserService;
import com.cannontech.common.databaseMigration.service.ConfigurationProcessorService;
import com.cannontech.common.databaseMigration.service.DatabaseMigrationService;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlUtils;
import com.cannontech.user.SystemUserContext;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ConfigurationProcessorServiceImpl implements ConfigurationProcessorService {

    private static Logger log = YukonLogManager.getLogger(ConfigurationProcessorServiceImpl.class);
    private DatabaseDefinition database;
    private ConfigurationParserService configurationParserService;
    private JdbcTemplate jdbcTemplate;
    
    private DatabaseMigrationService databaseMigrationService;
    
    public Iterable<DataTable> processDataTableTemplate(DataTableTemplate template, 
                                                        List<Integer> primaryKeyList, 
                                                        ExportDatabaseMigrationStatus status,
                                                        PrintWriter logFileWriter) {
    	
        Map<DataTableTemplate, Map<Integer, DataTable>> includedTables = Maps.newHashMap();
        
        List<DataTable> allData = Lists.newArrayList();
        
        for (int pk : primaryKeyList) {
        	allData.addAll(buildAndProcessSqlPrimaryKey(template, 
        												Collections.singletonList(pk), 
        												includedTables, 
        												status));
        	status.addCurrentCount();
        	
        	List<DatabaseMigrationContainer> databaseMigrationContainers = 
        	    databaseMigrationService.getItemsByIds(status.getExportTypeEnum(), 
        	                                           Collections.singletonList(pk), 
        	                                           new SystemUserContext());
        	for (DatabaseMigrationContainer databaseMigrationContainer : databaseMigrationContainers) {
        	    String message = "Exported "+databaseMigrationContainer.getDatabaseMigrationDisplay();
        	    log.debug(message);
        	    logFileWriter.println(message);
            }
        }
        
        Iterable<DataTable> result = allData;

        for (Entry<DataTableTemplate, Map<Integer, DataTable>> templateToValueMap : includedTables.entrySet()) {
            for (Entry<Integer, DataTable> primaryKeyToDataTable : templateToValueMap.getValue().entrySet()) {
                result = Iterables.concat(Collections.singleton(primaryKeyToDataTable.getValue()), result);
            }
        }
        
        return result;
        
    }
    
    private DataTable buildAndProcessSqlPrimaryKey(DataTableTemplate template,
                                                   Integer primaryKey,
                                                   Map<DataTableTemplate, 
                                                   		Map<Integer, DataTable>> includedTables) {
        
        List<Integer> primaryKeyList = Collections.singletonList(primaryKey);
        List<DataTable> inlineDataTables = 
        	buildAndProcessSqlPrimaryKey(template, primaryKeyList, includedTables, null);
        return inlineDataTables.get(0);
        
    }
    
    /**
     * Builds and processes the SQL for a DataTable that has the primary key values supplied
     */
    private List<DataTable> buildAndProcessSqlPrimaryKey(DataTableTemplate template,
                                                         List<Integer> primaryKeyList,
                                                         Map<DataTableTemplate, 
                                                         	Map<Integer, DataTable>> includedTables,
                                                         ExportDatabaseMigrationStatus status) {

        // Build up the sql
        SqlHolder sqlHolder = new SqlHolder();
        Map<String, DataEntryTemplate> tableColumns = template.getTableColumns();
        for (String columnName : tableColumns.keySet()) {
            sqlHolder.addSelectClause(columnName);
        }

        String tableName = template.getTable();
        sqlHolder.addFromClause(tableName);
        
        // Add filtering value where segment
        List<Object> whereValues = Lists.newArrayList();
        for (Entry<String, DataEntryTemplate> tableColumn : tableColumns.entrySet()) {
            if (tableColumn.getValue() instanceof DataValueTemplate) {
                DataValueTemplate dataValueTemplate = (DataValueTemplate)tableColumn.getValue();
                if (dataValueTemplate.getFilterValue() != null) {
                    sqlHolder.addWhereClause(tableColumn.getKey() + " = ? ");
                    whereValues.add(dataValueTemplate.getFilterValue());
                }
            }
        }
        
        SqlStatementBuilder selectSQL = sqlHolder.buildSelectSQL();
        
        // Add the primary keys to the query
        if (sqlHolder.getWhereClauses().size() > 0){
            selectSQL.append("AND " + sqlHolder.getSelectClauses().get(0) + " IN (", primaryKeyList, ") ");
        } else {
            selectSQL.append("WHERE " + sqlHolder.getSelectClauses().get(0) + " IN (", primaryKeyList, ") ");
        }
        
        // execute SQL
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> sqlResults = 
        	jdbcTemplate.queryForList(selectSQL.getSql(), whereValues.toArray());

        return processDataTableTemplate(template, primaryKeyList, includedTables, sqlResults, status);
    }

    private List<DataTable> buildAndProcessSqlForeignKey(DataTableTemplate template,
                                                         Integer primaryKey,
                                                         Map<DataTableTemplate, 
                                                         	Map<Integer, DataTable>> includedTables,
                                                         String referencesColumnName) {
        
        List<Integer> primaryKeyList = Collections.singletonList(primaryKey);
        return buildAndProcessSqlForeignKey(template, primaryKeyList, includedTables, referencesColumnName);
    }
    
    /**
     * Builds and processes the SQL for a DataTable that has the values of a foreign key supplied
     */
    private List<DataTable> buildAndProcessSqlForeignKey(DataTableTemplate template,
                                                         List<Integer> primaryKeyList,
                                                         Map<DataTableTemplate, 
                                                         	Map<Integer, DataTable>> includedTables,
                                                         String referencesColumnName) {

        // Build up the sql
        SqlHolder sqlHolder = new SqlHolder();
        Map<String, DataEntryTemplate> tableColumns = template.getTableColumns();
        for (String columnName : tableColumns.keySet()) {
            sqlHolder.addSelectClause(columnName);
        }
        if (!sqlHolder.getSelectClauses().contains(referencesColumnName)) {
            sqlHolder.addSelectClause(referencesColumnName);
        }

        String tableName = template.getTable();
        sqlHolder.getFromClauses().add(tableName);
        
        // Add filtering value where segment
        List<Object> whereValues = Lists.newArrayList();
        for (Entry<String, DataEntryTemplate> tableColumn : tableColumns.entrySet()) {
            if (tableColumn.getValue() instanceof DataValueTemplate) {
                DataValueTemplate dataValueTemplate = (DataValueTemplate)tableColumn.getValue();
                if (dataValueTemplate.getFilterValue() != null) {
                    sqlHolder.addWhereClause(tableColumn.getKey() + " = ? ");
                    whereValues.add(dataValueTemplate.getFilterValue());
                }
            }
        }
        
        
        SqlStatementBuilder selectSQL = sqlHolder.buildSelectSQL();
        
        // Added the primaryKeys of the referenced table to the where clause
        if (sqlHolder.getWhereClauses().size() > 0){
            selectSQL.append("AND " + referencesColumnName + " IN (", primaryKeyList, ") ");
        } else {
            selectSQL.append("WHERE " + referencesColumnName + " IN (", primaryKeyList, ") ");
        }
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> sqlResults = 
        	jdbcTemplate.queryForList(selectSQL.getSql(), whereValues.toArray());
        return processDataTableTemplate(template, primaryKeyList, includedTables, sqlResults, null);
    }
    
    private List<DataTable> processDataTableTemplate(DataTableTemplate template,
                                                     List<Integer> primaryKeyList,
                                                     Map<DataTableTemplate, 
                                                     	Map<Integer, DataTable>> includedTables,
                                                     List<Map<String, Object>> sqlResults,
                                                     ExportDatabaseMigrationStatus status) {
        List<DataTable> result = Lists.newArrayList();
        for (Map<String, Object> sqlResult : sqlResults) {
            
            DataTable dataForTable = processDataTableTemplate(template,
                                                             includedTables,
                                                             sqlResult);
            result.add(dataForTable);
        }

        return result;
    }

    /**
     * This method takes the information generated from the buildAndProcessSql calls and uses that
     * data to populate the DataTable object
     */
    private DataTable processDataTableTemplate(DataTableTemplate template,
                                               Map<DataTableTemplate, 
                                               		Map<Integer, DataTable>> includedTables,
                                               Map<String, Object> sqlResult) {
        DataTable dataForTable = new DataTable();
        dataForTable.setName(template.getName());
        dataForTable.setTable(template.getTable());
        dataForTable.setElementCategory(template.getElementCategory());

        Map<String, DataEntryTemplate> tableColumns = template.getTableColumns();
        for (Map.Entry<String, DataEntryTemplate> entry : tableColumns.entrySet()) {
        	
            String columnName = entry.getKey();
            DataTableEntity dataTableEntity = null;
            Object value = sqlResult.get(columnName);
            
            // The column we are looking at has a reference to another column
            // Process and build that Sql before jumping to the next column
            if (entry.getValue() instanceof DataTableTemplate) {
                DataTableTemplate thisTemplate = (DataTableTemplate) entry.getValue();
                int thisPrimaryKey = Integer.parseInt(value.toString());
                
                // inline
                if (thisTemplate.getElementCategory() == null) {
                    dataTableEntity = 
                    	buildAndProcessSqlPrimaryKey(thisTemplate, thisPrimaryKey, includedTables);
                
                // reference
                } else if (thisTemplate.getElementCategory().equals(ElementCategoryEnum.REFERENCE)) {
                    dataTableEntity = 
                    	buildAndProcessSqlPrimaryKey(thisTemplate, thisPrimaryKey, includedTables);
                    
                // include
                } else if (thisTemplate.getElementCategory().equals(ElementCategoryEnum.INCLUDE)) {
                    // Generate the item portion of the include
                    DataTable includeTable = 
                    	buildAndProcessSqlPrimaryKey(thisTemplate, thisPrimaryKey, includedTables);
                    Map<Integer, DataTable> primaryKeyToDataTableMap = includedTables.get(thisTemplate);
                    if (primaryKeyToDataTableMap == null){
                        primaryKeyToDataTableMap = Maps.newHashMap();
                        primaryKeyToDataTableMap.put(thisPrimaryKey, includeTable);
                        includedTables.put(thisTemplate, primaryKeyToDataTableMap);
                    } else {
                        primaryKeyToDataTableMap.put(Integer.valueOf(thisPrimaryKey), includeTable);
                    }
                    
                    // Generating the reference portion of the include 
                    DataTableTemplate referenceTableTemplate = 
                        new DataTableTemplate(ElementCategoryEnum.REFERENCE, 
                        					  thisTemplate.getName(), 
                        					  thisTemplate.getTable());
                    configurationParserService.buildDatabaseMapReferenceTemplate(referenceTableTemplate);
                    dataTableEntity = 
                    	buildAndProcessSqlPrimaryKey(referenceTableTemplate, thisPrimaryKey, includedTables);
                    
                }
            } else if (entry.getValue() instanceof DataValueTemplate) {
                String valueStr = null;
                if (value != null) {
                    valueStr = value.toString();
                }
                
                // Processing nullId case
                TableDefinition table = database.getTable(template.getName());
                List<Column> primaryKeyColumns = table.getColumns(ColumnTypeEnum.PRIMARY_KEY);
                boolean nullIdFound = false;
                for (Column primaryKeyColumn : primaryKeyColumns) {
                    if (primaryKeyColumn.getName().equals(entry.getKey())) {
                        if (primaryKeyColumn.getNullId() != null &&
                            primaryKeyColumn.getNullId().equals(value.toString())){
                            nullIdFound = true;
                        }
                        break;
                    }
                }
                if(nullIdFound){
                    break;
                }
                
                // Check to see if the string needs to be escaped.
                Column columnDef = table.getColumn(entry.getKey());
                if (columnDef.isEscapingNeeded()) {
                    valueStr = StringEscapeUtils.escapeJava(valueStr);
                }
                
                // Adds a value of a column to the DataTable object
                DataTableValue dataTableValue = new DataTableValue();
                if (valueStr != null) {
                    valueStr = SqlUtils.convertDbValueToString(valueStr);
                    dataTableValue.setValue(valueStr);
                }
                dataTableEntity = dataTableValue;
            }
            if (dataTableEntity == null) {
                throw new IllegalStateException(entry.getValue().getClass().toString());
            }
            dataForTable.getTableColumns().put(columnName, dataTableEntity);
        }
        
        // Create primayKey identifier for the references
        TableDefinition table = database.getTable(template.getName());
        List<String> primaryKeyColumnNames = 
        	TableDefinition.getColumnNames(table.getColumns(ColumnTypeEnum.PRIMARY_KEY));
        String primaryKey = primaryKeyColumnNames.get(0);
        Integer primaryKeyValue = Integer.parseInt(sqlResult.get(primaryKey).toString());
         
        generateReferencesDataTables(template, dataForTable, primaryKeyValue, includedTables);
        return dataForTable;
    }
    
    /**
     * This method generates the information for the references portion of the DataTable object.
     * This also handles omitting the reference column that is inherited from the DataTable higher 
     * up the structure
     */
    private void generateReferencesDataTables(DataTableTemplate template,
                                              DataTable dataForTable,
                                              Integer primaryKey,
                                              Map<DataTableTemplate, 
                                              	Map<Integer, DataTable>> includedTables) {

        List<DataTableTemplate> tableReferences = template.getTableReferences();
        for (DataTableTemplate referencesDataTableTemplate : tableReferences) {
            
            // Build up the table object and figure out which table we are pointing to
            TableDefinition referencesTable = database.getTable(referencesDataTableTemplate.getName());
            List<Column> allReferencesTableColumns = referencesTable.getAllColumns();
            for (Column column : allReferencesTableColumns) {
                if (column.getTableRef() != null &&
                    column.getTableRef().equals(dataForTable.getName())) {
                    String referencesColumnName = column.getName();
                                       
                    List<DataTable> result = 
                        buildAndProcessSqlForeignKey(referencesDataTableTemplate, 
                        							 primaryKey, 
                        							 includedTables, 
                        							 referencesColumnName);
                    dataForTable.getTableReferences().addAll(result);

                }
            }
        }
    }

    public void setDatabaseDefinitionXML(Resource databaseDefinitionXML){
        database = new DatabaseDefinition(databaseDefinitionXML);
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Autowired
    public void setConfigurationParserService(ConfigurationParserService configurationParserService) {
        this.configurationParserService = configurationParserService;
    }

    @Autowired
    public void setDatabaseMigrationService(DatabaseMigrationService databaseMigrationService) {
        this.databaseMigrationService = databaseMigrationService;
    }
}

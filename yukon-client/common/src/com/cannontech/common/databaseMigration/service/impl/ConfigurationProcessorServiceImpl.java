package com.cannontech.common.databaseMigration.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.databaseMigration.bean.ExportDatabaseMigrationStatus;
import com.cannontech.common.databaseMigration.bean.SqlHolder;
import com.cannontech.common.databaseMigration.bean.data.DataTable;
import com.cannontech.common.databaseMigration.bean.data.DataTableEntity;
import com.cannontech.common.databaseMigration.bean.data.DataTableReference;
import com.cannontech.common.databaseMigration.bean.data.DataTableValue;
import com.cannontech.common.databaseMigration.bean.data.ElementCategoryEnum;
import com.cannontech.common.databaseMigration.bean.data.template.DataEntryTemplate;
import com.cannontech.common.databaseMigration.bean.data.template.DataTableTemplate;
import com.cannontech.common.databaseMigration.bean.data.template.DataValueTemplate;
import com.cannontech.common.databaseMigration.bean.database.Column;
import com.cannontech.common.databaseMigration.bean.database.ColumnTypeEnum;
import com.cannontech.common.databaseMigration.bean.database.Database;
import com.cannontech.common.databaseMigration.bean.database.Table;
import com.cannontech.common.databaseMigration.service.ConfigurationProcessorService;
import com.cannontech.common.util.SqlStatementBuilder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ConfigurationProcessorServiceImpl implements ConfigurationProcessorService {

    private static Logger log = YukonLogManager.getLogger(ConfigurationProcessorServiceImpl.class);
    private Database database;
    private JdbcTemplate jdbcTemplate;
    
    public Iterable<DataTable> processDataTableTemplate(DataTableTemplate template, List<Integer> primaryKeyList, ExportDatabaseMigrationStatus status) {
    	
        Map<DataTableReference, DataTable> includedTables = Maps.newHashMap();
        
        List<DataTable> allData = Lists.newArrayList();
        
        for (int pk : primaryKeyList) {
        	allData.addAll(buildAndProcessSqlPrimaryKey(template, Collections.singletonList(pk), includedTables, status));
        	status.addCurrentCount();
        }
        
        Iterable<DataTable> result = Iterables.concat(includedTables.values(), allData);
        
        return result;
        
    }
    
    private DataTable buildAndProcessSqlPrimaryKey(DataTableTemplate template,
                                                   Integer primaryKey,
                                                   Map<DataTableReference, DataTable> includedTables) {
        
        List<Integer> primaryKeyList = Collections.singletonList(primaryKey);
        List<DataTable> inlineDataTables = buildAndProcessSqlPrimaryKey(template, primaryKeyList, includedTables, null);
        return inlineDataTables.get(0);
        
    }
    
    /**
     * Builds and processes the SQL for a DataTable that has the primary key values supplied
     * 
     * @param template
     * @param primaryKeyList
     * @param includedTables
     * @return
     */
    private List<DataTable> buildAndProcessSqlPrimaryKey(DataTableTemplate template,
                                                         List<Integer> primaryKeyList,
                                                         Map<DataTableReference, DataTable> includedTables,
                                                         ExportDatabaseMigrationStatus status) {

        // Build up the sql
        SqlHolder sqlHolder = new SqlHolder();
        Map<String, DataEntryTemplate> tableColumns = template.getTableColumns();
        for (String columnName : tableColumns.keySet()) {
            sqlHolder.addSelectClause(columnName);
        }

        String tableName = template.getTableName();
        sqlHolder.addFromClause(tableName);
        
        SqlStatementBuilder selectSQL = sqlHolder.buildSelectSQL();
        
        // Add the primary keys to the query
        if (sqlHolder.getWhereClauses().size() > 0){
            selectSQL.append("AND "+sqlHolder.getSelectClauses().get(0)+" IN (",primaryKeyList,") ");
        } else {
            selectSQL.append("WHERE "+sqlHolder.getSelectClauses().get(0)+" IN (",primaryKeyList,") ");
        }
        
        // execute SQL
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> sqlResults = jdbcTemplate.queryForList(selectSQL.getSql());

        return processDataTableTemplate(template, primaryKeyList, includedTables, sqlResults, status);
    }

    private List<DataTable> buildAndProcessSqlForeignKey(DataTableTemplate template,
                                                         Integer primaryKey,
                                                         Map<DataTableReference, DataTable> includedTables,
                                                         String referencesColumnName) {
        
        List<Integer> primaryKeyList = Collections.singletonList(primaryKey);
        return buildAndProcessSqlForeignKey(template, primaryKeyList, includedTables, referencesColumnName);
    }
    
    /**
     * Builds and processes the SQL for a DataTable that has the values of a foreign key supplied
     * 
     * @param template
     * @param primaryKey
     * @param includedTables
     * @param referencesColumnName
     * @return
     */
    private List<DataTable> buildAndProcessSqlForeignKey(DataTableTemplate template,
                                                         List<Integer> primaryKeyList,
                                                         Map<DataTableReference, DataTable> includedTables,
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

        String tableName = template.getTableName();
        sqlHolder.getFromClauses().add(tableName);
        
        SqlStatementBuilder selectSQL = sqlHolder.buildSelectSQL();
        
        // Added the primaryKeys of the referenced table to the where clause
        if (sqlHolder.getWhereClauses().size() > 0){
            selectSQL.append("AND "+referencesColumnName+" IN (",primaryKeyList,") ");
        } else {
            selectSQL.append("WHERE "+referencesColumnName+" IN (",primaryKeyList,") ");
        }
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> sqlResults = jdbcTemplate.queryForList(selectSQL.getSql());
        return processDataTableTemplate(template, primaryKeyList, includedTables, sqlResults, null);
    }
    
    private List<DataTable> processDataTableTemplate(DataTableTemplate template,
                                                     List<Integer> primaryKeyList,
                                                     Map<DataTableReference, DataTable> includedTables,
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
     * 
     * @param template
     * @param includedTables
     * @param sqlResult
     * @return
     */
    private DataTable processDataTableTemplate(DataTableTemplate template,
                                               Map<DataTableReference, DataTable> includedTables,
                                               Map<String, Object> sqlResult) {
        DataTable dataForTable = new DataTable();
        dataForTable.setTableName(template.getTableName());
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
                    dataTableEntity = buildAndProcessSqlPrimaryKey(thisTemplate, thisPrimaryKey, includedTables);
                
                // reference
                } else if (thisTemplate.getElementCategory().equals(ElementCategoryEnum.REFERENCE)) {
                    dataTableEntity = buildAndProcessSqlPrimaryKey(thisTemplate, thisPrimaryKey, includedTables);
                    
                // include
                } else if (thisTemplate.getElementCategory().equals(ElementCategoryEnum.INCLUDE)) {
                    dataTableEntity = buildAndProcessSqlPrimaryKey(thisTemplate, thisPrimaryKey, includedTables);
//                    includedTables.put(dataTableEntity, value);
                }
            } else if (entry.getValue() instanceof DataValueTemplate) {
                // Processing nullId case
                Table table = database.getTable(template.getTableName());
                List<Column> columns = table.getColumns(ColumnTypeEnum.PRIMARY_KEY);
                boolean nullIdFound = false;
                for (Column column : columns) {
                    if(column.getName().equals(entry.getKey()) &&
                       column.getNullId() != null &&
                       column.getNullId().equals(value.toString())){
                        nullIdFound = true;
                        break;
                    }
                }
                if(nullIdFound){
                    break;
                }
                
                // Adds a value of a column to the DataTable object
                DataTableValue dataTableValue = new DataTableValue();
                dataTableValue.setValue(value.toString());
                dataTableEntity = dataTableValue;
            }
            if (dataTableEntity == null) {
                throw new IllegalStateException(entry.getValue().getClass().toString());
            }
            dataForTable.getTableColumns().put(columnName, dataTableEntity);
        }
        
        // Create primayKey identifier for the references
        Table table = database.getTable(template.getTableName());
        List<String> primaryKeyColumnNames = Table.getColumnNames(table.getColumns(ColumnTypeEnum.PRIMARY_KEY));
        String primaryKey = primaryKeyColumnNames.get(0);
        Integer primaryKeyValue = Integer.parseInt(sqlResult.get(primaryKey).toString());
         
        generateReferencesDataTables(template, dataForTable, primaryKeyValue, includedTables);
        return dataForTable;
    }
    
    /**
     * This method generates the information for the references portion of the DataTable object.
     * This also handles omitting the reference column that is inherited from the DataTable higher up the structure
     * 
     * @param template
     * @param dataForTable
     * @param primaryKey
     * @param includedTables
     */
    private void generateReferencesDataTables(DataTableTemplate template,
                                              DataTable dataForTable,
                                              Integer primaryKey,
                                              Map<DataTableReference, DataTable> includedTables) {

        List<DataTableTemplate> tableReferences = template.getTableReferences();
        for (DataTableTemplate referencesDataTableTemplate : tableReferences) {
            
            // Build up the table object and figure out which table we are pointing to
            Table referencesTable = database.getTable(referencesDataTableTemplate.getTableName());
            List<Column> allReferencesTableColumns = referencesTable.getAllColumns();
            for (Column column : allReferencesTableColumns) {
                if (column.getTableRef() != null &&
                    column.getTableRef().equals(dataForTable.getTableName())) {
                    String referencesColumnName = column.getName();
                                       
                    List<DataTable> result = 
                        buildAndProcessSqlForeignKey(referencesDataTableTemplate, primaryKey, includedTables, referencesColumnName);
                    dataForTable.getTableReferences().addAll(result);

                }
            }
        }
    }

    public void setDatabaseDefinitionXML(Resource databaseDefinitionXML){
        database = new Database(databaseDefinitionXML);
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}

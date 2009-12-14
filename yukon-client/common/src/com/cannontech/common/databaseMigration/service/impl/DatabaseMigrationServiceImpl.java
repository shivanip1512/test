package com.cannontech.common.databaseMigration.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigHelper;
import com.cannontech.common.databaseMigration.TableChangeCallback;
import com.cannontech.common.databaseMigration.bean.DatabaseMigrationImportCallback;
import com.cannontech.common.databaseMigration.bean.ExportDatabaseMigrationStatus;
import com.cannontech.common.databaseMigration.bean.ImportDatabaseMigrationStatus;
import com.cannontech.common.databaseMigration.bean.SQLHolder;
import com.cannontech.common.databaseMigration.bean.config.ConfigurationTable;
import com.cannontech.common.databaseMigration.bean.data.DataTable;
import com.cannontech.common.databaseMigration.bean.data.template.DataTableTemplate;
import com.cannontech.common.databaseMigration.bean.database.Column;
import com.cannontech.common.databaseMigration.bean.database.ColumnTypeEnum;
import com.cannontech.common.databaseMigration.bean.database.Database;
import com.cannontech.common.databaseMigration.bean.database.Table;
import com.cannontech.common.databaseMigration.service.ConfigurationParserService;
import com.cannontech.common.databaseMigration.service.ConfigurationProcessorService;
import com.cannontech.common.databaseMigration.service.DatabaseMigrationService;
import com.cannontech.common.databaseMigration.service.ExportXMLGeneratorService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.PoolManager;
import com.cannontech.user.SystemUserContext;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class DatabaseMigrationServiceImpl implements DatabaseMigrationService, ResourceLoaderAware{
    private static Logger log = YukonLogManager.getLogger(DatabaseMigrationServiceImpl.class);
    Multimap<String, TableChangeCallback> tableChangeCallbacks = ArrayListMultimap.create();

    private ConfigurationParserService configurationParserService;
    private ConfigurationProcessorService configurationProcessorService;
    private Database database;
    private DateFormattingService dateFormattingService;
    private ExportXMLGeneratorService exportXMLGeneratorService;
    private JdbcTemplate jdbcTemplate;
    private PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver; 
    private RolePropertyDao rolePropertyDao;
    private TransactionOperations transactionTemplate;
    private ScheduledExecutor scheduledExecutor = null;

    public ImportDatabaseMigrationStatus validateImportFile(File importFile){
        
        List<Element> importItemList = getElementListFromFile(importFile);

        return validateElementList(importItemList);
        
    }

    private ImportDatabaseMigrationStatus validateElementList(final List<Element> importItemList) {
        final ImportDatabaseMigrationStatus importDatabaseMigrationStatus = new ImportDatabaseMigrationStatus();
        
/////// Change isolation level and make sure it works in both cases: SQL Server and Oracle.
        
        transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                for (Element element : importItemList) {
                    importDatabaseMigrationStatus.incrementProcessed();
                    
                    String label;
                    try {
                        label = getElementLable(element);
                    } catch (IllegalArgumentException e) {
                        importDatabaseMigrationStatus.addErrorListEntry("Invalid entry", e.getMessage());
                        continue;
                    }
                    
                    try {
                        processElement(element, null);
                    } catch (ConfigurationErrorException e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                        importDatabaseMigrationStatus.addErrorListEntry(label, e.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                        importDatabaseMigrationStatus.addErrorListEntry(label, e.getMessage());
                    }
                }
                status.setRollbackOnly();
                return null;
            }
        });

        return importDatabaseMigrationStatus;
    }

    
    /**
     * 
     * @param element
     * @return
     */
    private String getElementLable(Element element) {
        if(element.getName() != "item" ||
           element.getAttributeValue("name") == null) {
            throw new IllegalArgumentException("The configuration element is not the currect format. ");
        }
        String initialTableName = element.getAttributeValue("name");
        Table table = database.getTable(initialTableName);
        
        List<String> columnNames = 
            Table.getColumnNames(table.getColumns(ColumnTypeEnum.PRIMARY_KEY, ColumnTypeEnum.IDENTIFIER));
        
        
        
        return null;
    }

        
    /**
     * @return ******************/

    public ImportDatabaseMigrationStatus processImportDatabaseMigration (final File importFile){
        
        final ImportDatabaseMigrationStatus importMigrationStatus = new ImportDatabaseMigrationStatus();

        scheduledExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int primaryKey = 0;
                String tableName = "";

                List<Element> importItemList = getElementListFromFile(importFile);
                processElementList(importItemList, importMigrationStatus);
                handleRowInserted(tableName, primaryKey);
            }
        });
        
        return importMigrationStatus;
    }
    
    private void processElementList(final List<Element> importItemList, 
                                    ImportDatabaseMigrationStatus importMigrationStatus) {
        final DatabaseMigrationImportCallback databaseMigrationImportCallback = new DatabaseMigrationImportCallback();
        for (final Element element : importItemList) {
            databaseMigrationImportCallback.incrementProcessed();
            transactionTemplate.execute(new TransactionCallback() {
                public Object doInTransaction(TransactionStatus status) {
                    try {
                        processElement(element, null);
//                    } catch (ConfigurationWarningException e) {
//                        log.warn(e.getMessage());
//                        databaseMigrationImportCallback.addWarningListEntry(e.getMessage());
                    } catch (ConfigurationErrorException e) {
                        log.error(e.getMessage());
                        databaseMigrationImportCallback.addErrorListEntry(e.getMessage());
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        databaseMigrationImportCallback.addErrorListEntry(e.getMessage());
                    }
                    status.setRollbackOnly();
                    return null;
                }
            });
        }
    }
    
    /**
     * 
     * 
     * @param importItem
     * @throws ConfigurationErrorException 
     */
    private void processElement(Element importItem, Map<String, String> primaryKeyColumnValuePair) throws ConfigurationErrorException{
        if (!importItem.getName().equals("item")) {
            throw new IllegalArgumentException("Invalid import configuration file structure. Items must start with an item tag. "+importItem.getName());
        }
        
        String initialTableName = importItem.getAttributeValue("name");
        Table table = database.getTable(initialTableName);
        processElement(table, importItem, primaryKeyColumnValuePair);
    }

    
    
    private Integer processElement(Table table, Element importItem, Map<String, String> primaryKeyTableValuePair) throws ConfigurationErrorException{
        List<Column> allColumns = table.getAllColumns();
        List<String> columnNames = Table.getColumnNames(allColumns);
        List<?> children = importItem.getChildren();
        Map<String, String> columnValueMap = table.getDefaultColumnValueMap();
        
        // fill in primary key value if it exists
        if(primaryKeyTableValuePair != null){
            Object[] primaryKeyColumnValuePairEntryArray = primaryKeyTableValuePair.entrySet().toArray();
            @SuppressWarnings("unchecked")
            Entry<String, String> primaryKeyColumnValuePairEntry = (Entry<String, String>)primaryKeyColumnValuePairEntryArray[0];
            for ( Column column : allColumns) {
                if (primaryKeyColumnValuePairEntry.getKey().equals(column.getTableRef())) {
                    columnValueMap.put(column.getName(), primaryKeyColumnValuePairEntry.getValue());
                    break;
                }
            }
        }

        for (Object child : children) {
            if (!(child instanceof Element)){continue;}
                
            Element childElement = (Element)child;
            String childElementType = childElement.getName();
            String childColumnName = childElement.getAttributeValue("field");
            
            if (childElementType.equals("references")) { continue; }
            
            if (!columnNames.contains(childColumnName)){
                throw new IllegalArgumentException("The supplied column was not found in database definition file. ("+table.getTableName()+"."+childColumnName+")");
            }

            for (Column column : allColumns) {

                if (column.getName().equals(childColumnName)) {
                    if (childElementType.equals("value")) {
                        String content = childElement.getText();
                        if (content != null) {
                            columnValueMap.put(column.getName(), content);
                        }
                    } else if (childElementType.equals("item")) {
                        if(column.getTableRef() == null) {
                            throw new IllegalArgumentException("The supplied column does not have a table reference in the database definition file. ("+table.getTableName()+"."+childColumnName+")");
                        }

                        Table childTable = database.getTable(column.getTableRef());
                        Integer primaryKeyId = processElement(childTable, childElement, null);
                        columnValueMap.put(column.getName(),primaryKeyId.toString());
                    } else if (childElementType.equals("reference")) {
                        if(column.getTableRef() == null){
                            throw new IllegalArgumentException("The supplied column does not have a table reference in the database definition file. ("+table.getTableName()+"."+childColumnName+")");
                        }

                        Table childTable = database.getTable(column.getTableRef());
                        Integer primaryKeyId = processReferenceElement(childTable, childElement);
                        columnValueMap.put(column.getName(), primaryKeyId.toString());

                    } else {
                        throw new IllegalArgumentException("The supplied element type is not a valid. ("+childElementType+")");
                    }
                    continue;
                }
            }
        }
        
        Integer primaryKeyId = processTableEntry(columnValueMap, table, importItem);

        // handle references
        Map<String, String> newPrimaryKeyTableValuePair = Collections.singletonMap(table.getTableName(), primaryKeyId.toString());
        
        Element referencesElement = importItem.getChild("references");
        if (referencesElement != null) {
            List<?> referencesItems = referencesElement.getChildren("item");
            for (Object referencesItem : referencesItems) {
                if (!(referencesItem instanceof Element)){continue;}
                Element referencesItemElement = (Element) referencesItem;
                processElement(referencesItemElement, newPrimaryKeyTableValuePair);
            }
        }
        
        return primaryKeyId;
    }

    private Integer processReferenceElement(Table table, Element importItem) throws ConfigurationErrorException{

        List<Column> allColumns = table.getAllColumns();
        List<String> columnNames = Table.getColumnNames(allColumns);
        List<?> children = importItem.getChildren();
        Map<String, String> columnValueMap = table.getDefaultColumnValueMap();
        
        // Null reference found. Return the null id;
        if (children.size() == 0){
            Column primaryKeyColumn = table.getColumns(ColumnTypeEnum.PRIMARY_KEY).get(0);
            if (primaryKeyColumn.getNullId() != null){
                return Integer.valueOf(primaryKeyColumn.getNullId());
            }
        }
        
        for (Object child : children) {
            if (!(child instanceof Element)){continue;}
                
            Element childElement = (Element)child;
            String childElementType = childElement.getName();
            String childColumnName = childElement.getAttributeValue("field");
            
            if (!columnNames.contains(childColumnName)){
                throw new IllegalArgumentException("The supplied column was not found in database definition file. ("+table.getTableName()+"."+childColumnName+")");
            }

            for (Column column : allColumns) {

                if (column.getName().equals(childColumnName)) {
                    if (childElementType.equals("value")) {
                        String content = childElement.getText();
                        if (content != null) {
                            columnValueMap.put(column.getName(), content);
                        }
                    } else if (childElementType.equals("reference")) {
                        if(column.getTableRef() == null){
                            throw new IllegalArgumentException("The supplied column does not have a table reference in the database definition file. ("+table.getTableName()+"."+childColumnName+")");
                        }

                        Table childTable = database.getTable(column.getTableRef());
                        Integer primaryKeyId = processReferenceElement(childTable, childElement);
                        columnValueMap.put(column.getName(), primaryKeyId.toString());
                    } else {
                        throw new IllegalArgumentException("The supplied element type is not a valid. ("+childElementType+")");
                    }
                }
            }
        }
        
        return processReferenceTableEntry(columnValueMap, table, importItem);
    }
    
    /**
     * Processes the validation for a reference entry
     * 
     * @param columnValueMap
     * @param table
     * @param importItem
     * @return
     * @throws ConfigurationErrorException
     */
    private Integer processReferenceTableEntry(Map<String, String> columnValueMap,
                                               Table table,
                                               Element importItem) throws ConfigurationErrorException{
        
        // Check and make sure all the identifier keys are supplied.
        List<String> referenceColumnNames = Table.getColumnNames(table.getColumns(ColumnTypeEnum.IDENTIFIER));
        for (String referenceColumn : referenceColumnNames) {
            if(!columnValueMap.containsKey(referenceColumn)){
                throw new ConfigurationErrorException("The necessary identifiers were not supplied for the table being imported.  ("+table.getTableName()+"."+referenceColumn+") ");
            }
        }
        
        return buildAndProcessReferenceSQL(columnValueMap, table);
        
    }
    
    /**
     * Builds up the SQL for a reference item and returns the id for the primary key
     * 
     * @param columnValueMap
     * @param table
     * @return
     * @throws ConfigurationErrorException 
     */
    private Integer buildAndProcessReferenceSQL(Map<String, String> columnValueMap,
                                                Table table) throws ConfigurationErrorException {
        
        SQLHolder sqlHolder = new SQLHolder();

        List<String> primaryKeyColumnNames = Table.getColumnNames(table.getColumns(ColumnTypeEnum.PRIMARY_KEY));
        String primaryKeyColumnName = primaryKeyColumnNames.get(0);
        sqlHolder.addSelectClause(primaryKeyColumnName);

        sqlHolder.addFromClause(table.getTableName());

        List<Object> whereParameterValues = new ArrayList<Object>();
        for (Entry<String, String> nameValuePair : columnValueMap.entrySet()){
            sqlHolder.addWhereClause(nameValuePair.getKey()+" = ?");
            whereParameterValues.add(nameValuePair.getValue());
        }
        
        SqlStatementBuilder selectSQL = sqlHolder.buildSelectSQL();
        
        Integer primaryKey = jdbcTemplate.queryForInt(selectSQL.getSql(), whereParameterValues.toArray());
        
        if (primaryKey == null) {
            throw new ConfigurationErrorException("The reference in the import file does not exist for the table "+table.getTableName());
        }
        
        return primaryKey;
    }

    
    /**
     * Processes the validation for a inline entry
     * 
     * @param columnValueMap
     * @param table
     * @param importItem
     * @return
     * @throws ConfigurationErrorException
     */
    private Integer processTableEntry(Map<String, String> columnValueMap,
                                      Table table,
                                      Element importItem) throws ConfigurationErrorException {

        // Check and make sure all the identifier keys are supplied.
        List<String> referenceColumnNames = Table.getColumnNames(table.getColumns(ColumnTypeEnum.IDENTIFIER));
        for (String referenceColumn : referenceColumnNames) {
            if(!columnValueMap.containsKey(referenceColumn)){
                throw new ConfigurationErrorException("The necessary identifiers were not supplied for the table being imported.  ("+table.getTableName()+"."+referenceColumn+") ");
            }
        }
        
        // Check to see if the primary keys were supplied;  If they weren't we need to grab the max value.
        return buildAndProcessPrimaryKeySQL(columnValueMap, table);
    }

    private Integer buildAndProcessPrimaryKeySQL(Map<String, String> columnValueMap,
                                                 Table table) throws ConfigurationErrorException {
        List<Column> primaryKeyColumns = table.getColumns(ColumnTypeEnum.PRIMARY_KEY);
        List<String> primaryKeyColumnNames = Table.getColumnNames(primaryKeyColumns);
        
        // Counting the number of missing primaryKeys this is used for special cases like dual primary keys.
        int missingPKCount = 0;
        List<String> missingPrimaryKeyNameList = new ArrayList<String>();
        for (String primaryKeyColumnName : primaryKeyColumnNames) {
            if (!columnValueMap.containsKey(primaryKeyColumnName)) {
                missingPKCount++;
                missingPrimaryKeyNameList.add(primaryKeyColumnName);
            }
        }
        
        if (missingPKCount > 1) {
            throw new ConfigurationErrorException("More than one primary key was found missing on the input entry. ("+table.getTableName()+") ");

        // PrimaryKey was supplied, use the primary key to gather the information and then check for existing information
        } else if (missingPKCount == 0) {

            Map<String, Object> primaryKeySelectResultMap = 
                buildAndProcessPrimaryKeySelect(columnValueMap, table);

            // comparing data sets against each other using the table object;
            if (primaryKeySelectResultMap != null) {
                try {
                    compareResultTableDataToImportTableData(table, columnValueMap, primaryKeySelectResultMap);
                } catch (ConfigurationWarningException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                return Integer.valueOf(columnValueMap.get(primaryKeyColumnNames.get(0)));

            // The imported item does not exist.  Creating new instance.
            } else {
                insertNewTableEntry(table, columnValueMap);
            }

        // One primary key was missing.  Get the max primary key to insert.
        } else {
            
            // Figure out the missing primaryKey
//            String missingPrimaryKeyName = missingPrimaryKeyNameList.get(0);
//            Integer missingPrimaryKeyValue = getMissingPrimaryKeyInformation(table, columnValueMap, missingPrimaryKeyName);
//            columnValueMap.put(missingPrimaryKeyName, missingPrimaryKeyValue.toString());
            
            // Using the identifiers to see if the object currently exists in the system.
            Map<String, Object> identifierSelectResultMap =
                buildAndProcessIdentifierSelect(columnValueMap, table);
        
            // Checks to see if the imported item exists in the system and then tries to compare it
            // to see if the item matches the table;
            if (identifierSelectResultMap != null) {
                try {
                    compareResultTableDataToImportTableData(table, columnValueMap, identifierSelectResultMap);
                    String primaryKeyName = primaryKeyColumnNames.get(0);
                    return Integer.valueOf(identifierSelectResultMap.get(primaryKeyName).toString());
                } catch (ConfigurationWarningException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            // The imported item does not exist.  Creating new instance.
            } else {
                // !!! CURRENTLY NOT THREAD SAFE
                //     This needs to be locked down in some form so we can guarantee that this value 
                //     doesn't not get added while we are trying to add the entry.  Maybe sequence number table? !!!
                // !!! { !!!
                
/////////////////// next value helper.  Add missing entries
/////////////////// add to table_sequence if it is missing
                
                // Finding next primary key for insert and inserting it into the columnValueMap
                Integer primaryKeyValue = 
                    buildAndProcessGetNextPrimaryKeyValue(columnValueMap, table);
                String primaryKeyName = primaryKeyColumnNames.get(0);
                columnValueMap.put(primaryKeyName, primaryKeyValue.toString());
                
                return insertNewTableEntry(table, columnValueMap);
                // !!! } !!!
            }
        }
        return null;
    }

    /**
     * This method will get the available primary key
     * 
     * @param columnValueMap
     * @param table
     * @return
     */
    private Integer buildAndProcessGetNextPrimaryKeyValue(Map<String, String> columnValueMap,
                                                          Table table) {
 
        List<String> primaryKeyColumnNames = Table.getColumnNames(table.getColumns(ColumnTypeEnum.PRIMARY_KEY));
        String primaryKeyColumnName = primaryKeyColumnNames.get(0);
        
        SqlStatementBuilder selectMaxSQL = new SqlStatementBuilder();
        selectMaxSQL.append("SELECT MAX("+primaryKeyColumnName+")");
        selectMaxSQL.append("FROM "+table.getTableName());
        
        try {
            int maxPrimaryKeyValue = jdbcTemplate.queryForInt(selectMaxSQL.getSql());
            return ++maxPrimaryKeyValue;
        } catch (IncorrectResultSizeDataAccessException e) {
            // A value does not exist for this column.  Using the default of 0.
            return 0;
        }
    }

    /**
     * This method is used to figure out max values in the case of the (LMProgramDirectGear.GearNumber) and (LMProgramDirectGroup.GroupOrder)
     * 
     * @param columnValueMap
     * @param missingColumnName
     */
    private Integer getMissingPrimaryKeyInformation(Table table,
                                                 Map<String, String> columnValueMap,
                                                 String missingColumnName) {

        
        List<String> allColumnNames = 
            Table.getColumnNames(table.getAllColumns());
        
        SQLHolder sqlHolder = new SQLHolder();
        sqlHolder.addSelectClause("MAX ("+missingColumnName+")");
        sqlHolder.addFromClause(table.getTableName());

        for (String columnName : allColumnNames) {
            if (columnValueMap.containsKey(columnName)) {
                sqlHolder.addWhereClause(columnName+" IN ("+columnValueMap.get(columnName)+") ");
            }
        }
        SqlStatementBuilder selectMaxSQL = sqlHolder.buildSelectSQL();
        
        int maxPrimaryKeyValue = 1;
        try {
            maxPrimaryKeyValue = jdbcTemplate.queryForInt(selectMaxSQL.getSql());
        } catch (IncorrectResultSizeDataAccessException e) {
            // A value does not exist for this column.  Using the default of 0.
        }
        return maxPrimaryKeyValue++;
    }

    /**
     * Insert new table entry into the database
     * 
     * @param table
     * @param columnValueMap
     */
    private Integer insertNewTableEntry(Table table,
                                        Map<String, String> columnValueMap) {
        
        // Builds insert statement
        Object[] columnValueMapKeys  = columnValueMap.keySet().toArray();
        SqlStatementBuilder insertSQL = new SqlStatementBuilder();
        insertSQL.append("INSERT INTO "+table.getTableName()+"(");
        insertSQL.append(columnValueMapKeys[0].toString());
        for (int i = 1; i < columnValueMapKeys.length; i++) {
            insertSQL.append(", "+columnValueMapKeys[i].toString());
        }
        insertSQL.append(")");
        insertSQL.append("VALUES (?");
        
        List<Object> insertValues = new ArrayList<Object>();
        insertValues.add(columnValueMap.get(columnValueMapKeys[0].toString()));
        for (int i = 1; i < columnValueMapKeys.length; i++) {
            insertSQL.append(", ?");
            insertValues.add(columnValueMap.get(columnValueMapKeys[i].toString()));
        }
        insertSQL.append(") ");

        // Insert entry into the database
        jdbcTemplate.update(insertSQL.getSql(), insertValues.toArray());
        
        List<String> primaryKeyColumns = Table.getColumnNames(table.getColumns(ColumnTypeEnum.PRIMARY_KEY));
        return Integer.valueOf(columnValueMap.get(primaryKeyColumns.get(0)));

    }

    /**
     * builds and processes a select sql generated from the primary key fields found in the database definition file
     * and the values from the columnValueMap created from the import file.
     * 
     * @param columnValueMap
     * @param table
     * @return
     */
    private Map<String, Object> buildAndProcessPrimaryKeySelect(Map<String, String> columnValueMap,
                                                                Table table) {
        // Get information from primary key
        SQLHolder primaryKeySelectSQLHolder = new SQLHolder();
        for (Column column : table.getAllColumns()) {
            primaryKeySelectSQLHolder.addSelectClause(column.getName());
        }
        primaryKeySelectSQLHolder.addFromClause(table.getTableName());
        for (Column primaryKeyColumn : table.getColumns(ColumnTypeEnum.PRIMARY_KEY)) {
            primaryKeySelectSQLHolder.addWhereClause(primaryKeyColumn.getName()+" = "+columnValueMap.get(primaryKeyColumn.getName()));
        }
        SqlStatementBuilder primaryKeySelectSQL = primaryKeySelectSQLHolder.buildSelectSQL();
        
        // Process primaryKey Select clause
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> primaryKeySelectResultMap = jdbcTemplate.queryForMap(primaryKeySelectSQL.getSql());
            return primaryKeySelectResultMap;
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }
    
    /**
     * builds and processes a select sql generated from the identifiers fields found in the database definition file
     * and the values from the columnValueMap created from the import file.
     * 
     * @param columnValueMap
     * @param table
     * @return
     */
    private Map<String, Object> buildAndProcessIdentifierSelect(Map<String, String> columnValueMap,
                                                                Table table) {
        
        // Get information from identifier key
        SQLHolder identifierSelectSQLHolder = new SQLHolder();
        for (Column column : table.getAllColumns()) {
            identifierSelectSQLHolder.addSelectClause(column.getName());
        }
        identifierSelectSQLHolder.addFromClause(table.getTableName());
        
        List<Object> whereParameterValues = new ArrayList<Object>();
        for (Column identifierColumn : table.getColumns(ColumnTypeEnum.IDENTIFIER)) {
            identifierSelectSQLHolder.addWhereClause(identifierColumn.getName()+" = ? ");
            whereParameterValues.add(columnValueMap.get(identifierColumn.getName()));
        }
        SqlStatementBuilder identifierSelectSQL = identifierSelectSQLHolder.buildSelectSQL();
        
        // Process identifier Select clause
        try{
            @SuppressWarnings("unchecked")
            Map<String, Object> primaryKeySelectResultMap = jdbcTemplate.queryForMap(identifierSelectSQL.getSql(), whereParameterValues.toArray());
            return primaryKeySelectResultMap;
        } catch (IncorrectResultSizeDataAccessException e) {
            // The entry does not exist in the system.
            return null;
        }
        
        
    }

    private void compareResultTableDataToImportTableData(Table table,
                                                         Map<String, String> columnValueMap,
                                                         Map<String, Object> resultMap) throws ConfigurationErrorException, ConfigurationWarningException {
        
        // The resultMap has no entries therefore symbolizing no conflicts
        if (resultMap == null) {
            return;
        }
        
        List<String> warningList = new ArrayList<String>();
        List<String> errorList = new ArrayList<String>();
        boolean tableMatched = true;

        // Compare primary key values
        List<String> primaryKeyColumnNames = Table.getColumnNames(table.getColumns(ColumnTypeEnum.PRIMARY_KEY));
        for (String primaryKeyColumnName : primaryKeyColumnNames) {
            String sqlColumnValue = resultMap.get(primaryKeyColumnName).toString();
            String importColumnValue = columnValueMap.get(primaryKeyColumnName);

            if(importColumnValue == null ||
               sqlColumnValue.equals(importColumnValue)){
                continue;
            }

            if(sqlColumnValue == null) {}
            if(importColumnValue == null) {}
            tableMatched = false;
            errorList.add("The column "+table.getTableName()+"."+primaryKeyColumnName+" does not match the value in the database for an existing object. "+
                          " (Database value = "+sqlColumnValue+", Import value = "+importColumnValue+") " );
        }

        // Compare foreign key values
        List<String> identifierColumnNames = Table.getColumnNames(table.getColumns(ColumnTypeEnum.IDENTIFIER));
        for (String identifierColumnName : identifierColumnNames) {
            String sqlColumnValue = resultMap.get(identifierColumnName).toString();
            String importColumnValue = columnValueMap.get(identifierColumnName);

            if(sqlColumnValue.equals(importColumnValue)){
                continue;
            }
            
            if(sqlColumnValue == null) {}
            if(importColumnValue == null) {}
            errorList.add("The column "+table.getTableName()+"."+identifierColumnName+" does not match the value in the database for an existing object. "+
                          " (Database value = "+sqlColumnValue+", Import value = "+importColumnValue+") " );
            tableMatched = false;
        }

        
        List<String> dataColumnNames = Table.getColumnNames(table.getColumns(ColumnTypeEnum.DATA));
        for (String dataColumnName : dataColumnNames) {
            String sqlColumnValue = resultMap.get(dataColumnName).toString();
            String importColumnValue = columnValueMap.get(dataColumnName);
            
            if(sqlColumnValue.equals(importColumnValue)){
                continue;
            }
            
            if(sqlColumnValue == null) {}
            if(importColumnValue == null) {}
            
            warningList.add("The column "+table.getTableName()+"."+dataColumnName+" does not match the value in the database for an existing object. "+
            		" (Database value = "+sqlColumnValue+", Import value = "+importColumnValue+") " );
            tableMatched = false;
        }

        if (!tableMatched) {
            if (errorList.size() > 0) {
                throw new ConfigurationErrorException(errorList.toString());
            }
            if (warningList.size() > 0) {
                throw new ConfigurationWarningException(warningList.toString());
            }
        }
    }
    

    /********************************/
    
    private List<Element> getElementListFromFile(File importFile){
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            Document importXMLDocument = saxBuilder.build(importFile);
            Element importRoot = importXMLDocument.getRootElement();
            
//            Element dataElement = importRoot.getChild("data");
            Element configurationElement = importRoot.getChild("configuration");
            
            List<?> children = configurationElement.getChildren();
            List<Element> elementList = new ArrayList<Element>();
            for (Object child : children) {
                elementList.add((Element)child);
            }

            return elementList;
        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public ExportDatabaseMigrationStatus processExportDatabaseMigration(File configurationXMLFile, List<Integer> primaryKeyList, YukonUserContext userContext) throws IOException{
        // Create export file
///////        // move file out
        File xmlDataFile = createFile(configurationXMLFile,userContext);
///////
        
        // Parses the configuration file into a java object and uses that object to generate a template for processing
        ConfigurationTable baseTableElement = configurationParserService.buildConfigurationTemplate(configurationXMLFile);
        DataTableTemplate databaseMapTemplateWithMappingKeys = configurationParserService.buildDatabaseMapTemplate(baseTableElement);

        ExportDatabaseMigrationStatus exportDatabaseMigrationStatus = new ExportDatabaseMigrationStatus();
        exportDatabaseMigrationStatus.setTotalCount(primaryKeyList.size());
////////// Build counter around this
        Iterable<DataTable> data = configurationProcessorService.processDataTableTemplate(databaseMapTemplateWithMappingKeys, primaryKeyList);
//////////
        Element generatedXMLFile = null;
        generatedXMLFile = exportXMLGeneratorService.buildXMLFile(data, baseTableElement.getLabel());

        // Output the configuration to the XML file
        Format format = Format.getPrettyFormat(  );
        XMLOutputter xmlOutputter = new XMLOutputter(format);
        xmlOutputter.output(generatedXMLFile, new FileWriter(xmlDataFile));

        return exportDatabaseMigrationStatus;
    }

    public List<Object> getPossibleChoices(){
        
        return null;
    }
    
    public void addDBTableListener(String tableName, TableChangeCallback tableChangeCallback){
        tableChangeCallbacks.put(tableName, tableChangeCallback);
    }
    
    public List<Map<String, Object>> getConfigurationItems(String configurationName){
        Resource configurationResource = getAvailableConfigurationMap().get(configurationName);
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            // Get the first table entry
            Document configurationDocument = saxBuilder.build(configurationResource.getFile());
            @SuppressWarnings("unchecked")
            Iterator<Element> tableElements = configurationDocument.getDescendants(new ElementFilter("table"));
            String baseTableName = null;
            if(tableElements.hasNext()) {
                baseTableName = tableElements.next().getAttributeValue("name");
            } else {
                throw new IllegalArgumentException("The configuration file does not have a table element.");
            }

            Table table = database.getTable(baseTableName);
            return getTableIdentifiersAndInfo(table);

        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }

    public List<String> getConfigurationItemColumnIdentifiers(String configurationName) {
        Resource configurationResource = getAvailableConfigurationMap().get(configurationName);
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            // Get the first table entry
            Document configurationDocument = saxBuilder.build(configurationResource.getFile());
            @SuppressWarnings("unchecked")
            Iterator<Element> tableElements = configurationDocument.getDescendants(new ElementFilter("table"));
            String baseTableName = null;
            if(tableElements.hasNext()) {
                baseTableName = tableElements.next().getAttributeValue("name");
            } else {
                throw new IllegalArgumentException("The configuration file does not have a table element.");
            }

            Table table = database.getTable(baseTableName);
            return getConfigurationItemColumnIdentifiers(table);

        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }
    
    private List<String> getConfigurationItemColumnIdentifiers(Table table){
        List<String> columnList = new ArrayList<String>();

        List<Column> identifyingColumns = 
            table.getColumns(ColumnTypeEnum.PRIMARY_KEY, ColumnTypeEnum.IDENTIFIER);
        for (Column identifyingColumn : identifyingColumns) {
            if(identifyingColumn.getTableRef() != null) {
                Table referencedTable = database.getTable(identifyingColumn.getTableRef());
                columnList.addAll(getConfigurationItemColumnIdentifiers(referencedTable));
            } else {
                if(!identifyingColumn.getColumnType().equals(ColumnTypeEnum.PRIMARY_KEY))
                    columnList.add(identifyingColumn.getName());
            }
        }
        return columnList;
    }
    
    public SqlStatementBuilder getConfigurationItemsBaseSQL(String configurationName){
        Resource configurationResource = getAvailableConfigurationMap().get(configurationName);
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            // Get the first table entry
            Document configurationDocument = saxBuilder.build(configurationResource.getFile());
            @SuppressWarnings("unchecked")
            Iterator<Element> tableElements = configurationDocument.getDescendants(new ElementFilter("table"));
            String baseTableName = null;
            if(tableElements.hasNext()) {
                baseTableName = tableElements.next().getAttributeValue("name");
            } else {
                throw new IllegalArgumentException("The configuration file does not have a table element.");
            }

            Table table = database.getTable(baseTableName);
            return generateIdentifiersAndInfoSQL(table);

        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }

    
    private List<Map<String, Object>> getTableIdentifiersAndInfo(Table table) {

        SqlStatementBuilder selectSQL = generateIdentifiersAndInfoSQL(table);       
        
        // Process SQL Query
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(selectSQL.getSql());
        return queryForList;
    }

    private SqlStatementBuilder generateIdentifiersAndInfoSQL(Table table) {
        List<String> selectColumns = new ArrayList<String>();
        for (Column primaryKeyColumn : table.getColumns(ColumnTypeEnum.PRIMARY_KEY))
            selectColumns.add(primaryKeyColumn.getName());
        List<String> fromEntries = new ArrayList<String>();
        List<String> whereClauses = new ArrayList<String>();
        buildIdentifingSQL(table, selectColumns, fromEntries, whereClauses);
        
        // Create select part of the SQL
        SqlStatementBuilder selectSQL = new SqlStatementBuilder();
        selectSQL.append("SELECT ");
        for (int i = 0; i < selectColumns.size()-1; i++) {
            String columnName = selectColumns.get(i);
            selectSQL.append(columnName+", ");
        }
        selectSQL.append(selectColumns.get(selectColumns.size()-1));
        
        // Create from part of the SQL
        selectSQL.append("FROM ");
        for (int i = 0; i < fromEntries.size()-1; i++) {
            String tableName = fromEntries.get(i);
            selectSQL.append(tableName+", ");
        }
        selectSQL.append(fromEntries.get(fromEntries.size()-1));
        
        // Create where clause of the SQL
        if(whereClauses.size() > 0) {
            selectSQL.append("WHERE "+whereClauses.get(0)+" ");
            for (int i = 1; i < whereClauses.size(); i++) {
                String whereClause = whereClauses.get(i);
                selectSQL.append("AND "+whereClause+" ");
            }
        }
        return selectSQL;
    }

    private void buildIdentifingSQL(Table table, List<String> selectColumns, List<String> fromEntries, List<String> whereClauses){
        fromEntries.add(table.getTableName());
        List<Column> identifyingColumns = 
            table.getColumns(ColumnTypeEnum.PRIMARY_KEY, ColumnTypeEnum.IDENTIFIER);
        for (int i = 0; i < identifyingColumns.size(); i++) {
            Column identifyingColumn = identifyingColumns.get(i);
            if(identifyingColumn.getTableRef() != null) {
                Table referencedTable = database.getTable(identifyingColumn.getTableRef());
                List<Column> referencePrimaryKeys = referencedTable.getColumns(ColumnTypeEnum.PRIMARY_KEY);
                for (Column primaryKey : referencePrimaryKeys) {
                    whereClauses.add(table.getTableName()+"."+identifyingColumn.getName()+" = "+identifyingColumn.getTableRef()+"."+primaryKey.getName());
                    buildIdentifingSQL(referencedTable, selectColumns, fromEntries, whereClauses);
                }
            } else {
                if(!identifyingColumn.getColumnType().equals(ColumnTypeEnum.PRIMARY_KEY))
                    selectColumns.add(identifyingColumn.getName());
            }
        }
    }
    
    public Map<String, Resource> getAvailableConfigurationMap() {
        TreeMap<String, Resource> configurationMap = new TreeMap<String, Resource>();
        
        try {
            List<Resource> configurationResources = getConfigurationFiles();
            for (Resource configurationResource : configurationResources) {
                String configurationLabel = getConfigurationLabel(configurationResource.getFile());
                configurationMap.put(configurationLabel, configurationResource);
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        
        return configurationMap;
    }
    
    public SortedMap<String, SortedSet<String>> getAvailableConfigurationDatabaseTableMap(){
        SortedMap<String, SortedSet<String>> configNameToDatabaseTablesMap = new TreeMap<String, SortedSet<String>>();
        
        try {
            List<Resource> configurationResources = getConfigurationFiles();
            for (Resource configurationResource : configurationResources) {
                String configurationLabel = getConfigurationLabel(configurationResource.getFile());
                
                SortedSet<String> tables = getDBTables(configurationResource.getFile());
                if (tables != null)
                    configNameToDatabaseTablesMap.put(configurationLabel, tables);
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        
        return configNameToDatabaseTablesMap;
    }
    
    private SortedSet<String> getDBTables(File configurationXMLFile){
        SortedSet<String> tables = new TreeSet<String>();
        
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            Document configurationDocument = saxBuilder.build(configurationXMLFile);
        
            @SuppressWarnings("unchecked")
            Iterator<Element> descendants = configurationDocument.getDescendants(new ElementFilter("table")); 
            while (descendants.hasNext()){
                Element element = descendants.next();
                String tableName = element.getAttributeValue("name");
                
                Table table = database.getTable(tableName);
                tables.addAll(database.getConnectedTables(table));
            }

            return tables;
        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
        
    }
    
    private List<Resource> getConfigurationFiles() throws IOException {
        Resource[] resources = pathMatchingResourcePatternResolver.getResources("classpath:com/cannontech/database/configurations/*.xml");
        return Arrays.asList(resources);
    }
    
    /** 
     * This method forces the label to exist!!!!!
     * 
     * @param configurationXMLFile
     * @return
     */
    public String getConfigurationLabel(File configurationXMLFile){
        
        SAXBuilder saxBuilder = new SAXBuilder();
        try {
            Document configurationDocument = saxBuilder.build(configurationXMLFile);
            Element rootElement = configurationDocument.getRootElement();
        
            Element firstLabelElement = rootElement.getChild("configuration");
            return firstLabelElement.getAttribute("name").getValue();

        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    private File createFile(File configurationXMLFile, YukonUserContext userContext) throws IOException{
        
        String rolePropertyPath = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.DATABASE_MIGRATION_FILE_LOCATION, userContext.getYukonUser());
        String path = CtiUtilities.getYukonBase()+rolePropertyPath;
        
        String databaseName = MasterConfigHelper.getConfiguration().getRequiredString("DB_SQLSERVER");
        String schemaUsername = PoolManager.getInstance().getPrimaryUser();
        String component = getConfigurationLabel(configurationXMLFile);
        
        String currentDate = dateFormattingService.format(new Date(), DateFormatEnum.LONG_BOTH, new SystemUserContext());
        String defaultDataFileName = 
            databaseName+"_"+schemaUsername+"_"+component+"_"+currentDate+".xml";;

        File file = new File(path+"/"+defaultDataFileName);
        file.createNewFile();
        return file;
    }
    
    private void handleRowInserted(String tableName, int primaryKey) {
        Collection<TableChangeCallback> collection = tableChangeCallbacks.get(tableName);
        for (TableChangeCallback tableChangeCallback : collection) {
            tableChangeCallback.rowInserted(primaryKey);
        }
    }

    public void setDatabaseDefinitionXML(Resource databaseDefinitionXML){
        database = new Database(databaseDefinitionXML);
    }

    @Autowired
    public void setConfigurationParserService(ConfigurationParserService configurationParserService) {
        this.configurationParserService = configurationParserService;
    }
    
    @Autowired
    public void setConfigurationProcessorService(ConfigurationProcessorService configurationProcessorService) {
        this.configurationProcessorService = configurationProcessorService;
    }
    
    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }

    @Autowired
    public void setExportXMLGeneratorService(ExportXMLGeneratorService exportXMLGeneratorService) {
        this.exportXMLGeneratorService = exportXMLGeneratorService;
    }


    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.pathMatchingResourcePatternResolver = 
            new PathMatchingResourcePatternResolver(resourceLoader);
    }
    
    @Autowired
    public void setTransactionTemplate(TransactionOperations transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
}

class ConfigurationErrorException extends Exception {

    public ConfigurationErrorException(String message) {
        super(message);
    }

    public ConfigurationErrorException(String message, Throwable cause) {
        super(message, cause);
    }

}

class ConfigurationWarningException extends Exception {

    public ConfigurationWarningException(String message) {
        super(message);
    }

    public ConfigurationWarningException(String message, Throwable cause) {
        super(message, cause);
    }

}

class CountHolder{
    private int count = 0;
    
    public CountHolder(int startingValue){this.count = startingValue;}
    public int getCount(){return this.count;}
    public void add(){this.count++;}
}



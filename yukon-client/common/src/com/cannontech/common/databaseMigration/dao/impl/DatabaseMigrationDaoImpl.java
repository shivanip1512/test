package com.cannontech.common.databaseMigration.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cannontech.common.databaseMigration.bean.SqlHolder;
import com.cannontech.common.databaseMigration.bean.database.Column;
import com.cannontech.common.databaseMigration.bean.database.ColumnTypeEnum;
import com.cannontech.common.databaseMigration.bean.database.TableDefinition;
import com.cannontech.common.databaseMigration.dao.DatabaseMigrationDao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.google.common.collect.Lists;

public class DatabaseMigrationDaoImpl implements DatabaseMigrationDao {

    private JdbcTemplate jdbcTemplate;
    private YukonJdbcTemplate yukonJdbcTemplate;
    
    public Integer getNextMissingPrimaryKeyValue(Map<String, String> columnValueMap,
                                                 TableDefinition tableDefinition,
                                                 String missingPrimaryKeyName) {
 
        List<String> primaryKeyColumnNames = TableDefinition.getColumnNames(tableDefinition.getColumns(ColumnTypeEnum.PRIMARY_KEY));
        primaryKeyColumnNames.remove(missingPrimaryKeyName);
        
        // Generating the sql statement for the missing primaryKey
        SqlStatementBuilder selectMaxSQL = new SqlStatementBuilder();
        selectMaxSQL.append("SELECT MAX(" + missingPrimaryKeyName + ")");
        selectMaxSQL.append("FROM " + tableDefinition.getTable());
        if (primaryKeyColumnNames.size() > 0) {
            selectMaxSQL.append("WHERE").append(primaryKeyColumnNames.get(0)).eq(
                columnValueMap.get(primaryKeyColumnNames.get(0)));
        }
        try {
            Integer maxPrimaryKeyValue =
                    yukonJdbcTemplate.queryForInt(selectMaxSQL);
            return ++maxPrimaryKeyValue;
        } catch (IncorrectResultSizeDataAccessException e) {
            // A value does not exist for this column.  Using the default of 0.
            return 0;
        }
    }
    
    public Map<String, Object> findResultSetForPrimaryKeyValues(Map<String, String> columnValueMap,
                                                                TableDefinition tableDefinition) {
        // Generating the sql for all the columns and where all the primary key columns
        SqlHolder primaryKeySelectSqlHolder = new SqlHolder();
        for (Column column : tableDefinition.getAllColumns()) {
            primaryKeySelectSqlHolder.addSelectClause(column.getName());
        }
        primaryKeySelectSqlHolder.addFromClause(tableDefinition.getTable());

        List<Object> whereClauseArgs = Lists.newArrayList();
        for (Column primaryKeyColumn : tableDefinition.getColumns(ColumnTypeEnum.PRIMARY_KEY)) {
            primaryKeySelectSqlHolder.addWhereClause(primaryKeyColumn.getName() + " = ?");
            whereClauseArgs.add(columnValueMap.get(primaryKeyColumn.getName()));
        }
        SqlStatementBuilder primaryKeySelectSQL = primaryKeySelectSqlHolder.buildSelectSQL();
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> primaryKeySelectResultMap = 
            	jdbcTemplate.queryForMap(primaryKeySelectSQL.getSql(), whereClauseArgs.toArray());
            return primaryKeySelectResultMap;
        } catch (IncorrectResultSizeDataAccessException e) {
            // The entry does not exist in the system.
            return null;
        }
    }
    
    public Map<String, Object> findResultSetForIdentifierValues(Map<String, String> columnValueMap,
                                                                TableDefinition tableDefinition,
                                                                String missingPrimaryKey) {
        
        // Generating the sql for all the columns and whereing all the identifier columns
        SqlHolder identifierSelectSqlHolder = new SqlHolder();
        for (Column column : tableDefinition.getAllColumns()) {
            identifierSelectSqlHolder.addSelectClause(column.getName());
        }
        identifierSelectSqlHolder.addFromClause(tableDefinition.getTable());
        
        List<Object> whereParameterValues = new ArrayList<Object>();
        for (Column identifyingColumn : tableDefinition.getColumns(ColumnTypeEnum.PRIMARY_KEY, ColumnTypeEnum.IDENTIFIER)) {
            if (!identifyingColumn.getName().equals(missingPrimaryKey)) {
                identifierSelectSqlHolder.addWhereClause(identifyingColumn.getName() + " = ? ");
                whereParameterValues.add(columnValueMap.get(identifyingColumn.getName()));
            }
        }
        SqlStatementBuilder identifierSelectSQL = identifierSelectSqlHolder.buildSelectSQL();
        
        try{
            @SuppressWarnings("unchecked")
            Map<String, Object> primaryKeySelectResultMap = 
            	jdbcTemplate.queryForMap(identifierSelectSQL.getSql(), whereParameterValues.toArray());
            return primaryKeySelectResultMap;
        } catch (IncorrectResultSizeDataAccessException e) {
            // The entry does not exist in the system.
            return null;
        }
        
        
    }
    
    @Override
    public boolean uniqueColumnHasExistingValues(String tableName, String columnName, String columnValue) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT count(*) ");
    	sql.append("FROM " + tableName);
    	sql.append("WHERE " + columnName).eq(columnValue);
    	
    	int count = yukonJdbcTemplate.queryForInt(sql);
    	
    	return count > 0;
    }
    
    public Integer insertNewTableEntry(TableDefinition tableDefinition,
                                       Map<String, String> columnValueMap) {
        
        // Generating the sql for inserting all the values in the column value map
        // that correspond to the table object.
        Object[] columnValueMapKeys  = columnValueMap.keySet().toArray();
        SqlStatementBuilder insertSQL = new SqlStatementBuilder();
        insertSQL.append("INSERT INTO " + tableDefinition.getTable() + "(");
        insertSQL.append(columnValueMapKeys[0].toString());
        for (int i = 1; i < columnValueMapKeys.length; i++) {
            insertSQL.append(", " + columnValueMapKeys[i].toString());
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
        
        List<String> primaryKeyColumns = 
        	TableDefinition.getColumnNames(tableDefinition.getColumns(ColumnTypeEnum.PRIMARY_KEY));
        return Integer.valueOf(columnValueMap.get(primaryKeyColumns.get(0)));

    }
    
    public void updateTableEntry(TableDefinition tableDefinition,
                                  Map<String, String> columnValueMap) {

        // UPDATE
        Object[] columnValueMapKeys  = columnValueMap.keySet().toArray();
        SqlStatementBuilder updateSQL = new SqlStatementBuilder();
        updateSQL.append("UPDATE "+tableDefinition.getTable());
        
        // SET
        List<Object> argValues = new ArrayList<Object>();
        updateSQL.append("SET "+columnValueMapKeys[0].toString() + " = ? ");
        argValues.add(columnValueMap.get(columnValueMapKeys[0].toString()));
        for (int i = 1; i < columnValueMapKeys.length; i++) {
            updateSQL.append(", " + columnValueMapKeys[i].toString() + " = ? ");
            argValues.add(columnValueMap.get(columnValueMapKeys[i].toString()));
        }

        // WHERE
        List<String> primaryKeyColumnNames = TableDefinition.getColumnNames(tableDefinition.getColumns(ColumnTypeEnum.PRIMARY_KEY));
        updateSQL.append("WHERE " + primaryKeyColumnNames.get(0) + " = ? ");
        argValues.add(columnValueMap.get(primaryKeyColumnNames.get(0)));
        for (int i = 1; i < primaryKeyColumnNames.size(); i++) {
            updateSQL.append("AND " + primaryKeyColumnNames.get(i) + " = ? ");
            argValues.add(columnValueMap.get(primaryKeyColumnNames.get(i)));
        }

        // Update the given entry in the database
        jdbcTemplate.update(updateSQL.getSql(), argValues.toArray());
    }
    
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}

}
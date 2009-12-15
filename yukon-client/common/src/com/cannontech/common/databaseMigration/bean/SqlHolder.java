package com.cannontech.common.databaseMigration.bean;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.util.SqlStatementBuilder;

public class SqlHolder{
    private int databaseMappingKey;
    private List<Integer> primaryKeys;
    private List<String> selectClauses = new ArrayList<String>();
    private List<String> fromClauses = new ArrayList<String>();
    private List<String> whereClauses = new ArrayList<String>();
    
    public int getDatabaseMappingKey() {
        return databaseMappingKey;
    }
    public void setDatabaseMappingKey(int databaseMappingKey) {
        this.databaseMappingKey = databaseMappingKey;
    }
    
    public List<Integer> getPrimaryKeys() {
        return primaryKeys;
    }
    public void setPrimaryKeys(List<Integer> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public void addSelectClause(String selectClause) {
        this.selectClauses.add(selectClause);
    }
    public List<String> getSelectClauses() {
        return selectClauses;
    }
    public void setSelectClauses(List<String> selectClauses) {
        this.selectClauses = selectClauses;
    }

    public void addFromClause(String fromClause) {
        this.fromClauses.add(fromClause);
    }
    public List<String> getFromClauses() {
        return fromClauses;
    }
    public void setFromClauses(List<String> fromClauses) {
        this.fromClauses = fromClauses;
    }
    
    public void addWhereClause(String whereClause){
        this.whereClauses.add(whereClause);
    }
    public List<String> getWhereClauses() {
        return whereClauses;
    }
    public void setWhereClauses(List<String> whereClauses) {
        this.whereClauses = whereClauses;
    }
    
    public SqlStatementBuilder buildSelectSQL(){
        SqlStatementBuilder sqlStatement = new SqlStatementBuilder();
        sqlStatement.append(dataSelectClause(this.selectClauses));
        sqlStatement.append(dataFromClause(this.fromClauses));
        sqlStatement.append(dataWhereClause(this.whereClauses));
        
        return sqlStatement;
    }
    
    
    /**
     * Create select part of the data SQL
     * 
     * @param selectColumns
     * @return
     */
    private SqlStatementBuilder dataSelectClause(List<String> selectColumns){
        SqlStatementBuilder selectSQLFragment = new SqlStatementBuilder();
        selectSQLFragment.append("SELECT ");
        for (int i = 0; i < selectColumns.size()-1; i++) {
            String columnName = selectColumns.get(i);
            selectSQLFragment.append(columnName+" "+columnName.replace(".", "_")+", ");
        }
        String lastColumnName = selectColumns.get(selectColumns.size()-1);
        selectSQLFragment.append(lastColumnName+" "+lastColumnName.replace(".", "_"));
        return selectSQLFragment;
    }

    /**
     * Create from part of the data SQL
     * 
     * @param fromEntries
     * @return
     */
    private SqlStatementBuilder dataFromClause(List<String> fromEntries){
        SqlStatementBuilder fromSQLFragment = new SqlStatementBuilder();
        fromSQLFragment.append("FROM ");
        for (int i = 0; i < fromEntries.size()-1; i++) {
            String tableName = fromEntries.get(i);
            fromSQLFragment.append(tableName+", ");
        }
        fromSQLFragment.append(fromEntries.get(fromEntries.size()-1));
        return fromSQLFragment;
    }

    /**
     * Create where part of the data SQL
     * 
     * @param whereClauses
     * @return
     */
    private SqlStatementBuilder dataWhereClause(List<String> whereClauses){
        SqlStatementBuilder whereSQLFragment = new SqlStatementBuilder();
        if(whereClauses.size() > 0) {
            whereSQLFragment.append("WHERE "+whereClauses.get(0)+" ");
            for (int i = 1; i < whereClauses.size(); i++) {
                String whereClause = whereClauses.get(i);
                whereSQLFragment.append("AND "+whereClause+" ");
            }
        }
        return whereSQLFragment;
    }
}
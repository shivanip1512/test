package com.cannontech.yukon;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.database.TransactionException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.db.DBPersistent;

/**
 * Interface for adding,updating,deleting, and retrieving an object to/from
 * persistent storage.
 * @author alauinger
 */
public interface IDBPersistent {
    /* Methods that need defining */
    public void add(String tableName, Object[] values) throws SQLException;

    public void delete(String tableName, String columnNames[], String columnValues[]) throws SQLException;

    public void delete(String tableName, String columnName, String columnValue) throws SQLException;

    public Object[][] retrieve(String[] selectColumns, String tableName,
            String[] constraintColumns, String[] constraintValues, boolean multipleReturn)
            throws SQLException;

    public Object[] retrieve(String selectColumnNames[], String tableName, String keyColumnNames[],
            String keyColumnValues[]) throws SQLException;

    public void update(String tableName, String setColumnName[], Object setColumnValue[],
            String constraintColumnName[], Object constraintColumnValue[]) throws SQLException;

    public void update(String tableName, String constraintColumnName[],
            Object constraintColumnValue[]) throws SQLException;

    public Connection getDbConnection();

    public void setDbConnection(java.sql.Connection newValue);

    public DBPersistent execute(TransactionType operation, DBPersistent obj) throws TransactionException;
}
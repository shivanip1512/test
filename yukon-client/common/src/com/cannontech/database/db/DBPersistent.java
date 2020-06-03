package com.cannontech.database.db;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDBPersistent;

public abstract class DBPersistent implements Serializable {

    private transient IDBPersistent db = null;

    public DBPersistent() {
        super();
    }

    public abstract void add() throws SQLException;

    protected void add(String tableName, Object values[]) throws SQLException {
        getDB().add(tableName, values);
    }

    /**
     * @throws SQLException 
     */
    public void addPartial() throws SQLException {
        try {
            throw new IllegalAccessException("The method addPartial() must be overriden");
        } catch (IllegalAccessException e) {}

    }

    public abstract void delete() throws SQLException;

    protected void delete(String tableName, String columnNames[], Object columnValues[]) throws SQLException {

        String deleteValues[] = new String[columnValues.length];

        for (int i = 0; i < columnValues.length; i++)
            deleteValues[i] = prepareObjectForSQLStatement(columnValues[i]);

        delete(tableName, columnNames, deleteValues);
    }

    protected void delete(String tableName, String columnNames[], String columnValues[]) throws SQLException {
        getDB().delete(tableName, columnNames, columnValues);
    }

    protected void delete(String tableName, String columnName, Object columnValue) throws SQLException {

        String strColumnValue = prepareObjectForSQLStatement(columnValue);

        delete(tableName, columnName, strColumnValue);
    }

    private void delete(String tableName, String columnName, String columnValue) throws SQLException {
        getDB().delete(tableName, columnName, columnValue);
    }

    /**
     * @throws SQLException  
     */
    public void deletePartial() throws SQLException {
        try {
            throw new IllegalAccessException("The method deletePartial() must be overriden");
        } catch (IllegalAccessException e) {}

    }

    public Connection getDbConnection() {
        return getDB().getDbConnection();
    }

    private static String prepareObjectForSQLStatement(Object o) {
        if (o == null)
            return null;
        else if (o instanceof Integer || o instanceof Double || o instanceof Long || o instanceof BigDecimal ||
            o instanceof Character || o instanceof String || o instanceof Byte)
            
            return o.toString();
        else if (o instanceof java.sql.Date) {
            return new String("NULL").trim();
        } else if (o instanceof GregorianCalendar || o instanceof Date) {
            if (o instanceof GregorianCalendar)
                o = ((GregorianCalendar) o).getTime();

            return "'" + new Timestamp(((Date) o).getTime()).toString() + "'";
        } else {
            CTILogger.info("prepareObjectForSQLStatement - warning unhandled type");
            return o.toString();
        }

    }

    private synchronized IDBPersistent getDB() {
        if (db == null) {
            db = YukonSpringHook.getBean("dbPersistentBean", IDBPersistent.class);
        }

        return db;
    }

    public abstract void retrieve() throws SQLException;

    protected Object[] retrieve(String selectColumnNames[], String tableName, String keyColumnNames[],
            Object keyColumnValues[]) throws SQLException {

        String strKeyColumnValues[] = new String[keyColumnValues.length];

        for (int i = 0; i < keyColumnValues.length; i++)
            strKeyColumnValues[i] = prepareObjectForSQLStatement(keyColumnValues[i]);

        return retrieve(selectColumnNames, tableName, keyColumnNames, strKeyColumnValues);
    }

    protected Object[][] retrieve(String[] selectColumns, String tableName, String[] constraintColumns,
            Object constraintValues[], @SuppressWarnings("unused") boolean multipleReturn) throws SQLException {

        String strKeyColumnValues[] = new String[constraintColumns.length];

        for (int i = 0; i < constraintValues.length; i++)
            strKeyColumnValues[i] = prepareObjectForSQLStatement(constraintValues[i]);

        return retrieve(selectColumns, tableName, constraintColumns, strKeyColumnValues, true);
    }

    private Object[] retrieve(String selectColumnNames[], String tableName, String keyColumnNames[],
            String keyColumnValues[]) throws SQLException {

        return getDB().retrieve(selectColumnNames, tableName, keyColumnNames, keyColumnValues);
    }

    private Object[][] retrieve(String[] selectColumns, String tableName, String[] constraintColumns,
            String[] constraintValues, boolean multipleReturn) throws SQLException {

        return getDB().retrieve(selectColumns, tableName, constraintColumns, constraintValues, multipleReturn);
    }

    public void setDbConnection(Connection newValue) {
        getDB().setDbConnection(newValue);
    }

    public abstract void update() throws SQLException;

    protected void update(String tableName, String setColumnName[], Object setColumnValue[], String constraintColumnName[],
            Object constraintColumnValue[]) throws SQLException {
        getDB().update(tableName, setColumnName, setColumnValue, constraintColumnName, constraintColumnValue);
    }

    protected void update(String tableName, String constraintColumnName[], Object constraintColumnValue[])
            throws SQLException {
        getDB().update(tableName, constraintColumnName, constraintColumnValue);
    }

}

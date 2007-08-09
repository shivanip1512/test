package com.cannontech.yukon;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.database.Transaction;
/**
 * Interface for adding,updating,deleting, and retrieving
 * an object to/from persistent storage.
 * @author alauinger
 */
public interface IDBPersistent 
{
   public static final int INSERT = Transaction.INSERT;
   public static final int UPDATE = Transaction.UPDATE;
   public static final int RETRIEVE = Transaction.RETRIEVE;
   public static final int DELETE = Transaction.DELETE;
   public static final int DELETE_PARTIAL = Transaction.DELETE_PARTIAL;
   public static final int ADD_PARTIAL = Transaction.ADD_PARTIAL;


   /* Methods that need defining */
	public void add( String tableName, Object[] values ) throws SQLException;
	
   public void delete( String tableName, String columnNames[], String columnValues[] ) throws SQLException;
   public void delete( String tableName, String columnName, String columnValue ) throws SQLException;
   
	public Object[][] retrieve(String[] selectColumns, String tableName, String[] constraintColumns, String[] constraintValues, boolean multipleReturn) throws SQLException;
   public Object[] retrieve(String selectColumnNames[], String tableName, String keyColumnNames[], String keyColumnValues[]) throws SQLException;
   
   
	public void update( String tableName, String setColumnName[], Object setColumnValue[], 
                     String constraintColumnName[], Object constraintColumnValue[]) throws SQLException;
    
    public void update( String tableName, String constraintColumnName[], Object constraintColumnValue[]) throws SQLException;
                     
   public Connection getDbConnection();
   public void setDbConnection(java.sql.Connection newValue);
                        
   public com.cannontech.database.db.DBPersistent
          execute( int operation, com.cannontech.database.db.DBPersistent obj ) throws com.cannontech.database.TransactionException;
}

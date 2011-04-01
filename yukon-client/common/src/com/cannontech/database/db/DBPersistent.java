package com.cannontech.database.db;

/**
 * This type was created in VisualAge.
 */
import java.sql.Connection;
import java.sql.SQLException;
import java.util.GregorianCalendar;

import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDBPersistent;
 
public abstract class DBPersistent implements java.io.Serializable 
{
//   public static boolean printSQL = false;
//   public static String SQLFileName = com.cannontech.common.util.CtiUtilities.getLogDirPath() + "DBeditorSQL.sql";
   
   private transient com.cannontech.yukon.IDBPersistent db = null;
   
/**
 * DBPersistent constructor comment.
 */
public DBPersistent() {
	super();
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public abstract void add() throws java.sql.SQLException;

/**
 * This method was created in VisualAge.
 * @param tableName java.lang.String
 * @param values java.lang.Object[]
 */
protected void add( String tableName, Object values[] ) throws SQLException 
{
   getDB().add( tableName, values );
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 10:01:49 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException {
	try
	{
		throw new IllegalAccessException("The method addPartial() must be overriden");
	}
	catch (IllegalAccessException e)
	{
	}

}
/**
 * This method was created by a SmartGuide.
 */
public abstract void delete() throws java.sql.SQLException;
/**
 * This method was created in VisualAge.
 * @param tableName java.lang.String
 * @param columnName java.lang.String
 * @param columnValue java.lang.String
 */
protected void delete( String tableName, String columnNames[], Object columnValues[] ) throws SQLException{

	String deleteValues[] = new String[columnValues.length];

	for( int i = 0; i < columnValues.length; i++ )
		deleteValues[i] = prepareObjectForSQLStatement( columnValues[i] );
		
	delete( tableName, columnNames, deleteValues );
}
/**
 * This method was created in VisualAge.
 * @param tableName java.lang.String
 * @param columnName java.lang.String
 * @param columnValue java.lang.String
 */
protected void delete( String tableName, String columnNames[], String columnValues[] ) throws SQLException
{
   getDB().delete( tableName, columnNames, columnValues );
}

/**
 * This method was created in VisualAge.
 * @param tableName java.lang.String
 * @param columnName java.lang.String
 * @param columnValue java.lang.String
 */
protected void delete( String tableName, String columnName, Object columnValue ) throws SQLException{

	String strColumnValue = prepareObjectForSQLStatement(columnValue);

	delete( tableName, columnName, strColumnValue );
}
/**
 * This method was created in VisualAge.
 * @param tableName java.lang.String
 * @param columnName java.lang.String
 * @param columnValue java.lang.String
 */
private void delete( String tableName, String columnName, String columnValue ) throws SQLException
{
   getDB().delete( tableName, columnName, columnValue );
}

/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 10:57:30 AM)
 */
public void deletePartial() throws java.sql.SQLException {
	try
	{
		throw new IllegalAccessException("The method deletePartial() must be overriden");
	}
	catch (IllegalAccessException e)
	{
	}

}
/**
 * This method was created in VisualAge.
 * @return java.sql.Connection
 */
public Connection getDbConnection()
{
	return getDB().getDbConnection();
}

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param o java.lang.Object
 */
private static String prepareObjectForSQLStatement( Object o ) 
{
	if( o == null )
		return null;
	else
	if( o instanceof Integer ||
		 o instanceof Double ||
		 o instanceof Long || 
		 o instanceof java.math.BigDecimal ||
		 o instanceof Character ||
		 o instanceof String ||
		 o instanceof Byte )
		return o.toString();
	else
	if( o instanceof java.sql.Date )
	{
		return new String("NULL").trim();
	}
	else
	if( o instanceof java.util.GregorianCalendar || o instanceof java.util.Date )
	{
		if( o instanceof GregorianCalendar )
		 	o = ((GregorianCalendar)o).getTime();

		 return "'" + new java.sql.Timestamp( ((java.util.Date)o).getTime() ).toString() + "'";
	}
	else
	{
		com.cannontech.clientutils.CTILogger.info("prepareObjectForSQLStatement - warning unhandled type");
		return o.toString();
	}
	
}

private synchronized com.cannontech.yukon.IDBPersistent getDB()
{
   if (db == null) { 
      db = YukonSpringHook.getBean("dbPersistentBean", IDBPersistent.class);
   }

   return db;
}

/**
 * This method was created by a SmartGuide.
 */
public abstract void retrieve() throws java.sql.SQLException;
/**
 * This method was created in VisualAge.
 * @return java.lang.Object[]
 * @param selectColumnNames java.lang.String[]
 * @param tableName java.lang.String
 * @param keyColumnNames java.lang.String[]
 * @param keyColumnValues java.lang.String[]
 */
protected Object[] retrieve( String selectColumnNames[], String tableName, String keyColumnNames[], Object keyColumnValues[] ) throws SQLException{

	String strKeyColumnValues[] = new String[keyColumnValues.length];
	
	for( int i = 0; i < keyColumnValues.length; i++ )
		strKeyColumnValues[i] = prepareObjectForSQLStatement(keyColumnValues[i]);

	return retrieve( selectColumnNames, tableName, keyColumnNames, strKeyColumnValues );
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.DBPersistent[]
 * @param selectColumns java.lang.String[]
 * @param tableName java.lang.String
 * @param constarintColumns java.lang.String[]
 * @param constraintValues java.lang.Object[]
 * @param multipleReturn boolean
 */
protected Object[][] retrieve(String[] selectColumns, String tableName, String[] constraintColumns, Object constraintValues[], boolean multipleReturn) throws SQLException{
	String strKeyColumnValues[] = new String[constraintColumns.length];
	
	for( int i = 0; i < constraintValues.length; i++ )
		strKeyColumnValues[i] = prepareObjectForSQLStatement(constraintValues[i]);

	return retrieve( selectColumns,  tableName, constraintColumns, strKeyColumnValues, true );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object[]
 * @param selectColumnNames java.lang.String[]
 * @param tableName java.lang.String
 * @param keyColumnNames java.lang.String[]
 * @param keyColumnValues java.lang.String[]
 */
private Object[] retrieve(String selectColumnNames[], String tableName, String keyColumnNames[], String keyColumnValues[]) throws SQLException
{
   return getDB().retrieve( 
               selectColumnNames, 
               tableName, 
               keyColumnNames, 
               keyColumnValues );
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.DBPersistent
 * @param selectColumns java.lang.String[]
 * @param tableName java.lang.String
 * @param constraintColumns java.lang.String
 * @param constraintValues java.lang.String
 * @param multipleReturn boolean
 */
private Object[][] retrieve(String[] selectColumns, String tableName, String[] constraintColumns, String[] constraintValues, boolean multipleReturn) throws SQLException
{
   return getDB().retrieve( 
               selectColumns, 
               tableName,
               constraintColumns,
               constraintValues, 
               multipleReturn );
}

/**
 * This method was created in VisualAge.
 * @param newValue java.sql.Connection
 */
public void setDbConnection(Connection newValue) 
{
   getDB().setDbConnection( newValue );
}



/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public abstract void update() throws java.sql.SQLException;
/**
 * This method was created in VisualAge.
 * @param tableName java.lang.String
 * @param setColumnName java.lang.String[]
 * @param setColumnNameValue java.lang.String[]
 * @param constraintColumnName java.lang.String[]
 * @param constraintColumnNameValue java.lang.String[]
 */
protected void update( String tableName, String setColumnName[], Object setColumnValue[], 
											String constraintColumnName[], Object constraintColumnValue[]) throws SQLException
{
   getDB().update( 
               tableName, 
               setColumnName, 
               setColumnValue,
               constraintColumnName,
               constraintColumnValue );
}

/**
 * This method was created in VisualAge.
 * @param tableName java.lang.String
 * @param constraintColumnName java.lang.String[]
 * @param constraintColumnNameValue java.lang.String[]
 */
protected void update( String tableName, String constraintColumnName[], Object constraintColumnValue[]) throws SQLException
{
   getDB().update( 
               tableName, 
               constraintColumnName,
               constraintColumnValue );
}

}

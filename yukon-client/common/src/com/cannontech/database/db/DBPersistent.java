package com.cannontech.database.db;

/**
 * This type was created in VisualAge.
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.Vector;
import com.cannontech.yukon.concrete.ResourceFactory;
 
public abstract class DBPersistent implements java.io.Serializable 
{
   public static boolean printSQL = false;
   public static String SQLFileName = com.cannontech.common.util.CtiUtilities.getLogDirPath() + "DBeditorSQL.sql";
   
   private com.cannontech.yukon.IDBPersistent db = null;
   
// private transient Connection dbConnection = null;
   
   
         
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
public Connection getDbConnection() throws SQLException 
{
	return getDB().getDbConnection();
}
/**
 * Insert the method's description here.
 * Creation date: (5/21/2001 11:45:41 AM)
 * @return java.lang.String
 */
public static java.lang.String getSQLFileName() 
{
	return SQLFileName;
}
/**
 * Insert the method's description here.
 * Creation date: (5/21/2001 11:20:05 AM)
 * @return boolean
 */
public static boolean isPrintSQL() {
	return printSQL;
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
		 o instanceof java.math.BigDecimal )
		return o.toString();
	else
	if( o instanceof Character ||
		o instanceof String ||
		o instanceof Byte )
		return "'" + o.toString() + "'";
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
/**
 * Insert the method's description here.
 * Creation date: (1/30/2001 11:23:20 AM)
 * @param line java.lang.String
 */
private void printSQLToFile(String line, Object[] columnValues, SQLException exception )
{
	// Here we want to print all SQL to a file, creating a
	// script file that could be run later.
	java.io.PrintWriter pw = null;

	try
	{
		StringBuffer buffer = new StringBuffer(line);
		boolean missingColumnValue = false;
		
		if( columnValues != null )
		{
			buffer = new StringBuffer(line);
			for( int i = 0; i < columnValues.length; i++ )
			{
				int index = buffer.toString().indexOf("?");
				if( index != -1 )
					buffer = buffer.replace( index, index+1, prepareObjectForSQLStatement(columnValues[i]).toString() );
				else
					missingColumnValue = true;
			}
		}

		if( missingColumnValue )
			buffer.insert(0, "/*** MISSING COLUMN VALUE FOUND IN THE BELOW STATEMENT */\n");
			
		pw = new java.io.PrintWriter(new java.io.FileWriter( getSQLFileName(), true), true);
		pw.write(buffer + "; \r\n");
		pw.close();
	}
	catch (Exception e) //catch everything and write the Exception to the log file
	{
		if( e instanceof java.io.IOException )
			com.cannontech.clientutils.CTILogger.info("*** Cant find SQL Log file named : " + getSQLFileName() +
	   			" : " + e.getMessage() );
		else
		{
			if( pw != null )
				pw.write("/**** Caught EXCEPTION while trying to write to SQLFile : " +
	   			" : " + e.getMessage() + "*/" );
		}
		
 	}
	finally
	{
		if( pw != null )
			pw.close();
	}

}

private com.cannontech.yukon.IDBPersistent getDB()
{
   if( db == null )
      db = (com.cannontech.yukon.IDBPersistent)ResourceFactory.getIYukon().createIDBPersistent();
      
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
 * Insert the method's description here.
 * Creation date: (5/21/2001 11:20:05 AM)
 * @param newPrintSQL boolean
 */
public static void setPrintSQL(boolean newPrintSQL) {
	printSQL = newPrintSQL;
}
/**
 * Insert the method's description here.
 * Creation date: (5/21/2001 11:45:41 AM)
 * @param newSQLFileName java.lang.String
 */
public static void setSQLFileName(java.lang.String newSQLFileName) {
	SQLFileName = newSQLFileName;
}
/**
 * Insert the method's description here.
 * Creation date: (7/28/00 1:46:45 PM)
 * @return java.lang.Object
 * @param o java.lang.Object
 */
private Object substituteObject(Object o) 
{
	if( o == null )
		return null;
	else
	if( o instanceof Character )
	{
		return com.cannontech.common.util.StringUtils.trimSpaces(o.toString());
	}
	else
	if( o instanceof java.util.GregorianCalendar )
	{
		java.sql.Timestamp ts = new Timestamp(((GregorianCalendar)o).getTime().getTime());
		return ts;
	}
	else if( o instanceof java.util.Date )
	{
		Timestamp ts = new Timestamp( ((java.util.Date)o).getTime() );
		return ts;
	}
	else
	if( o instanceof Long )
	{
		return new java.math.BigDecimal( ((Long) o).longValue() );
	}
	else
	if( o instanceof String )
	{
		return com.cannontech.common.util.StringUtils.trimSpaces(o.toString());
	}
	else
		return o;
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

}

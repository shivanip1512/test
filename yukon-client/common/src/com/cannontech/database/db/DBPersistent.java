package com.cannontech.database.db;

/**
 * This type was created in VisualAge.
 */
 import java.sql.*;
 import java.util.*;
 
public abstract class DBPersistent implements java.io.Serializable 
{
	public static boolean printSQL = false;
	public static String SQLFileName = com.cannontech.common.util.CtiUtilities.getLogDirPath() + "DBeditorSQL.sql";
	
	private transient Connection dbConnection = null;
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
protected void add( String tableName, Object values[] ) throws SQLException {
	
	for( int i = 0; i < values.length; i++ )
		values[i] = substituteObject(values[i]);

	StringBuffer pSql = new StringBuffer("INSERT INTO ");
	pSql.append(tableName);
	pSql.append(" VALUES( ?");
	
	for( int j = 1; j < values.length; j++ )
		pSql.append(",?");

	pSql.append(")");
	
	PreparedStatement pstmt = null;

	try
	{
		pstmt = getDbConnection().prepareStatement(pSql.toString());
		for( int k = 0; k < values.length; k++ )
		{
			pstmt.setObject(k+1, values[k] );
		}
		pstmt.executeUpdate();

		//everything went well, print the SQL to a file if desired
		if( isPrintSQL() )
			printSQLToFile( pSql.toString(), values, null );
	}
	catch( SQLException e )
	{
		//something bad happened, print the SQL with the Exception to a file if desired
		if( isPrintSQL() )
			printSQLToFile( pSql.toString(), values, e );
		
		throw e;
	}
	finally
	{
		pstmt.close();
	}
	//add( tableName, strValues );
	

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
protected void delete( String tableName, String columnNames[], String columnValues[] ) throws SQLException{

	String deleteString = "DELETE FROM " + tableName + " WHERE ";

	if( columnNames.length < 1 )
	 	return;

	 deleteString += columnNames[0] + "=" + columnValues[0];

	 for( int i = 1; i < columnNames.length; i++ )
	 	deleteString += " AND " + columnNames[i] + "=" + columnValues[i];

	 Statement stmt = null;
	 
	 try
	 {
	 	stmt = getDbConnection().createStatement();
	 	stmt.executeUpdate(deleteString);
	 }
	 catch( SQLException e )
	 {
	 	throw e;
	 }
	 finally
	 {
	 	if( stmt != null )
	 		stmt.close();
	 }
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
private void delete( String tableName, String columnName, String columnValue ) throws SQLException{
	String deleteString = "DELETE FROM " + tableName + " WHERE " + columnName + "=" + columnValue;

	Statement stmt = null;

	try
	{
		stmt = getDbConnection().createStatement();
		stmt.executeUpdate(deleteString);
	}
	catch( SQLException e )
	{
		throw e;
	}
	finally
	{
		if( stmt != null )
			stmt.close();
	}
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
public Connection getDbConnection() throws SQLException {
	return dbConnection;
}
/**
 * Insert the method's description here.
 * Creation date: (5/21/2001 11:45:41 AM)
 * @return java.lang.String
 */
public static java.lang.String getSQLFileName() {
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
		System.out.println("prepareObjectForSQLStatement - warning unhandled type");
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
			System.out.println("*** Cant find SQL Log file named : " + getSQLFileName() +
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
	String selectString = "SELECT ";
	if (selectColumnNames.length > 0)
	{
		selectString += selectColumnNames[0];
		for (int i = 1; i < selectColumnNames.length; i++)
			selectString += "," + selectColumnNames[i];
	}
	selectString += " FROM " + tableName;
	if (keyColumnNames.length > 0)
	{
		selectString += " WHERE " + keyColumnNames[0] + "=" + keyColumnValues[0];
		for (int j = 1; j < keyColumnNames.length; j++)
			selectString += " AND " + keyColumnNames[j] + "=" + keyColumnValues[j];
	}
	Statement stmt = null;
	ResultSet rset = null;
	Object returnObjects[] = null;

	try
	{
		stmt = getDbConnection().createStatement();
		rset = stmt.executeQuery(selectString);
		java.util.Vector v = new java.util.Vector();
		int columns = rset.getMetaData().getColumnCount();
		if (rset.next())
			for (int k = 0; k < columns; k++)
			{
				//			if( rset.getObject(k+1) != null )
				v.addElement(rset.getObject(k + 1));
			}
		
		returnObjects = new Object[v.size()];


		/* WARNING - The code below converts primitives into objects
		   ALL numeric columns seem to come back as java.math.BigDecimal
		   This is the case using oracle's type 4(thin) jdbc driver!
		   This _should probably_ just return the objects associated with
		   numeric columns as BigDecimals
		   and allow sub-classes to figure out what they really need.
		   As it stands (12/98) there are a lot of sub-classes that rely on
		   receiving Integer objects rather than java.math.BigDecimal objects.
		   
		   Well, how do we determine whether a java.math.BigDecimal was intended
		   to be an Integer or Float or a Double or whatever?
		
		   An easy hack (FOR THE CURRENT SETUP! 12/98) is to test the precision
		   of the column using the java.sql.ResultSetMetaData interface.
		   And this is what I did.  
		   It so happens that an oracle 'INTEGER' type has a precision of 38,
		   while an oracle 'FLOAT' type has a precision of 126. I know this can
		   be manipulated using oracle specific stuff, BUT can it be manipulated
		   in a cross platform way...? 
		   Cheers, ACL 
		   >:)
		   */


		for (int n = 0; n < returnObjects.length; n++)
		{
			Object temp = v.elementAt(n);
			if (temp instanceof java.math.BigDecimal)
			{
				// >>>>>>>>>> Watch this - synchronize with above!
				if (rset.getMetaData().getPrecision(n + 1) == 126)
					temp = new Double(((java.math.BigDecimal) v.elementAt(n)).doubleValue());
				else
					temp = new Integer(((java.math.BigDecimal) v.elementAt(n)).intValue());
			}
			else
				if (temp instanceof byte[])
				{
					if (((byte[]) temp).length == 1)
						temp = new Byte(((byte[]) temp)[0]);
					else
					{
						Byte newTemp[] = new Byte[ ((byte[]) temp).length];
						for (int i = 0; i < ((byte[]) temp).length; i++)
							newTemp[i] = new Byte(((byte[]) temp)[i]);
						temp = newTemp;
					}
				}
			returnObjects[n] = temp;
		}
	}
	catch (SQLException e)
	{
		throw e;
	}
	finally
	{
		if (rset != null)
			rset.close();

		if (stmt != null)
			stmt.close();
	}

	return returnObjects;
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
	String selectString = "SELECT ";
	if (selectColumns.length > 0)
	{
		selectString += selectColumns[0];
		for (int i = 1; i < selectColumns.length; i++)
			selectString += "," + selectColumns[i];
	}
	selectString += " FROM " + tableName;
	if (constraintColumns.length > 0)
	{
		selectString += " WHERE " + constraintColumns[0] + "=" + constraintValues[0];
		for (int j = 1; j < constraintColumns.length; j++)
			selectString += " AND " + constraintColumns[j] + "=" + constraintValues[j];
	}
	Statement stmt = null;
	ResultSet rset = null;
	Object returnObjects[][] = null;
	try
	{
		stmt = getDbConnection().createStatement();
		rset = stmt.executeQuery(selectString);

		//Get all the rows
		java.util.Vector rows = new java.util.Vector();
		while (rset.next())
		{
			Vector columns = new java.util.Vector();
			for (int i = 1; i <= rset.getMetaData().getColumnCount(); i++)
					columns.addElement( rset.getObject(i) );
			
			rows.addElement(columns);
		}
		returnObjects = new Object[rows.size()][rset.getMetaData().getColumnCount()];
		for (int i = 0; i < rows.size(); i++)
			for (int j = 0; j < rset.getMetaData().getColumnCount(); j++)
			{
				Object temp = ((java.util.Vector) rows.elementAt(i)).elementAt(j);
				if (temp instanceof java.math.BigDecimal)
					temp = new Integer(((java.math.BigDecimal) temp).intValue());
					
				returnObjects[i][j] = temp;
			}
	}
	catch (SQLException e)
	{
		throw e;
	}
	finally
	{
		if( rset != null )
			rset.close();

		if( stmt != null )
			stmt.close();
	}
	return returnObjects;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.sql.Connection
 */
public void setDbConnection(Connection newValue) {
	this.dbConnection = newValue;
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
		return o.toString();
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
		return o.toString();
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
											String constraintColumnName[], Object constraintColumnValue[]) throws SQLException{
												
	for( int i = 0; i < setColumnValue.length; i++ )
		setColumnValue[i] = substituteObject( setColumnValue[i] );

	for( int i = 0; i < constraintColumnValue.length; i++ )
		constraintColumnValue[i] = substituteObject( constraintColumnValue[i] );

	StringBuffer pSql = new StringBuffer("UPDATE ");
	pSql.append(tableName);
	pSql.append(" SET ");
	pSql.append(setColumnName[0]);
	pSql.append("=?");

	for( int i = 1; i < setColumnName.length; i++ )
	{
		pSql.append(",");
		pSql.append(setColumnName[i]);
		pSql.append("=?");
	}

	if( constraintColumnName.length > 0 )
	{
		pSql.append(" WHERE ");
		pSql.append(constraintColumnName[0]);
		pSql.append("=?");

		for( int i = 1; i < constraintColumnName.length; i++ )
		{
			pSql.append(" AND ");
			pSql.append(constraintColumnName[i]);
			pSql.append("=?");
		}
	}
	
	PreparedStatement pstmt = null;
	
	try
	{
		pstmt = getDbConnection().prepareStatement(pSql.toString());
		for( int i = 0; i < setColumnValue.length; i++ )
			pstmt.setObject(i+1, setColumnValue[i]);
			
		for( int i = 0; i < constraintColumnValue.length; i++ )
			pstmt.setObject(i+setColumnValue.length+1, constraintColumnValue[i]);
			
		pstmt.executeUpdate();	
	}
	catch( SQLException e )
	{
		throw e;
	}
	finally
	{
		if( pstmt != null ) pstmt.close();
	}	
}
}

package com.cannontech.tdc.utils;

/**
 * Insert the type's description here.
 * Creation date: (9/12/00 2:21:06 PM)
 * @author: 
 */
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.PoolManager;
import com.cannontech.tdc.custom.CustomDisplay;

public class DataBaseInteraction 
{
	private static String dbAlias = TDCDefines.DBALIAS;
	private static Connection connection = null;
	private static ResultSet resultSet = null;
	private static ResultSetMetaData metaData = null;
	private static DatabaseMetaData dataBaseMetaData = null;
/**
 * DataBaseInteraction constructor comment.
 */
private DataBaseInteraction() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (9/14/00 3:10:16 PM)
 * @return java.sql.DatabaseMetaData
 */
public static java.sql.DatabaseMetaData getDataBaseMetaData() {
	return dataBaseMetaData;
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/00 2:32:04 PM)
 * @return java.lang.String
 */
public static java.lang.String getDbAlias() {
	return dbAlias;
}

public static Object[][] queryResults( String query, Object[] parameters )
{
   return queryResults( query, parameters, false );
}

/**
 * Insert the method's description here.
 * Creation date: (3/14/00 12:37:10 PM)
 */
// NEVER ALLOW THIS METHOD TO RETURN NULL!!!
public static Object[][] queryResults( String query, Object[] parameters, boolean useMaxRowCount )
{	
	if( parameters != null )
	{
		for( int i = 0; i < parameters.length; i++ )
			parameters[i] = substituteObject(parameters[i]);

		//Debug purposes
		StringBuffer buf = new StringBuffer(query);
		for( int i = 0; i < parameters.length; i++ )
		{
			int loc = buf.toString().indexOf("?");
			buf.replace( loc, loc+1, parameters[i].toString() );
		}

		CTILogger.debug(" TDC Query = " + buf.toString());
	}



	
	if( query.length() >= 30000 ) //way to big of a query!!!
		throw new IllegalArgumentException("The size of the current database query was >= 30000 characters");

	PreparedStatement prepStmt = null;
	int columnCount = 0;
	Vector rows = new Vector();
	Object[][] rowData = null;
	
	try
	{
		if ( !query.equals("") )
		{
			connection = PoolManager.getInstance().getConnection( getDbAlias() );
			dataBaseMetaData = connection.getMetaData();
			prepStmt = connection.prepareStatement( query );
         
         if( useMaxRowCount )
            prepStmt.setMaxRows( TDCDefines.MAX_ROWS );


			if( parameters != null )
			{
				for( int i = 0; i< parameters.length; i++ )
					prepStmt.setObject( i+1, parameters[i] );
			}
			
			resultSet = prepStmt.executeQuery();
			metaData = resultSet.getMetaData();
			columnCount = resultSet.getMetaData().getColumnCount();

			while (resultSet.next()) 
			{
				Vector newRow = new Vector( columnCount );
				
				for (int i = 1; i <= columnCount; i++) 
				{
					Object res = resultSet.getObject(i);
						
					if ( res == null )
						newRow.addElement("");
					else if ( res.toString().equals(CustomDisplay.DYNAMIC_ROW) )
						newRow.addElement("   -----");					
					else
						newRow.addElement( res );
				}

				if( newRow.size() > 0 )				
					rows.addElement( newRow );
			}
		}
		
		rowData = new Object[rows.size()][columnCount];
	
		for( int i = 0; i < rows.size(); i++ )
		{
			java.util.Vector temp = (java.util.Vector) rows.elementAt(i);
			temp.copyInto( rowData[i] );
		}

		return rowData; //success			
	}
	catch (Exception e) // must catch any and all exceptions
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return new Object[0][0]; // just return a zero length 2D Object, not NULL!!
	}
	finally
	{
		try
		{
			if( resultSet != null )
				resultSet.close();
		}
		catch( java.sql.SQLException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}

		try
		{
			if( prepStmt != null )
				prepStmt.close();
		}
		catch( java.sql.SQLException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}

		try
		{
			if( connection != null )
				connection.close();
		}
		catch( java.sql.SQLException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/00 2:32:04 PM)
 * @param newDbAlias java.lang.String
 */
public static void setDbAlias(java.lang.String newDbAlias) {
	dbAlias = newDbAlias;
}
/**
 * Insert the method's description here.
 * Creation date: (7/28/00 1:46:45 PM)
 * @return java.lang.Object
 * @param o java.lang.Object
 */
private static Object substituteObject(Object o) 
{
	if( o == null )
		return null;
	else if( o instanceof Character )
	{
		return o.toString();
	}
	else if( o instanceof java.util.GregorianCalendar )
	{
		java.sql.Timestamp ts = new java.sql.Timestamp(((java.util.GregorianCalendar)o).getTime().getTime());
		return ts;
	}
	else if( o instanceof java.util.Date )
	{
		java.sql.Timestamp ts = new java.sql.Timestamp( ((java.util.Date)o).getTime() );
		return ts;
	}
	else if( o instanceof Long )
	{
		java.math.BigDecimal bd = new java.math.BigDecimal( ((Long)o).longValue() );
		return bd;
	}
	else
		return o;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 12:37:10 PM)
 */
// returns the number of rows changed
public static int updateDataBase( String query, Object[] parameters )
{	
	if ( query.equals("") )
		return -1;

	if( parameters != null )
	{
		for( int i = 0; i < parameters.length; i++ )
			parameters[i] = substituteObject(parameters[i]);
	}
	
	PreparedStatement prepStmt = null;
	
	try
	{		
		connection = PoolManager.getInstance().getConnection( getDbAlias() );
		prepStmt = connection.prepareStatement( query );

		if( parameters != null )
		{		
			for( int i = 0; i< parameters.length; i++ )
			{
				prepStmt.setObject( i+1, parameters[i] );
			}
		}
		
		return prepStmt.executeUpdate();
		
	}
	catch (Exception e) // must catch any and all exceptions
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return -1;
	}
	finally
	{
		try
		{
			if( prepStmt != null )
				prepStmt.close();
		}
		catch( java.sql.SQLException e )
		{}

		try
		{
			if( connection != null )
				connection.close();
		}
		catch( java.sql.SQLException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	}
}
}

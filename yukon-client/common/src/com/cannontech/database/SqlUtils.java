package com.cannontech.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

/**
 * Insert the type's description here.
 * Creation date: (4/3/2002 11:50:36 AM)
 * @author: 
 */
public final class SqlUtils {
	public static final int ORACLE_FLOAT_PRECISION = 126;	
/**
 * SqlUtils constructor comment.
 */
private SqlUtils() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2002 11:51:10 AM)
 * @return java.lang.String
 */
public static String createSqlString( String preText, String[] constraintColumnNames, 
	String[] tableNames,String postText)
{
	StringBuffer sql = new StringBuffer(preText);
					 for( int i = 0; i < constraintColumnNames.length; i++ )
					 	sql.append( constraintColumnNames[i] + "," );
					 sql.deleteCharAt( sql.length()-1); //delete the extra , on the end
					 
					 sql.append(" from ");

					 for( int i = 0; i < tableNames.length; i++ )
					 	sql.append( tableNames[i] + "," );
					 sql.deleteCharAt( sql.length()-1); //delete the extra , on the end


					 if( postText != null )
					 	sql.append(postText);

	return sql.toString();
}

/*
 * Return an Object[][] of rows and columns from sqlString('s) query results.
 * This method takes special care of the BigDecimal problem with Oracle/SQLServer
 */
public static Object[][] getResultObjects(String sqlString, String dbAlias ) throws SQLException
{
	Statement stmt = null;
	ResultSet rset = null;
	Connection conn = null;
	Object returnObjects[][] = null;
	try
	{
		conn = PoolManager.getInstance().getConnection(dbAlias);
		stmt = conn.createStatement();
		rset = stmt.executeQuery(sqlString);
   
		//Get all the rows
		Vector rows = new Vector();
		while (rset.next())
		{
			Vector columns = new Vector();
			for (int i = 1; i <= rset.getMetaData().getColumnCount(); i++)
				columns.addElement( rset.getObject(i) );
            
			rows.addElement(columns);
		}
		returnObjects = new Object[rows.size()][rset.getMetaData().getColumnCount()];
		
		for (int i = 0; i < rows.size(); i++)
		{
			for (int j = 0; j < rset.getMetaData().getColumnCount(); j++)
			{
				Object temp = ((java.util.Vector) rows.elementAt(i)).elementAt(j);
				if (temp instanceof BigDecimal)
				{
				   // >>>>>>>>>> Watch this - synchronize with above!
				   if (rset.getMetaData().getPrecision(j + 1) == ORACLE_FLOAT_PRECISION )
					  temp = new Double(((BigDecimal) temp).doubleValue());
				   else
					  temp = new Integer(((BigDecimal) temp).intValue());
				}				
                  
				returnObjects[i][j] = temp;
			}
		}
	}
	catch (SQLException e)
	{
		throw e;
	}
	finally
	{
		if( rset != null ) rset.close();
		if( stmt != null ) stmt.close();
		if( conn != null ) conn.close();
   }
   return returnObjects;
}
}

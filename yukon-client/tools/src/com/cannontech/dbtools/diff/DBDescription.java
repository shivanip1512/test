package com.cannontech.dbtools.diff;
/**
 * DBDescription is a utility program that connected with a database
 * and dumps the tables and columns to standard out in the java
 * properties format.
 * i.e.
 * TABLE1=COLUMN1 COLUMN2 COLUMN3 ...
 * TABLE2=COLUMNA COLUMNB COLUMNC ...
 * ...
 * Creation date: (7/17/00 2:28:39 PM)
 * @author: Aaron Lauinger
 * @see DBDiff
 */
public class DBDescription {
	private static String GET_ALL_TABLES_ORACLE = "select tname from tab";	
	private static String GET_ALL_TABLES_SQLSERVER = "SELECT NAME FROM SYSOBJECTS WHERE TYPE = 'U'";
/**
 * DBDescription constructor comment.
 */
private DBDescription() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (7/17/00 2:29:33 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) 
{
	
	if( args.length != 4 )
	{
		System.out.println("DBDescription url driver username password");
		System.out.println("Example of a url is:  jdbc:oracle:thin:@127.0.0.1:1521:yuk1");
		System.out.println("Example of a driver is:  oracle.jdbc.driver.OracleDriver");
		return;
	}

	String url1 = args[0];
	String driver1 = args[1];
	String user1 = args[2];
	String pass1 = args[3];

	try
	{
		Class.forName(driver1);
	}
	catch( Exception e )
	{
		e.printStackTrace();
		return;
	}
	
	long startTime = (new java.util.Date()).getTime() / 1000;

	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;
	
	try
	{
		conn = java.sql.DriverManager.getConnection( url1, user1, pass1 );
	}
	catch( Exception e )
	{
		System.out.println("Unable to get connection...");
		e.printStackTrace();
		return;
	}

	try
	{
		stmt = conn.createStatement();
		rset = stmt.executeQuery(GET_ALL_TABLES_ORACLE);

		java.util.ArrayList tables = new java.util.ArrayList();
		while( rset.next() )
		{
					
			tables.add( rset.getString(1) );
		}

		rset.close();
		
		java.util.Iterator iter = tables.iterator();
		while( iter.hasNext() )
		{
			String tableName = (String) iter.next();
			System.out.print(tableName + "=" );
			rset = stmt.executeQuery("select * from " + tableName);
			java.sql.ResultSetMetaData meta = rset.getMetaData();

			int numColumns = meta.getColumnCount();
			for( int i = 0; i < numColumns; i++ )			
				System.out.print(meta.getColumnName(i+1) + " ");

			rset.close();
			System.out.print("\r\n");
		}
	}
	catch( java.sql.SQLException e )
				{
					System.out.println( e.getMessage() );
				}
				finally
				{
					try
					{
						if( rset != null )
							rset.close();
					}
					catch( java.sql.SQLException e )
					{
						System.out.println( e );						
					}

					try
					{
						if( stmt != null )
							stmt.close();
					}
					catch( java.sql.SQLException e )
					{
						System.out.println( e.getMessage());
					}

					rset = null;
					stmt = null;
				}
			
}
}

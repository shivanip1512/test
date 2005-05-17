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
public class DBDescription
{
	//private static String GET_ALL_TABLES_ORACLE = "select tname from tab";	
	//private static String GET_ALL_TABLES_SQLSERVER = "SELECT NAME FROM SYSOBJECTS WHERE TYPE = 'U'";

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
		System.out.println("Examples of url:");
		System.out.println("		jdbc:oracle:thin:@127.0.0.1:1521:yuk1");
		System.out.println("		jdbc:jtds:sqlserver://127.0.0.1:1433;APPNAME=yukon-client;TDS=8.0");
		System.out.println("");
		System.out.println("Example of a driver is:");
		System.out.println("		oracle.jdbc.driver.OracleDriver");
		System.out.println("		net.sourceforge.jtds.jdbc.Driver");
		
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
		if( url1.toLowerCase().indexOf("oracle") >= 0 )
		{
			//get all ORACLE tables from the database using the userName as the Schema
			rset = conn.getMetaData().getTables( null, user1.toUpperCase(), null, null);
		}
		else
			rset = conn.getMetaData().getTables( null, null, null, new String[]{"TABLE","VIEW"});


		java.util.ArrayList tables = new java.util.ArrayList();
		while( rset.next() )
			tables.add( rset.getString("TABLE_NAME") );

		rset.close();
		
		java.util.Iterator iter = tables.iterator();
		while( iter.hasNext() )
		{
			String tableName = (String) iter.next();
			System.out.print(tableName + "=" );			

			rset = conn.getMetaData().getColumns( null, null, tableName, "%");
			while( rset.next() )
				System.out.print(rset.getString("COLUMN_NAME") + " ");


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

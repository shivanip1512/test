package com.cannontech.dbtools.diff;

import com.cannontech.database.SqlUtils;
import com.cannontech.dbtools.tools.ModifyConstraints;

/**
 * Given two databases, CopyEntireSchema will log in to them and copy
 * everything from one to the other.  Note that database constraints will
 * generally cause this to fail.  
 * Creation date: (1/13/00 10:44:13 AM)
 * @author Aaron Lauinger
 * @see ModifyConstraints
 */
class CopyEntireSchema {
/**
 * Insert the method's description here.
 * Creation date: (1/13/00 10:44:58 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) 
{
	if( args.length != 8 )
	{
		System.out.println("Usage:  CopyEntireScheme url1 driver1 user1 pass1 url2 driver2 user2 pass2");
		System.out.println("Example of a url is:  jdbc:oracle:thin:@127.0.0.1:1521:yuk1");
		System.out.println("Example of a driver is:  oracle.jdbc.driver.OracleDriver");
		System.out.println("Copies entire scheme1 into schema2");
	}

	String url1 = args[0];
	String driver1 = args[1];
	String user1 = args[2];
	String pass1 = args[3];
	String url2 = args[4];
	String driver2 = args[5];
	String user2 = args[6];
	String pass2 = args[7];

	String selectAllTables = "select * from tab";
	
	//load the drivers
	try
	{
		Class.forName(driver1);
		Class.forName(driver2);
	}
	catch( ClassCastException e )
	{
		e.printStackTrace();
		return;
	}
	catch( ClassNotFoundException e )
	{
		e.printStackTrace();
		return;
	}
	
	java.sql.Connection conn1 = null;
	java.sql.Connection conn2 = null;

	java.sql.Statement stmt1 = null;

	java.sql.ResultSet rset1 = null;

	StringBuffer buf = null;
		
	try
	{
		conn1 = java.sql.DriverManager.getConnection( url1, user1, pass1 );
		conn2 = java.sql.DriverManager.getConnection( url2, user2, pass2 );
		
		//Get a list of all the tables
		stmt1 = conn1.createStatement();
		rset1 = stmt1.executeQuery( selectAllTables );			
		
		java.util.LinkedList tableNames = new java.util.LinkedList();

		while( rset1.next() )
			tableNames.add( rset1.getString(1) );
	
		// Go through tableNames and copy all the data
		java.util.Iterator iter = tableNames.iterator();
		String name;

		while( iter.hasNext() )
		{
			name = (String) iter.next();

			java.sql.Statement stmt2 = null;
			java.sql.PreparedStatement pstmt = null;
			java.sql.ResultSet rset2 = null;
			try
			{
				stmt2 = conn1.createStatement();
				rset2 = stmt2.executeQuery("select * from " + name);
				java.sql.ResultSetMetaData metaData = rset2.getMetaData();
			
				System.out.println(name);
			
				buf = new StringBuffer("insert into " + name + " values(");			
			
				if( metaData.getColumnCount() > 0 )
				{
					buf.append("?");
				
					for( int i = 1; i < metaData.getColumnCount(); i++ )
				 		buf.append(",?");	

				 	buf.append(")");
				}
				
				pstmt = conn2.prepareStatement(buf.toString());

				int rowCount = 0;
				int soFar = 0;
								
				while( rset2.next() )
				{
					for( int i = 0; i < metaData.getColumnCount(); i++ )
					{
						pstmt.setObject( i+1, SqlUtils.getResultObject(rset2, i+1));							
					}

					pstmt.execute();
					
					rowCount++;
					soFar++;

					if( soFar == 1000 )
					{
						soFar = 0;
						System.out.println(" Inserted " + rowCount + " rows so far..." );
					}	
				}

				System.out.println("Copied " + rowCount + " rows.");
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
			finally
			{
				SqlUtils.close(rset2, stmt2, pstmt);
			}
		}
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
	} finally{
		SqlUtils.close(rset1, stmt1, conn1, conn2);
	}
}
}

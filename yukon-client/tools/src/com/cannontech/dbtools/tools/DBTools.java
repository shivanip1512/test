package com.cannontech.dbtools.tools;

import java.sql.Connection;

import com.cannontech.database.SqlUtils;
import com.cannontech.dbtools.diff.DBDiff;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DBTools
{

	/**
	 * 
	 */
	public DBTools()
	{
		super();
	}


	/**
	 * Gives the DBMS we are using.
	 * @return String
	 */
	public synchronized static String getDBMS( Connection conn )
	{
		String dbType = "sqlserver"; 
		
		try
		{
			if( conn.getMetaData().getDriverName().indexOf("oracle") >= 0  )
				dbType = "oracle";
		}
		catch( Exception e )
		{
			e.printStackTrace( System.out );  //just in case
		}

		return dbType;
	}
	


	/**
	 * Given two databases, CopyEntireSchema will log in to them and copy
	 * everything from one to the other.  Note that database constraints will
	 * generally cause this to fail.  
	 * Creation date: (1/13/00 10:44:13 AM)
	 * @author Aaron Lauinger
	 * @see ModifyConstraints
	 */
	public static void copyEntireSchema( String[] args )
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
		java.sql.Statement stmt2 = null;
		java.sql.PreparedStatement pstmt = null;
	
		java.sql.ResultSet rset = null;
	
		StringBuffer buf = null;
			
		try
		{
			conn1 = java.sql.DriverManager.getConnection( url1, user1, pass1 );
			conn2 = java.sql.DriverManager.getConnection( url2, user2, pass2 );
			
			//Get a list of all the tables
			stmt1 = conn1.createStatement();
			rset = stmt1.executeQuery( selectAllTables );			
			
			java.util.LinkedList tableNames = new java.util.LinkedList();
	
			while( rset.next() )
				tableNames.add( rset.getString(1) );
		
			rset.close();
			stmt1.close();
	
			// Go through tableNames and copy all the data
			java.util.Iterator iter = tableNames.iterator();
			String name;
	
			while( iter.hasNext() )
			{
				name = (String) iter.next();
				try
				{
					stmt1 = conn1.createStatement();
					rset = stmt1.executeQuery("select * from " + name);
					java.sql.ResultSetMetaData metaData = rset.getMetaData();
				
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
									
					while( rset.next() )
					{
						for( int i = 0; i < metaData.getColumnCount(); i++ )
						{
							pstmt.setObject( i+1, SqlUtils.getResultObject(rset, i+1));							
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
					if( stmt1 != null ) stmt1.close();
					if( pstmt != null ) pstmt.close();				
				}
			}
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
	}



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
	public static void dbDescription( String[] args )
	{
		String GET_ALL_TABLES_SQL = "select tname from tab";	

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
			rset = stmt.executeQuery(GET_ALL_TABLES_SQL);
	
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



	/**
	 * Allows the user to insert random data values into RawPointHistory
	 * @param args an array of command-line arguments
	 */
	public static void insertRawPtHist(java.lang.String[] args) 
	{
		if( args.length < 1 )
		{
			System.out.println("Syntax:");
			System.out.println("  InsertRawPtHist <start change id>");
			return;
		}

		System.out.println("");
		System.out.println("Starting Inserts.....");

		java.sql.Connection conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
		java.sql.Statement statement = null;

		long COUNT = 10000000;
		try
		{
			int startID = new Integer(args[0]).intValue();
		
			statement = conn.createStatement();
			conn.setAutoCommit(false);		
		
			for( int i = 0; i < COUNT; i++ )
			{
				statement.addBatch(
					"insert into rawpointhistory values(" +
					(startID+i) + "," +
					1 + "," +
					"2002-" + Math.random() * 10 + "-" + Math.random() * 10 + ","+
					1 + ","+
					(Math.random() * 100) * (Math.random()*10) * Math.random()+ ")");

				if( i != 0 && (i%1000) == 0 )
				{
					System.out.print("+");
					statement.executeBatch();
					conn.commit();
				}
			
			}
		}
		catch( Exception e )
		{
			e.printStackTrace( System.out );
		}
		finally
		{
			try
			{
				conn.close();
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace( System.out );
			}
		}
		
		System.out.println("Finished Inserting ");
	}

}

package com.cannontech.dbtools.tools;

import com.cannontech.database.SqlUtils;

/**
 * Reads all VARCHAR rows from every table, massages the 
 *   data, then updates the rows
 *
 * @author: 
 */
public class DataFixer 
{
	/**
	 * DataFixer constructor comment.
	 */
	public DataFixer() {
		super();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/6/2001 11:34:02 AM)
	 * @param strings java.util.ArrayList
	 */
	public final static void executeStringUpdate(java.util.ArrayList strings) 
	{
		System.out.println("");
		System.out.println("------------ Starting Trimmed String Insertions ----------");
	
		java.sql.Connection conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
		java.sql.Statement statement = null;
		
		try
		{
			for( int i = 0; i < strings.size(); i++ )
			{
				try
				{
					statement = conn.createStatement();
					statement.executeUpdate(  strings.get(i).toString() );
				}
				catch( java.sql.SQLException e )
				{
					System.out.println("***BAD*** : " + strings.get(i) );
					System.out.println( e.getMessage() );
				}
				finally
				{
					try
					{
						if( statement != null )
							statement.close();
					}
					catch( java.sql.SQLException e )
					{
						System.out.println("***BAD*** : " + strings.get(i) );
						System.out.println( e.getMessage() );
					}
				}
					
			}
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
			
		System.out.println("------------ Finished Trimmed String Insertions ----------");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/5/2001 4:36:50 PM)
	 * @param args java.lang.String[]
	 */
	public static void main(String[] args) throws Exception 
	{
	   System.out.println("Retreiving Database Data...");
	
	   java.sql.Connection conn = null;
	   java.sql.Statement stmt = null;
	   java.sql.Statement stmt2 = null;
	
	   java.sql.ResultSet rset = null;
	   java.sql.ResultSet rset2 = null;
	
	   conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
	   java.sql.DatabaseMetaData dbMeta = conn.getMetaData();
	   rset = dbMeta.getTables(null, null, null, null);
	   java.util.ArrayList tableNames = new java.util.ArrayList(100);
		java.util.ArrayList tableTypes = new java.util.ArrayList(100);
		java.util.ArrayList updateStrings = new java.util.ArrayList(500);
		
	   while (rset.next())
			tableTypes.add( rset.getString("TABLE_TYPE") );
	
		int i =0;
	   rset.close();
	   rset = dbMeta.getTables(null, null, null, null); 	
	   while (rset.next())
		{
			//only add our TABLES to the victims
			if( tableTypes.get(i++).toString().equalsIgnoreCase("TABLE") )
		  		tableNames.add( rset.getString("TABLE_NAME") );
		}
	
	   rset.close();
	   for (int j = 0; j < tableNames.size(); j++)
	   {
			stmt2 = conn.createStatement();
			rset2 = stmt2.executeQuery("SELECT * FROM " + tableNames.get(j));
			System.out.print("		Processing table " + tableNames.get(j) + "..." );
			boolean changed = false;
	
			java.sql.ResultSetMetaData rMeta = rset2.getMetaData();
			while (rset2.next())
			{
				String updateString = null;
				String valueString = null;
	
				for (int h = 1; h <= rMeta.getColumnCount(); h++)
				{
				   if (rMeta.getColumnType(h) == java.sql.Types.VARCHAR)
				   {
					  String temp = null;
					  if ((temp = rset2.getString(h)) != null)
					  {
						 if (valueString == null)
							valueString = "update " + tableNames.get(j) + " set " + rMeta.getColumnName(h)
								  + " = '" + temp.trim()  + "'";
						 else
							valueString += "," + rMeta.getColumnName(h) + " = '" + temp.trim() + "'";
					  }
				   }
				   else
				   {
                      Object object = SqlUtils.getResultObject(rset2, h);
					  if (updateString == null)
						 updateString =
							" where " + rMeta.getColumnName(h) + " = " + DataFixer.substituteObject(object);
					  else
						 updateString += " and " + rMeta.getColumnName(h)  + " = " + DataFixer.substituteObject(object);
				   }
				}
	
				if (valueString != null)
				{
					changed = true;
					updateStrings.add( valueString + updateString );
				}
	
		 	}
	
			if( changed )
				System.out.println("(Found VARCHARS)");
			else
				System.out.println("(No VARCHARS Found)");
				
			if( rset2 != null )
				rset2.close();
	
			stmt2.close();
	   }
	
	   System.gc();
	   if( updateStrings.size() > 0 )
	   	executeStringUpdate( updateStrings );
	   	
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (7/28/00 1:46:45 PM)
	 * @return java.lang.Object
	 * @param o java.lang.Object
	 */
	public static final Object substituteObject(Object o) 
	{
		if( o == null )
			return null;
		else
		if( o instanceof Character )
		{
			return "'" + o.toString().trim() + "'";
		}
		else
		if( o instanceof java.util.GregorianCalendar )
		{
			java.sql.Timestamp ts = new java.sql.Timestamp(((java.util.GregorianCalendar)o).getTime().getTime());
			return "'" + ts.toString() + "'";
		}
		else if( o instanceof java.util.Date )
		{
			java.sql.Timestamp ts = new java.sql.Timestamp( ((java.util.Date)o).getTime() );
			return "'" + ts.toString() + "'";
		}
		else
		if( o instanceof Long )
		{
			return new java.math.BigDecimal( ((Long) o).longValue() );
		}
		else
		if( o instanceof String )
		{
			return "'" + o.toString().trim() + "'";
		}
		else
			return o;
	}
}

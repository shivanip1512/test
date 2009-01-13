package com.cannontech.dbtools.tools;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.dbtools.updater.MessageFrameAdaptor;
import com.cannontech.tools.gui.IRunnableDBTool;


/**
 * ModifyConstraints disables/enables all the user constraints of the database 
 * provided.  Use with caution.  Possibly useful in conjunction with
 * CopyEntireScheme in this package.
 *
 * Creation date: (1/13/00 11:56:40 AM)
 * @author: Aaron Lauinger
 * @see CopyEntireSchema
 */
public class ModifyConstraints extends MessageFrameAdaptor
{

	/**
	 * 
	 */
	public ModifyConstraints()
	{
		super();
	}

	public String getName()
	{
		return "Modify Constraints";
	}
	
	public void run()
	{
		if( modifyConstraints() )
			getIMessageFrame().finish( "All Tables Processed" );
	}

	public String getParamText()
	{
		return "Enable or Disable:";
	}

	public String getDefaultValue()
	{
		return "Enable";
	}

	/**
	 * ModifyConstraints disables/enables all the user constraints of the database 
	 * provided.  Use with caution.  Possibly useful in conjunction with
	 * CopyEntireScheme in this package.
	 *
	 * Creation date: (1/13/00 11:56:40 AM)
	 * @author: Aaron Lauinger
	 * @see CopyEntireSchema
	 */
	private boolean modifyConstraints()
	{
		final String enable = System.getProperty( IRunnableDBTool.PROP_VALUE );

		if( enable == null 
			 || 
			 !(enable.equalsIgnoreCase("enable")
			   || enable.equalsIgnoreCase("disable")) )
		{
			System.out.println(" ModifyConstraints [ enable | disable ]");
			getIMessageFrame().addOutput("Valid input parameters : enable | disable");
			return false;
		}

		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
	
		java.util.LinkedList tableNames = new java.util.LinkedList();
		java.util.LinkedList constraintNames = new java.util.LinkedList();
		
		try
		{
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
	
			String dbtype = DBTools.getDBMS( conn );

			stmt = conn.createStatement();
			java.sql.ResultSet rset = null;
	
			if( dbtype.equalsIgnoreCase("sqlserver") )
				rset = stmt.executeQuery(
					"select o1.name, o2.name from sysconstraints c, sysobjects o2, " +
					"sysobjects o1 where c.constid=o2.id and o1.id=c.id and o1.xtype='U' " +
					"group by c.id, o1.name, o2.name" );
			else
				rset = stmt.executeQuery("select table_name,constraint_name from sys.user_constraints");
	
			while( rset.next() )
			{
				tableNames.add( rset.getString(1) );

				constraintNames.add( rset.getString(2) );
			}
	
			stmt.close();
	
			java.util.Iterator iterNames = tableNames.iterator();
			java.util.Iterator iterConstraints = constraintNames.iterator();
	
	
	
			while( iterNames.hasNext() )
			{
				String name = null;
				String constraintName = null;
				
				try
				{
					name = (String) iterNames.next();
					constraintName = (String) iterConstraints.next();
				
					stmt = conn.createStatement();
	
					String cmd = null;
					if( dbtype.equalsIgnoreCase("sqlserver") )
						cmd = "alter table " + name + " " + 
								(enable.equalsIgnoreCase("enable") ? "CHECK" : "NOCHECK")
								+ " constraint " + constraintName;
					else
						cmd = "alter table " + name + " " + enable + 
									" constraint " + constraintName;
				
					stmt.executeUpdate( cmd );
					getIMessageFrame().addOutput( cmd );
				}
				catch( Exception e )
				{
					getIMessageFrame().addOutput("");
					getIMessageFrame().addOutput( "Unable to " + enable + " on the the table " + name );
					getIMessageFrame().addOutput( e.getMessage() );
				}
				finally
				{
					if( stmt != null )
						stmt.close();
				}
			}
			
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if( stmt != null ) stmt.close();
				if( conn != null ) conn.close();
			}
			catch( Exception e )
			{
			}
		}
		
		return true;
	}

}

package com.cannontech.common.constants;

import java.util.Properties;

import com.cannontech.common.util.CtiUtilities;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public final class YukonListFuncs implements YukonListEntryTypes 
{

	public static Properties yukonListEntries = new Properties();
	
	
	//let us init all of this instantly!
	static
	{
		initAllConstants();
	}
	
	
	/**
	 * Constructor for YukonListFuncs.
	 */
	private YukonListFuncs() {
		super();
	}

	public synchronized static YukonListEntry getYukonListEntry( int entryID_ )
	{
		YukonListEntry entry = 
				(YukonListEntry)getYukonListEntries().get( new Integer(entryID_) );
				
		if( entry == null )
		{
			//very strange, should not occur!
			throw new IllegalStateException(
				"Unable to find " + YukonListEntry.TABLE_NAME + " with an ID of " + entryID_ );			
		}
		else
			return entry;
	}
	
	private static Properties getYukonListEntries()
	{
		return yukonListEntries;
	}

	private static void initAllConstants()
	{
	
		long start = System.currentTimeMillis();
			
		String sqlString = 
				"select EntryID, ListID, EntryOrder, EntryText, YukonDefinitionID " + 
				"from " + YukonListEntry.TABLE_NAME + " " +
				"where EntryID > " + CtiUtilities.NONE_ID + " " + 
				"order by EntryID, ListID";
	
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( 
					CtiUtilities.getDatabaseAlias() );

			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlString);
	
			while( rset.next() )
			{
				YukonListEntry entry = new YukonListEntry();
				entry.setEntryID( rset.getInt(1) );
				entry.setListID( rset.getInt(2) );
				entry.setListOrder( rset.getInt(3) );				
				entry.setEntryText( rset.getString(4).trim() );
				entry.setYukonDefID( rset.getInt(5) );
	
				getYukonListEntries().put( 
						new Integer(entry.getEntryID()), 
						entry );
			}
		}
		catch( java.sql.SQLException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			try
			{
				if( stmt != null )
					stmt.close();
				if( conn != null )
					conn.close();
			}
			catch( java.sql.SQLException e )
			{
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			}


			com.cannontech.clientutils.CTILogger.info( 
			    (System.currentTimeMillis() - start)*.001 + 
			      " Secs for YukonConstants (" + getYukonListEntries().size() + " loaded)" );
		}
		
	}
	




	public synchronized static boolean isListEntryValid( int entryID_, String entry_ )
	{
		switch( entryID_ )
		{
			case YukonListEntryTypes.YUK_DEF_ID_EMAIL:
				return (
					entry_ != null
					&& entry_.length() > 0
					&& entry_.indexOf("@") > -1);
				 
			case YukonListEntryTypes.YUK_DEF_ID_PHONE:
				break; //no validation of phone numbers yet!
			
			
			
			default: //what is this?? Must be good!!
				return true;
		}


		return true;
	}
	
}

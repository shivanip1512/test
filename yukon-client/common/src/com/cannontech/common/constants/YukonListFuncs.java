package com.cannontech.common.constants;

import java.util.ArrayList;
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

	private static Properties yukonListEntries = new Properties();
	private static Properties yukonSelectionLists = new Properties();
	
	
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
	
	public static Properties getYukonListEntries()
	{
		return yukonListEntries;
	}

	public static Properties getYukonSelectionLists()
	{
		return yukonSelectionLists;
	}

	private static void initAllConstants()
	{
		
		long start = System.currentTimeMillis();
	
		java.sql.Connection conn = com.cannontech.database.PoolManager.getInstance().getConnection( 
				CtiUtilities.getDatabaseAlias() );
		
		initYukonListEntries( conn );
		initYukonSelectionLists( conn );

		try
		{
			if( conn != null ) conn.close();
		}
		catch( java.sql.SQLException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}


		com.cannontech.clientutils.CTILogger.info( 
		    (System.currentTimeMillis() - start)*.001 + 
		      " Secs for YukonConstants (" + getYukonListEntries().size() + " loaded)" );
		
	}
	
	
	private static void initYukonListEntries(java.sql.Connection conn) {
		String sqlString = 
				"select EntryID, ListID, EntryOrder, EntryText, YukonDefinitionID " + 
				"from " + YukonListEntry.TABLE_NAME + " " +
				//"where EntryID > " + CtiUtilities.NONE_ID + " " + 
				"order by EntryID, ListID";
	
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		try
		{
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlString);
	
			while( rset.next() )
			{
				YukonListEntry entry = new YukonListEntry();
				entry.setEntryID( rset.getInt(1) );
				entry.setListID( rset.getInt(2) );
				entry.setEntryOrder( rset.getInt(3) );				
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
                if (rset != null) rset.close();
				if( stmt != null ) stmt.close();
			}
			catch( java.sql.SQLException e )
			{
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			}
		}
	}
	
	private static void initYukonSelectionLists(java.sql.Connection conn) {
		String sqlString = 
				"select ListID, Ordering, SelectionLabel, WhereIsList, ListName, UserUpdateAvailable " + 
				"from " + YukonSelectionList.TABLE_NAME + " " +
				"where ListID > " + CtiUtilities.NONE_ID;
	
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		try
		{
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlString);
	
			while( rset.next() )
			{
				YukonSelectionList list = new YukonSelectionList();
				list.setListID( rset.getInt(1) );
				list.setOrdering( rset.getString(2) );
				list.setSelectionLable( rset.getString(3).trim() );
				list.setWhereIsList( rset.getString(4).trim() );
				list.setListName( rset.getString(5).trim() );
				list.setUserUpdateAvailable( rset.getString(6).trim() );
				
				list.setYukonListEntries( getAllListEntries(list, conn) );
	
				getYukonSelectionLists().put( 
						new Integer(list.getListID()), 
						list );
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
                if (rset != null) rset.close();
				if( stmt != null ) stmt.close();
			}
			catch( java.sql.SQLException e )
			{
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			}
		}
	}
	
    
    public static YukonSelectionList getYukonSelectionList(int listID) {
		YukonSelectionList list = 
				(YukonSelectionList) getYukonSelectionLists().get( new Integer(listID) );
				
		if( list == null )
		{
			//very strange, should not occur!
			throw new IllegalStateException(
				"Unable to find " + YukonSelectionList.TABLE_NAME + " with an ID of " + listID );			
		}
		else
			return list;
    }
    
    
    private static ArrayList getAllListEntries(int listID, java.sql.Connection conn) {
    	YukonSelectionList list = new YukonSelectionList();
    	list.setListID( listID );
    	list.setOrdering( "N" );
    	
    	return getAllListEntries(list, conn);
    }
	
	private static ArrayList getAllListEntries(YukonSelectionList list, java.sql.Connection conn) {
        String sql =
        		"SELECT EntryID, ListID, EntryOrder, EntryText, YukonDefinitionID"
        		+ " FROM " + YukonListEntry.TABLE_NAME
        		+ " WHERE ListID = ?";
        if (list.getOrdering().equalsIgnoreCase("A"))	// Alphabetical order
        	sql += " ORDER BY EntryText";
        else if (list.getOrdering().equalsIgnoreCase("O"))	// Order by "EntryOrder"
        	sql += " ORDER BY EntryOrder";
        else
        	sql += " ORDER BY EntryID";

        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        ArrayList entries = new ArrayList();

        try
        {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt( 1, list.getListID() );
            rset = pstmt.executeQuery();

            while (rset.next()) {
            	YukonListEntry entry = new YukonListEntry();
            	entry.setEntryID( rset.getInt(1) );
            	entry.setListID( rset.getInt(2) );
            	entry.setEntryOrder( rset.getInt(3) );
            	entry.setEntryText( rset.getString(4).trim() );
            	entry.setYukonDefID( rset.getInt(5) );
            	
            	entries.add( entry );
            }
        }
        catch( java.sql.SQLException e )
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rset != null) rset.close();
                if( pstmt != null ) pstmt.close();
            }
            catch( java.sql.SQLException e2 )
            {
                e2.printStackTrace();
            }
        }

        return entries;
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

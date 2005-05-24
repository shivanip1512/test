package com.cannontech.database.cache.functions;

import java.util.ArrayList;
import java.util.Properties;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.util.Validator;

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

	private static Properties yukonListEntries = null;
	private static Properties yukonSelectionLists = null;
	
	
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
	
	public synchronized static Properties getYukonListEntries()
	{
		if (yukonListEntries == null)
			initAllConstants();
		return yukonListEntries;
	}

	public synchronized static Properties getYukonSelectionLists()
	{
		if (yukonSelectionLists == null)
			initAllConstants();
		return yukonSelectionLists;
	}
	
	public synchronized static void releaseAllConstants() {
		yukonListEntries = null;
		yukonSelectionLists = null;
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
				//"where EntryID > " + CtiUtilities.NONE_ZERO_ID + " " + 
				"order by EntryID, ListID";
	
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		try
		{
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlString);
			
			yukonListEntries = new Properties();
			while( rset.next() )
			{
				YukonListEntry entry = new YukonListEntry();
				entry.setEntryID( rset.getInt(1) );
				entry.setListID( rset.getInt(2) );
				entry.setEntryOrder( rset.getInt(3) );				
				entry.setEntryText( rset.getString(4).trim() );
				entry.setYukonDefID( rset.getInt(5) );
	
				yukonListEntries.put( 
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
				"where ListID > " + CtiUtilities.NONE_ZERO_ID;
	
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		try
		{
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlString);
			
			yukonSelectionLists = new Properties();
			while( rset.next() )
			{
				YukonSelectionList list = new YukonSelectionList();
				list.setListID( rset.getInt(1) );
				list.setOrdering( rset.getString(2) );
				list.setSelectionLabel( rset.getString(3).trim() );
				list.setWhereIsList( rset.getString(4).trim() );
				list.setListName( rset.getString(5).trim() );
				list.setUserUpdateAvailable( rset.getString(6).trim() );
				
				list.setYukonListEntries( getAllListEntries(list, conn) );
	
				yukonSelectionLists.put( 
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
	
    
    public synchronized static YukonSelectionList getYukonSelectionList(int listID) {
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




	public static boolean isListEntryValid( int entryID_, String entry_ )
	{
		switch( entryID_ )
		{
			case YukonListEntryTypes.YUK_DEF_ID_EMAIL:
				return Validator.isEmailAddress( entry_ );
				 
			case YukonListEntryTypes.YUK_DEF_ID_PHONE:
				return Validator.isPhoneNumber( entry_ );
			
			case YukonListEntryTypes.YUK_DEF_ID_PIN:
				return Validator.isNumber( entry_ );
			
			
			default: //what is this?? Must be good!!
				return true;
		}

	}
	
	public static boolean areSameInYukon(int entryID1, int entryID2) {
		YukonListEntry entry1 = getYukonListEntry( entryID1 );
		YukonListEntry entry2 = getYukonListEntry( entryID2 );
		return (entry1 != null && entry2 != null && entry1.getYukonDefID() == entry2.getYukonDefID());
	}

	/**
	 * Checks for a valid phone number entry
	 */
	public static boolean isPhoneNumber( int listEntryID )
	{
	    return(
	         listEntryID == YukonListEntryTypes.YUK_ENTRY_ID_PHONE
	         || listEntryID == YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE
	         || listEntryID == YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE );
	}

	/**
	 * Checks for an email entry
	 */
	public static boolean isEmail( int listEntryID )
	{
		return
			 listEntryID == YukonListEntryTypes.YUK_ENTRY_ID_EMAIL;
	}

	/**
	 * Checks for a PIN entry
	 */
	public static boolean isPIN( int listEntryID )
	{
		return
			 listEntryID == YukonListEntryTypes.YUK_ENTRY_ID_PIN;
	}

	/**
	 * Get the selection list name based on the specified list entry yukon definition ID 
	 */
	public static String getYukonListName(int yukonDefID) {
		if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_STATUS)
			return YukonSelectionListDefs.YUK_LIST_NAME_SO_FILTER_BY;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_SO_SORT_BY_ORDER_NO)
			return YukonSelectionListDefs.YUK_LIST_NAME_SO_SORT_BY;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_ORDER_NO)
			return YukonSelectionListDefs.YUK_LIST_NAME_SO_SEARCH_BY;
		else if (yukonDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO)
			return YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_TYPE)
			return YukonSelectionListDefs.YUK_LIST_NAME_INV_FILTER_BY;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO)
			return YukonSelectionListDefs.YUK_LIST_NAME_INV_SORT_BY;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_SERIAL_NO)
			return YukonSelectionListDefs.YUK_LIST_NAME_INV_SEARCH_BY;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_DEFAULT)
			return YukonSelectionListDefs.YUK_LIST_NAME_THERMOSTAT_FAN_STATE;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_DEFAULT)
			return YukonSelectionListDefs.YUK_LIST_NAME_THERMOSTAT_MODE;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_ANS_TYPE_SELECTION)
			return YukonSelectionListDefs.YUK_LIST_NAME_ANSWER_TYPE;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_QUE_TYPE_SIGNUP)
			return YukonSelectionListDefs.YUK_LIST_NAME_QUESTION_TYPE;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_TOW_WEEKDAY)
			return YukonSelectionListDefs.YUK_LIST_NAME_TIME_OF_WEEK;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_LOC_UNKNOWN)
			return YukonSelectionListDefs.YUK_LIST_NAME_APP_LOCATION;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_MANU_UNKNOWN)
			return YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL)
			return YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_ACCT_NO)
			return YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_PENDING)
			return YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DEFAULT)
			return YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT)
			return YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC)
			return YukonSelectionListDefs.YUK_LIST_NAME_INVENTORY_CATEGORY;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_SIGNUP)
			return YukonSelectionListDefs.YUK_LIST_NAME_LM_CUSTOMER_ACTION;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMPROGRAM)
			return YukonSelectionListDefs.YUK_LIST_NAME_LM_CUSTOMER_EVENT;
		
		return null;
	}
	
	public static YukonListEntry getYukonListEntry(YukonSelectionList list, String entryText) {
		for (int i = 0; i < list.getYukonListEntries().size(); i++) {
			YukonListEntry entry = (YukonListEntry) list.getYukonListEntries().get(i);
			if (entry.getEntryText().equalsIgnoreCase( entryText ))
				return entry;
		}
		return null;
	}
	
}

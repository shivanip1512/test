package com.cannontech.yukon.server.cache;

import java.util.Map;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.db.contact.ContactNotification;

/**
 * Insert the type's description here.
 */
public class ContactNotifcationLoader implements Runnable 
{
    //Map<Integer(contactNotifID), LiteContactNotification>
    private Map contactNotifMap = null;
	private String databaseAlias = null;
	
	/**
	 * CustomerContactLoader constructor comment.
	 */
	public ContactNotifcationLoader(Map contactNotifMap, String alias) {
		super();
		this.contactNotifMap = contactNotifMap;
		this.databaseAlias = alias;
	}
	/**
	 * run method comment.
	 */
	public void run() 
	{
	//temp code
	java.util.Date timerStart = null;
	java.util.Date timerStop = null;
	//temp code
	
	//temp code
	timerStart = new java.util.Date();
	//temp code
	
		//get all the customer contacts that are assigned to a customer
		String sqlString = 
			"SELECT cn.ContactNotifID, cn.ContactID, cn.NotificationCategoryID, " + 
			"cn.DisableFlag, cn.Notification " + 
			"FROM " + ContactNotification.TABLE_NAME + " cn " +
			"where cn.ContactNotifID > " + CtiUtilities.NONE_ZERO_ID + " " +
			"order by cn.ContactID";
	
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( this.databaseAlias );
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlString);

			while( rset.next() )
			{
				LiteContactNotification lcn = new LiteContactNotification(
						rset.getInt(1), 
						rset.getInt(2), 
						rset.getInt(3),
						rset.getString(4),
						rset.getString(5) );

				contactNotifMap.put( new Integer(lcn.getContactNotifID()), lcn );
			}			
			
		}
		catch( java.sql.SQLException e )
		{
			CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			try
			{
				if( stmt != null ) stmt.close();
				if( conn != null ) conn.close();
			}
			catch( java.sql.SQLException e )
			{
				CTILogger.error( e.getMessage(), e );
			}
	//temp code
	timerStop = new java.util.Date();
	com.cannontech.clientutils.CTILogger.info( 
	    (timerStop.getTime() - timerStart.getTime())*.001 + 
	      " Secs for ContactNotifcationLoader (" + contactNotifMap.values().size() + " loaded)" );
	//temp code
		}
	}
}

package com.cannontech.yukon.server.cache;

import java.util.Map;
import java.util.Vector;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.db.contact.ContactNotification;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
public class ContactLoader implements Runnable 
{
    //Map<Integer(contactID), LiteContact>
    private Map allContactsMap = null;

    private java.util.ArrayList allContacts = null;
	private String databaseAlias = null;
	
	/**
	 * CustomerContactLoader constructor comment.
	 */
	public ContactLoader(java.util.ArrayList customerContactArray, Map contactMap, String alias) {
		super();
		this.allContacts = customerContactArray;
        this.allContactsMap = contactMap;
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
			"SELECT cnt.contactID, cnt.ContFirstName, cnt.ContLastName, " + 
			"cnt.Loginid, cnt.AddressID " + 
			"FROM " + Contact.TABLE_NAME + " cnt " +
			"where cnt.ContactID > " + CtiUtilities.NONE_ZERO_ID + " " +
			"order by cnt.contactID";
	
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( this.databaseAlias );
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlString);
	
			//get out LiteContacts first
			while( rset.next() )
			{
				LiteContact lc = new LiteContact(
								rset.getInt(1), 
								rset.getString(2).trim(),
								rset.getString(3).trim(), 
								rset.getInt(4),
								rset.getInt(5) );
	
				allContacts.add(lc);
                allContactsMap.put( new Integer(lc.getContactID()), lc );
			}

			//load up our ContactNotification objects			
			sqlString = 
				"SELECT cn.ContactNotifID, cn.ContactID, cn.NotificationCategoryID, " + 
				"cn.DisableFlag, cn.Notification " + 
				"FROM " + ContactNotification.TABLE_NAME + " cn " +
				"where cn.ContactNotifID > " + CtiUtilities.NONE_ZERO_ID + " " +
				"order by cn.ContactID";
			
			Vector vectVals = new Vector(8);
			rset = stmt.executeQuery(sqlString);

			while( rset.next() )
			{
				LiteContactNotification ln = new LiteContactNotification(
						rset.getInt(1), 
						rset.getInt(2), 
						rset.getInt(3),
						rset.getString(4),
						rset.getString(5) );

				vectVals.add( ln);
			}
			
			
			//assign our ContactNotifications to their owner Contact
			for( int i = 0; i < allContacts.size(); i++ )
			{
				LiteContact lc = (LiteContact)allContacts.get(i);
				boolean found = false; //tries to make this loop fast
				
				for( int j = 0; j < vectVals.size(); j++ )
				{
					LiteContactNotification ltCntNotif 
							= (LiteContactNotification)vectVals.get(j);

					if( ltCntNotif.getContactID() == lc.getContactID() )
					{
						lc.getLiteContactNotifications().add(
							ltCntNotif );
						
						found = true;
					}
					else if( found )  //speed it up!
						break;
				}
				
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
	//temp code
	timerStop = new java.util.Date();
	com.cannontech.clientutils.CTILogger.info( 
	    (timerStop.getTime() - timerStart.getTime())*.001 + 
	      " Secs for ContactLoader (" + allContacts.size() + " loaded)" );
	//temp code
		}
	}
}

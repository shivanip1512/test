package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Map;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
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
    //if total contact number is over this, better not load this way
    private final int MAX_CONTACT_LOAD = 50000;
    private boolean willNeedDynamicLoad = false;
    
    //Map<Integer(contactID), LiteContact>
    private final Map<Integer, LiteContact> allContactsMap;

	private final String databaseAlias;

    //Map<Integer(ContactNotifID), LiteContactNotif>
    private final Map<Integer, LiteContactNotification> allContactNotifsMap;
	
	/**
	 * CustomerContactLoader constructor comment.
	 */
	public ContactLoader(Map<Integer, LiteContact> contactMap, Map<Integer, LiteContactNotification> allContactNotifsMap, String alias) {
		super();
        this.allContactsMap = contactMap;
        this.allContactNotifsMap = allContactNotifsMap;
		this.databaseAlias = alias;
	}
	/**
	 * run method comment.
	 */
	public void run() 
	{
	    Date timerStart = null;
	    Date timerStop = null;
	    timerStart = new java.util.Date();

		//get all the customer contacts that are assigned to a customer
		String sqlString = 
			"SELECT cnt.contactID, cnt.ContFirstName, cnt.ContLastName, " + 
			"cnt.Loginid, cnt.AddressID " + 
			"FROM " + Contact.TABLE_NAME + " cnt " +
			"where cnt.ContactID > " + CtiUtilities.NONE_ZERO_ID + " " +
			"order by cnt.contactID";
	
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
        
        int maxContactID = 0;
        int loadIterator = MAX_CONTACT_LOAD;
        
		try
		{
			conn = PoolManager.getInstance().getConnection( this.databaseAlias );
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlString);
	
			//get out LiteContacts first
			while( rset.next() && loadIterator > 0)
			{
				LiteContact lc = new LiteContact(
								rset.getInt(1), 
								rset.getString(2).trim(),
								rset.getString(3).trim(), 
								rset.getInt(4),
								rset.getInt(5) );
	
                allContactsMap.put( new Integer(lc.getContactID()), lc );
                if(maxContactID < lc.getContactID())
                    maxContactID = lc.getContactID();
                loadIterator--;
			}
			
			//load up our ContactNotification objects			
			sqlString = 
				"SELECT cn.ContactNotifID, cn.ContactID, cn.NotificationCategoryID, " + 
				"cn.DisableFlag, cn.Notification " + 
				"FROM " + ContactNotification.TABLE_NAME + " cn " +
				"where cn.ContactNotifID > " + CtiUtilities.NONE_ZERO_ID + " " +
                "and cn.ContactID <= " + maxContactID  + " and cn.contactid > 0 " +
				"order by cn.ContactID, cn.Ordering";
			
            // we'll store the last contact to take advantage of the
            // fact that the ContactNotifications are ordered by ContactID
			LiteContact lastContact = null;

            SqlUtils.close(rset);
            rset = stmt.executeQuery(sqlString);
			while( rset.next() )
			{
				LiteContactNotification ln = new LiteContactNotification(
						rset.getInt(1), 
						rset.getInt(2), 
						rset.getInt(3),
						rset.getString(4),
						rset.getString(5) );
                // add to all contact notif map
                allContactNotifsMap.put(new Integer(ln.getContactNotifID()), ln);

                // check if the lastContact is still valid
                if (lastContact == null || lastContact.getContactID() != ln.getContactID()) {
                    lastContact = allContactsMap.get( new Integer(ln.getContactID()) );
                }
                
                // add this contact notif to its contact
                lastContact.getLiteContactNotifications().add(ln);
			}			
		} 
		catch( java.sql.SQLException e )
		{
			CTILogger.error( "Unable to load contacts.", e );
		}
		finally
		{
            SqlUtils.close(rset, stmt, conn);

            timerStop = new Date();
        	double secondsToLoad = (timerStop.getTime() - timerStart.getTime())*.001;
            CTILogger.info(secondsToLoad + " Secs for ContactLoader" );
            if(loadIterator <= 0)
            {
                CTILogger.warn("Contact loader limit exceeded!  System will need to use " +
                                                             "dynamic loading for some contacts and notifications.");
                willNeedDynamicLoad = true;
            }
        }
	}
    
    public boolean limitExceeded()
    {
        return willNeedDynamicLoad;
    }
}

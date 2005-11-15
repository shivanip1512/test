/*
 * Created on Nov 14, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yukon.server.cache.bypass;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.db.contact.ContactNotification; 

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class YukonUserContactNotificationLookup 
{
	/**
	 * Constructor for YukonUserFuncs.
	 */
	public YukonUserContactNotificationLookup()
	{
		super();
	}
   
    /*
     * Grab a contact notification straight from the DB using the ContactNotifID 
     */
    public static LiteContactNotification loadSpecificContactNotificationByID(int contactNotifID)
    {

        LiteContactNotification contactNotify = null;
        
        try
        {
            contactNotify = new LiteContactNotification(contactNotifID);
            contactNotify.retrieve(CtiUtilities.YUKONDBALIAS);
        }
        catch( Exception e )
        {
            com.cannontech.clientutils.CTILogger.error( "Error retrieving contact notification with ID: "+ contactNotifID + "  " + e.getMessage(), e );
        }
          
        return contactNotify;
    }
    
}

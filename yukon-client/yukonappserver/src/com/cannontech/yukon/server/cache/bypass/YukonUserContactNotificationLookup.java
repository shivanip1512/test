/*
 * Created on Nov 14, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yukon.server.cache.bypass;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.db.contact.ContactNotification; 
import com.cannontech.database.db.user.YukonRole;

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
        /*
         * Grab a contact straight from the DB using the LoginID 
         */
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
    
    /*
     * Grab contacts straight from the DB using last name (or similar last name) 
     */
    public static LiteContact[] loadContactsByLastName(String lName, boolean partialMatch)
    {
        com.cannontech.database.SqlStatement stmt;
        
        /*
         * Just get the ContactIDs first.  We'll want to do a retrieve so we get notifications, etc.
         */
        if(partialMatch)
        {        
            stmt = new com.cannontech.database.SqlStatement("SELECT CONTACTID FROM " +
                                                           Contact.TABLE_NAME + " WHERE CONTLASTNAME LIKE '" + lName + "%'", "yukon");
        }
        else
        {
            stmt = new com.cannontech.database.SqlStatement("SELECT CONTACTID FROM " +
                                                           Contact.TABLE_NAME + " WHERE CONTLASTNAME = '" + lName + "'", "yukon");
        }
        
        LiteContact[] foundContacts;
        
        try
        {
            stmt.execute();
            
            foundContacts = new LiteContact[stmt.getRowCount()];
            for( int j = 0; j < stmt.getRowCount(); j++ )
            {
                LiteContact newlyFound = new LiteContact(((java.math.BigDecimal) stmt.getRow(j)[0]).intValue());
                newlyFound.retrieve(CtiUtilities.YUKONDBALIAS);
                foundContacts[j] = newlyFound;
            }
            
            return foundContacts;
        }
        catch( Exception e )
        {
            com.cannontech.clientutils.CTILogger.error( "Error retrieving contacts with last name " + lName + ": " + e.getMessage(), e );
        }
        
        foundContacts = new LiteContact[0];
        return foundContacts;
    }
    
    /*
     * Grab contacts straight from the DB using last name (or similar last name) 
     */
    public static LiteContact[] loadContactsByFirstName(String fName, boolean partialMatch)
    {
        com.cannontech.database.SqlStatement stmt;
        
        /*
         * Just get the ContactIDs first.  We'll want to do a retrieve so we get notifications, etc.
         */
        if(partialMatch)
        {        
            stmt = new com.cannontech.database.SqlStatement("SELECT CONTACTID FROM " +
                                                           Contact.TABLE_NAME + " WHERE CONTFIRSTNAME LIKE '" + fName + "%'", "yukon");
        }
        else
        {
            stmt = new com.cannontech.database.SqlStatement("SELECT CONTACTID FROM " +
                                                           Contact.TABLE_NAME + " WHERE CONTFIRSTNAME = '" + fName + "'", "yukon");
        }
        
        LiteContact[] foundContacts;
        
        try
        {
            stmt.execute();
            
            foundContacts = new LiteContact[stmt.getRowCount()];
            for( int j = 0; j < stmt.getRowCount(); j++ )
            {
                LiteContact newlyFound = new LiteContact(((java.math.BigDecimal) stmt.getRow(j)[0]).intValue());
                newlyFound.retrieve(CtiUtilities.YUKONDBALIAS);
                foundContacts[j] = newlyFound;
            }
            
            return foundContacts;
        }
        catch( Exception e )
        {
            com.cannontech.clientutils.CTILogger.error( "Error retrieving contacts with first name " + fName + ": " + e.getMessage(), e );
        }
        
        foundContacts = new LiteContact[0];
        return foundContacts;
    }
    
    public static LiteContact[] loadContactsByPhoneNumber(String phone, boolean partialMatch)
    {
        com.cannontech.database.SqlStatement stmt;
        
        /*
         * Just get the ContactIDs first.  We'll want to do a retrieve so we get notifications, etc.
         */
        if(partialMatch)
        {        
            stmt = new com.cannontech.database.SqlStatement("SELECT CONTACTID FROM " +
                                                           ContactNotification.TABLE_NAME + " WHERE NOTIFICATION LIKE '%" + phone + "'", "yukon");
        }
        else
        {
            stmt = new com.cannontech.database.SqlStatement("SELECT CONTACTID FROM " +
                                                           ContactNotification.TABLE_NAME + " WHERE NOTIFICATION = '" + phone + "'", "yukon");
        }
        
        LiteContact[] foundContacts;
        
        try
        {
            stmt.execute();
            
            foundContacts = new LiteContact[stmt.getRowCount()];
            for( int j = 0; j < stmt.getRowCount(); j++ )
            {
                LiteContact newlyFound = new LiteContact(((java.math.BigDecimal) stmt.getRow(j)[0]).intValue());
                newlyFound.retrieve(CtiUtilities.YUKONDBALIAS);
                foundContacts[j] = newlyFound;
            }
            
            return foundContacts;
        }
        catch( Exception e )
        {
            com.cannontech.clientutils.CTILogger.error( "Error retrieving contacts that use phone number " + phone + ": " + e.getMessage(), e );
        }
        
        foundContacts = new LiteContact[0];
        return foundContacts;
    }
    
    public static LiteContact loadContactsByEmail(String email)
    {
        com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement("SELECT CONTACTID FROM " +
                                                           ContactNotification.TABLE_NAME + " WHERE NOTIFICATION = '" + email + "'", "yukon");
        try
        {
            stmt.execute();
            
            if(stmt.getRowCount() > 0)
            {
                LiteContact newlyFound = new LiteContact(((java.math.BigDecimal) stmt.getRow(0)[0]).intValue());
                newlyFound.retrieve(CtiUtilities.YUKONDBALIAS);
                return newlyFound;
            }

        }
        catch( Exception e )
        {
            com.cannontech.clientutils.CTILogger.error( "Error retrieving contacts that use email address " + email + ": " + e.getMessage(), e );
        }
        
        return null;
    }
    

}

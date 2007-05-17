/*
 * Created on Nov 14, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yukon.server.cache.bypass;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.db.contact.ContactNotification;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class YukonUserContactLookup 
{
	/**
	 * Constructor for DaoFactory.getYukonUserDao().
	 */
	public YukonUserContactLookup()
	{
		super();
	}
   
    /*
     * Grab a contact straight from the DB using the LoginID 
     */
	public static LiteContact loadSpecificUserContact(int userID)
	{
		com.cannontech.database.SqlStatement stmt =
			new com.cannontech.database.SqlStatement("SELECT CONTACTID, CONTFIRSTNAME, CONTLASTNAME, ADDRESSID FROM " +
                                                       Contact.TABLE_NAME + " WHERE LOGINID = " + userID, "yukon");
		LiteContact ThreeTwoOneContact = null;
		
		try
		{
			stmt.execute();
			
			//it found one
			if( stmt.getRowCount() > 0 )
			{
			    ThreeTwoOneContact = new LiteContact(((java.math.BigDecimal) stmt.getRow(0)[0]).intValue());
                ThreeTwoOneContact.setContFirstName( (String) stmt.getRow(0)[1] );
                ThreeTwoOneContact.setContLastName( (String) stmt.getRow(0)[2] );
                ThreeTwoOneContact.setLoginID( userID );
                ThreeTwoOneContact.setAddressID( ((java.math.BigDecimal) stmt.getRow(0)[3]).intValue() );
			}
		}
		catch( Exception e )
		{
			com.cannontech.clientutils.CTILogger.error( "Error retrieving contact for userID " + userID + ": " + e.getMessage(), e );
		}
          
        return ThreeTwoOneContact;
    }

    /*
     * Grab a contact straight from the DB using the ContactID 
     */
    public static LiteContact loadSpecificContact(int contactID)
    {

        LiteContact ThreeTwoOneContact = null;
        
        try
        {
            ThreeTwoOneContact = new LiteContact(contactID);
            ThreeTwoOneContact.retrieve(CtiUtilities.getDatabaseAlias());
        }
        catch( Exception e )
        {
            com.cannontech.clientutils.CTILogger.error( "Error retrieving contact with ID: "+ contactID + "  " + e.getMessage(), e );
        }
          
        return ThreeTwoOneContact;
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
                                                           Contact.TABLE_NAME + " WHERE UPPER(CONTLASTNAME) LIKE '" + lName.toUpperCase() + "%'", "yukon");
        }
        else
        {
            stmt = new com.cannontech.database.SqlStatement("SELECT CONTACTID FROM " +
                                                           Contact.TABLE_NAME + " WHERE UPPER(CONTLASTNAME) = '" + lName.toUpperCase() + "'", "yukon");
        }
        
        LiteContact[] foundContacts;
        
        try
        {
            stmt.execute();
            
            foundContacts = new LiteContact[stmt.getRowCount()];
            for( int j = 0; j < stmt.getRowCount(); j++ )
            {
                LiteContact newlyFound = new LiteContact(((java.math.BigDecimal) stmt.getRow(j)[0]).intValue());
                newlyFound.retrieve(CtiUtilities.getDatabaseAlias());
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
                                                           Contact.TABLE_NAME + " WHERE UPPER(CONTFIRSTNAME) LIKE '" + fName.toUpperCase() + "%'", "yukon");
        }
        else
        {
            stmt = new com.cannontech.database.SqlStatement("SELECT CONTACTID FROM " +
                                                           Contact.TABLE_NAME + " WHERE UPPER(CONTFIRSTNAME) = '" + fName.toUpperCase() + "'", "yukon");
        }
        
        LiteContact[] foundContacts;
        
        try
        {
            stmt.execute();
            
            foundContacts = new LiteContact[stmt.getRowCount()];
            for( int j = 0; j < stmt.getRowCount(); j++ )
            {
                LiteContact newlyFound = new LiteContact(((java.math.BigDecimal) stmt.getRow(j)[0]).intValue());
                newlyFound.retrieve(CtiUtilities.getDatabaseAlias());
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
        com.cannontech.database.SqlStatement stmtRevised = null;
        /*
         * Just get the ContactIDs first.  We'll want to do a retrieve so we get notifications, etc.
         */
        if(partialMatch)
        {        
            stmt = new com.cannontech.database.SqlStatement("SELECT CONTACTID FROM " +
                                                           ContactNotification.TABLE_NAME + " WHERE NOTIFICATION LIKE '%" + phone + 
                                                           "%' AND NOTIFICATIONCATEGORYID IN (SELECT ENTRYID FROM " + YukonListEntry.TABLE_NAME +
                                                           " WHERE YUKONDEFINITIONID =" + YukonListEntryTypes.YUK_DEF_ID_PHONE + ")", "yukon");
            //legacy numbers have hyphens in the db; let's at least attempt to be understanding about it
            if(phone.length() == 10)
                stmtRevised = new com.cannontech.database.SqlStatement("SELECT CONTACTID FROM " +
                                                            ContactNotification.TABLE_NAME + " WHERE NOTIFICATION LIKE '%" + phone.substring(0, 3) + "-" + phone.substring(3, 6) + "-" + phone.substring(6) +
                                                            "' AND NOTIFICATIONCATEGORYID IN (SELECT ENTRYID FROM " + YukonListEntry.TABLE_NAME +
                                                           " WHERE YUKONDEFINITIONID =" + YukonListEntryTypes.YUK_DEF_ID_PHONE + ")", "yukon");
        }
        else
        {
            stmt = new com.cannontech.database.SqlStatement("SELECT CONTACTID FROM " +
                                                           ContactNotification.TABLE_NAME + " WHERE NOTIFICATION = '" + phone + 
                                                           "' AND NOTIFICATIONCATEGORYID IN (SELECT ENTRYID FROM " + YukonListEntry.TABLE_NAME +
                                                           " WHERE YUKONDEFINITIONID =" + YukonListEntryTypes.YUK_DEF_ID_PHONE + ")", "yukon");
        }
        
        LiteContact[] foundContacts;
        
        try
        {
            stmt.execute();
            
            foundContacts = new LiteContact[stmt.getRowCount()];

            for( int j = 0; j < stmt.getRowCount(); j++ ) {
                LiteContact newlyFound = new LiteContact(((java.math.BigDecimal) stmt.getRow(j)[0]).intValue());
                newlyFound.retrieve(CtiUtilities.getDatabaseAlias());
                foundContacts[j] = newlyFound;
            }
            
            //legacy phone numbers have hyphens in the db; let's at least attempt to be understanding about it
            if(partialMatch && foundContacts.length < 1 && stmtRevised != null) {
                stmtRevised.execute();
                foundContacts = new LiteContact[stmtRevised.getRowCount()];
                
                for( int j = 0; j < stmtRevised.getRowCount(); j++ ) {
                    LiteContact newlyFound = new LiteContact(((java.math.BigDecimal) stmtRevised.getRow(j)[0]).intValue());
                    newlyFound.retrieve(CtiUtilities.getDatabaseAlias());
                    foundContacts[j] = newlyFound;
                }
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
                                                           ContactNotification.TABLE_NAME + " WHERE UPPER(NOTIFICATION) = '" + email.toUpperCase() + "'", "yukon");
        try
        {
            stmt.execute();
            
            if(stmt.getRowCount() > 0)
            {
                LiteContact newlyFound = new LiteContact(((java.math.BigDecimal) stmt.getRow(0)[0]).intValue());
                newlyFound.retrieve(CtiUtilities.getDatabaseAlias());
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

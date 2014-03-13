/*
 * Created on Nov 14, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yukon.server.cache.bypass;

import java.sql.SQLException;
import java.util.List;

import com.cannontech.common.model.ContactNotificationMethodType;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.ImmutableCollection;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class YukonUserContactLookup 
{
    // This is duplicate of ContactDao. But didn't want to cross reference that object in yukonappserver package.
    private static YukonRowMapper<LiteContact> rowMapper = new YukonRowMapper<LiteContact>() {
        final ContactNotificationDao contactNotificationDao = YukonSpringHook.getBean(ContactNotificationDao.class);
        @Override
        public LiteContact mapRow(YukonResultSet rs) throws SQLException {
            int id = rs.getInt("ContactId");
            int loginId = rs.getInt("LoginId");
            int addressId = rs.getInt("AddressId");
            String firstName = SqlUtils.convertDbValueToString(rs.getString("ContFirstName")).trim();
            String lastName = SqlUtils.convertDbValueToString(rs.getString("ContLastName")).trim();

            LiteContact contact = new LiteContact(id);
            contact.setLoginID(loginId);
            contact.setAddressID(addressId);
            contact.setContFirstName(firstName);
            contact.setContLastName(lastName);
            
            List<LiteContactNotification> notifications = contactNotificationDao.getNotificationsForContact(contact.getContactID());
            contact.getLiteContactNotifications().addAll(notifications);
            
            return contact;
        };
    };
        
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
    
    public static LiteContact[] loadContactsByPhoneNumber(String phone, boolean partialMatch)
    {
        YukonJdbcTemplate yukonJdbcTemplate = YukonSpringHook.getBean("simpleJdbcTemplate", YukonJdbcTemplate.class);
        SqlStatementBuilder sql = new SqlStatementBuilder();

        ImmutableCollection<ContactNotificationType> phoneTypes = ContactNotificationType.getFor(ContactNotificationMethodType.PHONE);

        sql.append("SELECT c.ContactId, c.ContFirstName, c.ContLastName, c.LoginID, c.AddressID ");
        sql.append("FROM Contact c JOIN ContactNotification cn on c.ContactId = cn.ContactId");
        sql.append("WHERE NotificationCategoryId").in(phoneTypes);
        if (partialMatch) {
            sql.append("AND Notification").contains(phone);
        } else {
            sql.append("AND Notification").eq(phone);
        }
        List<LiteContact> contacts = yukonJdbcTemplate.query(sql, rowMapper);
        
        
        //legacy numbers have hyphens in the db; let's at least attempt to be understanding about it
        if(phone.length() == 10) {
            String oldPhoneSyntax = phone.substring(0, 3) + "-" + phone.substring(3, 6) + "-" + phone.substring(6);
            SqlStatementBuilder legacySql = new SqlStatementBuilder();
            legacySql.append("SELECT c.ContactId, c.ContFirstName, c.ContLastName, c.LoginID, c.AddressID ");
            sql.append("FROM Contact c JOIN ContactNotification cn on c.ContactId = cn.ContactId");
            legacySql.append("WHERE Notification").startsWith(oldPhoneSyntax);
            legacySql.append("WHERE NotificationCategoryId").in(phoneTypes);
            
            contacts.addAll(yukonJdbcTemplate.query(legacySql, rowMapper));
        }
        
        if (contacts.size() > 0) {
            LiteContact[] foundContacts = new LiteContact[contacts.size()];
            return contacts.toArray(foundContacts);
        } else {
            return new LiteContact[0];
        }
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

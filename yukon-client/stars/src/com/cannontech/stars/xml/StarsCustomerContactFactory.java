package com.cannontech.stars.xml;

import java.util.Vector;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.customer.Contact;
import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.serialize.Email;
import com.cannontech.stars.xml.serialize.StarsContactNotification;
import com.cannontech.stars.xml.serialize.StarsCustomerContact;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class StarsCustomerContactFactory {

    public StarsCustomerContactFactory() {
    }
    
    public static StarsContactNotification newStarsContactNotification(boolean enabled, String notification, Class type) {
        try {
            StarsContactNotification newNotif = (StarsContactNotification) type.newInstance();
            newNotif.setEnabled( enabled );
            newNotif.setNotification( notification );
            
            return newNotif;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static StarsCustomerContact newStarsCustomerContact(StarsCustomerContact contact, Class type) {
        try {
            StarsCustomerContact newContact = (StarsCustomerContact) type.newInstance();
            newContact.setContactID( contact.getContactID() );
            newContact.setLastName( contact.getLastName() );
            newContact.setFirstName( contact.getFirstName() );
            newContact.setHomePhone( contact.getHomePhone() );
            newContact.setWorkPhone( contact.getWorkPhone() );
            newContact.setEmail( (Email) newStarsContactNotification(
            		contact.getEmail().getEnabled(), contact.getEmail().getNotification(), Email.class) );

            return newContact;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void setCustomerContact(Contact contact, StarsCustomerContact starsContact) {
    	//contact.setContactID( new Integer(starsContact.getContactID()) );
        contact.getContact().setContLastName( starsContact.getLastName() );
        contact.getContact().setContFirstName( starsContact.getFirstName() );
        
        Vector contactNotifVect = contact.getContactNotifVect();
        contactNotifVect.clear();
        
        if (starsContact.getHomePhone().length() > 0) {
	        ContactNotification notif = new ContactNotification();
	        notif.setNotificationCatID( new Integer(SOAPServer.YUK_LIST_ENTRY_ID_HOME_PHONE) );
	        notif.setNotification( starsContact.getHomePhone() );
	        notif.setDisableFlag( "Y" );
	        contactNotifVect.add( notif );
        }
        
        if (starsContact.getWorkPhone().length() > 0) {
	        ContactNotification notif = new ContactNotification();
	        notif.setNotificationCatID( new Integer(SOAPServer.YUK_LIST_ENTRY_ID_WORK_PHONE) );
	        notif.setNotification( starsContact.getWorkPhone() );
	        notif.setDisableFlag( "Y" );
	        contactNotifVect.add( notif );
        }
        
        if (starsContact.getEmail().getNotification().length() > 0) {
	        ContactNotification notif = new ContactNotification();
	        notif.setNotificationCatID( new Integer(SOAPServer.YUK_LIST_ENTRY_ID_EMAIL) );
	        notif.setNotification( starsContact.getEmail().getNotification() );
	        notif.setDisableFlag( starsContact.getEmail().getEnabled() ? "N" : "Y" );
	        contactNotifVect.add( notif );
        }
    }
}
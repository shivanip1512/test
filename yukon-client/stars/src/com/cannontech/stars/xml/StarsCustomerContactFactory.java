package com.cannontech.stars.xml;

import com.cannontech.database.Transaction;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.database.db.customer.CustomerContact;
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

    public static StarsCustomerContact newStarsCustomerContact(StarsCustomerContact contact, Class type) {
        try {
            StarsCustomerContact newContact = (StarsCustomerContact) type.newInstance();

            newContact.setContactID( contact.getContactID() );
            newContact.setLastName( contact.getLastName() );
            newContact.setFirstName( contact.getFirstName() );
            newContact.setHomePhone( contact.getHomePhone() );
            newContact.setWorkPhone( contact.getWorkPhone() );

            return newContact;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void setCustomerContact(CustomerContact contact, StarsCustomerContact starsContact) {
    	//contact.setContactID( new Integer(starsContact.getContactID()) );
        contact.setContLastName( starsContact.getLastName() );
        contact.setContFirstName( starsContact.getFirstName() );
        contact.setContPhone1( starsContact.getHomePhone() );
        contact.setContPhone2( starsContact.getWorkPhone() );
    }
    
    public static boolean identical(StarsCustomerContact contact1, StarsCustomerContact contact2) {
    	return (contact1.getLastName().equals( contact2.getLastName() )
    		  && contact1.getFirstName().equals( contact2.getFirstName() )
    		  && contact1.getHomePhone().equals( contact2.getHomePhone() )
    		  && contact1.getWorkPhone().equals( contact2.getWorkPhone() ));
    }
    
    public static void updateCustomerContact(StarsCustomerContact contact, StarsCustomerContact newContact) {
    	if (identical(contact, newContact)) return;
    	
    	contact.setLastName( newContact.getLastName() );
    	contact.setFirstName( newContact.getFirstName() );
    	contact.setHomePhone( newContact.getHomePhone() );
    	contact.setWorkPhone( newContact.getWorkPhone() );
    	
    	try {
	    	CustomerContact contactDB = new CustomerContact();
	    	setCustomerContact(contactDB, contact);
	    	Transaction.createTransaction( Transaction.UPDATE, contactDB ).execute();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public static StarsCustomerContact insertCustomerContact(StarsCustomerContact newContact) {
    	try {
    		CustomerContact contactDB = new CustomerContact();
    		setCustomerContact(contactDB, newContact);
    		contactDB = (CustomerContact)
    				Transaction.createTransaction( Transaction.INSERT, contactDB ).execute();
    		newContact.setContactID( contactDB.getContactID().intValue() );
	    	
	    	return newContact;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return null;
    }
}
package com.cannontech.stars.xml.serialize;

import com.cannontech.database.db.customer.CustomerContact;

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
        contact.setContLastName( starsContact.getLastName() );
        contact.setContFirstName( starsContact.getFirstName() );
        contact.setContPhone1( starsContact.getHomePhone() );
        contact.setContPhone2( starsContact.getWorkPhone() );
    }
}
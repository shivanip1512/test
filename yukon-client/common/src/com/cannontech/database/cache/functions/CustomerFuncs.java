package com.cannontech.database.cache.functions;

import java.util.Iterator;

import com.cannontech.database.data.lite.LiteCustomerContact;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
public final class CustomerFuncs {
/**
 * PointFuncs constructor comment.
 */
private CustomerFuncs() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 9:41:59 AM)
 * @return com.cannontech.database.data.lite.LitePoint
 * @param pointID int
 */
public static boolean contactExists(int contactID) 
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List contacts = cache.getAllCustomerContacts();
		java.util.Collections.sort( contacts, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		
		for( int j = 0; j < contacts.size(); j++ )
		{
			if( contactID == ((com.cannontech.database.data.lite.LiteCustomerContact)contacts.get(j)).getContactID() )
				return true;
		}

		return false;
	}

}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 9:41:59 AM)
 * @return com.cannontech.database.data.lite.LitePoint
 * @param pointID int
 */
public static java.util.List getAllCustomers()
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List paos = cache.getAllYukonPAObjects();
		java.util.ArrayList customers = new java.util.ArrayList(paos.size() / 2 );
		
		for( int i = 0; i < paos.size(); i++ )
		{
			if( ((com.cannontech.database.data.lite.LiteYukonPAObject)paos.get(i)).getCategory() == com.cannontech.database.data.pao.PAOGroups.CAT_DEVICE
				 && ((com.cannontech.database.data.lite.LiteYukonPAObject)paos.get(i)).getPaoClass() == com.cannontech.database.data.pao.DeviceClasses.METER
				 && com.cannontech.database.data.device.DeviceTypesFuncs.isMeter(((com.cannontech.database.data.lite.LiteYukonPAObject)paos.get(i)).getType()) )
			{
				customers.add( paos.get(i) );
			}
			
		}
		
		return customers;
	}

}

/**
 * Finds the customer contact for this user id
 * @param userID
 * @return LiteCustomerContact
 */
public static LiteCustomerContact getCustomerContact(int userID) {
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache) {
		Iterator iter = cache.getAllCustomerContacts().iterator();
		while(iter.hasNext()) {
			LiteCustomerContact contact = (LiteCustomerContact) iter.next();
			if(contact.getUserID() == userID) {
				return contact;
			}
		}		
	}	
	return null;
}

/**
 * Finds a lite pao object representing the customer that
 * that this user id belongs to
S * @param userID
 * @return LiteYukonPAObject
 */
public static LiteYukonPAObject getCustomer(int userID) {
	LiteCustomerContact lcc = getCustomerContact(userID);
	return PAOFuncs.getLiteYukonPAO(lcc.getCustomerID());
}
}

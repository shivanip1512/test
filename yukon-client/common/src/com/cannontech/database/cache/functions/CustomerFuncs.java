package com.cannontech.database.cache.functions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
public final class CustomerFuncs {
/**
 * CustomerFuncs constructor comment.
 */
private CustomerFuncs() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 9:41:59 AM)
 * @return boolean
 * @param contactID int
 */
public static boolean contactExists(int contactID) 
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List contacts = cache.getAllContacts();
		java.util.Collections.sort( contacts, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		
		for( int j = 0; j < contacts.size(); j++ )
		{
			if( contactID == ((LiteContact)contacts.get(j)).getContactID() )
				return true;
		}

		return false;
	}

}

/**
 * Finds the customer contact for this user id
 * @param userID
 * @return LiteCustomerContact
 */
public static LiteContact getCustomerContact(int userID) 
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache) 
	{
		Iterator iter = cache.getAllContacts().iterator();
		while(iter.hasNext()) {
			LiteContact contact = (LiteContact) iter.next();
			if(contact.getLoginID() == userID) {
				return contact;
			}
		}		
	}
	
	return null;
}

/**
 * Finds a LiteCICustomer representing the contacts owner CICustomer.
 * @param contactID_
 * @return LiteCICustomer
 * 
 * MAY Replace the return type with LiteCustomer when LiteCustomer exists!!
 */
public static LiteCICustomer getOwnerCICustomer( int contactID_ ) 
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = 
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)	 
	{
		Iterator iter = cache.getAllCICustomers().iterator();
		while( iter.hasNext() ) 
		{
			LiteCICustomer cst = (LiteCICustomer) iter.next();
			
			for( int i = 0; i < cst.getAdditionalContacts().size(); i++ )
			{
				if( ((LiteContact)cst.getAdditionalContacts().get(i)).getContactID() 
					 == contactID_ )
				{
					return cst;
				}
			}
		}		
	}
		
	//no owner CICustomer...strange
	return null;
}


/**
 * Finds all LiteContact instances not used by a CICustomer
 * @return LiteContact
 * 
 *
public static LiteContact[] getUnusedContacts( )
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = 
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	ArrayList retValues = new ArrayList(50);
	
	synchronized(cache)	 
	{
		List customers = cache.getAllCICustomers();
		List contacts = cache.getAllContacts();
		
		for( int i = 0; i < contacts.size(); i++ ) 
		{
			LiteContact cnt = (LiteContact)contacts.get(i);
			boolean found = false;
					
			for( int j = 0; j < customers.size(); j++ )
			{				
				if( !((LiteCICustomer)customers.get(j)).getAdditionalContacts().contains(cnt) ) 
					continue;
				else
				{
					found = true;
					break;
				}
			}
			
			if( !found )
				retValues.add( cnt );

		}		
	}

	//sort the contacts
	java.util.Collections.sort( 
				retValues, 
				com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		
	LiteContact[] cnts = new LiteContact[ retValues.size() ];
	return (LiteContact[])retValues.toArray( cnts );
}
*/
}

package com.cannontech.database.cache.functions;

import java.util.Iterator;
import java.util.List;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;

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
 * Finds the customer contact for this user id
 * @param userID
 * @return List LiteContact
 */
public static List getAllContacts(int customerID_) 
{
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized(cache) 
	{
		Iterator iter = cache.getAllCustomers().iterator();
		java.util.Vector allContacts = new java.util.Vector(5);	//guess capacity
		while(iter.hasNext())
		{
			LiteCustomer customer = (LiteCustomer) iter.next();
			if(customer.getCustomerID() == customerID_)
			{
				int primCntctID = customer.getPrimaryContactID();
				LiteContact liteContact = ContactFuncs.getContact(primCntctID);
				if( liteContact != null)
					allContacts.addElement(liteContact);
				
				for (int i = 0; i < customer.getAdditionalContacts().size(); i++)
				{
					allContacts.addElement(customer.getAdditionalContacts().get(i));
				}
				return allContacts;
			}
		}		
	}
	return null;
}

public static LiteContact getPrimaryContact(int customerID_) 
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache) 
	{
		Iterator iter = cache.getAllCICustomers().iterator();
		while(iter.hasNext())
		{
			LiteCICustomer ciCustomer = (LiteCICustomer) iter.next();
			if(ciCustomer.getCustomerID() == customerID_)
			{
				int primCntctID = ciCustomer.getPrimaryContactID();
				LiteContact liteContact = ContactFuncs.getContact(primCntctID);
				if( liteContact != null)
					return liteContact;
			}
		}		
	}
	return null;
}
/**
 * This method was created in VisualAge.
 * @return String
 */
public static LiteCustomer getLiteCustomer( int custID)
{
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		return (LiteCustomer)cache.getAllCustomersMap().get(new Integer (custID));
	}
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
/**
 * Returns the lite cicustomer with the given customer id.
 * @param customerID
 * @return LiteCICustomer
 */
public static LiteCICustomer getLiteCICustomer(int customerID) {
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized( cache ) {
			for(Iterator i = cache.getAllCICustomers().iterator(); i.hasNext();) {
				LiteCICustomer lc = (LiteCICustomer) i.next();
				if(lc.getCustomerID() == customerID ) {
					return lc;
				}
			}	
		}
	return null;
}
}

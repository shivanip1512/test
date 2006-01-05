package com.cannontech.database.cache.functions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

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
		LiteCustomer customer = (LiteCustomer)cache.getACustomerByCustomerID(customerID_);
		java.util.Vector allContacts = new java.util.Vector(5);	//guess capacity
		if(customer != null)
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
	return null;
}

public static LiteContact getPrimaryContact(int customerID_) 
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache) 
	{
		LiteCustomer customer = (LiteCustomer)cache.getACustomerByCustomerID(customerID_);
		if( customer != null)
		{
			int primCntctID = customer.getPrimaryContactID();
			LiteContact liteContact = ContactFuncs.getContact(primCntctID);
			if( liteContact != null)
				return liteContact;
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
		return (LiteCustomer)cache.getACustomerByCustomerID(custID);
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
public static LiteCICustomer getLiteCICustomer(int customerID)
{
	//Get the customer from AllCustomersMap (make use of the map), retun null if NOT instance LiteCICustomer
	LiteCustomer lc = getLiteCustomer(customerID);
	if ( lc instanceof LiteCICustomer)
		return (LiteCICustomer)lc;
		
	return null;
}

/**
 * Returns a vector of LiteCustomers with given energyCompanyID
 * @param energycompanyID
 * @return Vector liteCustomers
 */
public static Vector getAllLiteCustomersByEnergyCompany(int energyCompanyID)
{
	Vector liteCustomers = new Vector();
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized(cache) 
	{
		List allCustomers = cache.getAllCustomers();
		for( int i= 0; i < allCustomers.size(); i++)
		{
			LiteCustomer lc = (LiteCustomer)allCustomers.get(i);
			if( lc.getEnergyCompanyID() == energyCompanyID)
				liteCustomers.add(lc);
		}
	}
	return liteCustomers;
}
}

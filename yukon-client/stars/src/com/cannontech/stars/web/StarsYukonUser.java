package com.cannontech.stars.web;

import java.util.Enumeration;
import java.util.Hashtable;

import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.util.ServerUtils;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StarsYukonUser {

	private int userID = 0;
	private int energyCompanyID = 0;
	private LiteCustomer customer = null;
	private Hashtable attributes = new Hashtable();
	
	private StarsYukonUser(LiteYukonUser user) {
		userID = user.getUserID();
	}
	
	public StarsYukonUser(StarsYukonUser starsUser) {
		userID = starsUser.getUserID();
		energyCompanyID = starsUser.getEnergyCompanyID();
		customer = starsUser.getCustomer();
	}
	
	public static StarsYukonUser newInstance(LiteYukonUser user) throws InstantiationException {
		StarsYukonUser starsUser = new StarsYukonUser(user);
		starsUser.init();
		
		return starsUser;
	}
	
	public int getUserID() {
		return userID;
	}
	
	public LiteYukonUser getYukonUser() {
		return com.cannontech.database.cache.functions.YukonUserFuncs.getLiteYukonUser( userID );
	}
	
	public LiteCustomer getCustomer() {
		return customer;
	}
	
	public int getEnergyCompanyID() {
		return energyCompanyID;
	}
	
	/**
	 * If the yukon user belongs to a residential customer, returns
	 * all the account IDs of this customer. Otherwise, returns null.
	 */
	public int[] getCustomerAccountIDs() {
		if (customer != null) {
			java.util.Vector acctIDs = customer.getAccountIDs();
			int[] accountIDs = new int[ acctIDs.size() ];
			for (int i = 0; i < acctIDs.size(); i++)
				accountIDs[i] = ((Integer) acctIDs.get(i)).intValue();
			
			return accountIDs;
		}
		
		return null;
	}
	
	public Object getAttribute(Object name) {
		return attributes.get( name );
	}
	
	public Enumeration getAttributeNames() {
		return attributes.keys();
	}
	
	public void setAttribute(Object name, Object value) {
		attributes.put( name, value );
	}
	
	public void removeAttribute(Object name) {
		attributes.remove( name );
	}
	
	public synchronized Integer getIncAttribute(Object name) {
		Integer value = (Integer) attributes.get( name );
		if (value != null) {
			Integer newValue = new Integer( value.intValue()+1 );
			attributes.put( name, newValue );
		}
		return value;
	}
	
	private void init() throws InstantiationException {
		if (ServerUtils.isOperator(this)) {
			LiteEnergyCompany energyCompany = EnergyCompanyFuncs.getEnergyCompany( getYukonUser() );
			if (energyCompany == null)
				throw new InstantiationException( "Cannot find the energy company of this user" );
			
			energyCompanyID = energyCompany.getEnergyCompanyID();
		}
		else if (ServerUtils.isResidentialCustomer(this)) {
			LiteContact liteContact = YukonUserFuncs.getLiteContact( getUserID() );
			customer = ContactFuncs.getCustomer( liteContact.getContactID() );
			if (customer == null)
				throw new InstantiationException( "Cannot find customer information of this user" );
			
			energyCompanyID = customer.getEnergyCompanyID();
		}
	}
}

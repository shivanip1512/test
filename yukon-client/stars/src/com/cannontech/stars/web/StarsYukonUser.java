package com.cannontech.stars.web;

import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.util.StarsUtils;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StarsYukonUser {

	private LiteYukonUser yukonUser = null;
	private int energyCompanyID = 0;
	private LiteCustomer customer = null;
	
	private StarsYukonUser(LiteYukonUser user) {
		yukonUser = user;
	}
	
	public static StarsYukonUser newInstance(LiteYukonUser user) throws InstantiationException {
		StarsYukonUser starsUser = new StarsYukonUser(user);
		starsUser.init();
		
		return starsUser;
	}
	
	public int getUserID() {
		return yukonUser.getUserID();
	}
	
	public LiteYukonUser getYukonUser() {
		return yukonUser;
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
	
	private void init() throws InstantiationException {
		if (StarsUtils.isOperator(this)) {
			LiteEnergyCompany energyCompany = EnergyCompanyFuncs.getEnergyCompany( getYukonUser() );
			if (energyCompany == null)
				throw new InstantiationException( "Cannot find the energy company for user id = " + getYukonUser().getUserID() );
			
			energyCompanyID = energyCompany.getEnergyCompanyID();
		}
		else if (StarsUtils.isResidentialCustomer(this)) {
			LiteContact liteContact = YukonUserFuncs.getLiteContact( getUserID() );
			if (liteContact == null)
				throw new InstantiationException( "Cannot find contact information for user id = " + getYukonUser().getUserID() );
			customer = ContactFuncs.getCustomer( liteContact.getContactID() );
			if (customer == null)
				throw new InstantiationException( "Cannot find customer information for user id = " + getYukonUser().getUserID() );
			
			energyCompanyID = customer.getEnergyCompanyID();
		}
	}
}

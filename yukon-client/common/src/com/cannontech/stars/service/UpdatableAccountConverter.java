package com.cannontech.stars.service;

import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public interface UpdatableAccountConverter {

	/**
	 * Creates a new UpdatableAccount object based on data String[] which is indexed using standard ImportManagerUtil index constants.
	 * This method sets forcePasswordReset to Yes. 
	 * @param custFields
	 * @param ec
	 * @return
	 */
	public UpdatableAccount createNewUpdatableAccount(String[] custFields, YukonEnergyCompany ec);
	
	/**
	 * Creates a UpdatableAccount based on an existing LiteStarsCustAccountInformation account number, loads current data for customer
	 * into the UpdatableAccount's DTO, then updates it using data from a String[] which is indexed using standard ImportManagerUtil index constants.
	 * This method sets forcePasswordReset to Yes.
	 * @param starsCustAcctInfo
	 * @param custFields
	 * @param ec
	 * @return
	 */
	public UpdatableAccount getUpdatedUpdatableAccount(LiteAccountInfo starsCustAcctInfo, String[] custFields, YukonEnergyCompany ec);
	
}

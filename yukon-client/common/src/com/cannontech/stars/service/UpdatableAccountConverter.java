package com.cannontech.stars.service;

import com.cannontech.stars.database.data.lite.LiteStarsCustAccountInformation;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.model.UpdatableAccount;

public interface UpdatableAccountConverter {

	/**
	 * Creates a new UpdatableAccount object based on data String[] which is indexed using standard ImportManagerUtil index constants.
	 * @param custFields
	 * @param ec
	 * @return
	 */
	public UpdatableAccount createNewUpdatableAccount(String[] custFields, LiteStarsEnergyCompany ec);
	
	/**
	 * Creates a UpdatableAccount based on an existing LiteStarsCustAccountInformation account number, loads current data for customer
	 * into the UpdatableAccount's DTO, then updates it using data from a String[] which is indexed using standard ImportManagerUtil index constants.
	 * @param starsCustAcctInfo
	 * @param custFields
	 * @param ec
	 * @return
	 */
	public UpdatableAccount getUpdatedUpdatableAccount(LiteStarsCustAccountInformation starsCustAcctInfo, String[] custFields, LiteStarsEnergyCompany ec);
	
}

package com.cannontech.stars.service;

import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.stars.util.WebClientException;

public interface UpdatableAccountConverter {

	/**
	 * Creates a new UpdatableAccount object based on data String[] which is indexed using standard ImportManagerUtil index constants.
	 * @param custFields
	 * @param ec
	 * @return
	 * @throws WebClientException 
	 */
	public UpdatableAccount createNewUpdatableAccount(String[] custFields, LiteStarsEnergyCompany ec) throws WebClientException;
	
	/**
	 * Creates a UpdatableAccount based on an existing LiteStarsCustAccountInformation account number, loads current data for customer
	 * into the UpdatableAccount's DTO, then updates it using data from a String[] which is indexed using standard ImportManagerUtil index constants.
	 * @param starsCustAcctInfo
	 * @param custFields
	 * @param ec
	 * @return
	 * @throws WebClientException 
	 */
	public UpdatableAccount getUpdatedUpdatableAccount(LiteStarsCustAccountInformation starsCustAcctInfo, String[] custFields, LiteStarsEnergyCompany ec) throws WebClientException;
	
}

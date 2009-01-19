package com.cannontech.stars.service;

import com.cannontech.common.bulk.field.impl.UpdatableAccount;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;

public interface UpdatableAccountConversionService {

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

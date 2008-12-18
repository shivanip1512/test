package com.cannontech.i18n.service;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;

/**
 * Service for getting yukon user contexts 
 */
public interface YukonUserContextService {

	/**
	 * Method to get a user context for a user with default settings from the user's
	 * energyCompany
	 * @param user - User to get context for
	 * @return Default user context
	 */
	public YukonUserContext getEnergyCompanyDefaultUserContext(LiteYukonUser user);
	
}

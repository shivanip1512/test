package com.cannontech.i18n.service;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;

/**
 * Service for getting yukon user contexts 
 */
public interface YukonUserContextService {

	/**
	 * Method to get a user context for a user with default settings from the user's
	 * energyCompany.  This method should only be used if you REALLY want the energy
	 * company default context.  This context includes the system default time zone and
	 * a locale of US, regardless of which user is provided.
	 * @param user - User to get context for
	 * @return Default user context
	 */
	public YukonUserContext getEnergyCompanyDefaultUserContext(LiteYukonUser user);
	
}

package com.cannontech.stars.dr.optout.service;

import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Service used to determine the current state of opt out availability
 */
public interface OptOutStatusService {

	/**
	 * Method to determine if Opt Outs are currently enabled
	 * @param user - User to get value for
	 * @return True if opt outs are enabled, False if disabled
	 */
	public boolean getOptOutEnabled(LiteYukonUser user);
	
	/**
	 * Method to determine if Opt Outs currently count against the limit
	 * @param user - User to get value for
	 * @return True if opt outs count, False if don't count
	 */
	public boolean getOptOutCounts(LiteYukonUser user);
	
}

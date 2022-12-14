package com.cannontech.stars.dr.optout.service;

import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.optout.model.OptOutCountsTemporaryOverride;
import com.cannontech.stars.dr.optout.model.OptOutEnabled;

/**
 * Service used to determine the current state of opt out availability
 */
public interface OptOutStatusService {

    /**
     * Gets the system wide opt out enabled setting for the given user.
     * Either from the OptOutTemporaryOverride record that is currently in effect where programId == null (and OptOutType==ENABLED), 
     * otherwise from the role property value if a OptOutTemporaryOverride does not exist.
     * @param user
     * @return
     */
    public OptOutEnabled getDefaultOptOutEnabled(LiteYukonUser user);
    
	/**
	 * Method to determine if Opt Outs are currently enabled for all enrolled programs and at the 
	 * energy company level.  If any enrolled program has optouts disabled while the energy company
	 * is enabled, the most restrictive optout value is returned.
	 * @param user - User to get value for
	 * @return Will return the highest value OptOutEnabled enum (most restrictive)
	 */
	public OptOutEnabled getOptOutEnabled(LiteYukonUser user);

    /**
     * Return all OptOutTemporaryOverride settings where programId != null (and OptOutType==ENABLED).
     * @param energyCompanyId
     * @return
     */
	public Map<Integer, OptOutEnabled> getProgramSpecificEnabledOptOuts(int energyCompanyId);

	/**
	 * Gets opt out counts setting for the "null programId" case.
	 * Either from the OptOutTemporaryOverride record that is currently in effect where programId == null (and OptOutType==COUNTS), otherwise
	 * from the role property value if a OptOutTemporaryOverride does not exist.
	 * @param user
	 * @return
	 */
	public OptOutCountsTemporaryOverride getDefaultOptOutCounts(LiteYukonUser user);
	
	/**
	 * Return all OptOutTemporaryOverride settings where programId != null (and OptOutType==COUNTS).
	 * @param user
	 * @return
	 */
	public List<OptOutCountsTemporaryOverride> getProgramSpecificOptOutCounts(LiteYukonUser user);

}

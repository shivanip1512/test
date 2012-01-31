package com.cannontech.stars.dr.optout.dao;

import java.util.Date;
import java.util.List;

import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.optout.exception.NoTemporaryOverrideException;
import com.cannontech.stars.dr.optout.model.OptOutCountsTemporaryOverride;
import com.cannontech.stars.dr.optout.model.OptOutEnabled;
import com.cannontech.stars.dr.optout.model.OptOutEnabledTemporaryOverride;

/**
 * Dao class for persisting OptOutEvents and OptOutTemporaryOverrides
 * 
 */
public interface OptOutTemporaryOverrideDao {

	/**
	 * The method returns the current system level OptOutTemporaryOverride for a given energy company id.
	 * This OptOutTemporaryOverride object can be used with getCurrentProgramOptOutTemporaryOverrides
	 * to figure out if a user has access to opt out a device or not.
	 */
	public OptOutEnabledTemporaryOverride findCurrentSystemOptOutTemporaryOverrides(int energyCompanyId);
	
    /**
     * The method returns all of the current program level OptOutTemporaryOverrides for a given energy company id.
     * These OptOutTemporaryOverride objects can be used with getCurrentSystemOptOutTemporaryOverrides
     * to figure out if a user has access to opt out a device or not.
     */
    public List<OptOutEnabledTemporaryOverride> getCurrentProgramOptOutTemporaryOverrides(int energyCompanyId);

    /**
	 * Method to determine if Opt Outs currently count against the limit
	 * @param energyCompany - Energy company to check
	 * @return True if opt outs temporarily count, False if temporarily don't count
	 * @throws NoTemporaryOverrideException if there is no temporary override value
	 */
	public List<OptOutCountsTemporaryOverride> getAllOptOutCounts(LiteEnergyCompany energyCompany) throws NoTemporaryOverrideException;

	/**
	 * Method used to set the opt out enabled state to a given value for the time period supplied
	 * @param user - User requesting the change - change will only affect this user's energy company
	 * @param startDate - Date to start temporary change
	 * @param stopDate - Date to stop temporary change
	 * @param enabled - True if temp enable opt outs
	 */
	public void setTemporaryOptOutEnabled(LiteYukonUser user, Date startDate, Date stopDate, OptOutEnabled enabled);

    /**
     * Method used to set the opt out enabled state to a given value for the time period supplied
     * @param user - User requesting the change - change will only affect this user's energy company
     * @param startDate - Date to start temporary change
     * @param stopDate - Date to stop temporary change
     * @param enabled - True if temp enable opt outs
     */
	public void setTemporaryOptOutEnabled(LiteYukonUser user, Date startDate, Date stopDate, 
                                          OptOutEnabled enabled, int webpublishingProgramId);
	
	/**
	 * Method used to set the opt out counts state to a given value for the time period supplied
	 * @param user - User requesting the change - change will only affect this user's energy company
	 * @param startDate - Date to start temporary change
	 * @param stopDate - Date to stop temporary change
	 * @param counts - True if temp count opt outs
	 */
	public void setTemporaryOptOutCounts(LiteYukonUser user, Date startDate, Date stopDate, boolean counts);
	
	/**
	 * Method used to set the opt out counts state to a given value for the time period supplied
	 * Only applies to those temp opt outs with given program Id
	 * @param user - User requesting the change - change will only affect this user's energy company
	 * @param startDate - Date to start temporary change
	 * @param stopDate - Date to stop temporary change
	 * @param counts - True if temp count opt outs
	 */
	public void setTemporaryOptOutCountsForProgramId(LiteYukonUser user, Date startDate, Date stopDate, 
	                                                 boolean counts, int webpublishingProgramId);

}

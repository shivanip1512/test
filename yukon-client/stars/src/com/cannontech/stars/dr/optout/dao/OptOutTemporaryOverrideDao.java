package com.cannontech.stars.dr.optout.dao;

import java.util.Date;
import java.util.List;

import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.optout.exception.NoTemporaryOverrideException;
import com.cannontech.stars.dr.optout.model.OptOutCountsDto;

/**
 * Dao class for persisting Opt out events
 * 
 */
public interface OptOutTemporaryOverrideDao {

	/**
	 * Method to determine if Opt Outs are currently enabled
	 * @param energyCompany - Energy company to check
	 * @return True if opt outs are temporarily enabled, False if temporarily disabled
	 * @throws NoTemporaryOverrideException if there is no temporary override value
	 */
	public boolean getOptOutEnabled(LiteEnergyCompany energyCompany) throws NoTemporaryOverrideException;
	
	/**
	 * Method to determine if Opt Outs currently count against the limit
	 * @param energyCompany - Energy company to check
	 * @return True if opt outs temporarily count, False if temporarily don't count
	 * @throws NoTemporaryOverrideException if there is no temporary override value
	 */
	public List<OptOutCountsDto> getAllOptOutCounts(LiteEnergyCompany energyCompany) throws NoTemporaryOverrideException;

	/**
	 * Method used to set the opt out enabled state to a given value for the time period supplied
	 * @param user - User requesting the change - change will only affect this user's energy company
	 * @param startDate - Date to start temporary change
	 * @param stopDate - Date to stop temporary change
	 * @param enabled - True if temp enable opt outs
	 */
	public void setTemporaryOptOutEnabled(LiteYukonUser user, Date startDate, Date stopDate, boolean enabled);

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
	public void setTemporaryOptOutCountsForProgramId(LiteYukonUser user, Date startDate, Date stopDate, boolean counts, int webpublishingProgramId);

}

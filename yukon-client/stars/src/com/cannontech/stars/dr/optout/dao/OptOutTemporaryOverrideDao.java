package com.cannontech.stars.dr.optout.dao;

import java.util.Date;

import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.optout.exception.NoTemporaryOverrideException;

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
	public boolean getOptOutCounts(LiteEnergyCompany energyCompany) throws NoTemporaryOverrideException;

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

}

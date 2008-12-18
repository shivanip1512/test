package com.cannontech.stars.dr.optout.service;

import java.util.Date;
import java.util.List;

import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.optout.model.OptOutCountHolder;
import com.cannontech.stars.dr.optout.model.OptOutLimit;
import com.cannontech.stars.dr.optout.model.OverrideHistory;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;

/**
 * Interface for service which handles opt outs and canceling of opt outs
 * 
 */
public interface OptOutService {
	
	public static final int NO_OPT_OUT_LIMIT = -1;

	/**
	 * Method to opt a device out or schedule an opt out for a future date
	 * @param customerAccount - Account the device is on
	 * @param request - Contains info about the opt out such as start date and duration
	 * @param user - User requesting opt out
	 * @throws CommandCompletionException - If the command could not be sent to the device
	 */
	public void optOut(CustomerAccount customerAccount, OptOutRequest request,
			LiteYukonUser user) 
		throws CommandCompletionException;

	/**
	 * Method to cancel an opt out or a scheduled opt out
	 * @param eventIdList - List of opt out event ids to cancel
	 * @param user - User requesting the cancel
	 * @throws CommandCompletionException - If the cancel command could not be sent to the device
	 */
	public void cancelOptOut(List<Integer> eventIdList, LiteYukonUser user) 
		throws CommandCompletionException;

	/**
	 * Method to cancel all current opt outs for the given user's energy company
	 * @param user - User requesting cancel
	 * @throws CommandCompletionException - If the cancel command could not be sent to the devices
	 */
	public void cancelAllOptOuts(LiteYukonUser user) 
		throws CommandCompletionException;

	/**
	 * Method to resend any current opt out command
	 * @param inventoryId - Id of device to resend opt out to
	 * @param customerAccountId - Account the device is on
	 * @param user - User requesting the resend
	 * @throws CommandCompletionException - If the command could not be sent to the device
	 */
	public void resendOptOut(int inventoryId, int customerAccountId, LiteYukonUser user)
			throws CommandCompletionException;
	
	/**
	 * Method to temporarily change the Opt Out Counts state for the user's energyCompany.  The
	 * change will take effect now and last through the end of today.  Also changes any current
	 * Opt Out's count state to the new state.
	 * @param user - User making change
	 * @param optOutCounts - True if all opt outs should count for the rest of the day.
	 */
	public void changeOptOutCountStateForToday(LiteYukonUser user, boolean optOutCounts);

	/**
	 * Method to temporarily change whether Opt Outs are allowed by end users for the given user's 
	 * energyCompany.  The change will take effect now and last through the end of today. Does not
	 * affect any currently active or scheduled opt outs.
	 * @param user - User making change
	 * @param optOutsEnabled - True if opt outs should be allowed for the rest of the day.
	 */
	public void changeOptOutEnabledStateForToday(LiteYukonUser user, boolean optOutsEnabled);
	
	/**
	 * Method to get the opt out counts for the given inventory/account pair
	 * @param inventoryId - Inventory to get opt out counts 
	 * @param customerAccountId - Account for inventory
	 * @return The opt out counts for the inventory/account
	 */
	public OptOutCountHolder getCurrentOptOutCount(int inventoryId, int customerAccountId);

	/**
	 * Method to get all opt out limits for a given group
	 * @param group - Group to get limits for
	 * @return List of current opt out limits
	 */
	public List<OptOutLimit> getAllOptOutLimits(LiteYukonGroup group);

	/**
	 * Method to reset the number of opt outs a user has remaining to the limit. (basically
	 * don't count any existing opt outs against the limit)
	 * @param inventoryId - Inventory to reset
	 * @param accountId - Account for inventory
	 */
	public void resetOptOutLimitForInventory(Integer inventoryId, int accountId);
	
	/**
	 * Method to get the total number of devices that were opted out during the given time period
	 * @param accountNumber - Account to get count for
	 * @param startTime - Start of time period
	 * @param stopTime - End of time period
	 * @param user - User requesting count
 	 * @return - Total count
	 */
	public int getOptOutDeviceCountForAccount(String accountNumber, Date startTime,
			Date stopTime, LiteYukonUser user);

	/**
	 * Method to get the total number of devices that were opted out during the given time period
	 * @param programName - Program to get count for
	 * @param startTime - Start of time period
	 * @param stopTime - End of time period
	 * @param user - User requesting count
	 * @return - Total count
	 */
	public int getOptOutDeviceCountForProgram(String programName, Date startTime, 
			Date stopTime, LiteYukonUser user);

	/**
	 * Method to allow additional opt outs for a given inventory
	 * @param accountNumber - Account to add opt outs to
	 * @param serialNumber - Serial number of device to add opt outs to
	 * @param additionalOptOuts - Number of opt outs to add
	 * @param user - User requesting additional opt outs
	 * @throws ObjectInOtherEnergyCompanyException 
	 */
	public void allowAdditionalOptOuts(
			String accountNumber, String serialNumber, int additionalOptOuts, LiteYukonUser user) 
		throws ObjectInOtherEnergyCompanyException;

	/**
	 * Method to get opt out history by program for a given time period
	 * @param programName - Name of program to get history for
	 * @param startTime - Start of time period
	 * @param stopTime - End of time period
	 * @param user - User requesting history
	 * @return List of opt out history
	 */
	public List<OverrideHistory> getOptOutHistoryByProgram(
			String programName, Date startTime, Date stopTime, LiteYukonUser user);
	
	/**
	 * Method to get a list of opt out history for a given account for all inventory on that account
	 * @param accountNumber - Account to get history for
	 * @param startTime - Start of time period
	 * @param stopTime - End of time period
	 * @param user - User requesting history
	 * @return List of opt out history
	 */
	public List<OverrideHistory> getOptOutHistoryForAccount(
			String accountNumber, Date startTime, Date stopTime, LiteYukonUser user);
}

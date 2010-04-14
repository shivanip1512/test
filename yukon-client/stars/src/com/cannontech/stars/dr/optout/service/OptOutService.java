package com.cannontech.stars.dr.optout.service;

import java.util.Date;
import java.util.List;

import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.core.dao.InventoryNotFoundException;
import com.cannontech.core.dao.ProgramNotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.optout.model.OptOutCountHolder;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.OptOutLimit;
import com.cannontech.stars.dr.optout.model.OverrideHistory;

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
	 */
	public void cancelAllOptOuts(LiteYukonUser user);
	
	/**
	 * Method to cancel all current opt outs for the given user's energy company.
	 * Limited to those opt outs that tie to inventory that is currently enrolled in a given program.
	 * @param programName
	 * @param user
	 */
	public void cancelAllOptOutsByProgramName(String programName, LiteYukonUser user);

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
	 * Method to temporarily change the Opt Out Counts state for the user's energyCompany.  The
	 * change will take effect now and last through the end of today.  Also changes any current
	 * Opt Out's count state to the new state.
	 * Only applies to those opt outs for inventory of the given program id.
	 * @param user - User making change
	 * @param optOutCounts - True if all opt outs should count for the rest of the day.
	 * @param programName
	 * @throws ProgramNotFoundException if program name isn't found
	 */
	public void changeOptOutCountStateForTodayByProgramName(LiteYukonUser user, boolean optOutCounts, String programName) throws ProgramNotFoundException;

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
     * Method to get allowed Opt out periods for a given user based on the
     * OptOutPeriod role property. Defaults to 1 day if role property value is
     * not set.
     * @param user
     * @return List of allowed opt out period values
     */
    public List<Integer> getAvailableOptOutPeriods(LiteYukonUser user);
	
	/**
	 * Method to get the current opt out limit for an account
	 * @param customerAccountId - Account to get limit for
	 * @return Current limit or null if there is no limit
	 */
	public OptOutLimit getCurrentOptOutLimit(int customerAccountId);

	/**
	 * Method to reset the number of opt outs a user has remaining to the limit. (basically
	 * don't count any existing opt outs against the limit)
	 * @param inventoryId - Inventory to reset
	 * @param accountId - Account for inventory
	 */
	public void resetOptOutLimitForInventory(Integer inventoryId, int accountId);
	
	/**
	 * Method to reset the number of opt outs a user has remaining to the limit. (basically
	 * don't count any existing opt outs against the limit)
	 * @param accountNumber
	 * @param serialNumber
	 * @param user
	 * @throws InventoryNotFoundException - no found or not found in users energy company
	 * @throws AccountNotFoundException
	 * @throws IllegalArgumentException - If the inventory with serial number is not associated with the account with account number
	 */
	public void resetOptOutLimitForInventory(String accountNumber, String serialNumber, LiteYukonUser user) throws InventoryNotFoundException, AccountNotFoundException, IllegalArgumentException;
	
	/**
	 * Method to get the total number of devices that were opted out during the given time period
	 * @param accountNumber - Account to get count for
	 * @param startTime - Start of time period (inclusive)
	 * @param stopTime - End of time period (inclusive)
	 * @param user - User requesting count
	 * @param programName - Optional programName to narrow count to only that program - null if no
	 		programName
 	 * @return - Total count
 	 * @throws AccountNotFoundException - If account number is not found
	 * @throws ProgramNotFoundException - If program name is not found
	 */
	public int getOptOutDeviceCountForAccount(String accountNumber, Date startTime,
			Date stopTime, LiteYukonUser user, String programName) 
		throws AccountNotFoundException, ProgramNotFoundException;

	/**
	 * Method to get the total number of devices that were opted out during the given time period
	 * @param programName - Program to get count for
	 * @param startTime - Start of time period (inclusive)
	 * @param stopTime - End of time period (inclusive)
	 * @param user - User requesting count
	 * @return - Total count
	 * @throws ProgramNotFoundException - if program name is invalid
	 */
	public int getOptOutDeviceCountForProgram(String programName, Date startTime, 
			Date stopTime, LiteYukonUser user) throws ProgramNotFoundException;

	/**
	 * Method to allow additional opt outs for a given inventory
	 * @param accountNumber - Account to add opt outs to
	 * @param serialNumber - Serial number of device to add opt outs to
	 * @param additionalOptOuts - Number of opt outs to add
	 * @param user - User requesting additional opt outs
	 * @throws InventoryNotFoundException - if serial number is not found
	 * @throws AccountNotFoundException - if account number is not found
	 */
	public void allowAdditionalOptOuts(
			String accountNumber, String serialNumber, int additionalOptOuts, LiteYukonUser user) 
		throws InventoryNotFoundException, AccountNotFoundException;

	/**
	 * Method to get opt out history by program for a given time period
	 * @param programName - Name of program to get history for
	 * @param startTime - Start of time period (inclusive)
	 * @param stopTime - End of time period (inclusive)
	 * @param user - User requesting history
	 * @return List of opt out history
	 * @throws ProgramNotFoundException - if program name is not valid
	 */
	public List<OverrideHistory> getOptOutHistoryByProgram(
			String programName, Date startTime, Date stopTime, LiteYukonUser user)
		throws ProgramNotFoundException;
	
	/**
	 * Method to get a list of opt out history for a given account for all inventory on that account
	 * @param accountNumber - Account to get history for
	 * @param startTime - Start of time period (inclusive)
	 * @param stopTime - End of time period (inclusive)
	 * @param user - User requesting history
	 * @param programName - Optional programName to narrow history to only that program - null if no
	 * 		programName
	 * @return List of opt out history
	 * @throws AccountNotFoundException - If account number is not found
	 * @throws ProgramNotFoundException - If program name is not found
	 */
	public List<OverrideHistory> getOptOutHistoryForAccount(
			String accountNumber, Date startTime, Date stopTime, LiteYukonUser user, 
			String programName)
		throws AccountNotFoundException, ProgramNotFoundException;

	/**
	 * Method to clean up an opt out that should already be complete - sends reenable command
	 * to be sure device is no longer opted out, sends notification of end of opt out, updates
	 * LMHardwareControlGroup opt out stop date
	 * @param inventory - Inventory to cancel opt out for
	 * @param energyCompany - Inventory's energy company
	 * @param event - Opt out event being canceled
	 * @param customerAccount - Customer account for inventory
	 * @param userContext - User canceling opt out 
	 */
	public void cleanUpCancelledOptOut(LiteStarsLMHardware inventory,
			LiteStarsEnergyCompany energyCompany, OptOutEvent event,
			CustomerAccount customerAccount, LiteYukonUser user)
			throws CommandCompletionException;
}

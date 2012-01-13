package com.cannontech.stars.dr.optout.dao;

import java.util.Date;
import java.util.List;

import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.optout.model.OptOutAction;
import com.cannontech.stars.dr.optout.model.OptOutCounts;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.OptOutEventDto;
import com.cannontech.stars.dr.optout.model.OptOutLog;
import com.cannontech.stars.dr.optout.model.OverrideHistory;
import com.google.common.collect.Multimap;

/**
 * Dao class for persisting Opt out events
 * 
 */
public interface OptOutEventDao {

	public OptOutEvent getOptOutEventById(int eventId);

	/**
	 * Method to save/update an Opt out event and log the action taken
	 * 
	 * @param event - Event to save/update
	 * @param action - Action taken on the opt out event
	 * @param user - User requesting the action
	 * @return The log entry saved to optOutEventLog.
	 */
	public OptOutLog save(OptOutEvent event, OptOutAction action, LiteYukonUser user);
	
	/**
	 * Method to determine if there is a current active opt out for a given inventory/customer account 
	 * pair.
	 */
	public boolean isOptedOut(int inventoryId, int customerAccountId);
	
	/**
	 * Method to get a list of opt out history for a given account for all inventory on that account
	 * @param customerAccountId - Account to get history for
	 * @param numberOfRecords - Optional parameter to specify max number of records to return
	 * @return List of opt out event history
	 */
	public List<OptOutEventDto> getOptOutHistoryForAccount(int customerAccountId, int... numberOfRecords);

    /**
     * Find event log details for a given event.
     * @param optOutEvents A list of OptOutEventDto instances, probably from
     *            getOptOutHistoryForAccount.
     * @return A multimap of eventId -> list of OptOutLog instances, ordered by
     *         logDate.
     */
	public Multimap<OptOutEventDto, OptOutLog> getOptOutEventDetails(Iterable<OptOutEventDto> optOutEvents);

	/**
	 * Method to get a list of opt out events by account and time period
	 * @param accountId - Account to get history for
	 * @param startDate - Start of time period
	 * @param stopDate - End of time period
	 * @return List of opt out history
	 */
	public List<OverrideHistory> getOptOutHistoryForAccount(int accountId, Date startDate, Date stopDate);

    /**
     * Method to get a list of opt out events by account, time period, and login group
     * @param accountId - Account to get history for
     * @param startDate - Start of time period
     * @param stopDate - End of time period
     * @return List of opt out history
     */
    public List<OverrideHistory> getOptOutHistoryForAccount(int accountId, ReadableInstant reportStartDate, ReadableInstant stopDate, LiteYukonGroup residentialGroup);

    /**
     * Method to get a list of opt out events by the assigning user of the opt out, time period, and login group. 
     */
    public List<OverrideHistory> getOptOutHistoryByLogUserId(int userId, ReadableInstant reportStartDate, ReadableInstant stopDate, LiteYukonGroup residentialGroup);

    /**
	 * Method to get a list of opt out events by inventory and time period
	 * @param inventoryId - Inventory to get history for
	 * @param startDate - Start of time period
	 * @param stopDate - End of time period
	 * @return List of opt out history
	 */
	public List<OverrideHistory> getOptOutHistoryForInventory(int inventoryId, Date startDate, Date stopDate);

    /**
     * Method to get a list of opt out events by inventory, time period, and login group
     * @param inventoryId - Inventory to get history for
     * @param startDate - Start of time period
     * @param stopDate - End of time period
     * @return List of opt out history
     */
	public List<OverrideHistory> getOptOutHistoryForInventory(int inventoryId, ReadableInstant reportStartDate, ReadableInstant stopDate, LiteYukonGroup residentialGroup);
	
	/**
	 * Method to get the last (or current) opt out event that actually happened (ignoring canceled 
	 * scheduled events) for the given inventory and account
	 * @return Last or current event or null if no events
	 */
	public OptOutEvent findLastEvent(int inventoryId, int customerAccountId);
	
	/**
	 * Method to save an OptOutLog
	 * @param optOutLog - Change to be saved
	 */
	public void saveOptOutLog(OptOutLog optOutLog);
	
	/**
	 * Method to get the active (hasn't been canceled) scheduled opt out event if one exists
	 * @param inventoryId - Inventory to get event for
	 * @param customerAccountId - Account to get event for
	 * @return Event or null if none exists
	 */
	public OptOutEvent getScheduledOptOutEvent(int inventoryId, int customerAccountId);
	
	/**
	 * Method to get a list of all currently scheduled opt out events for all accounts for the
	 * energy company
	 * @param energyCompany - Energy company to get opt outs for
	 * @return List of scheduled opt outs
	 */
	public List<OptOutEvent> getAllScheduledOptOutEvents(LiteStarsEnergyCompany energyCompany);
	
	/**
	 * Method to get a list of all currently scheduled opt out events for the account
	 * @param customerAccountId - Account to get opt outs for
	 * @return List of scheduled opt outs
	 */
	public List<OptOutEvent> getAllScheduledOptOutEvents(int customerAccountId);
	
	/**
	 * Method to get a list of all currently active and scheduled opt outs for an account
	 * @param customerAccountId - Account to get opt outs for
	 * @param inventoryId - Inventory to get event for
	 * @return List of events
	 */
	public List<OptOutEventDto> getCurrentOptOuts(int customerAccountId, int inventoryId);
	
	/**
	 * Method to get a list of all currently active and scheduled opt outs for an account
	 * @param customerAccountId - Account to get opt outs for
	 * @return List of events
	 */
	public List<OptOutEventDto> getCurrentOptOuts(int customerAccountId);

	/**
	 * Method to get all current opt out events for a given energy company
	 * @param energyCompany - Company to get opt outs for
	 * @return List of Opt out events
	 */
	public List<OptOutEvent> getAllCurrentOptOuts(LiteStarsEnergyCompany energyCompany);
	
	/**
	 * Method to get all current opt out events for a given energy company.
	 * Limited to those opt outs that tie to inventory that is currently enrolled in a given program.
	 * @param programId
	 * @param energyCompany
	 * @return
	 */
	public List<OptOutEvent> getAllCurrentOptOutsByProgramId(int programId, LiteStarsEnergyCompany energyCompany);
	
	/**
	 * Method to get the total number of opt outs that have been used for a given inventory and 
	 * account for a given time period
	 * 
	 * @param inventoryId - Inventory to count opt outs for
	 * @param customerAccountId - Account to count opt outs for
	 * @param startDate - Start of time period to count opt outs
	 * @param endDate - End of time period to count opt outs
	 * @return Number of opt outs used in time period
	 */
	public Integer getNumberOfOptOutsUsed(int inventoryId, int customerAccountId, Instant startDate, Instant endDate);
	
	/**
	 * Method to get a count of all currently active Opt Outs
	 * @param energyCompany - Company to get opt outs for
	 * @return Count of active Opt Outs
	 */
	public int getTotalNumberOfActiveOptOuts(LiteStarsEnergyCompany energyCompany);

	/**
	 * Method to get a count of all scheduled Opt Outs
	 * @param energyCompany - Company to get opt outs for
	 * @return Count of scheduled Opt Outs
	 */
	public int getTotalNumberOfScheduledOptOuts(LiteStarsEnergyCompany energyCompany);
	
	/**
	 * Method to change the current count state of all active opt outs to the given value
	 * @param energyCompany - Energy company to change state for
	 * @param counts - Count or Don't Count
	 */
	public void changeCurrentOptOutCountState(LiteStarsEnergyCompany energyCompany, OptOutCounts counts);

	/**
	 * Method to change the current count state of all active opt outs to the given value
	 * Only applies to opt outs for inventory for given program id
	 * @param energyCompany - Energy company to change state for
	 * @param counts - Count or Don't Count
	 * @param webpublishingProgramId
	 */
	public void changeCurrentOptOutCountStateForProgramId(LiteStarsEnergyCompany energyCompany, OptOutCounts counts, int webpublishingProgramId);
	
	/**
	 * Method to get the inventory ids of opt outed out inventory for an account in a given time period
	 * @param accountId - Account to get count for
	 * @param startTime - Start of time period
	 * @param stopTime - End of time period
	 * @return Ids of opted out devices
	 */
	public List<Integer> getOptedOutDeviceIdsForAccount(int accountId, Date startTime, Date stopTime);

	/**
	 * Method to get a list of opt outs that are scheduled to start before now.
	 * @return List of scheduled opt outs
	 */
	public List<OptOutEvent> getScheduledOptOutsToBeStarted();

	/**
	 * Method to get a scheduled event that is over due to start (if one exists)
	 * @param inventoryId - Inventory to get event for
	 * @param customerAccountId - Account for inventory
	 * @return Event if there is one
	 */
	public OptOutEvent getOverdueScheduledOptOut(Integer inventoryId, int customerAccountId);
}
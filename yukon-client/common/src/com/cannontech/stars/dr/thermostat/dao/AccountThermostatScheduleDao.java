package com.cannontech.stars.dr.thermostat.dao;

import java.util.List;

import javax.management.InvalidAttributeValueException;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;

public interface AccountThermostatScheduleDao {

	/**
	 * Retrieve the AccountThermostatSchedule with given accountThermostatScheduleId excluding archived entries.
	 */
	public AccountThermostatSchedule getById(int atsId);
	
	/**
	 * Retrieve AccountThermostatSchedule with given accountThermostatScheduleId, accountId excluding archived entries.
	 * Returns null if the AccountThermostatSchedule can not be found.
	 */
	public AccountThermostatSchedule findByIdAndAccountId(int atsId, int accountId);
	
    /**
     * Retrieve AccountThermostatSchedule with given accountThermostatScheduleId and accountId.
     * Returns null if the AccountThermostatSchedule can not be found.
     * 
     * @param includeArchived - true will include archived entries
     */
	public AccountThermostatSchedule findByIdAndAccountId(int acctThermostatScheduleId, int accountId, boolean includeArchived);

	/**
	 * Retrieve AccountThermostatSchedule linked to the given inventoryId excluding archived entries.
	 * Returns null if the AccountThermostatSchedule can not be found.
	 */
	public AccountThermostatSchedule findByInventoryId(int inventoryId);
	
	/**
	 * Delete the AccountThermostatSchedule (and all of its associated date such as entries and account/inventory links) with the given accountThermostatScheduleId.
	 */
	public void deleteById(int atsId);
	
	/**
	 * Deletes all of the thermostat schedule ids supplied.
	 */
    public void deleteByThermostatScheduleIds(Iterable<Integer> thermostatScheduleIds);
	
	/**
	 * Deletes all of the AccountThermostatSchedule (and all of its associated date such as entries and account/inventory links) 
	 * associated with the given accountId.
	 */
	public void deleteAllByAccountId(int accountId);
	
	/**
	 * Delete the AccountThermostatSchedule (and all of its associated date such as entries and account/inventory links) associated with the given inventoryId.
	 */
	public void deleteByInventoryId(int inventoryId);
	
	/**
	 * Get the inventoryIds of all the thermostats associated with the given accountThermostatScheduleId.
	 */
	public List<Integer> getThermostatIdsUsingSchedule(int atsId);
	
	/**
	 * Get all AccountThermostatSchedules for the specified energy company excluding archived entries.
	 */
	public List<AccountThermostatSchedule> getAllThermostatSchedulesForEC(int ecId);
	
	/**
	 * Get the energy company default schedule for the given energyCompanyId and thermostat type excluding archived entries.
	 * If one does not exists it will be created.
	 */
	public AccountThermostatSchedule getEnergyCompanyDefaultScheduleByType(int ecId, SchedulableThermostatType type);
	
	/**
	 * Get the energy company default schedule for the given thermostat type.
	 * energyCompanyId used is determined by accountId.
	 * If one does not exists it will be created.
	 */
	public AccountThermostatSchedule getEnergyCompanyDefaultScheduleByAccountAndType(int accountId, SchedulableThermostatType type);
	
	/**
     * Get a schedule for an account by matching an exact schedule name excluding archived entries.  If there are more than one schedule it will use the 
     * first one although this should never be the case.
     * 
     * @param accountId
     * @param scheduleName
     * 
     * @throws EmptyResultDataAccessException - The schedule name doesn't exist for the given account
     */
    public AccountThermostatSchedule getSchedulesForAccountByScheduleName(int accountId, String scheduleName);

    /**
     * Get a schedule for an account by matching an exact schedule name excluding archived entries.  If there are more than one schedule it will use the 
     * first one although this should never be the case.
     * 
     * @param accountId
     * @param scheduleName
     * 
     */
    public AccountThermostatSchedule findSchedulesForAccountByScheduleName(int accountId, String scheduleName);
    
	/**
	 * Get a schedule for an account by matching an exact schedule name excluding archived entries.
	 * @param accountId
	 * @param scheduleName
	 * @param fragment - pass in any conditions you like eg. say you wanted to check for name uniqueness
	 *                   among other schedules, pass in something like:
	 *                   
	 *                   SqlStatementBuilder sql = new SqlStatementBuilder();
	 *                   sql.append("AND AcctThermostatScheduleId").neq(schedule.getAccountThermostatScheduleId());
	 *                   
	 * @return AccountThermostatSchedule or null
	 */
	public List<AccountThermostatSchedule> getSchedulesForAccountByScheduleName(int accountId, String scheduleName, Integer ignorableScheduleId);
	
    /**
     * Save given AccountThermostatSchedule.<br><br>
     * Will either update or insert the AccountThermostatSchedule and its entries.<br>
     * If the attempt is made to update a schedule that has an event associated with the schedule
     * the current schedule will be archived (AcctThermostatSchedule.Archived = "true") and a
     * new identical schedule will be created.
     * Update: accountThermostatScheduleId >= 0.<br>
     * Insert: accountThermostatScheduleId < 0.
     * @throws InvalidAttributeValueException
     */
	public void save(AccountThermostatSchedule ats);
	
	/**
	 * Creates mapping in the InventoryToAcctThermostatSch table to associated given thermostat inventoryIds to the given accountThermostatScheduleId.
	 */
	public void mapThermostatsToSchedule(List<Integer> thermostatIds, int atsId);
	
	/**
	 * Retrieve list of all AccountThermostatSchedules for a given accountId and thermostat type excluding archived entries.
	 * a.k.a. "all saved schedules for account"
	 */
    public List<AccountThermostatSchedule> getAllSchedulesForAccountByType(int accountId, SchedulableThermostatType type);

    /**
     * Get all allowed thermostat schedules and entries for an Account based on the user excluding archived entries
     */
    public List<AccountThermostatSchedule> getAllAllowedSchedulesAndEntriesForAccountByTypes(int accountId, List<SchedulableThermostatType> types);

    /**
     * Get all allowed thermostat schedules for an Account based on the user excluding archived entries
     * 
     * @param accountId
     * @param types
     * @return
     */
    public List<AccountThermostatSchedule> getAllAllowedSchedulesForAccountByTypes(int accountId, List<SchedulableThermostatType> types);

}

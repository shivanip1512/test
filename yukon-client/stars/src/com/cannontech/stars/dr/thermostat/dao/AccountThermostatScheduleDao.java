package com.cannontech.stars.dr.thermostat.dao;

import java.util.List;

import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;

public interface AccountThermostatScheduleDao {

	/**
	 * Retrieve the AccountThermostatSchedule with given accountThermostatScheduleId.
	 */
	public AccountThermostatSchedule getById(int atsId);
	
	/**
	 * Retrieve AccountThermostatSchedule with given accountThermostatScheduleId and accountId.
	 * Returns null if the AccountThermostatSchedule can not be found.
	 */
	public AccountThermostatSchedule findByIdAndAccountId(int atsId, int accountId);

	/**
	 * Retrieve AccountThermostatSchedule linked to the given inventoryId.
	 * Returns null if the AccountThermostatSchedule can not be found.
	 */
	public AccountThermostatSchedule findByInventoryId(int inventoryId);
	
	/**
	 * Delete the AccountThermostatSchedule (and all of its associated date such as entries and account/inventory links) with the given accountThermostatScheduleId.
	 */
	public void deleteById(int atsId);
	
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
	 * Get all AccountThermostatSchedules for the specified energy company.
	 */
	public List<AccountThermostatSchedule> getAllThermostatSchedulesForEC(int ecId);
	
	/**
	 * Get the energy company default schedule for the given energyCompanyId and thermostat type.
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
	 * Save given AccountThermostatSchedule.<br><br>
	 * Will either update or insert the AccountThermostatSchedule and its entries.<br>
	 * Update: accountThermostatScheduleId >= 0.<br>
	 * Insert: accountThermostatScheduleId < 0.
	 */
	public void save(AccountThermostatSchedule ats);
	
	/**
	 * Creates mapping in the InventoryToAcctThermostatSch table to associated given thermostat inventoryIds to the given accountThermostatScheduleId.
	 */
	public void mapThermostatsToSchedule(List<Integer> thermostatIds, int atsId);
	
	/**
	 * Convenience method to save and map thermostats to schedule.
	 */
	public void saveAndMapToThermostats(AccountThermostatSchedule ats, List<Integer> thermostatIds);
	
	/**
	 * Retrieve list of all AccountThermostatSchedules for a given accountId and thermostat type.
	 * a.k.a. "all saved schedules for account"
	 */
	public List<AccountThermostatSchedule> getAllSchedulesForAccountByType(int accountId, SchedulableThermostatType type);

	/**
	 * Get the AccountThermostatSchedules for an Account based on the user.
	 */
    public List<AccountThermostatSchedule> getAllAllowedSchedulesAndEntriesForAccountByTypes(int accountId,
                                                                                            List<SchedulableThermostatType> types);
}

package com.cannontech.stars.dr.thermostat.dao;

import java.util.List;

import javax.management.InvalidAttributeValueException;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;

public interface AccountThermostatScheduleDao {

    /**
     * Retrieve the AccountThermostatSchedule with given accountThermostatScheduleId excluding archived
     * entries.
     */
    AccountThermostatSchedule getById(int atsId);

    /**
     * Retrieve AccountThermostatSchedule with given accountThermostatScheduleId, accountId excluding archived
     * entries.
     * Returns null if the AccountThermostatSchedule can not be found.
     */
    AccountThermostatSchedule findByIdAndAccountId(int atsId, int accountId);

    /**
     * Retrieve AccountThermostatSchedule with given accountThermostatScheduleId and accountId.
     * Returns null if the AccountThermostatSchedule can not be found.
     * 
     * @param includeArchived - true will include archived entries
     */
    AccountThermostatSchedule findByIdAndAccountId(int acctThermostatScheduleId, int accountId, boolean includeArchived);

    /**
     * Retrieve AccountThermostatSchedule linked to the given inventoryId excluding archived entries.
     * Returns null if the AccountThermostatSchedule can not be found.
     */
    AccountThermostatSchedule findByInventoryId(int inventoryId);

    /**
     * Delete the AccountThermostatSchedule (and all of its associated date such as entries and
     * account/inventory links) with the given accountThermostatScheduleId.
     */
    void deleteById(int atsId);

    /**
     * Deletes all of the AccountThermostatSchedule (and all of its associated date such as entries and
     * account/inventory links)
     * associated with the given accountId.
     */
    void deleteAllByAccountId(int accountId);

    /**
     * Delete the AccountThermostatSchedule (and all of its associated date such as entries and
     * account/inventory links) associated with the given inventoryId.
     */
    void deleteByInventoryId(int inventoryId);

    /**
     * Get the inventoryIds of all the thermostats associated with the given accountThermostatScheduleId.
     */
    List<Integer> getThermostatIdsUsingSchedule(int atsId);

    /**
     * Get all AccountThermostatSchedules for the specified energy company excluding archived entries.
     */
    List<AccountThermostatSchedule> getAllThermostatSchedulesForEC(int ecId);

    /**
     * Get the energy company default schedule for the given energyCompanyId and thermostat type excluding
     * archived entries.
     * If one does not exists it will be created.
     */
    AccountThermostatSchedule getEnergyCompanyDefaultScheduleByType(int ecId, SchedulableThermostatType type);

    /**
     * Get the energy company default schedule for the given thermostat type.
     * energyCompanyId used is determined by accountId.
     * If one does not exists it will be created.
     */
    AccountThermostatSchedule getEnergyCompanyDefaultScheduleByAccountAndType(int accountId,
            SchedulableThermostatType type);

    /**
     * Get a schedule for an account by matching an exact schedule name excluding archived entries. If there
     * are more than one schedule it will use the
     * first one although this should never be the case.
     * 
     * @param accountId
     * @param scheduleName
     * 
     * @throws EmptyResultDataAccessException - The schedule name doesn't exist for the given account
     */
    AccountThermostatSchedule getSchedulesForAccountByScheduleName(int accountId, String scheduleName);

    /**
     * Get a schedule for an account by matching an exact schedule name excluding archived entries. If there
     * are more than one schedule it will use the
     * first one although this should never be the case.
     * 
     * @param accountId
     * @param scheduleName
     * 
     */
    AccountThermostatSchedule findSchedulesForAccountByScheduleName(int accountId, String scheduleName);

    /**
     * Get a schedule for an account by matching an exact schedule name excluding archived entries.
     * 
     * @param accountId
     * @param scheduleName
     * @param fragment - pass in any conditions you like eg. say you wanted to check for name uniqueness
     *        among other schedules, pass in something like:
     * 
     *        SqlStatementBuilder sql = new SqlStatementBuilder();
     *        sql.append("AND AcctThermostatScheduleId").neq(schedule.getAccountThermostatScheduleId());
     * 
     * @return AccountThermostatSchedule or null
     */
    List<AccountThermostatSchedule> getSchedulesForAccountByScheduleName(int accountId, String scheduleName,
            Integer ignorableScheduleId);

    /**
     * Save given AccountThermostatSchedule.<br>
     * <br>
     * Will either update or insert the AccountThermostatSchedule and its entries.<br>
     * If the attempt is made to update a schedule that has an event associated with the schedule
     * the current schedule will be archived (AcctThermostatSchedule.Archived = "true") and a
     * new identical schedule will be created.
     * Update: accountThermostatScheduleId >= 0.<br>
     * Insert: accountThermostatScheduleId < 0.
     * 
     * @throws InvalidAttributeValueException
     */
    void save(AccountThermostatSchedule ats);

    /**
     * Creates mapping in the InventoryToAcctThermostatSch table to associated given thermostat inventoryIds
     * to the given accountThermostatScheduleId.
     */
    void mapThermostatsToSchedule(List<Integer> thermostatIds, int atsId);

    /**
     * Remove the mapping between inventory and any account thermostat schedules. Used when moving a piece of
     * inventory
     * from an account to warehouse, since inventory in the warehouse (i.e. not assigned to an account) should
     * not have
     * ties to a specific account schedule.
     */
    void unmapThermostatsFromSchedules(List<Integer> thermostatIds);

    /**
     * Retrieve list of all AccountThermostatSchedules for a given accountId and thermostat type excluding
     * archived entries.
     * a.k.a. "all saved schedules for account"
     */
    List<AccountThermostatSchedule> getAllSchedulesForAccountByType(int accountId, SchedulableThermostatType type);

    /**
     * Get all allowed thermostat schedules and entries for an Account based on the user excluding archived
     * entries
     */
    List<AccountThermostatSchedule> getAllAllowedSchedulesAndEntriesForAccountByTypes(int accountId,
            List<SchedulableThermostatType> types);

    /**
     * Get all allowed thermostat schedules for an Account based on the user excluding archived entries
     * 
     * @param accountId
     * @param types
     * @return
     */
    List<AccountThermostatSchedule> getAllAllowedSchedulesForAccountByTypes(int accountId,
            List<SchedulableThermostatType> types);

}

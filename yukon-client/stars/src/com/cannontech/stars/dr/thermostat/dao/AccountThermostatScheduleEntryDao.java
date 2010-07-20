package com.cannontech.stars.dr.thermostat.dao;

import java.util.List;

import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;

public interface AccountThermostatScheduleEntryDao {

	/**
	 * Retrieve an AccountThermostatScheduleEntry by its accountThermostatScheduleEntryId.<br><br>
	 * <b><i>WARNING</i></b>: Intended only for use by the AccountThermostatScheduleDao. Individual entries should never need to be retrieved by client code.
	 */
	public AccountThermostatScheduleEntry getById(int atsEntryId);

	/**
	 * Retrieve a list of all AccountThermostatScheduleEntrys for a given AccountThermostatSchedule id.<br><br>
	 * <b><i>WARNING</i></b>: Intended only for use by the AccountThermostatScheduleDao. AccountThermostatScheduleEntrys should never need to be retrieved by client code.
	 */
	public List<AccountThermostatScheduleEntry> getAllEntriesForSchduleId(int atsId);
	
	/**
	 * Save given AccountThermostatScheduleEntry.<br><br>
	 * <b><i>WARNING</i></b>: Intended only for use by the AccountThermostatScheduleDao. AccountThermostatScheduleEntrys should never need to be saved by client code.<br><br>
	 * Will either update or insert the AccountThermostatScheduleEntry.<br>
	 * Update: accountThermostatScheduleEntryId > 0.
	 * Insert: accountThermostatScheduleEntryId <= 0.
	 */
	public void save(AccountThermostatScheduleEntry atsEntry);
	
	/**
	 * Delete all AccountThermostatScheduleEntry for a given AccountThermostatSchedule id.<br><br>
	 * <b><i>WARNING</i></b>: Intended only for use by the AccountThermostatScheduleDao. AccountThermostatScheduleEntrys should never need to be deleted by client code.
	 */
	public void removeAllEntriesForScheduleId(int atsId);
}

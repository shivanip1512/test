package com.cannontech.stars.dr.thermostat.dao;

import java.util.List;

import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;

public interface AccountThermostatScheduleEntryDao {

    /**
     * Retrieve a list of all AccountThermostatScheduleEntrys for a given AccountThermostatSchedule id.<br>
     * <br>
     * <b><i>WARNING</i></b>: Intended only for use by the AccountThermostatScheduleDao.
     * AccountThermostatScheduleEntrys should never need to be retrieved by client code.
     */
    List<AccountThermostatScheduleEntry> getAllEntriesForSchduleId(int atsId);

    /**
     * Save given AccountThermostatScheduleEntry.<br>
     * <br>
     * <b><i>WARNING</i></b>: Intended only for use by the AccountThermostatScheduleDao.
     * AccountThermostatScheduleEntrys should never need to be saved by client code.<br>
     * <br>
     * Will either update or insert the AccountThermostatScheduleEntry.<br>
     * Update: accountThermostatScheduleEntryId > 0.
     * Insert: accountThermostatScheduleEntryId <= 0.
     */
    void save(AccountThermostatScheduleEntry atsEntry);

    /**
     * Delete all AccountThermostatScheduleEntry for a given AccountThermostatSchedule id.<br>
     * <br>
     * <b><i>WARNING</i></b>: Intended only for use by the AccountThermostatScheduleDao.
     * AccountThermostatScheduleEntrys should never need to be deleted by client code.
     */
    void removeAllEntriesForScheduleId(int atsId);
}

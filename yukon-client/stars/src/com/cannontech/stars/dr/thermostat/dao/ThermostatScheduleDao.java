package com.cannontech.stars.dr.thermostat.dao;

import java.util.List;

import com.cannontech.stars.dr.hardware.model.HardwareType;
import com.cannontech.stars.dr.thermostat.model.ScheduleDropDownItem;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedule;

/**
 * Data Access interface for thermostat schedules
 */
public interface ThermostatScheduleDao {

    /**
     * Method to get the default thermostat schedule for an energy company based
     * on account id and thermostat type
     * @param accountId - Id of account to get energy company for
     * @param type - Type of thermostat to get default schedule for
     * @return The default schedule for the energy company
     */
    public ThermostatSchedule getEnergyCompanyDefaultSchedule(int accountId,
            HardwareType type);

    /**
     * Method to get the thermostat schedule for a piece of inventory
     * @param inventoryId - Id of inventory
     * @return The schedule for that inventory
     */
    public ThermostatSchedule getThermostatScheduleByInventoryId(int inventoryId);

    /**
     * Method to get the thermostat schedule
     * @param scheduleId - Id of schedule
     * @param accountId - Id of current user's account
     * @return The schedule
     */
    public ThermostatSchedule getThermostatScheduleById(int scheduleId, int accountId);

    /**
     * Method to get a list of currently saved schedules for an account and thermostat type
     * @param accountId - Id of account to get schedules for
     * @param type - Type of thermostat to get schedules for
     * @return List of schedules
     */
    public List<ScheduleDropDownItem> getSavedThermostatSchedulesByAccountId(
            int accountId, HardwareType type);

    /**
     * Method to get a list of currently saved schedules for an account
     * @param accountId - Id of account to get schedules for
     * @return List of schedules
     */
    public List<ScheduleDropDownItem> getSavedThermostatSchedulesByAccountId(
    		int accountId);

    /**
     * Method to save a thermostat schedule
     * @param schedule - Schedule to save
     */
    public void save(ThermostatSchedule schedule);
    
    /**
     * Method to delete a thermostat schedule
     * @param scheduleId - Id of schedule to delete
     */
    public void delete(int scheduleId);
    
    /**
     * Method to get the inventory ids that the given schedules are associated with
     * @param scheduleId - Ids of schedules in question
     * @return - list inventory ids for schedules
     */
    public List<Integer> getInventoryIdsForSchedules(Integer... scheduleId);

}

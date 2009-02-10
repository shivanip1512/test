package com.cannontech.stars.dr.thermostat.dao;

import java.util.List;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
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
     * Method to get the default thermostat schedule for an energy company and thermostat type. This
     * method will create a new default schedule for the energy company/type if one does not already
     * exist
     * @param energyCompany - Energy company to get default schedule for
     * @param type - Type of thermostat to get schedule for
     * @return Default schedule
     */
    public ThermostatSchedule getEnergyCompanyDefaultSchedule(LiteStarsEnergyCompany energyCompany,
    		HardwareType type);

    /**
     * Method to get a copy of the energy company default schedule for the given company and
     * hardware type
     * @param energyCompany - Energy company to get default for
     * @param type - Hardware type to get default for
     * @return A copy of the energy company's default schedule for the hardware type
     */
    public ThermostatSchedule getCopyOfEnergyCompanyDefaultSchedule(
    		LiteStarsEnergyCompany energyCompany,
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
     * @param energyCompany - Energy company for schedule
     */
    public void save(ThermostatSchedule schedule, LiteStarsEnergyCompany energyCompany);

    /**
     * Method to save an energy company default thermostat schedule
     * @param schedule - Schedule to save
     * @param energyCompany - Energy company for schedule
     */
    public void saveDefaultSchedule(
    		ThermostatSchedule schedule, LiteStarsEnergyCompany energyCompany);
    
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

    /**
     * Method to delete events from LMThermostatManualEvent for an inventory id.
     * @param inventoryId
     */
    public void deleteManualEvents(Integer inventoryId);

    /**
     * Method to retrieve a list of manual event ids for an inventory id.
     * @param inventoryId
     * @return
     */
    public List<Integer> getAllManualEventIds(Integer inventoryId);

    /**
     * Method to delete schedules for an account.
     * @param accountId
     */
    public void deleteSchedulesForAccount(Integer accountId);

    /**
     * Method to get a list of schedule ids for an account.
     * @param accountId
     * @return
     */
    public List<Integer> getScheduleIdsForAccount(Integer accountId);

    /**
     * Method to delete schedules for an inventory id.
     * @param inventoryId
     */
    public void deleteScheduleForInventory(Integer inventoryId);

}

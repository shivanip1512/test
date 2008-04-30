package com.cannontech.stars.dr.thermostat.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.CustomerAction;
import com.cannontech.stars.dr.hardware.model.CustomerEventType;
import com.cannontech.stars.dr.hardware.model.HardwareType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.dao.CustomerEventDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.CustomerThermostatEventBase;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEventResult;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.stars.dr.thermostat.model.ThermostatSeason;
import com.cannontech.stars.dr.thermostat.model.ThermostatSeasonEntry;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.user.YukonUserContext;

/**
 * Implementation class for ThermostatService
 */
public class ThermostatServiceImpl implements ThermostatService {

    private Logger logger = YukonLogManager.getLogger(ThermostatServiceImpl.class);
    private CustomerEventDao customerEventDao;
    private InventoryDao inventoryDao;
    private ECMappingDao ecMappingDao;
    private ThermostatScheduleDao thermostatScheduleDao;

    private SimpleDateFormat SCHEDULE_START_TIME_FORMAT = new SimpleDateFormat("HH:mm");

    @Autowired
    public void setCustomerEventDao(CustomerEventDao customerEventDao) {
        this.customerEventDao = customerEventDao;
    }

    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }

    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }

    @Autowired
    public void setThermostatScheduleDao(
            ThermostatScheduleDao thermostatScheduleDao) {
        this.thermostatScheduleDao = thermostatScheduleDao;
    }

    @Override
    public ThermostatManualEventResult executeManualEvent(
            CustomerAccount account, ThermostatManualEvent event,
            YukonUserContext userContext) {

        Integer thermostatId = event.getThermostatId();
        Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        LiteYukonUser yukonUser = userContext.getYukonUser();

        // Make sure the device is available
        if (!thermostat.isAvailable()) {

            ThermostatManualEventResult error;
            if (StarsUtils.isOperator(yukonUser)) {
                error = ThermostatManualEventResult.OPERATOR_UNAVAILABLE_ERROR;
            } else {
                error = ThermostatManualEventResult.CONSUMER_MANUAL_ERROR;
            }
            return error;

        }

        // Build command to send to thermostat
        String command = this.buildManualEventCommand(thermostat, event);

        // Send command to thermostat
        int routeID = thermostat.getRouteId();
        try {
            ServerUtils.sendSerialCommand(command, routeID, yukonUser);
        } catch (WebClientException e) {
            logger.error("Thermostat manual event failed.", e);
            return ThermostatManualEventResult.CONSUMER_MANUAL_ERROR;
        }

        // Save event
        customerEventDao.save(event);

        // Log manual event into activity log
        this.logManualEventActivity(thermostat,
                                    event,
                                    yukonUser.getUserID(),
                                    account.getAccountId(),
                                    account.getCustomerId());

        return ThermostatManualEventResult.CONSUMER_MANUAL_SUCCESS;

    }

    @Override
    public ThermostatSchedule getThermostatSchedule(Thermostat thermostat,
            CustomerAccount account) {

        Integer thermostatId = thermostat.getId();
        ThermostatSchedule schedule = thermostatScheduleDao.getThermostatScheduleByInventoryId(thermostatId);

        if (schedule == null) {
            HardwareType type = thermostat.getType();
            schedule = thermostatScheduleDao.getEnergyCompanyDefaultSchedule(account.getAccountId(),
                                                                             type);
        }

        return schedule;
    }

    @Override
    public void updateSchedule(CustomerAccount account,
            ThermostatSchedule schedule, ThermostatMode mode,
            TimeOfWeek timeOfWeek, boolean applyToAll,
            YukonUserContext userContext) {

        Integer thermostatId = schedule.getInventoryId();
        Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);

        // Get the existing (or default in none exists) schedule for the
        // thermostat and save the changes
        ThermostatSchedule scheduleToSave = this.getScheduleToSave(account,
                                                                   thermostat,
                                                                   schedule,
                                                                   mode,
                                                                   timeOfWeek,
                                                                   applyToAll);
        thermostatScheduleDao.save(scheduleToSave);

    }

    @Override
    public ThermostatScheduleUpdateResult sendSchedule(CustomerAccount account,
            ThermostatSchedule schedule, ThermostatMode mode,
            TimeOfWeek timeOfWeek, boolean applyToAll,
            YukonUserContext userContext) {

        Integer thermostatId = schedule.getInventoryId();
        Thermostat thermostat = inventoryDao.getThermostatById(thermostatId);
        LiteYukonUser yukonUser = userContext.getYukonUser();

        // Make sure the device is available
        if (!thermostat.isAvailable()) {

            ThermostatScheduleUpdateResult error;
            if (StarsUtils.isOperator(yukonUser)) {
                error = ThermostatScheduleUpdateResult.OPERATOR_UNAVAILABLE_ERROR;
            } else {
                error = ThermostatScheduleUpdateResult.CONSUMER_UPDATE_SCHEDULE_ERROR;
            }
            return error;

        }

        // Make sure the device has a serial number
        String serialNumber = thermostat.getSerialNumber();
        if (StringUtils.isBlank(serialNumber)) {

            ThermostatScheduleUpdateResult error;
            if (StarsUtils.isOperator(yukonUser)) {
                error = ThermostatScheduleUpdateResult.OPERATOR_NO_SERIAL_ERROR;
            } else {
                error = ThermostatScheduleUpdateResult.CONSUMER_NO_SERIAL_ERROR;
            }
            return error;
        }

        // Build the command to send to the thermostat
        String updateScheduleCommand = this.buildUpdateScheduleCommand(thermostat,
                                                                       schedule,
                                                                       mode,
                                                                       timeOfWeek,
                                                                       applyToAll);

        // *****************************************************
        // TODO Change sending request to use new CommandRequest
        // *****************************************************
        // Send command to thermostat
        int routeID = thermostat.getRouteId();
        try {
            ServerUtils.sendSerialCommand(updateScheduleCommand,
                                          routeID,
                                          yukonUser);

        } catch (WebClientException e) {
            logger.error("Failed to update thermostat schedules.", e);
            return ThermostatScheduleUpdateResult.CONSUMER_UPDATE_SCHEDULE_ERROR;
        }
        // *****************************************************
        // TODO Change sending request to use new CommandRequest
        // *****************************************************

        // Log the schedule update in the activity log
        LiteStarsEnergyCompany energyCompany = ecMappingDao.getCustomerAccountEC(account);

        ThermostatSeason season = schedule.getSeason(mode);
        List<ThermostatSeasonEntry> entryList = season.getSeasonEntries(timeOfWeek);
        this.logUpdateScheduleActivity(thermostat,
                                       entryList,
                                       mode,
                                       timeOfWeek,
                                       yukonUser.getUserID(),
                                       account.getAccountId(),
                                       account.getCustomerId(),
                                       energyCompany.getEnergyCompanyID());

        // Save update schedule event
        CustomerThermostatEventBase event = new CustomerThermostatEventBase();
        event.setAction(CustomerAction.PROGRAMMING);
        event.setEventType(CustomerEventType.HARDWARE);
        event.setThermostatId(thermostatId);

        customerEventDao.save(event);

        return ThermostatScheduleUpdateResult.CONSUMER_UPDATE_SCHEDULE_SUCCESS;

    }

    /**
     * Helper method to build a command string for the manual event
     * @param thermostat - Thermostat to send command to
     * @param event - Event for the command
     * @return The command string
     */
    private String buildManualEventCommand(Thermostat thermostat,
            ThermostatManualEvent event) {

        StringBuilder commandString = new StringBuilder();

        if (thermostat.isTwoWay()) {
            commandString.append("putconfig epro setstate ");
        } else {
            commandString.append("putconfig xcom setstate ");
        }

        ThermostatMode mode = event.getMode();
        if (mode != null) {
            String modeString = mode.getCommandString();
            commandString.append(" system ");
            commandString.append(modeString);
        }

        if (event.isRunProgram()) {
            // Run scheduled program
            commandString.append(" run");
        } else {
            // Set manual values
            Integer temperature = event.getPreviousTemperature();
            commandString.append(" temp ");
            commandString.append(temperature);

            ThermostatFanState fanState = event.getFanState();
            if (fanState != null) {
                String fanStatString = fanState.getCommandString();
                commandString.append(" fan ");
                commandString.append(fanStatString);
            }

            if (event.isHoldTemperature()) {
                commandString.append(" hold");
            }
        }

        String serialNumber = thermostat.getSerialNumber();
        commandString.append(" serial ");
        commandString.append(serialNumber);

        return commandString.toString();
    }

    /**
     * Helper method to build a command string for updating schedule (creates a
     * command for only the mode and time of week specified)
     * @param thermostat - Thermostat to update
     * @param schedule - Full thermostat schedule
     * @param mode - Thermostat mode for schedule to update
     * @param timeOfWeek - Time of week for schedule to update
     * @param applyToAll - True if schedule should be applied to all days
     * @return Command string in format:
     *         <p>
     *         putconfig xcom schedule weekday 01:30, ff, 72, 08:00 ff, 72,
     *         15:00, ff, 72, 20:00, ff, 72 serial 1234567
     *         </p>
     */
    private String buildUpdateScheduleCommand(Thermostat thermostat,
            ThermostatSchedule schedule, ThermostatMode mode,
            TimeOfWeek timeOfWeek, boolean applyToAll) {

        ThermostatSeason season = schedule.getSeason(mode);
        List<ThermostatSeasonEntry> seasonEntries = season.getSeasonEntries(timeOfWeek);

        StringBuilder commandString = new StringBuilder();

        if (thermostat.isTwoWay()) {
            commandString.append("putconfig epro schedule ");
        } else {
            commandString.append("putconfig xcom schedule ");
        }

        String timeOfWeekCommand = timeOfWeek.getCommandString();
        if (applyToAll) {
            commandString.append("all ");
        } else {
            commandString.append(timeOfWeekCommand + " ");
        }

        int count = 1;
        for (ThermostatSeasonEntry entry : seasonEntries) {

            Date startDate = entry.getStartDate();

            Integer temperature = entry.getTemperature();

            if (temperature == -1) {
                // temp of -1 means ignore this time/temp pair - used when only
                // sending two time/temp values
                commandString.append("HH:MM,");
                commandString.append("ff,");
                commandString.append("ff");

            } else {

                String startTimeString = SCHEDULE_START_TIME_FORMAT.format(startDate);
                commandString.append(startTimeString + ",");

                if (mode.equals(ThermostatMode.COOL)) {
                    // Default heat temp
                    commandString.append("ff,");

                    // Cool temp
                    commandString.append(temperature);
                } else {
                    // Heat temp
                    commandString.append(temperature + ",");

                    // Default cool temp
                    commandString.append("ff");
                }
            }

            // No trailing comma on the last season entry cool temp
            if (count++ != seasonEntries.size()) {
                commandString.append(", ");
            }

        }

        commandString.append(" serial " + thermostat.getSerialNumber());

        return commandString.toString();
    }

    /**
     * Helper method to create the schedule that should be saved. Start with the
     * existing schedule (or the energy company default if there is no
     * existing), then update the settings that are being changed with the
     * current update
     * @param account - Account for thermostat
     * @param thermostat - Thermostat to update schedule for
     * @param updatedSchedule - The schedule containing the updates
     * @param mode - The mode to be updated
     * @param timeOfWeek - Time of week to be updated
     * @param applyToWeekend - True if updates apply to saturday and sunday
     * @return The updated schedule to be saved
     */
    private ThermostatSchedule getScheduleToSave(CustomerAccount account,
            Thermostat thermostat, ThermostatSchedule updatedSchedule,
            ThermostatMode mode, TimeOfWeek timeOfWeek, boolean applyToWeekend) {

        // Get the current (or default) schedule for the thermostat
        ThermostatSchedule schedule = this.getThermostatSchedule(thermostat,
                                                                 account);
        schedule.setAccountId(account.getAccountId());
        schedule.setInventoryId(thermostat.getId());
        schedule.setThermostatType(thermostat.getType());

        // Get the season that is being updated
        ThermostatSeason updatedSeason = updatedSchedule.getSeason(mode);

        // Update the season entries for the given time of week
        ThermostatSeason season = schedule.getSeason(mode);
        List<ThermostatSeasonEntry> updatedEntries = updatedSeason.getSeasonEntries(timeOfWeek);
        List<ThermostatSeasonEntry> entries = season.getSeasonEntries(timeOfWeek);
        this.updateScheduleEntries(updatedEntries, entries);

        if (applyToWeekend) {
            // Update saturday entries
            List<ThermostatSeasonEntry> saturdayEntries = season.getSeasonEntries(TimeOfWeek.SATURDAY);
            this.updateScheduleEntries(updatedEntries, saturdayEntries);

            // Update sunday entries
            List<ThermostatSeasonEntry> sundayEntries = season.getSeasonEntries(TimeOfWeek.SUNDAY);
            this.updateScheduleEntries(updatedEntries, sundayEntries);

        }

        return schedule;
    }

    /**
     * Helper method to update time/temp values from a list of entries to
     * another
     * @param updatedEntries - Updated time/temp values
     * @param entries - Current time/temp values
     */
    private void updateScheduleEntries(
            List<ThermostatSeasonEntry> updatedEntries,
            List<ThermostatSeasonEntry> entries) {

        // Update the season entries
        for (int i = 0; i < updatedEntries.size(); i++) {

            ThermostatSeasonEntry entry = updatedEntries.get(i);
            ThermostatSeasonEntry originalEntry = entries.get(i);

            Integer startTime = entry.getStartTime();
            originalEntry.setStartTime(startTime);

            Integer temperature = entry.getTemperature();
            originalEntry.setTemperature(temperature);
        }
    }

    /**
     * Helper method to log the manual event to the activity log
     * @param thermostat - Thermostat for manual event
     * @param event - Event that happened
     * @param userId - Id of user submitting the event
     * @param accountId - Account id for the thermostat
     * @param customerId - Customer id for the thermostat
     */
    private void logManualEventActivity(Thermostat thermostat,
            ThermostatManualEvent event, int userId, int accountId,
            int customerId) {

        String tempUnit = event.getTemperatureUnit();

        StringBuilder logMsg = new StringBuilder("Serial #: " + thermostat.getSerialNumber());
        logMsg.append(", Mode:" + event.getMode());

        if (event.isRunProgram()) {
            logMsg.append(", Run Program");
        } else {
            logMsg.append(", Temp:" + event.getPreviousTemperatureForUnit() + tempUnit);
            if (event.isHoldTemperature()) {
                logMsg.append(" (HOLD)");
            }
            logMsg.append(", Fan: " + event.getFanState());
        }

        LiteStarsEnergyCompany energyCompany = ecMappingDao.getInventoryEC(thermostat.getId());
        Integer energyCompanyID = energyCompany.getEnergyCompanyID();

        ActivityLogger.logEvent(userId,
                                accountId,
                                energyCompanyID,
                                customerId,
                                ActivityLogActions.THERMOSTAT_MANUAL_ACTION,
                                logMsg.toString());
    }

    /**
     * Helper method to log an update schedule activity
     * @param thermostat - Thermsotat that was updated
     * @param entryList - List of season entries that were sent to thermostat
     * @param mode - Heating / Cooling mode of update
     * @param timeOfWeek - Time of week update applies to
     * @param userId - Id of user that did update
     * @param accountId - Account for updated thermostat
     * @param customerId - Customer for updated Thermsotat
     * @param energyCompanyId - Energy company for updated thermostat
     */
    private void logUpdateScheduleActivity(Thermostat thermostat,
            List<ThermostatSeasonEntry> entryList, ThermostatMode mode,
            TimeOfWeek timeOfWeek, int userId, int accountId, int customerId,
            int energyCompanyId) {

        String tempUnit = "F";

        StringBuilder logMessage = new StringBuilder();
        logMessage.append("Serial #:" + thermostat.getSerialNumber() + ", ");
        logMessage.append("Mode:" + mode.toString() + ", ");
        logMessage.append("Day:" + timeOfWeek.toString() + ", ");

        if (HardwareType.COMMERCIAL_EXPRESSSTAT.equals(thermostat.getType())) {

            ThermostatSeasonEntry occupiedEntry = entryList.get(0);
            Date occupiedStart = occupiedEntry.getStartDate();
            String occupiedDate = SCHEDULE_START_TIME_FORMAT.format(occupiedStart);
            Integer occupiedTemp = occupiedEntry.getTemperature();
            logMessage.append("Occupied:" + occupiedDate + "," + occupiedTemp + tempUnit + ", ");

            ThermostatSeasonEntry unOccupiedEntry = entryList.get(3);
            Date unOccupiedStart = unOccupiedEntry.getStartDate();
            String unOccupiedDate = SCHEDULE_START_TIME_FORMAT.format(unOccupiedStart);
            Integer unOccupiedTemp = unOccupiedEntry.getTemperature();
            logMessage.append("Unoccupied:" + unOccupiedDate + "," + unOccupiedTemp + tempUnit);

        } else {

            ThermostatSeasonEntry wakeEntry = entryList.get(0);
            Date wakeStart = wakeEntry.getStartDate();
            String wakeDate = SCHEDULE_START_TIME_FORMAT.format(wakeStart);
            Integer wakeTemp = wakeEntry.getTemperature();
            logMessage.append("Wake:" + wakeDate + "," + wakeTemp + tempUnit + ", ");

            ThermostatSeasonEntry leaveEntry = entryList.get(1);
            Date leaveStart = leaveEntry.getStartDate();
            String leaveDate = SCHEDULE_START_TIME_FORMAT.format(leaveStart);
            Integer leaveTemp = leaveEntry.getTemperature();
            logMessage.append("Leave:" + leaveDate + "," + leaveTemp + tempUnit + ", ");

            ThermostatSeasonEntry returnEntry = entryList.get(2);
            Date returnStart = returnEntry.getStartDate();
            String returnDate = SCHEDULE_START_TIME_FORMAT.format(returnStart);
            Integer returnTemp = returnEntry.getTemperature();
            logMessage.append("Return:" + returnDate + "," + returnTemp + tempUnit + ", ");

            ThermostatSeasonEntry sleepEntry = entryList.get(3);
            Date sleepStart = sleepEntry.getStartDate();
            String sleepDate = SCHEDULE_START_TIME_FORMAT.format(sleepStart);
            Integer sleepTemp = sleepEntry.getTemperature();
            logMessage.append("Sleep:" + sleepDate + "," + sleepTemp + tempUnit + ", ");

        }

        ActivityLogger.logEvent(userId,
                                accountId,
                                energyCompanyId,
                                customerId,
                                ActivityLogActions.THERMOSTAT_SCHEDULE_ACTION,
                                logMessage.toString());
    }
}

package com.cannontech.stars.dr.thermostat.service.impl;

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
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.dao.ManualEventDao;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEventResult;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
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
    private ManualEventDao manualEventDao;
    private InventoryDao inventoryDao;
    private ECMappingDao ecMappingDao;

    @Autowired
    public void setManualEventDao(ManualEventDao manualEventDao) {
        this.manualEventDao = manualEventDao;
    }

    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }

    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
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
        String command = this.buildCommand(thermostat, event);

        // Send command to thermostat
        int routeID = thermostat.getRouteId();
        try {
            ServerUtils.sendSerialCommand(command, routeID, yukonUser);
        } catch (WebClientException e) {
            logger.error("Thermostat manual event failed.", e);
            return ThermostatManualEventResult.CONSUMER_MANUAL_ERROR;
        }

        // Save event
        manualEventDao.save(event);

        // Log manual event into activity log
        this.logManualEventActivity(thermostat,
                                    event,
                                    yukonUser.getUserID(),
                                    account.getAccountId(),
                                    account.getCustomerId());

        return ThermostatManualEventResult.CONSUMER_MANUAL_SUCCESS;

    }

    /**
     * Helper method to build a command string for the manual event
     * @param thermostat - Thermostat to send command to
     * @param event - Event for the command
     * @return The command string
     */
    private String buildCommand(Thermostat thermostat,
            ThermostatManualEvent event) {

        StringBuilder commandString = new StringBuilder();

        if (thermostat.isTwoWay()) {
            commandString.append("putconfig epro setstate");
        } else {
            commandString.append("putconfig xcom setstate");
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
}

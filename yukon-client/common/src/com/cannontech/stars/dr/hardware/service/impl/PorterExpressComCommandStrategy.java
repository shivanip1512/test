package com.cannontech.stars.dr.hardware.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.hardware.model.LmCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandParam;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.hardware.service.HardwareStrategyType;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandRequestExecutor;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandStrategy;
import com.cannontech.stars.dr.thermostat.dao.CustomerEventDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.collect.Lists;

public class PorterExpressComCommandStrategy implements LmHardwareCommandStrategy {
    
    private static final Logger log = YukonLogManager.getLogger(PorterExpressComCommandStrategy.class);

    @Autowired private InventoryDao inventoryDao;
    @Autowired private LmHardwareCommandRequestExecutor executor;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private PorterExpressComCommandBuilder xcomCommandBuilder;
    @Autowired private CustomerEventDao customerEventDao;
    @Autowired private EnergyCompanyRolePropertyDao ecRolePropertyDao;
    @Autowired private YukonEnergyCompanyService yecService;

    @Override
    public void doManualAdjustment(ThermostatManualEvent event, Thermostat thermostat, LiteYukonUser user) 
    throws CommandCompletionException {
        
        String command = xcomCommandBuilder.buildManualCommand(thermostat, event);
        executor.execute(thermostat.getId(), command, user);
    }

    @Override
    public ThermostatScheduleUpdateResult doScheduleUpdate(CustomerAccount account, 
                                                           AccountThermostatSchedule ats,
                                                           ThermostatScheduleMode mode, 
                                                           Thermostat stat, 
                                                           LiteYukonUser user) {
        
        ThermostatScheduleUpdateResult result = ThermostatScheduleUpdateResult.SEND_SCHEDULE_SUCCESS;
        
        for (TimeOfWeek timeOfWeek : mode.getAssociatedTimeOfWeeks()) {
            ThermostatScheduleUpdateResult tempResult = sendSchedulePart(account, ats, stat.getId(), timeOfWeek, mode, user);
            if (tempResult.isFailed()) {
                result = tempResult;  // do we really want to keep trying here? doesn't make a lot of sense
            }
        }
        
        return result;
    }
    
    @Transactional
    private ThermostatScheduleUpdateResult sendSchedulePart(CustomerAccount account, 
                                                            AccountThermostatSchedule ats, 
                                                            int thermostatId, 
                                                            TimeOfWeek tow, 
                                                            ThermostatScheduleMode mode, 
                                                            LiteYukonUser user) {

        Thermostat stat = inventoryDao.getThermostatById(thermostatId);
        HardwareType type = stat.getType();

        if (type == HardwareType.UTILITY_PRO) {
            try {
                // We have to update the schedule mode for Utility Pro thermostats every
                // time we update the schedule if 5-2 mode is enabled
                YukonRoleProperty modeProperty;
                modeProperty = YukonRoleProperty.ADMIN_ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_WEEKEND;
                
                boolean mode52Enabled = rolePropertyDao.checkProperty(modeProperty, user);

                if (mode52Enabled) {
                    String serialString = " serial " + stat.getSerialNumber();
                    if (mode == ThermostatScheduleMode.WEEKDAY_SAT_SUN 
                            || mode == ThermostatScheduleMode.ALL) {
                            
                        // Schedule mode ALL is considered 5-1-1 by the stat
                        executor.execute(stat.getId(), "putconfig xcom raw 0x2b 0x08 0x03" + serialString, user);
                        
                    } else if (ThermostatScheduleMode.WEEKDAY_WEEKEND.equals(mode)) {
                        executor.execute(stat.getId(), "putconfig xcom raw 0x2b 0x08 0x02" + serialString, user);
                    }
                }
            } catch (CommandCompletionException e) {
                log.error("Failed to update thermostat schedule mode.", e);
                return ThermostatScheduleUpdateResult.UPDATE_SCHEDULE_ERROR;
            }
        }
        
        // Send command to thermostat
        try {
            String command = xcomCommandBuilder.buildScheduleCommand(stat, ats, tow, mode);
            executor.execute(stat.getId(), command, user);
        } catch (CommandCompletionException e) {
            log.error("Failed to update thermostat schedule.", e);
            return ThermostatScheduleUpdateResult.UPDATE_SCHEDULE_ERROR;
        }

        customerEventDao.saveAndLogScheduleUpdate(account, ats, tow, stat, user);

        return ThermostatScheduleUpdateResult.SEND_SCHEDULE_SUCCESS;
    }
    
    @Override
    public void sendCommand(LmHardwareCommand parameters) throws CommandCompletionException {
        
        List<String> commands = Lists.newArrayList();
        YukonEnergyCompany yec = yecService.getEnergyCompanyByInventoryId(parameters.getDevice().getInventoryID());
        boolean trackAddressing = ecRolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.TRACK_HARDWARE_ADDRESSING, yec);
        
        if (parameters.getType() == LmHardwareCommandType.CONFIG) {
            
            Integer optionalGroupId = null;
            Integer param = parameters.findParam(LmHardwareCommandParam.OPTIONAL_GROUP_ID, Integer.class);
            if (param != null) {
                optionalGroupId = param;
            }
            commands = xcomCommandBuilder.getConfigCommands(parameters.getDevice(), trackAddressing, optionalGroupId);
            
        } else if (parameters.getType() == LmHardwareCommandType.IN_SERVICE) {
            
            commands = xcomCommandBuilder.getEnableCommands(parameters.getDevice(), false);
            
        } else if (parameters.getType() == LmHardwareCommandType.OUT_OF_SERVICE) {
            
            commands = xcomCommandBuilder.getDisableCommands(parameters.getDevice());
            
        } else if (parameters.getType() == LmHardwareCommandType.TEMP_OUT_OF_SERVICE) {
            
            Duration duration = parameters.findParam(LmHardwareCommandParam.DURATION, Duration.class);
            boolean restoreFirst = rolePropertyDao.checkProperty(YukonRoleProperty.EXPRESSCOM_TOOS_RESTORE_FIRST, parameters.getUser());
            commands = xcomCommandBuilder.getOptOutCommands(parameters.getDevice(), duration, trackAddressing, restoreFirst);
            
        } else if (parameters.getType() == LmHardwareCommandType.CANCEL_TEMP_OUT_OF_SERVICE) {
            
            commands = xcomCommandBuilder.getCancelOptOutCommands(parameters.getDevice(), trackAddressing);
            
        }
        
        Integer param = parameters.findParam(LmHardwareCommandParam.OPTIONAL_ROUTE_ID, Integer.class);
        if (param != null) {
            Integer optionalRouteId = param;
            if (optionalRouteId == 0) {
                throw new CommandCompletionException("The route to send the command is not specified.");
            }
            for (String command : commands) {
                executor.executeOnRoute(command, optionalRouteId, parameters.getUser());
            }
        } else {
            for (String command : commands) {
                executor.execute(parameters.getDevice().getInventoryID(), command, parameters.getUser());
            }
        }
    }
    
    @Override
    public void sendBroadcastCommand(LmCommand command) throws CommandCompletionException {
        log.debug("Sending porter ExpressCom broadcast command: " + command.getType());
        
        if (command.getType() == LmHardwareCommandType.CANCEL_TEMP_OUT_OF_SERVICE) {
            LiteStarsEnergyCompany energyCompany = (LiteStarsEnergyCompany) yecService.getEnergyCompanyByOperator(command.getUser());
            int routeId = energyCompany.getDefaultRouteId();
            Integer spid = (Integer) command.getParams().get(LmHardwareCommandParam.SPID);
            String xcomCommand = xcomCommandBuilder.getBroadcastCancelAllOptOuts(spid);
            
            try {
                executor.executeOnRoute(xcomCommand, routeId, command.getUser());
            } catch (CommandCompletionException e) {
                
                throw new CommandCompletionException("Error executing porter ExpressCom broadcast command.");
            }
        }

    }
    
    @Override
    public HardwareStrategyType getType() {
        return HardwareStrategyType.PORTER;
    }

    @Override
    public boolean canHandle(HardwareType type) {
        return !type.isRf() && !type.isZigbee(); // Maybe this should be more specific
    }
    
    @Override
    public void sendTextMessage(YukonTextMessage message) {
        //TODO Cleanup this up, (move xcom to builder) could be part of the interface but would have to adjust zigbee service calls
        
        Map<Integer, HardwareSummary> hardwareSummary = inventoryDao.findHardwareSummariesById(message.getInventoryIds());
        
        StringBuilder command = new StringBuilder();
        command.append("putconfig xcom data '");
        command.append(message.getMessage());
        command.append("' msgpriority 7");
        
        if (message.getDisplayDuration() != null) {
            int displayDuration = message.getDisplayDuration().toPeriod().toStandardMinutes().getMinutes();
            command.append(" timeout ");
            command.append(displayDuration);
        }
        for (Integer inventoryId : message.getInventoryIds()) {
            try {
                String serial = " serial " + hardwareSummary.get(inventoryId).getSerialNumber();
                executor.execute(inventoryId, command.toString()+serial, message.getYukonUser());
            } catch (CommandCompletionException e) {
                //continue sending text messages
                log.warn("Unable to send text message to thermostat "+ inventoryId, e);
            }
        }
    }
    
    @Override
    public void cancelTextMessage(YukonCancelTextMessage message) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
}
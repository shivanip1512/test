package com.cannontech.stars.dr.thermostat.service.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.model.ZigbeeTextMessage;
import com.cannontech.common.temperature.FahrenheitTemperature;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.stars.dr.thermostat.service.AbstractCommandExecutionService;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.exception.DigiWebServiceException;
import com.cannontech.thirdparty.exception.ZigbeeClusterLibraryException;
import com.cannontech.thirdparty.model.ZigbeeEndpoint;
import com.cannontech.thirdparty.service.ZigbeeWebService;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableSetMultimap.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

public class ZigbeeCommandService extends AbstractCommandExecutionService {
    
    private InventoryDao inventoryDao;
    private ZigbeeDeviceDao zigbeeDeviceDao;
    private ZigbeeWebService zigbeeWebService;
    
    private final SetMultimap<TimeOfWeek, String> dayLetterLookup;
    {
        Builder<TimeOfWeek, String> builder = ImmutableSetMultimap.builder();
        builder.putAll(TimeOfWeek.MONDAY, "M");
        builder.putAll(TimeOfWeek.TUESDAY, "T");
        builder.putAll(TimeOfWeek.WEDNESDAY, "W");
        builder.putAll(TimeOfWeek.THURSDAY, "R");
        builder.putAll(TimeOfWeek.FRIDAY, "F");
        builder.putAll(TimeOfWeek.SATURDAY, "S");
        builder.putAll(TimeOfWeek.SUNDAY, "U");
        builder.putAll(TimeOfWeek.WEEKEND, "S", "U");
        builder.putAll(TimeOfWeek.WEEKDAY, "M", "T", "W", "R", "F");
        builder.putAll(TimeOfWeek.EVERYDAY, "M", "T", "W", "R", "F", "S", "U");
        dayLetterLookup = builder.build();
    }

    @Override
    public void doManualAdjustment(ThermostatManualEvent event, 
                                                          Thermostat stat, 
                                                          LiteYukonUser user) throws CommandCompletionException {
        try {
            String command = buildManualEventZigbeeCommand(stat,event);
            ZigbeeTextMessage message = new ZigbeeTextMessage();
            message.setAccountId(inventoryDao.getAccountIdForInventory(stat.getId()));
            message.setDisplayDuration(Duration.standardMinutes(1));
            Integer gatewayId = zigbeeDeviceDao.findGatewayIdForInventory(stat.getId());
            if (gatewayId == null) {
                /*TODO Should probably do this check farther up and ensure that the send button
                 * is not available for stats not assigned to a gateway in Jackson's new consumer pages.  */
                throw new CommandCompletionException("Device not assigned to a gateway");
            }
            message.setGatewayId(gatewayId);
            message.setInventoryId(stat.getId());
            message.setMessage(command);
            message.setStartTime(new Instant());
            zigbeeWebService.sendManualAdjustment(message);
        } catch (ZigbeeClusterLibraryException e) {
            throw new CommandCompletionException(e.getMessage(), e);
        } catch (DigiWebServiceException e) {
            throw new CommandCompletionException(e.getMessage(), e);
        }
    }

    @Override
    public ThermostatScheduleUpdateResult doScheduleUpdate(CustomerAccount account, 
                                 AccountThermostatSchedule ats,
                                 ThermostatScheduleMode mode, 
                                 Thermostat stat, 
                                 LiteYukonUser user) {
        
        for (TimeOfWeek timeOfWeek : mode.getAssociatedTimeOfWeeks()) {
            try {
                List<String> commands = buildUpdateScheduleZigbeeCommand(stat, ats, timeOfWeek, mode);
                for (String command : commands) {
                    ZigbeeTextMessage message = new ZigbeeTextMessage();
                    message.setAccountId(inventoryDao.getAccountIdForInventory(stat.getId()));
                    message.setDisplayDuration(Duration.standardMinutes(1));
                    Integer gatewayId = zigbeeDeviceDao.findGatewayIdForInventory(stat.getId());
                    if (gatewayId == null) {
                        /*TODO Should probably do this check farther up and ensure that the send button
                         * is not available for stats not assigned to a gateway in Jackson's new consumer pages.  */
                        return ThermostatScheduleUpdateResult.UPDATE_SCHEDULE_ERROR;
                    }
                    message.setGatewayId(gatewayId);
                    message.setInventoryId(stat.getId());
                    message.setMessage(command);
                    message.setStartTime(new Instant());
                    zigbeeWebService.sendManualAdjustment(message);
                }
                
                saveAndLogUpdateEvent(account, ats, timeOfWeek, stat, user);
                
            } catch (ZigbeeClusterLibraryException e) {
                return ThermostatScheduleUpdateResult.UPDATE_SCHEDULE_ERROR;
            } catch (DigiWebServiceException e) {
                return ThermostatScheduleUpdateResult.UPDATE_SCHEDULE_ERROR;
            }
        }
        
        return ThermostatScheduleUpdateResult.SEND_SCHEDULE_SUCCESS;
    }
    
    private String buildHexNodeId(Thermostat stat) {
        ZigbeeEndpoint zbStat = zigbeeDeviceDao.getZigbeeEndPointByInventoryId(stat.getId());
        int nodeId = zbStat.getNodeId();
        
        String hexNodeId = String.format("%04x",nodeId);
        
        return hexNodeId;
    }
    
    private List<String> buildUpdateScheduleZigbeeCommand(Thermostat stat, AccountThermostatSchedule schedule, TimeOfWeek timeOfWeek, ThermostatScheduleMode mode) {
        
        Multimap<TimeOfWeek, AccountThermostatScheduleEntry> entries = schedule.getEntriesByTimeOfWeekMultimap();
        Collection<AccountThermostatScheduleEntry> entriesForToW = entries.get(timeOfWeek);
        
        /* For ZigBee use the EveryDay time of week instead of WeekDay
         * since we also need to send saturday and sunday set points. */
        if (mode == ThermostatScheduleMode.ALL) {
            timeOfWeek = TimeOfWeek.EVERYDAY;
        }
        
        String hexNodeId = buildHexNodeId(stat);
        List<String> commands = Lists.newArrayList();
        for (String day : dayLetterLookup.get(timeOfWeek)) {
            commands.add("_!" + hexNodeId +"S" + day + buildZigbeeSetPoints(entriesForToW));
        }
        
        return commands;
    }
    
    private String buildZigbeeSetPoints(Collection<AccountThermostatScheduleEntry> entries) {
        StringBuilder builder = new StringBuilder();
        
        /* Assume for ZigBee devices we will always be provided 4 set point entries */
        String[] setPoints = new String[] {"w", "l", "r", "s"};

        Iterator<AccountThermostatScheduleEntry> iter = entries.iterator();
        
        int entryPos = 0;
        while (iter.hasNext()) {
            
            /* Set point type: wake, leave, return, sleep */
            builder.append(":" + setPoints[entryPos] + ",");
            
            AccountThermostatScheduleEntry entry = iter.next();
            
            LocalTime startTime = entry.getStartTimeLocalTime();
            FahrenheitTemperature coolTemp = entry.getCoolTemp().toFahrenheit();
            FahrenheitTemperature heatTemp = entry.getHeatTemp().toFahrenheit();

            String startTimeString = startTime.toString("HHmm");
            builder.append(startTimeString + ",");
            builder.append(coolTemp.toIntValue() + ",");
            builder.append(heatTemp.toIntValue());
            
            entryPos++;
        }
        
        return builder.toString();
    }
    
    private String buildManualEventZigbeeCommand(Thermostat stat, ThermostatManualEvent event) {
        String hexNodeId = buildHexNodeId(stat);
        StringBuilder command = new StringBuilder();
        
        command.append("_!" + hexNodeId);
        
        if (event.isRunProgram()) {
            /* P for restore program */
            command.append("P");
        } else {
            
            /* Use hold command to do manual changes */
            command.append("H,");
            
            /* Mode */
            ThermostatMode mode = event.getMode();
            if (mode != null) {
                if (mode == ThermostatMode.COOL) {
                    command.append("C,");
                } else if (mode == ThermostatMode.HEAT) {
                    command.append("H,");
                } else if (mode == ThermostatMode.OFF) {
                    command.append("O,");
                }
            } else {
                command.append("R,");
            }
            
            /* Temp */
            command.append(event.getPreviousTemperature() + ",");
            
            /* Fan */
            if (event.getFanState() == ThermostatFanState.AUTO) {
                command.append("AU,");
            } else if (event.getFanState() == ThermostatFanState.CIRCULATE) {
                command.append("CI,");
            } else if (event.getFanState() == ThermostatFanState.ON) {
                command.append("ON,");
            }
            
            /* Hold */
            if (event.isHoldTemperature()) {
                command.append("P");
            } else {
                command.append("T");
            }
        
        }
        
        return command.toString();
    }

    @Autowired
    public void setZigbeeWebService(ZigbeeWebService zigbeeWebService) {
        this.zigbeeWebService = zigbeeWebService;
    }
    
    @Autowired
    public void setZigbeeDeviceDao(ZigbeeDeviceDao zigbeeDeviceDao) {
        this.zigbeeDeviceDao = zigbeeDeviceDao;
    }
    
    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }
    
}
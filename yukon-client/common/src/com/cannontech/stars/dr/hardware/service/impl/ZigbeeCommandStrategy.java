package com.cannontech.stars.dr.hardware.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.device.commands.exception.SystemConfigurationException;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.common.model.ZigbeeTextMessageDto;
import com.cannontech.common.temperature.FahrenheitTemperature;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dao.LMGroupDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.loadgroup.model.SEPGroupAttributes;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.LmCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.hardware.service.HardwareStrategyType;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandStrategy;
import com.cannontech.stars.dr.thermostat.dao.CustomerEventDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.exception.DigiNotConfiguredException;
import com.cannontech.thirdparty.digi.exception.DigiWebServiceException;
import com.cannontech.thirdparty.exception.ZigbeeClusterLibraryException;
import com.cannontech.thirdparty.model.DRLCClusterAttribute;
import com.cannontech.thirdparty.model.ZigbeeEndpoint;
import com.cannontech.thirdparty.service.ZigbeeWebService;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableSetMultimap.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

public class ZigbeeCommandStrategy implements LmHardwareCommandStrategy {
    
    private static final Logger log = YukonLogManager.getLogger(ZigbeeCommandStrategy.class);
    
    @Autowired private InventoryDao inventoryDao;
    @Autowired private ZigbeeDeviceDao zigbeeDeviceDao;
    @Autowired private GatewayDeviceDao gatewayDeviceDao;
    @Autowired private ZigbeeWebService zigbeeWebService;
    @Autowired private CustomerEventDao customerEventDao;
    @Autowired private LMGroupDao lmGroupDao;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;

    private YukonJmsTemplate zigbeeSepTextJmsTemplate;
    private YukonJmsTemplate zigbeeSepTextCancelJmsTemplate;

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

    @PostConstruct
    public void init() {
        zigbeeSepTextJmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.ZIGBEE_SEP_TEXT);
        zigbeeSepTextCancelJmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.ZIGBEE_SEP_TEXT_CANCEL);
    }

    @Override
    public void sendCommand(LmHardwareCommand parameters) throws CommandCompletionException {
        
        /** There is nothing to send for In-Service and Out-of-Service commands for ZigBee devices. */
        if (parameters.getType() == LmHardwareCommandType.CONFIG) {
            //Determine Gateway and Device Id.
            LiteLmHardwareBase device = parameters.getDevice();
            List<Integer> lmGroupIds = gatewayDeviceDao.getLMGroupIdByEndPointId(device.getDeviceID());
            
            if (lmGroupIds.isEmpty()) {
                throw new BadConfigurationException("Device is not Enrolled in any program.");
            }
            
            //Grabbing only the first GroupId the device is assigned to.
            //All groups this device is assigned to should have the same Utility Enrollment Group Id. 
            //If they do not it is considered a misconfiguration and will have un expected behavior
            int lmGroupId = lmGroupIds.get(0);
            
            //Build Attributes to send
            Map<DRLCClusterAttribute,Integer> attributes = Maps.newHashMap();
            
            //Utility Enrollment group
            SEPGroupAttributes groupAttributes = lmGroupDao.getSEPAttributesGroupForSepGroup(lmGroupId);
            if (groupAttributes.getUtilityEnrollmentGroup() == 0) {
                log.warn("Not sending Utility Enrollment Group to device because it is '0'. ");
            } else {
                attributes.put(DRLCClusterAttribute.UTILITY_ENROLLMENT_GROUP, (int)groupAttributes.getUtilityEnrollmentGroup());
                attributes.put(DRLCClusterAttribute.START_RANDOMIZE_MINUTES, groupAttributes.getRampIn());
                attributes.put(DRLCClusterAttribute.STOP_RANDOMIZE_MINTES, groupAttributes.getRampOut());
                try {
                    zigbeeWebService.sendLoadGroupAddressing(device.getDeviceID(), attributes);
                    
                } catch (DigiWebServiceException e) {
                    log.error("Unable to configure Digi device:", e);
                    throw new CommandCompletionException("Unable to configure Digi device.", e);
                } catch (DigiNotConfiguredException e) {
                    log.error("Unable to configure Digi device: Digi is disabled ", e);
                    throw new SystemConfigurationException("Digi is disabled.");
                }
            }
        } else {
            log.debug("Nothing to send for " + parameters.getType() + " command.");
        }
    }
    
    @Override
    public boolean canBroadcast(LmCommand command) {
        // No broadcast commands are implemented for the ZigBee command strategy.
        return false;
    }
    
    @Override
    public void sendBroadcastCommand(LmCommand command) {
        if (command.getType() == LmHardwareCommandType.CANCEL_TEMP_OUT_OF_SERVICE) {
            // TEMP_OUT_OF_SERVICE (opt out) is not supported by ZigBee.
            // So CANCEL_TEMP_OUT_OF_SERVICE should do nothing in the ZigBee strategy.
            throw new UnsupportedOperationException("ZigBee doesn't currently support opt outs, so they can't be canceled.");
        }
    }

    @Override
    public void doManualAdjustment(ThermostatManualEvent event, 
                                                          Thermostat stat, 
                                                          LiteYukonUser user) throws CommandCompletionException {
        try {
            String command = buildManualEventZigbeeCommand(stat,event);
            ZigbeeTextMessageDto message = new ZigbeeTextMessageDto();
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
            message.setInventoryIds(Collections.singleton(stat.getId()));
            
            zigbeeWebService.sendManualAdjustment(message);
        } catch (DigiNotConfiguredException e) {
            throw new SystemConfigurationException(e.getMessage(), e);
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
                    ZigbeeTextMessageDto message = new ZigbeeTextMessageDto();
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
                    message.setInventoryIds(Collections.singleton(stat.getId()));
                    
                    zigbeeWebService.sendManualAdjustment(message);
                }
                
                customerEventDao.saveAndLogScheduleUpdate(account, ats, timeOfWeek, stat, user);
                
            //TODO: The general theme of this file is to throw an exception on error.
            //Consider changing to match
            } catch (DigiNotConfiguredException e) {
                return ThermostatScheduleUpdateResult.UPDATE_SCHEDULE_ERROR;
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
                // TODO: Auto mode not supported just yet for digi.  This will be added when we have a command to do so. 
                if (mode == ThermostatMode.COOL) {
                    command.append("C,");
                    /* Temp - needs to be sent as an integer fahrenheit value */
                    command.append(event.getPreviousCoolTemperature().toFahrenheit().toIntValue() + ",");
                } else if (mode == ThermostatMode.HEAT) {
                    command.append("H,");
                    /* Temp - needs to be sent as an integer fahrenheit value */
                    command.append(event.getPreviousHeatTemperature().toFahrenheit().toIntValue() + ",");
                } else if (mode == ThermostatMode.OFF) {
                    command.append("O,");
                }
            } else {
                command.append("R,");
            }
            
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

    @Override
    public HardwareStrategyType getType() {
        return HardwareStrategyType.ZIGBEE;
    }

    @Override
    public boolean canHandle(HardwareType type) {
        return type.isZigbee();
    }

    @Override
    public void sendTextMessage(YukonTextMessage message) {
        zigbeeSepTextJmsTemplate.convertAndSend( message);
    }
    
    @Override
    public void cancelTextMessage(YukonCancelTextMessage message) {
        zigbeeSepTextCancelJmsTemplate.convertAndSend(message);
    }

    @Override
    public void verifyCanSendConfig(LmHardwareCommand command) throws BadConfigurationException {
        LiteLmHardwareBase device = command.getDevice();
        List<Integer> lmGroupIds = gatewayDeviceDao.getLMGroupIdByEndPointId(device.getDeviceID());
        if (lmGroupIds.isEmpty()) {
            throw new BadConfigurationException("Device is not Enrolled in any program.");
        }
    }
}
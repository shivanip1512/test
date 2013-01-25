package com.cannontech.stars.dr.hardware.service.impl;

import java.util.Collections;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.device.commands.impl.CommandRequestExecutionDefaults;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnMessageClass;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.rfn.message.broadcast.RfnExpressComBroadcastRequest;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastReplyType;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastRequest;
import com.cannontech.dr.rfn.service.RawExpressComCommandBuilder;
import com.cannontech.dr.rfn.service.RfnExpressComMessageService;
import com.cannontech.dr.rfn.service.RfnUnicastCallback;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.model.LmCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandParam;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.hardware.service.HardwareStrategyType;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandStrategy;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;

public class RfCommandStrategy implements LmHardwareCommandStrategy {
    
    private static final Logger log = YukonLogManager.getLogger(RfCommandStrategy.class);
    private static final LogHelper logHelper = LogHelper.getInstance(log);

    @Autowired private RawExpressComCommandBuilder rawExpressComCommandBuilder;
    @Autowired private RfnExpressComMessageService rfnExpressComMessageService;
    @Autowired private RawExpressComCommandBuilder commandBuilder;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private YukonListDao yukonListDao;

    @Override
    public void sendCommand(final LmHardwareCommand parameters) throws CommandCompletionException {
        
        LiteLmHardwareBase device = parameters.getDevice();
        YukonListEntry typeEntry = yukonListDao.getYukonListEntry(device.getLmHardwareTypeID());
        HardwareType type = HardwareType.valueOf(typeEntry.getYukonDefID());
        RfnManufacturerModel template = RfnManufacturerModel.getForType(type.getForHardwareType()).get(0);
        RfnIdentifier rfnIdentifier = new RfnIdentifier(device.getManufacturerSerialNumber(), template.getManufacturer(), template.getModel());
        
        RfnExpressComUnicastRequest request = new RfnExpressComUnicastRequest(rfnIdentifier);
        request.setPayload(commandBuilder.getCommandAsHexStringByteArray(parameters));
        
        Long expirationDuration = parameters.findParam(LmHardwareCommandParam.EXPIRATION_DURATION, Long.class);
        if (expirationDuration != null) {
            request.setExpirationDuration(expirationDuration);
        }
        String groupId = parameters.findParam(LmHardwareCommandParam.GROUP_ID, String.class);
        if (groupId != null) {
            request.setGroupId(groupId);
        }
        Integer priority = parameters.findParam(LmHardwareCommandParam.PRIORITY, Integer.class);
        if (priority != null) {
            request.setMessagePriority(priority);
        } else {
            request.setMessagePriority(7);
        }
        Boolean respExpected = parameters.findParam(LmHardwareCommandParam.EXPECT_RESPONSE, Boolean.class);
        if (respExpected != null) {
            request.setResponseExpected(respExpected);
        }
        request.setRfnMessageClass(RfnMessageClass.DR);
        
        boolean sendOnBulk = false;
        Boolean param = parameters.findParam(LmHardwareCommandParam.BULK, Boolean.class);
        if (param != null) {
            sendOnBulk = param;
        }
        
        if (sendOnBulk) {
            rfnExpressComMessageService.sendUnicastBulkRequest(Collections.singletonList(request));
        } else {
            rfnExpressComMessageService.sendUnicastRequest(request, new RfnUnicastCallback() {
                @Override
                public void processingExceptionOccured(MessageSourceResolvable message) {
                    log.error("Unable to send " + parameters.getType() + ": " + message);
                }
                
                @Override public void complete() {}
                
                @Override 
                public void receivedStatus(RfnExpressComUnicastReplyType replyType) {
                    logHelper.debug("Recieved status %s for %s", replyType, parameters.getType());
                }
                
                @Override 
                public void receivedStatusError(RfnExpressComUnicastReplyType replyType) {
                    logHelper.debug("Recieved status error %s for %s", replyType, parameters.getType());
                }
            });
        }
    }
    
    @Override
    public boolean canBroadcast(LmCommand command) {
        if (command.getType() == LmHardwareCommandType.CANCEL_TEMP_OUT_OF_SERVICE) {
            return true;
        }
        // No other broadcast commands are implemented for the RF command strategy.
        return false;
    }
    
    
    @Override
    public void sendBroadcastCommand(LmCommand command) {
        // On a Network Manager enabled system utilizing LCR devices for load control,
        // the following CPARM will be set; if not it will return null.
        String rfnEcName = configurationSource.getString("RFN_ENERGY_COMPANY_NAME");
        if (rfnEcName != null) {
            log.debug("Sending RFN ExpressCom broadcast command: " + command.getType().toString());

            if (command.getType() == LmHardwareCommandType.CANCEL_TEMP_OUT_OF_SERVICE) {
                RfnExpressComBroadcastRequest request = new RfnExpressComBroadcastRequest();
                request.setRfnMessageClass(RfnMessageClass.NONE);
                request.setExpirationDuration(-1); // Messages will not expire.
                int commandPriority = configurationSource.getInteger("OVERRIDE_PRIORITY_LM_HARDWARE_COMMAND", 
                        CommandRequestExecutionDefaults.getPriority(DeviceRequestType.LM_HARDWARE_COMMAND));
                request.setMessagePriority(commandPriority);
                Integer spid = (Integer) command.getParams().get(LmHardwareCommandParam.SPID);
                request.setPayload(rawExpressComCommandBuilder.getBroadcastCancelAllTempOutOfServiceCommand(spid));
            
                rfnExpressComMessageService.sendBroadcastRequest(request);
            }
            
        }
    }
    
    @Override
    public void doManualAdjustment(ThermostatManualEvent event, Thermostat stat, LiteYukonUser user) 
    throws CommandCompletionException {
        /** No RF thermostats yet */
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ThermostatScheduleUpdateResult doScheduleUpdate(CustomerAccount account,
                                                           AccountThermostatSchedule ats,
                                                           ThermostatScheduleMode mode,
                                                           Thermostat stat, LiteYukonUser user) {
        /** No RF thermostats yet */
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    @Override
    public HardwareStrategyType getType() {
        return HardwareStrategyType.RF;
    }

    @Override
    public boolean canHandle(HardwareType type) {
        return type.isRf();
    }
    
    @Override
    public void sendTextMessage(YukonTextMessage message) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    @Override
    public void cancelTextMessage(YukonCancelTextMessage message) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
}
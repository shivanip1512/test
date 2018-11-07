package com.cannontech.stars.dr.hardware.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.device.commands.impl.CommandRequestExecutionDefaults;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.exception.InvalidExpressComSerialNumberException;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.RfnMessageClass;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.rfn.message.broadcast.RfnExpressComBroadcastRequest;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastReplyType;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastRequest;
import com.cannontech.dr.rfn.service.RawExpressComCommandBuilder;
import com.cannontech.dr.rfn.service.RfnExpressComMessageService;
import com.cannontech.dr.rfn.service.RfnUnicastCallback;
import com.cannontech.dr.rfn.service.WaitableRfnUnicastCallback;
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
import com.cannontech.yukon.IDatabaseCache;

public class RfCommandStrategy implements LmHardwareCommandStrategy {
    
    private static final Logger log = YukonLogManager.getLogger(RfCommandStrategy.class);
    private static final AtomicInteger nextMessageId = new AtomicInteger((int)new Instant().getMillis());

    @Autowired private RawExpressComCommandBuilder rawExpressComCommandBuilder;
    @Autowired private RfnExpressComMessageService rfnExpressComMessageService;
    @Autowired private RawExpressComCommandBuilder commandBuilder;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private IDatabaseCache databaseCache;

    @Override
    public void sendCommand(final LmHardwareCommand parameters) throws CommandCompletionException {
        // make sure you have a (non-system) paobject to send command to
        if (parameters.getDevice().getDeviceID() == 0) {
            log.warn("Attempted to send command to a RF device with no linked PAO");
            return;
        }
        
        RfnDevice rfnDevice = rfnDeviceDao.getDeviceForId(parameters.getDevice().getDeviceID());
        
        RfnExpressComUnicastRequest request = new RfnExpressComUnicastRequest(rfnDevice.getRfnIdentifier());
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
        boolean waitable = false;
        Boolean waitableParam = parameters.findParam(LmHardwareCommandParam.WAITABLE, Boolean.class);
        if (waitableParam != null) {
            waitable = waitableParam;
        }
        if (!waitable) {
            sendCommand(request, parameters, sendOnBulk);
        } else {
            Map<String, Object> result = sendCommandAndWait(request, parameters);
            Boolean status = (Boolean)result.get("success");
            if (!status) {
                throw new CommandCompletionException(result.get("message").toString());
            }
        }
    }
    
    /*
     * Send command and do not wait for the response.
     * If error occurs it will be logged.
     */
    private void sendCommand(RfnExpressComUnicastRequest request,
            final LmHardwareCommand parameters, boolean sendOnBulk) {
        if (sendOnBulk) {
            rfnExpressComMessageService.sendUnicastBulkRequest(Collections.singletonList(request));
        } else {
            rfnExpressComMessageService.sendUnicastRequest(request, new RfnUnicastCallback() {
                @Override
                public void processingExceptionOccurred(MessageSourceResolvable message) {
                    log.error("Unable to send " + parameters.getType() + ": " + message);
                }

                @Override
                public void complete() {
                }

                @Override
                public void receivedStatus(RfnExpressComUnicastReplyType replyType) {
                    log.debug(String.format("Received status %s for %s", replyType, parameters.getType()));
                }

                @Override
                public void receivedStatusError(RfnExpressComUnicastReplyType replyType) {
                    log.debug(String.format("Received status error %s for %s", replyType, parameters.getType()));
                }
            });
        }
    }
    
    /*
     * Send command and wait for the response
     */
    private Map<String, Object> sendCommandAndWait(RfnExpressComUnicastRequest request,
            final LmHardwareCommand parameters) {

        final Map<String, Object> resultMap = new HashMap<>();
        WaitableRfnUnicastCallback waitableCallback = new WaitableRfnUnicastCallback(new RfnUnicastCallback() {

            @Override
            public void processingExceptionOccurred(MessageSourceResolvable message) {
                resultMap.put("success", false);
                resultMap.put("message", "Unable to send command");
                log.error("Unable to send " + parameters.getType() + ": " + message);
            }

            @Override
            public void receivedStatus(RfnExpressComUnicastReplyType status) {
                boolean success = status == RfnExpressComUnicastReplyType.OK ? true : false;
                resultMap.put("success", success);
                if (!success) {
                    log.debug(String.format("Received status %s for %s", status, parameters.getType()));
                    resultMap.put("message", status);
                }
            }

            @Override
            public void receivedStatusError(RfnExpressComUnicastReplyType replyType) {
                resultMap.put("success", false);
                resultMap.put("message", replyType);
                log.debug(String.format("Received status error %s for %s", replyType, parameters.getType()));
            }

            @Override
            public void complete() { }
        });

        rfnExpressComMessageService.sendUnicastRequest(request, waitableCallback);

        try {
            waitableCallback.waitForCompletion();
        } catch (InterruptedException e) {/* ignore */}
        
        return resultMap;
    }
    
    @Override
    public boolean canBroadcast(LmCommand command) {
        switch (command.getType()) {
            case CANCEL_TEMP_OUT_OF_SERVICE:
            case PERFORMANCE_VERIFICATION:
                return true;
            default:
                // No other broadcast commands are implemented for the RF command strategy.
                return false;
        }
    }
    
    
    @Override
    public void sendBroadcastCommand(LmCommand command) {
        //Checks to see if an rfn LCR device exist
        if (databaseCache.getAllPaoTypes().stream().anyMatch(PaoType.getRfLcrTypes()::contains)) {
            log.debug("Sending RFN ExpressCom broadcast command: " + command.getType().toString());

            if (canBroadcast(command)) {
                RfnExpressComBroadcastRequest request = new RfnExpressComBroadcastRequest();
                request.setRfnMessageClass(RfnMessageClass.DR);
                request.setExpirationDuration(-1); // Messages will not expire.
                // C++ will use unique positive values, Java gets the unique negative values
                request.setMessageId((short)(nextMessageId.incrementAndGet() | 0x8000));
                int commandPriority = configurationSource.getInteger("OVERRIDE_PRIORITY_LM_HARDWARE_COMMAND", 
                        CommandRequestExecutionDefaults.getPriority(DeviceRequestType.LM_HARDWARE_COMMAND));
                request.setMessagePriority(commandPriority);
                
                if (command.getType() == LmHardwareCommandType.CANCEL_TEMP_OUT_OF_SERVICE) {
                    Integer spid = (Integer) command.getParams().get(LmHardwareCommandParam.SPID);
                    request.setPayload(rawExpressComCommandBuilder.getBroadcastCancelAllTempOutOfServiceCommand(spid));
                } else if (command.getType() == LmHardwareCommandType.PERFORMANCE_VERIFICATION) {
                    Long messageId = (Long) command.getParams().get(LmHardwareCommandParam.UNIQUE_MESSAGE_ID);
                    request.setPayload(rawExpressComCommandBuilder.getPerformanceVerificationCommand(messageId));
                }
                
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
                                                           Thermostat stat,
                                                           LiteYukonUser user) {
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

    @Override
    public void verifyCanSendConfig(LmHardwareCommand command) throws BadConfigurationException {
        try {
            commandBuilder.getCommandAsHexStringByteArray(command);
        } catch (InvalidExpressComSerialNumberException e) {
            throw new BadConfigurationException(e.getMessage());
        }
    }
    
}
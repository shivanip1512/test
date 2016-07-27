package com.cannontech.web.dev.dataStreaming;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.streaming.dao.DeviceBehaviorDao;
import com.cannontech.common.device.streaming.model.BehaviorReport;
import com.cannontech.common.device.streaming.model.BehaviorType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingAttribute;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;

public class FakeDataStreamingCommandRequestDeviceExecutor {
    
    private static final Logger log = YukonLogManager.getLogger(FakeDataStreamingCommandRequestDeviceExecutor.class);
    private DeviceBehaviorDao deviceBehaviorDao; 
    private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    private static int ERROR_CODE = 295;
    private static final List<BuiltInAttribute> attributes = ImmutableList.of(BuiltInAttribute.KVAR,
        BuiltInAttribute.DEMAND, BuiltInAttribute.DELIVERED_KWH, BuiltInAttribute.RECEIVED_KWH);
    public final static String STREAMING_ENABLED_STRING = "streamingEnabled";
    
    public FakeDataStreamingCommandRequestDeviceExecutor(DeviceBehaviorDao deviceBehaviorDao,
            DeviceErrorTranslatorDao deviceErrorTranslatorDao,
            CommandRequestExecutionResultDao commandRequestExecutionResultDao) {
        this.deviceBehaviorDao = deviceBehaviorDao;
        this.deviceErrorTranslatorDao = deviceErrorTranslatorDao;
        this.commandRequestExecutionResultDao = commandRequestExecutionResultDao;
    }

    public void execute(CommandRequestExecution execution,
            CommandCompletionCallback<CommandRequestDevice> callback, List<CommandRequestDevice> commands, LiteYukonUser user) {
        
        //Just return the expected values (as pulled from the DB) as the "device response"
        //TODO: add a configurable delay, since these may take up to a day to get back in the real world
        DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(ERROR_CODE);
        Runnable callbackResponder = () -> {
            commands.forEach(command -> {
                SimpleDevice device = command.getDevice();
                String jsonResponse = getResponseJson(device.getDeviceId());
                boolean isSuccess = new Random().nextBoolean();
                if (isSuccess) {
                    callback.receivedLastResultString(command, jsonResponse);
                } else {
                    String error = "Bad error device id=" + device.getDeviceId();
                    MessageSourceResolvable detail = YukonMessageSourceResolvable.createSingleCodeWithArguments(
                        "yukon.common.device.errorDetail", error);
                    SpecificDeviceErrorDescription deviceError =
                        new SpecificDeviceErrorDescription(errorDescription, error, detail);
                    callback.receivedLastError(command, deviceError);
                }
                saveCommandRequestExecutionResult(execution.getId(), device.getDeviceId(), isSuccess);
            });
            callback.complete();
        };
        Thread callbackResponderThread = new Thread(callbackResponder);
        callbackResponderThread.start();
    }

    private void saveCommandRequestExecutionResult(int execId, int deviceId, boolean isSuccess) {
        CommandRequestExecutionResult commandRequestExecutionResult = new CommandRequestExecutionResult();
        commandRequestExecutionResult.setCommandRequestExecutionId(execId);
        commandRequestExecutionResult.setCommand(DeviceRequestType.DATA_STREAMING_CONFIG.getShortName());
        commandRequestExecutionResult.setDeviceId(deviceId);
        commandRequestExecutionResult.setCompleteTime(new Date());
        if (isSuccess) {
            commandRequestExecutionResult.setErrorCode(0);
        } else {
            commandRequestExecutionResult.setErrorCode(ERROR_CODE);
        }
        commandRequestExecutionResultDao.saveOrUpdate(commandRequestExecutionResult);
    }

    private String getResponseJson(int deviceId) {
        BehaviorReport report = null;
        try {
            report = deviceBehaviorDao.getBehaviorReportByDeviceIdAndType(deviceId, BehaviorType.DATA_STREAMING);
        } catch (NotFoundException e) {}
        
        ReportedDataStreamingConfig config = new ReportedDataStreamingConfig();
        boolean streamingEnabled = new Boolean(report.getValuesMap().get(STREAMING_ENABLED_STRING));
        List<ReportedDataStreamingAttribute> reportedAttributes= new ArrayList<>();
        config.setStreamingEnabled(streamingEnabled);
        config.setAttributes(reportedAttributes);
        for (BuiltInAttribute attribute : attributes) {
            ReportedDataStreamingAttribute metric = new ReportedDataStreamingAttribute();
            boolean isAttributeEnabled = new Random().nextBoolean();
            metric.setEnabled(isAttributeEnabled);
            metric.setInterval(1);
            metric.setAttribute(attribute.getKey());
            reportedAttributes.add(metric);
        }
        String json = "";
        try {
            json = JsonUtils.toJson(config);
        } catch (JsonProcessingException e) {
            log.warn("Caught exception in getResponseJson", e);
        }

        return json;
    }
}

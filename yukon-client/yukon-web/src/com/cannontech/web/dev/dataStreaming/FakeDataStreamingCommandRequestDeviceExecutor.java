package com.cannontech.web.dev.dataStreaming;

import static com.cannontech.web.rfn.dataStreaming.service.impl.DataStreamingServiceImpl.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionParameterDto;
import com.cannontech.common.device.commands.CommandRequestExecutionTemplate;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.streaming.dao.DeviceBehaviorDao;
import com.cannontech.common.device.streaming.model.Behavior;
import com.cannontech.common.device.streaming.model.BehaviorType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.rfn.dataStreaming.DataStreamingMetric;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.fasterxml.jackson.core.JsonProcessingException;

public class FakeDataStreamingCommandRequestDeviceExecutor implements CommandRequestDeviceExecutor {
    private static final Logger log = YukonLogManager.getLogger(FakeDataStreamingCommandRequestDeviceExecutor.class);
    private DeviceBehaviorDao deviceBehaviorDao; 
    
    public FakeDataStreamingCommandRequestDeviceExecutor(DeviceBehaviorDao deviceBehaviorDao) {
        this.deviceBehaviorDao = deviceBehaviorDao;
    }
    
    @Override
    public CommandRequestExecutionIdentifier execute(List<CommandRequestDevice> commands,
                                                     CommandCompletionCallback<? super CommandRequestDevice> callback,
                                                     DeviceRequestType type, LiteYukonUser user) {
        
        //Just return the expected values (as pulled from the DB) as the "device response"
        //TODO: add a configurable delay, since these may take up to a day to get back in the real world
        Runnable callbackResponder = () -> {
            commands.forEach(command -> {
                SimpleDevice device = command.getDevice();
                String jsonResponse = getResponseJson(device.getDeviceId());
                callback.receivedIntermediateResultString(command, jsonResponse);
            });
            callback.complete();
        };
        Thread callbackResponderThread = new Thread(callbackResponder);
        callbackResponderThread.start();
        
        return new CommandRequestExecutionIdentifier(42);
    }
    
    private String getResponseJson(int deviceId) {
        
        ReportedDataStreamingConfig config = new ReportedDataStreamingConfig();
        
        Behavior behavior = null;
        try {
            behavior = deviceBehaviorDao.getBehaviorByDeviceIdAndType(deviceId, BehaviorType.DATA_STREAMING);
            log.debug("Behavior found: " + behavior);
            List<DataStreamingMetric> metrics = getMetricsFromBehavior(behavior);
            config.setStreamingEnabled(true);
            config.setConfiguredMetrics(metrics);
        } catch (NotFoundException e) {
            log.debug("Behavior not found.");
            config.setStreamingEnabled(false);
            config.setConfiguredMetrics(new ArrayList<>());
        }
        
        String json = "";
        try {
            json = JsonUtils.toJson(config);
        } catch (JsonProcessingException e) {
            log.warn("Caught exception in getResponseJson", e);
        }
        
        return json;
    }
    
    private List<DataStreamingMetric> getMetricsFromBehavior(Behavior behavior) {
        List<DataStreamingMetric> metrics = new ArrayList<>();
        
        String channelsString = behavior.getValue(CHANNELS_STRING);
        int channels = Integer.parseInt(channelsString);
        
        for (int i = 0; i < channels; i++) {
            String key = CHANNELS_STRING + "." + i;
            String attributeValue = behavior.getValue(key + ATTRIBUTE_STRING);
            String intervalValue = behavior.getValue(key + INTERVAL_STRING);
            
            DataStreamingMetric metric = new DataStreamingMetric();
            metric.setEnabled(true);
            metric.setInterval(Integer.parseInt(intervalValue));
            metric.setAttribute(attributeValue);
            metrics.add(metric);
        }
        
        return metrics;
    }
    
    /**** Unused Methods *****/
    
    @Override
    public CommandResultHolder execute(CommandRequestDevice command, DeviceRequestType type, LiteYukonUser user)
            throws CommandCompletionException {
        return null;
    }
    
    @Override
    public long cancelExecution(CommandCompletionCallback<? super CommandRequestDevice> callback, LiteYukonUser user,
                                boolean updateExecutionStatus) {
        return 0;
    }

    @Override
    public CommandRequestExecutionTemplate<CommandRequestDevice> getExecutionTemplate(DeviceRequestType type,
                                                                                      LiteYukonUser user) {
        return null;
    }

    @Override
    public CommandRequestExecutionIdentifier executeWithParameterDto(List<CommandRequestDevice> commands,
                                                                     CommandCompletionCallback<? super CommandRequestDevice> callback,
                                                                     CommandRequestExecutionParameterDto parameterDto,
                                                                     CommandRequestExecution execution,
                                                                     boolean multipleStrategies) {
        return null;
    }

    @Override
    public void createTemplateAndExecute(CommandRequestExecution execution,
                                         CommandCompletionCallback<? super CommandRequestDevice> callback,
                                         List<CommandRequestDevice> commands, LiteYukonUser user,
                                         boolean multipleStrategies) {
    }

    @Override
    public CommandRequestExecutionTemplate<CommandRequestDevice> getExecutionTemplate(CommandRequestExecution execution,
                                                                                      LiteYukonUser user) {
        return null;
    }

    @Override
    public CommandResultHolder execute(YukonDevice device, String command, DeviceRequestType type, LiteYukonUser user)
            throws Exception {
        return null;
    }

}

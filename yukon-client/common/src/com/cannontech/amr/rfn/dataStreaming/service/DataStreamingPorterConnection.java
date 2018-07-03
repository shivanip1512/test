package com.cannontech.amr.rfn.dataStreaming.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.callbackResult.DataStreamingConfigCallback;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionCancellationCallback;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.StrategyType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;

/**
 * Abstraction of the Porter connection for data streaming purposes. This class handles the actual data streaming config
 * messages sent to Porter, which then sends configuration messages to the devices.
 */
public class DataStreamingPorterConnection {
    private static final Logger log = YukonLogManager.getLogger(DataStreamingPorterConnection.class);
    private static final String sendCommand = "putconfig behavior rfndatastreaming";
    private static final String readCommand = "getconfig behavior rfndatastreaming";
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    @Autowired private CommandExecutionService commandExecutionService;

    /**
     * Build a list of data streaming configuration commands for the specified devices. Porter will use the
     * configurations currently in the database for those devices.
     */
    public List<CommandRequestDevice> buildConfigurationCommandRequests(Collection<SimpleDevice> devices, CollectionAction action) {
        List<CommandRequestDevice> commands = devices.stream().map(device -> {
            return new CommandRequestDevice(action == CollectionAction.READ_DATA_STREAMING_CONFIG ? readCommand : sendCommand, device);
        }).collect(Collectors.toList());

        return commands;
    }
    
    /**
     * Send porter a list of devices to configure data streaming. Porter will use the configurations currently in the
     * database for those devices. All responses will be routed to the callback, which is responsible for updating the
     * reported configuration values in the database.
     */
    public void sendConfiguration(List<CommandRequestDevice> commands, CollectionActionResult result, DataStreamingConfigCallback callback,
            LiteYukonUser user) {
        CommandCompletionCallback<CommandRequestDevice> commandCompletionCallback = buildCallbackProxy(callback);
        result.addCancellationCallback(new CollectionActionCancellationCallback(StrategyType.PORTER, null, commandCompletionCallback));
        log.info("Sending data streaming configuration to Porter. " + commands);
        commandExecutionService.execute(commands, commandCompletionCallback, result.getExecution(), true, user);
    }

    /**
     * This callback is a go-between. It receives the Porter responses from the command executor, parses the JSON into
     * a POJO, and passes it to the nested DataStreamingConfigCallback for updating the UI and database.
     */
    private CommandCompletionCallback<CommandRequestDevice> buildCallbackProxy(DataStreamingConfigCallback configCallback) {
        CommandCompletionCallback<CommandRequestDevice> callback = new CommandCompletionCallback<CommandRequestDevice>() {

            @Override
            public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                log.info("Recieved error for device="+ command.getDevice()+" error="+error);
                ReportedDataStreamingConfig config = null;
                //  If the error is a JSON string, it's a config report
                if (error.getPorter().startsWith(DataStreamingPorterUtil.porterJsonPrefix)) {
                    try {
                        config = DataStreamingPorterUtil.extractReportedDataStreamingConfig(error.getPorter());
                    } catch(IOException e) {
                        log.debug("Error text appeared to be JSON, but could not be decoded: " + error);
                    }
                }
                configCallback.receivedConfigError(command.getDevice(), error, config);
            }
            
            @Override
            public void receivedLastResultString(CommandRequestDevice command, String value) {
                SimpleDevice device = command.getDevice();
                try {
                    ReportedDataStreamingConfig config = null;
                    if (value.startsWith(DataStreamingPorterUtil.porterJsonPrefix)) {
                        config = DataStreamingPorterUtil.extractReportedDataStreamingConfig(value);
                    }
                    configCallback.receivedConfigSuccess(device, config);
                    log.debug("last result="+value);
                } catch(IOException e) {
                    DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(1027);
                    String error = "Unable to parse data streaming metric response json from Porter, for device " 
                            + device.getDeviceId() + " : \"" + value + "\"";
                    log.error(error, e);
                    MessageSourceResolvable detail = YukonMessageSourceResolvable.createSingleCodeWithArguments(
                        "yukon.common.device.errorDetail", error);
                    SpecificDeviceErrorDescription deviceError =
                        new SpecificDeviceErrorDescription(errorDescription, error, detail);
                    configCallback.receivedConfigError(device, deviceError, null);
                }
            }

            @Override
            public void complete() {
                configCallback.complete();
            }

            @Override
            public void processingExceptionOccured(String reason) {
                configCallback.processingExceptionOccured(reason);
            }           
        };
        
        return callback;
    }
}

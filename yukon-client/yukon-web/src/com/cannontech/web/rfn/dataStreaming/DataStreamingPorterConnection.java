package com.cannontech.web.rfn.dataStreaming;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.callbackResult.DataStreamingConfigCallback;
import com.cannontech.common.bulk.callbackResult.DataStreamingConfigResult;
import com.cannontech.common.device.commands.CommandCallback;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.impl.PorterCommandCallback;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.dev.dataStreaming.DataStreamingDevSettings;

/**
 * Abstraction of the Porter connection for data streaming purposes. This class handles the actual data streaming config
 * messages sent to Porter, which then sends configuration messages to the devices.
 */
public class DataStreamingPorterConnection {
    private static final Logger log = YukonLogManager.getLogger(DataStreamingPorterConnection.class);
    private static final CommandCallback commandCallback = new PorterCommandCallback("putconfig behavior rfndatastreaming");

    @Autowired private CommandRequestDeviceExecutor commandExecutor;
    @Autowired private DataStreamingDevSettings devSettings;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;


    /**
     * Build a list of data streaming configuration commands for the specified devices. Porter will use the
     * configurations currently in the database for those devices.
     */
    public List<CommandRequestDevice> buildConfigurationCommandRequests(Collection<SimpleDevice> devices) {
        List<CommandRequestDevice> commands = devices.stream().map(device -> {
            CommandRequestDevice command = new CommandRequestDevice();
            command.setDevice(device);
            command.setCommandCallback(commandCallback);
            return command;
        }).collect(Collectors.toList());

        return commands;
    }

    /**
     * Send porter a list of devices to configure data streaming. Porter will use the configurations currently in the
     * database for those devices. All responses will be routed to the callback, which is responsible for updating the
     * reported configuration values in the database.
     */
    public void sendConfiguration(List<CommandRequestDevice> commands, DataStreamingConfigResult result,
            LiteYukonUser user) {
        CommandCompletionCallback<CommandRequestDevice> commandCompletionCallback =
            buildCallbackProxy(result.getConfigCallback());
        result.setCommandCompletionCallback(commandCompletionCallback);
        log.info("Sending data streaming configuration to Porter. " + commands);
        commandExecutor.createTemplateAndExecute(result.getExecution(), commandCompletionCallback, commands, user,
            false);
    }

    /**
     * This callback is a go-between. It receives the Porter responses from the command executor, parses the JSON into
     * a POJO, and passes it to the nested DataStreamingConfigCallback for updating the UI and database.
     */
    private CommandCompletionCallback<CommandRequestDevice> buildCallbackProxy(DataStreamingConfigCallback configCallback) {
        CommandCompletionCallbackAdapter<CommandRequestDevice> callback = new CommandCompletionCallbackAdapter<CommandRequestDevice>() {

            @Override
            public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                log.info("Recieved error for device="+ command.getDevice()+" error="+error);
                ReportedDataStreamingConfig config = null;
                //  If the error is a JSON string, it's a config report
                if (error.getPorter().startsWith("json{")) {
                    try {
                        config = JsonUtils.fromJson(error.getPorter(), ReportedDataStreamingConfig.class);
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
                    ReportedDataStreamingConfig config = JsonUtils.fromJson(value, ReportedDataStreamingConfig.class);
                    configCallback.receivedConfigReport(device, config);
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

    public void cancel(DataStreamingConfigResult result, LiteYukonUser user) {
        if (devSettings.isSimulatePorterConfigResponse()) {
            CommandRequestExecution cre = result.getExecution();
            cre.setStopTime(new Date());
            cre.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.CANCELLED);
            commandRequestExecutionDao.saveOrUpdate(cre);
        }else{
            commandExecutor.cancelExecution(result.getCommandCompletionCallback(), user, true);
        }
    }
}

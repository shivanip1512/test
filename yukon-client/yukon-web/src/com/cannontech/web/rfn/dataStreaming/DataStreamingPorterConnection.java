package com.cannontech.web.rfn.dataStreaming;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.callbackResult.DataStreamingConfigCallback;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCallback;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.impl.PorterCommandCallback;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.device.streaming.dao.DeviceBehaviorDao;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.dev.dataStreaming.DataStreamingDevSettings;
import com.cannontech.web.dev.dataStreaming.FakeDataStreamingCommandRequestDeviceExecutor;
import com.cannontech.web.tools.commander.service.CommanderServiceImpl;

/**
 * Abstraction of the Porter connection for data streaming purposes. This class handles the actual data streaming config
 * messages sent to Porter, which then sends configuration messages to the devices.
 */
public class DataStreamingPorterConnection {
    private static final Logger log = YukonLogManager.getLogger(CommanderServiceImpl.class);
    private static final CommandCallback commandCallback = new PorterCommandCallback("putconfig behavior rfndatastreaming");

    @Autowired private CommandRequestDeviceExecutor commandExecutor;
    @Autowired private DeviceBehaviorDao deviceBehaviorDao;
    @Autowired private DataStreamingDevSettings devSettings;
    private CommandRequestDeviceExecutor fakeCommandExecutor;
    
    @PostConstruct
    public void init() {
        fakeCommandExecutor = new FakeDataStreamingCommandRequestDeviceExecutor(deviceBehaviorDao);
    }
    
    /**
     * Build a list of data streaming configuration commands for the specified devices. Porter will use the
     * configurations currently in the database for those devices.
     */
    public List<CommandRequestDevice> buildConfigurationCommandRequests(Collection<SimpleDevice> devices) {
        List<CommandRequestDevice> commands = devices.stream().map(
            device -> {
                CommandRequestDevice command = new CommandRequestDevice();
                command.setDevice(device);
                command.setCommandCallback(commandCallback);
                return command;
            }
        ).collect(Collectors.toList());
        
        return commands;
    }
    
    /**
     * Send porter a list of devices to configure data streaming. Porter will use the configurations currently in the
     * database for those devices. All responses will be routed to the callback, which is responsible for updating the
     * reported configuration values in the database.
     */
    public void sendConfiguration(List<CommandRequestDevice> commands, DataStreamingConfigCallback callback, LiteYukonUser user) {
        if (devSettings.isSimulatePorterConfigResponse()) {
            //If developer settings are set to simulate, replace the real commandExecutor with a simulator.
            log.debug("Simulating data streaming configuration via fake executor.");
            fakeCommandExecutor.execute(commands, buildCallbackProxy(callback), DeviceRequestType.DATA_STREAMING_CONFIG, user);
        } else {
            //Otherwise send the commands to Porter
            log.info("Sending data streaming configuration to Porter. " + commands);
            commandExecutor.execute(commands, buildCallbackProxy(callback), DeviceRequestType.DATA_STREAMING_CONFIG, user);
        }
    }
    
    /**
     * This callback is a go-between. It receives the Porter responses from the command executor, parses the JSON into
     * a POJO, and passes it to the nested DataStreamingConfigCallback for updating the UI and database.
     */
    private CommandCompletionCallback<CommandRequestDevice> buildCallbackProxy(DataStreamingConfigCallback nestedCallback) {
        CommandCompletionCallback<CommandRequestDevice> callback = new CommandCompletionCallbackAdapter<CommandRequestDevice>() {

            @Override
            public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                nestedCallback.receivedConfigError(command.getDevice(), error);
            }
            
            @Override
            public void receivedLastResultString(CommandRequestDevice command, String value) {
                processResult(command, value);
            }

            @Override
            public void complete() {
                nestedCallback.complete();
            }

            @Override
            public void cancel() {
                nestedCallback.cancel();
            }

            @Override
            public void receivedIntermediateError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                nestedCallback.receivedConfigError(command.getDevice(), error);
            }

            @Override
            public void receivedIntermediateResultString(CommandRequestDevice command, String value) {
                processResult(command, value);
            }

            @Override
            public void processingExceptionOccured(String reason) {
                log.error("Processing exception occurred sending data streaming config: " + reason);
            }
            
            private void processResult(CommandRequestDevice command, String value) {
                SimpleDevice device = command.getDevice();
                try {
                    ReportedDataStreamingConfig config = JsonUtils.fromJson(value, ReportedDataStreamingConfig.class);
                    nestedCallback.receivedConfigReport(device, config);
                } catch(IOException e) {
                    log.error("Unable to parse data streaming metric response json from Porter, for device " 
                              + device.getDeviceId() + " : \"" + value + "\"", e);
                    nestedCallback.receivedConfigError(device, null); //TODO: clean this up
                }
            }
        };
        
        return callback;
    }
}

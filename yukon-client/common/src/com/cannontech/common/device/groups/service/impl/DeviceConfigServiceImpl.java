package com.cannontech.common.device.groups.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.commands.impl.VerifyConfigCommandCompletionCallback;
import com.cannontech.common.device.commands.impl.WaitableCommandCompletionCallback;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.groups.service.DeviceConfigService;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;

public class DeviceConfigServiceImpl implements DeviceConfigService {
    
    private GroupCommandExecutor groupCommandExecutor;
    private CommandRequestDeviceExecutor commandRequestExecutor;
    
    public String pushConfigs(DeviceCollection deviceCollection, boolean force, SimpleCallback<GroupCommandResult> callback, LiteYukonUser user) {
        String commandString = "putconfig emetcon install all";
        if (force) {
            commandString += " force";
        }
        return groupCommandExecutor.execute(deviceCollection,commandString, callback, user);
    }
    
    @Override
    public Map<YukonDevice, VerifyResult> verifyConfigs(DeviceCollection deviceCollection, LiteYukonUser user) {
        final String commandString = "putconfig emetcon install all verify";
        
        ObjectMapper<YukonDevice, CommandRequestDevice> objectMapper = new ObjectMapper<YukonDevice, CommandRequestDevice>() {
            public CommandRequestDevice map(YukonDevice from) throws ObjectMappingException {
                return buildStandardRequest(from, commandString);
            }
        };
        
        List<CommandRequestDevice> requests = new MappingList<YukonDevice, CommandRequestDevice>(deviceCollection.getDeviceList(), objectMapper);
        
        VerifyConfigCommandCompletionCallback commandCompletionCallback = new VerifyConfigCommandCompletionCallback(deviceCollection.getDeviceList());
        
        WaitableCommandCompletionCallback<CommandRequestDevice> waitableCallback = new WaitableCommandCompletionCallback<CommandRequestDevice>(commandCompletionCallback);
        
        commandRequestExecutor.execute(requests, waitableCallback, user);
        try {
            waitableCallback.waitForCompletion(60, 120);
        } catch (InterruptedException e) {
            return commandCompletionCallback.getResults();
        } catch (TimeoutException e) {
            CTILogger.error(e);
        }
        
        return commandCompletionCallback.getResults();
    }
    
    private CommandRequestDevice buildStandardRequest(YukonDevice device, final String command) {
        CommandRequestDevice request = new CommandRequestDevice();
        request.setDevice(device);
        request.setBackgroundPriority(true);
        
        final String commandStr = command + " update";
        request.setCommand(commandStr);
        return request;
    }
    
    @Autowired
    public void setGroupCommandExecutor(GroupCommandExecutor groupCommandExecutor) {
        this.groupCommandExecutor = groupCommandExecutor;
    }

    @Autowired
    public void setCommandRequestExecutor(CommandRequestDeviceExecutor commandRequestExecutor) {
        this.commandRequestExecutor = commandRequestExecutor;
    }
}

package com.cannontech.common.device.groups.service.impl;

import java.util.Collections;
import java.util.List;
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
import com.cannontech.common.device.commands.VerifyConfigCommandResult;
import com.cannontech.common.device.commands.impl.VerifyConfigCommandCompletionCallback;
import com.cannontech.common.device.commands.impl.WaitableCommandCompletionCallback;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.groups.service.DeviceConfigService;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Lists;

public class DeviceConfigServiceImpl implements DeviceConfigService {
    
    private GroupCommandExecutor groupCommandExecutor;
    private CommandRequestDeviceExecutor commandRequestExecutor;
    
    public String pushConfigs(DeviceCollection deviceCollection, String method, SimpleCallback<GroupCommandResult> callback, LiteYukonUser user) {
        String commandString = "putconfig emetcon install all";
        if (method.equalsIgnoreCase("force")) {
            commandString += " force";
        } else if (method.equalsIgnoreCase("read")) {
            commandString = "getconfig model";
        }
        return groupCommandExecutor.execute(deviceCollection,commandString, callback, user);
    }
    
    @Override
    public VerifyConfigCommandResult verifyConfigs(Iterable<? extends YukonDevice> devices, LiteYukonUser user) {
        List<YukonDevice> deviceList = Lists.newArrayList(devices);
        
        final String commandString = "putconfig emetcon install all verify";
        
        ObjectMapper<YukonDevice, CommandRequestDevice> objectMapper = new ObjectMapper<YukonDevice, CommandRequestDevice>() {
            public CommandRequestDevice map(YukonDevice from) throws ObjectMappingException {
                return buildStandardRequest(from, commandString);
            }
        };
        
        List<CommandRequestDevice> requests = new MappingList<YukonDevice, CommandRequestDevice>(deviceList, objectMapper);
        
        VerifyConfigCommandCompletionCallback commandCompletionCallback = new VerifyConfigCommandCompletionCallback(deviceList);
        
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
    
    @Override
    public VerifyResult verifyConfig(YukonDevice device, LiteYukonUser user) {
        VerifyConfigCommandResult verifyConfigResult = verifyConfigs(Collections.singleton(device), user);
        return verifyConfigResult.getResults().get(device);
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

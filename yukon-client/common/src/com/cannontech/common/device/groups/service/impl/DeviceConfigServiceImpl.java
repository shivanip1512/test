package com.cannontech.common.device.groups.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.commands.VerifyConfigCommandResult;
import com.cannontech.common.device.commands.impl.WaitableCommandCompletionCallback;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.groups.service.DeviceConfigService;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Lists;

public class DeviceConfigServiceImpl implements DeviceConfigService {
    
    private GroupCommandExecutor groupCommandExecutor;
    private CommandRequestDeviceExecutor commandRequestExecutor;
    private Logger log = YukonLogManager.getLogger(DeviceConfigServiceImpl.class);
    private DeviceConfigurationDao deviceConfigurationDao;
    private MeterDao meterDao;
    
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

        final VerifyConfigCommandResult result = new VerifyConfigCommandResult();
        
        for(YukonDevice device : devices) {
            Meter meter = meterDao.getForYukonDevice(device);
            VerifyResult verifyResult = new VerifyResult(meter);
            verifyResult.setConfig(deviceConfigurationDao.findConfigurationForDevice(device));
            result.getVerifyResultsMap().put(device, verifyResult);
        }
        
        CommandCompletionCallbackAdapter<CommandRequestDevice> commandCompletionCallback = new CommandCompletionCallbackAdapter<CommandRequestDevice>() {
            @Override
            public void receivedIntermediateResultString(CommandRequestDevice command, String value) {
                YukonDevice device = command.getDevice();
                result.addResultString(device, value);
            }
            
            @Override
            public void receivedIntermediateError(CommandRequestDevice command, DeviceErrorDescription error) {
                YukonDevice device = command.getDevice();
                result.addError(device, error.getPorter());
            }
            
            @Override
            public void receivedLastError(CommandRequestDevice command, DeviceErrorDescription error) {
                YukonDevice device = command.getDevice();
                result.addError(device, error.getPorter());
                result.handleFailure(device);
            }

            @Override
            public void receivedLastResultString(CommandRequestDevice command, String value) {
                YukonDevice device = command.getDevice();
                result.addResultString(device, value);
                if(result.getVerifyResultsMap().get(device).getDiscrepancies().isEmpty()) {
                    result.handleSuccess(device);
                }else {
                    result.handleFailure(device);
                }
            }
        };
        
        WaitableCommandCompletionCallback<CommandRequestDevice> waitableCallback = new WaitableCommandCompletionCallback<CommandRequestDevice>(commandCompletionCallback);
        
        commandRequestExecutor.execute(requests, waitableCallback, CommandRequestExecutionType.DEVICE_CONFIG_VERIFY, user);
        try {
            waitableCallback.waitForCompletion(60, 120);
        } catch (InterruptedException e) {
            log.error(e);
        } catch (TimeoutException e) {
            log.error(e);
        }
        
        return result;
    }
    
    @Override
    public VerifyResult verifyConfig(YukonDevice device, LiteYukonUser user) {
        VerifyConfigCommandResult verifyConfigResult = verifyConfigs(Collections.singleton(device), user);
        return verifyConfigResult.getVerifyResultsMap().get(device);
    }
    
    @Override
    public CommandResultHolder readConfig(YukonDevice device, LiteYukonUser user) throws Exception {
        String commandString = "getconfig model";
        CommandResultHolder resultHolder = commandRequestExecutor.execute(device, commandString, CommandRequestExecutionType.DEVICE_COMMAND, user);
        return resultHolder;
    }
    
    @Override
    public CommandResultHolder pushConfig(YukonDevice device, LiteYukonUser user) throws Exception {
        String commandString = "putconfig emetcon install all force";
        CommandResultHolder resultHolder = commandRequestExecutor.execute(device, commandString, CommandRequestExecutionType.DEVICE_CONFIG_PUSH, user);
        return resultHolder;
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
    
    @Autowired
    public void setDeviceConfigurationDao(DeviceConfigurationDao deviceConfigurationDao) {
        this.deviceConfigurationDao = deviceConfigurationDao;
    }
    
    @Autowired
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
}

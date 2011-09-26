package com.cannontech.common.device.config.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.commands.VerifyConfigCommandResult;
import com.cannontech.common.device.commands.WaitableCommandCompletionCallbackFactory;
import com.cannontech.common.device.commands.impl.PorterCommandCallback;
import com.cannontech.common.device.commands.impl.WaitableCommandCompletionCallback;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.config.service.DeviceConfigService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
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
    private PaoDefinitionDao paoDefinitionDao;
    private TemporaryDeviceGroupService temporaryDeviceGroupService;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    private WaitableCommandCompletionCallbackFactory waitableCommandCompletionCallbackFactory;

    private String sendConfigCommand(DeviceCollection deviceCollection, SimpleCallback<GroupCommandResult> callback, String command, DeviceRequestType type, LiteYukonUser user) {
        List<SimpleDevice> unsupportedDevices = new ArrayList<SimpleDevice>();
        List<SimpleDevice> supportedDevices = new ArrayList<SimpleDevice>();
        for(SimpleDevice device : deviceCollection.getDeviceList()){
        	ConfigurationBase configurationBase = deviceConfigurationDao.findConfigurationForDevice(device);
        	if (configurationBase != null &&
        			paoDefinitionDao.isTagSupported(device.getDeviceType(), configurationBase.getType().getSupportedDeviceTag())) {
        		supportedDevices.add(device);
        	} else {
        		unsupportedDevices.add(device);
        	}
        }
        
        StoredDeviceGroup unsupportedGroup = temporaryDeviceGroupService.createTempGroup(null);
        StoredDeviceGroup supportedGroup = temporaryDeviceGroupService.createTempGroup(null);
        deviceGroupMemberEditorDao.addDevices(unsupportedGroup, unsupportedDevices);
        deviceGroupMemberEditorDao.addDevices(supportedGroup, supportedDevices);
        DeviceCollection unsupportedCollection = deviceGroupCollectionHelper.buildDeviceCollection(unsupportedGroup);
        DeviceCollection supportedCollection = deviceGroupCollectionHelper.buildDeviceCollection(supportedGroup);
        
        String key = groupCommandExecutor.execute(supportedCollection, command, type, callback, user);
        GroupCommandResult result = groupCommandExecutor.getResult(key);
        result.setHandleUnsupported(true);
        result.setUnsupportedCollection(unsupportedCollection);
        
        return key;
    }
    
    @Override
    public String sendConfigs(DeviceCollection deviceCollection, String method, SimpleCallback<GroupCommandResult> callback, LiteYukonUser user) {
        String commandString = "putconfig emetcon install all";
        if (method.equalsIgnoreCase("force")) {
            commandString += " force";
        }
        
        return sendConfigCommand(deviceCollection, callback, commandString, DeviceRequestType.GROUP_DEVICE_CONFIG_SEND, user);
    }
    
    @Override
    public String readConfigs(DeviceCollection deviceCollection, SimpleCallback<GroupCommandResult> callback, LiteYukonUser user) {
        String commandString = "getconfig install all";
        
        return sendConfigCommand(deviceCollection, callback, commandString, DeviceRequestType.GROUP_DEVICE_CONFIG_READ, user);
    }
    
    @Override
    public VerifyConfigCommandResult verifyConfigs(Iterable<? extends YukonDevice> devices, LiteYukonUser user) {
        final VerifyConfigCommandResult result = new VerifyConfigCommandResult();
        final String commandString = "putconfig emetcon install all verify";
        List<YukonDevice> deviceList = Lists.newArrayList(devices);
        
        ObjectMapper<YukonDevice, CommandRequestDevice> objectMapper = new ObjectMapper<YukonDevice, CommandRequestDevice>() {
            public CommandRequestDevice map(YukonDevice from) throws ObjectMappingException {
                return buildStandardRequest(from, commandString);
            }
        };
        
        for(YukonDevice device : devices) {
            Meter meter = meterDao.getForYukonDevice(device);
            ConfigurationBase configurationBase = deviceConfigurationDao.findConfigurationForDevice(device);
            if (configurationBase != null && 
            		paoDefinitionDao.isTagSupported(meter.getPaoType(), configurationBase.getType().getSupportedDeviceTag())) {
                VerifyResult verifyResult = new VerifyResult(meter);
                verifyResult.setConfig(configurationBase);
                result.getVerifyResultsMap().put(new SimpleDevice(device.getPaoIdentifier()), verifyResult);
            }else {
                deviceList.remove(device);
                result.getUnsupportedList().add(new SimpleDevice(device));
            }
        }

        List<CommandRequestDevice> requests = new MappingList<YukonDevice, CommandRequestDevice>(deviceList, objectMapper);
        
        CommandCompletionCallbackAdapter<CommandRequestDevice> commandCompletionCallback = new CommandCompletionCallbackAdapter<CommandRequestDevice>() {
            @Override
            public void receivedIntermediateResultString(CommandRequestDevice command, String value) {
                SimpleDevice device = command.getDevice();
                result.addResultString(device, value);
            }
            
            @Override
            public void receivedIntermediateError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                SimpleDevice device = command.getDevice();
                result.addError(device, error.getPorter());
            }
            
            @Override
            public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                SimpleDevice device = command.getDevice();
                result.addError(device, error.getPorter());
                result.handleFailure(device);
            }

            @Override
            public void receivedLastResultString(CommandRequestDevice command, String value) {
                SimpleDevice device = command.getDevice();
                result.addResultString(device, value);
                if(result.getVerifyResultsMap().get(device).getDiscrepancies().isEmpty()) {
                    result.handleSuccess(device);
                }else {
                    result.handleFailure(device);
                }
            }
            
            @Override
            public void processingExceptionOccured(String reason) {
                result.setExceptionReason(reason);
            }
        };

        WaitableCommandCompletionCallback<CommandRequestDevice> waitableCallback =
            waitableCommandCompletionCallbackFactory.createWaitable(commandCompletionCallback);

        commandRequestExecutor.execute(requests, waitableCallback, DeviceRequestType.GROUP_DEVICE_CONFIG_VERIFY, user);
        try {
            waitableCallback.waitForCompletion();
        } catch (InterruptedException e) {
            result.setExceptionReason(e.getMessage());
            log.error(e);
        } catch (TimeoutException e) {
            result.setExceptionReason("Operation Timed Out");
            log.error(e);
        }
        
        return result;
    }
    
    @Override
    public VerifyResult verifyConfig(YukonDevice device, LiteYukonUser user) {
        VerifyConfigCommandResult verifyConfigResult = verifyConfigs(Collections.singleton(device), user);
        return verifyConfigResult.getVerifyResultsMap().get(new SimpleDevice(device));
    }
    
    @Override
    public CommandResultHolder readConfig(YukonDevice device, LiteYukonUser user) throws Exception {
        String commandString = "getconfig install all";
        CommandResultHolder resultHolder = commandRequestExecutor.execute(device, commandString, DeviceRequestType.GROUP_DEVICE_CONFIG_READ, user);
        return resultHolder;
    }
    
    @Override
    public CommandResultHolder sendConfig(YukonDevice device, LiteYukonUser user) throws Exception {
        String commandString = "putconfig emetcon install all force";
        CommandResultHolder resultHolder = commandRequestExecutor.execute(device, commandString, DeviceRequestType.GROUP_DEVICE_CONFIG_SEND, user);
        return resultHolder;
    }

    @Override
    public boolean isDeviceConfigAvailable(PaoType paoType) {
    	List<ConfigurationBase> configurations = deviceConfigurationDao.getAllConfigurations();
    	for (ConfigurationBase configurationBase : configurations) {
        	PaoTag paoTag = configurationBase.getType().getSupportedDeviceTag();
        	if (paoDefinitionDao.isTagSupported(paoType, paoTag)) {
        		return true;
        	}
		}
        return false;
    }
    
    private CommandRequestDevice buildStandardRequest(YukonDevice device, final String command) {
        CommandRequestDevice request = new CommandRequestDevice();
        request.setDevice(new SimpleDevice(device.getPaoIdentifier()));
        
        request.setCommandCallback(new PorterCommandCallback(command));
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
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
    
    @Autowired
    public void setTemporaryDeviceGroupService(TemporaryDeviceGroupService temporaryDeviceGroupService) {
        this.temporaryDeviceGroupService = temporaryDeviceGroupService;
    }
    
    @Autowired
    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    
    @Autowired
    public void setDeviceGroupCollectionHelper(DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
    }
    
    @Autowired
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }

    @Autowired
    public void setWaitableCommandCompletionCallbackFactory(
            WaitableCommandCompletionCallbackFactory waitableCommandCompletionCallbackFactory) {
        this.waitableCommandCompletionCallbackFactory = waitableCommandCompletionCallbackFactory;
    }
}

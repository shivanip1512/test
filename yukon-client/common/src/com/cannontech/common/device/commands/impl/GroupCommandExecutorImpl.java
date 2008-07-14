package com.cannontech.common.device.commands.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.GroupCommandCompletionCallback;
import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Implementation class for GroupCommandExecutor
 */
public class GroupCommandExecutorImpl implements GroupCommandExecutor {

    private CommandRequestDeviceExecutor commandRequestExecutor;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private TemporaryDeviceGroupService temporaryDeviceGroupService;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;

    private RecentResultsCache<GroupCommandResult> resultsCache
        = new RecentResultsCache<GroupCommandResult>();
    
    private Logger log = YukonLogManager.getLogger(GroupCommandExecutorImpl.class);
    
    @Autowired
    public void setCommandRequestExecutor(CommandRequestDeviceExecutor commandRequestExecutor) {
        this.commandRequestExecutor = commandRequestExecutor;
    }

    @Autowired
    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    
    @Autowired
    public void setTemporaryDeviceGroupService(TemporaryDeviceGroupService temporaryDeviceGroupService) {
        this.temporaryDeviceGroupService = temporaryDeviceGroupService;
    }
    
    @Autowired
    public void setDeviceGroupCollectionHelper(DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
    }

    public String execute(final DeviceCollection deviceCollection, final String command, final SimpleCallback<GroupCommandResult> callback,
                        LiteYukonUser user)
    throws PaoAuthorizationException {
        
        ObjectMapper<YukonDevice, CommandRequestDevice> objectMapper = new ObjectMapper<YukonDevice, CommandRequestDevice>() {
            public CommandRequestDevice map(YukonDevice from) throws ObjectMappingException {
                return buildStandardRequest(from, command);
            }
        };
        List<CommandRequestDevice> requests = new MappingList<YukonDevice, CommandRequestDevice>(deviceCollection.getDeviceList(), objectMapper);
        
        // create temporary groups
        final StoredDeviceGroup successGroup = temporaryDeviceGroupService.createTempGroup(null);
        final StoredDeviceGroup failureGroup = temporaryDeviceGroupService.createTempGroup(null);
        
        final GroupCommandResult groupCommandResult = new GroupCommandResult();
        
        GroupCommandCompletionCallback commandCompletionCallback = new GroupCommandCompletionCallback() {
            @Override
            public void doComplete() {
                try {
                    callback.handle(groupCommandResult);
                } catch (Exception e) {
                    log.warn("There was an error executing the callback", e);
                }
            }
            
            @Override
            public void handleSuccess(YukonDevice device) {
                deviceGroupMemberEditorDao.addDevices(successGroup, device);
            }
            
            @Override
            public void handleFailure(YukonDevice device) {
                deviceGroupMemberEditorDao.addDevices(failureGroup, device);
            }
            
        };
        
        groupCommandResult.setCommand(command);
        groupCommandResult.setResultHolder(commandCompletionCallback);
        groupCommandResult.setDeviceCollection(deviceCollection);
        
        DeviceCollection successCollection = deviceGroupCollectionHelper.buildDeviceCollection(successGroup);
        groupCommandResult.setSuccessCollection(successCollection);
        DeviceCollection failureCollectioin = deviceGroupCollectionHelper.buildDeviceCollection(failureGroup);
        groupCommandResult.setFailureCollection(failureCollectioin);
        
        String key = resultsCache.addResult(groupCommandResult);
        // is the weird?
        groupCommandResult.setKey(key);
        
        commandRequestExecutor.execute(requests,
                                       commandCompletionCallback,
                                       user);
        
        log.debug("executing " + command + " on the " + requests.size() + " devices in " + deviceCollection);
        
        return key;
    }
    
    private CommandRequestDevice buildStandardRequest(YukonDevice device, final String command) {
        CommandRequestDevice request = new CommandRequestDevice();
        request.setDevice(device);
        request.setBackgroundPriority(true);
        
        final String commandStr = command + " update" + " noqueue";
        request.setCommand(commandStr);
        return request;
    }

    public List<GroupCommandResult> getCompleted() {
        return resultsCache.getCompleted();
    }

    public List<GroupCommandResult> getPending() {
        return resultsCache.getPending();
    }

    public GroupCommandResult getResult(String id) {
        return resultsCache.getResult(id);
    }
}

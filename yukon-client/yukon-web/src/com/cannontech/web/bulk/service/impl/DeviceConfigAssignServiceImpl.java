package com.cannontech.web.bulk.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.dao.CollectionActionDao;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionBulkProcessorCallback;
import com.cannontech.common.bulk.collection.device.model.CollectionActionInput;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.bulk.processor.ProcessorFactory;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DeviceConfigState;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.service.DeviceConfigService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.service.DeviceConfigAssignService;

public class DeviceConfigAssignServiceImpl implements DeviceConfigAssignService {
    @Autowired private ProcessorFactory processorFactory;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private DeviceConfigService deviceConfigService;
    @Autowired protected CollectionActionService collectionActionService;
    @Autowired private CollectionActionDao collectionActionDao;
    @Resource(name="oneAtATimeProcessor") private BulkProcessor bulkProcessor;
    @Autowired private TemporaryDeviceGroupService tempGroupService;
    @Autowired private  DeviceGroupMemberEditorDao editorDao;
    @Autowired private DeviceGroupCollectionHelper groupHelper;
    
    @Override 
    public CollectionActionResult assign(int configuration, DeviceCollection deviceCollection, YukonUserContext userContext) {
        Map<Integer, DeviceConfigState> deviceToState = deviceConfigurationDao.getDeviceConfigStatesByDeviceIds(getDeviceIds(deviceCollection.getDeviceList()));
        DeviceConfiguration deviceConfig = deviceConfigurationDao.getDeviceConfiguration(configuration);
        Processor<SimpleDevice> processor = processorFactory.createAssignConfigurationToYukonDeviceProcessor(deviceConfig,
                deviceToState, userContext);

        LinkedHashMap<String, String> userInputs = new LinkedHashMap<>();
        userInputs.put(CollectionActionInput.CONFIGURATION.name(), deviceConfig.getName());
        CollectionActionResult result = collectionActionService.createResult(CollectionAction.ASSIGN_CONFIG, userInputs,
                deviceCollection, userContext);
        ObjectMapper<SimpleDevice, SimpleDevice> mapper = new PassThroughMapper<>();
        bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, processor,
                new AssignUnassignCallback(result, collectionActionService, collectionActionDao));
        return result;
    }
    
    @Override 
    public void assign(int configuration, SimpleDevice device, YukonUserContext userContext) {
        DeviceCollection deviceCollection = getDeviceCollection(device);
        CollectionActionResult result = assign(configuration, deviceCollection, userContext);
        while(!result.isComplete()) {
            //wait for completion
            Thread.onSpinWait();
        }
    }
    
    /**
     * Returns device collection of one device
     */
    private DeviceCollection getDeviceCollection(SimpleDevice device) {
        StoredDeviceGroup tempGroup = tempGroupService.createTempGroup();
        editorDao.addDevices(tempGroup, device);
        DeviceCollection deviceCollection  = groupHelper.buildDeviceCollection(tempGroup);
        return deviceCollection;
    }
    
    @Override 
    public CollectionActionResult unassign(DeviceCollection deviceCollection, YukonUserContext userContext) {
        Map<Integer, DeviceConfigState> deviceToState = deviceConfigurationDao
                .getDeviceConfigStatesByDeviceIds(getDeviceIds(deviceCollection.getDeviceList()));
        Processor<SimpleDevice> processor = processorFactory.createUnassignConfigurationToYukonDeviceProcessor(deviceToState,
                userContext.getYukonUser());
        CollectionActionResult result = collectionActionService.createResult(CollectionAction.UNASSIGN_CONFIG, null,
                deviceCollection, userContext);
        ObjectMapper<SimpleDevice, SimpleDevice> mapper = new PassThroughMapper<>();
        bulkProcessor.backgroundBulkProcess(deviceCollection.iterator(), mapper, processor,
                new AssignUnassignCallback(result, collectionActionService, collectionActionDao));
        return result;
    }
    
    @Override 
    public void unassign(SimpleDevice device, YukonUserContext userContext) {
        DeviceCollection deviceCollection = getDeviceCollection(device);
        CollectionActionResult result = unassign(deviceCollection, userContext);
        //wait for completion
        while(!result.isComplete()) {
            Thread.onSpinWait();
        }
    }
    
    private class AssignUnassignCallback extends CollectionActionBulkProcessorCallback {
        public AssignUnassignCallback(CollectionActionResult result, CollectionActionService collectionActionService,
                CollectionActionDao collectionActionDao) {
            super(result, collectionActionService, collectionActionDao);
        }
        
        @Override
        public void processingSucceeded() {
            deviceConfigService.updateConfigStateForAssignAndUnassign(result);
            super.processingSucceeded();
        }
    }
    
    private List<Integer> getDeviceIds(List<SimpleDevice> devices) {
        return devices
                .stream()
                .map(device -> device.getDeviceId())
                .collect(Collectors.toList());
    }
}
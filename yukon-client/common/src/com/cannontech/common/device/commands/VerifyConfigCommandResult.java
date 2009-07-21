package com.cannontech.common.device.commands;

import java.util.HashMap;
import java.util.Map;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.VerifyResult;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.spring.YukonSpringHook;

public class VerifyConfigCommandResult {
    
    Map<YukonDevice, VerifyResult> results = new HashMap<YukonDevice, VerifyResult>();
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao = YukonSpringHook.getBean("deviceGroupMemberEditorDao", DeviceGroupMemberEditorDao.class);
    private TemporaryDeviceGroupService temporaryDeviceGroupService = YukonSpringHook.getBean("temporaryDeviceGroupService", TemporaryDeviceGroupService.class);;
    private StoredDeviceGroup successGroup;
    private StoredDeviceGroup failureGroup;

    public VerifyConfigCommandResult() {
        successGroup = temporaryDeviceGroupService.createTempGroup(null);
        failureGroup = temporaryDeviceGroupService.createTempGroup(null);
    }
    
    public void initializeResults(Iterable<? extends YukonDevice> devices) {
        MeterDao meterDao = YukonSpringHook.getBean("meterDao", MeterDao.class);
        DeviceConfigurationDao deviceConfigurationDao = YukonSpringHook.getBean("deviceConfigurationDao", DeviceConfigurationDao.class);
        for(YukonDevice device : devices) {
            Meter meter = meterDao.getForYukonDevice(device);
            VerifyResult result = new VerifyResult(meter);
            result.setConfig(deviceConfigurationDao.getConfigurationForDevice(device));
            results.put(device, result);
        }
    }
    
    public void handleSuccess(YukonDevice device) {
        deviceGroupMemberEditorDao.addDevices(successGroup, device);
    }
    
    public void handleFailure(YukonDevice device) {
        deviceGroupMemberEditorDao.addDevices(failureGroup, device);
    }
    
    public StoredDeviceGroup getFailureGroup() {
        return failureGroup;
    }
    
    public StoredDeviceGroup getSuccessGroup() {
        return successGroup;
    }
    
    public Map<YukonDevice, VerifyResult> getResults(){
        return results;
    }
    
}

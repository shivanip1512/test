package com.cannontech.common.device.definition.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.cannontech.database.data.point.PointBase;

public class DeviceDefinitionServiceImpl implements DeviceDefinitionService {
    private PaoGroupsWrapper paoGroupsWrapper;
    private SimpleDeviceDefinitionService simpleDeviceDefinitionService;
    
    @Autowired
    public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
    }
    
    @Autowired
    public void setSimpleDeviceDefinitionService(
            SimpleDeviceDefinitionService simpleDeviceDefinitionService) {
        this.simpleDeviceDefinitionService = simpleDeviceDefinitionService;
    }
    
    public List<PointBase> createAllPointsForDevice(DeviceBase device) {
        return simpleDeviceDefinitionService.createAllPointsForDevice(getYukonDeviceForDevice(device));
    }

    public List<PointBase> createDefaultPointsForDevice(DeviceBase device) {
        return simpleDeviceDefinitionService.createDefaultPointsForDevice(getYukonDeviceForDevice(device));
    }

    public Set<DeviceDefinition> getChangeableDevices(DeviceBase device) {
        return simpleDeviceDefinitionService.getChangeableDevices(getYukonDeviceForDevice(device));
    }
    
    public Set<DeviceDefinition> getChangeableDevices(YukonDevice device) {
        return simpleDeviceDefinitionService.getChangeableDevices(device);
    }

    public Map<String, List<DeviceDefinition>> getDeviceDisplayGroupMap() {
        return simpleDeviceDefinitionService.getDeviceDisplayGroupMap();
    }

    public Set<PointTemplate> getNewPointTemplatesForTransfer(
                                                              DeviceBase device, DeviceDefinition deviceDefinition) {
        return simpleDeviceDefinitionService.getNewPointTemplatesForTransfer(getYukonDeviceForDevice(device), deviceDefinition);
    }

    public Set<PointTemplate> getPointTemplatesToAdd(DeviceBase device, DeviceDefinition deviceDefinition) {
        return simpleDeviceDefinitionService.getPointTemplatesToAdd(getYukonDeviceForDevice(device), deviceDefinition);
    }

    public Set<PointTemplate> getPointTemplatesToRemove(DeviceBase device, DeviceDefinition deviceDefinition) {
        return simpleDeviceDefinitionService.getPointTemplatesToRemove(getYukonDeviceForDevice(device), deviceDefinition);
    }

    public Set<PointTemplate> getPointTemplatesToTransfer(DeviceBase device, DeviceDefinition deviceDefinition) {
        return simpleDeviceDefinitionService.getPointTemplatesToTransfer(getYukonDeviceForDevice(device), deviceDefinition);
    }

    public boolean isDeviceTypeChangeable(DeviceBase device) {
        return simpleDeviceDefinitionService.isDeviceTypeChangeable(getYukonDeviceForDevice(device));
    }
    

    public YukonDevice getYukonDeviceForDevice(DeviceBase oldDevice) {
        YukonDevice device = new YukonDevice();
        device.setDeviceId(oldDevice.getPAObjectID());
        String typeStr = oldDevice.getPAOType();
        int deviceType = paoGroupsWrapper.getDeviceType(typeStr);
        device.setType(deviceType);
        return device;
    }

}

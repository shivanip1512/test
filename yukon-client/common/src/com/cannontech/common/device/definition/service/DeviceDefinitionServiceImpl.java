package com.cannontech.common.device.definition.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.point.PointBase;

public class DeviceDefinitionServiceImpl implements DeviceDefinitionService {
    private SimpleDeviceDefinitionService simpleDeviceDefinitionService;
    private DeviceDao deviceDao;
    
    @Autowired
    public void setSimpleDeviceDefinitionService(
            SimpleDeviceDefinitionService simpleDeviceDefinitionService) {
        this.simpleDeviceDefinitionService = simpleDeviceDefinitionService;
    }
    
    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}
    
    public List<PointBase> createAllPointsForDevice(DeviceBase device) {
        return simpleDeviceDefinitionService.createAllPointsForDevice(deviceDao.getYukonDeviceForDevice(device));
    }

    public List<PointBase> createDefaultPointsForDevice(DeviceBase device) {
        return simpleDeviceDefinitionService.createDefaultPointsForDevice(deviceDao.getYukonDeviceForDevice(device));
    }

    public Set<DeviceDefinition> getChangeableDevices(DeviceBase device) {
        return simpleDeviceDefinitionService.getChangeableDevices(deviceDao.getYukonDeviceForDevice(device));
    }
    
    public Set<DeviceDefinition> getChangeableDevices(YukonDevice device) {
        return simpleDeviceDefinitionService.getChangeableDevices(device);
    }

    public Map<String, List<DeviceDefinition>> getDeviceDisplayGroupMap() {
        return simpleDeviceDefinitionService.getDeviceDisplayGroupMap();
    }

    public Set<PointTemplate> getNewPointTemplatesForTransfer(
                                                              DeviceBase device, DeviceDefinition deviceDefinition) {
        return simpleDeviceDefinitionService.getNewPointTemplatesForTransfer(deviceDao.getYukonDeviceForDevice(device), deviceDefinition);
    }

    public Set<PointTemplate> getPointTemplatesToAdd(DeviceBase device, DeviceDefinition deviceDefinition) {
        return simpleDeviceDefinitionService.getPointTemplatesToAdd(deviceDao.getYukonDeviceForDevice(device), deviceDefinition);
    }

    public Set<PointTemplate> getPointTemplatesToRemove(DeviceBase device, DeviceDefinition deviceDefinition) {
        return simpleDeviceDefinitionService.getPointTemplatesToRemove(deviceDao.getYukonDeviceForDevice(device), deviceDefinition);
    }

    public Set<PointTemplate> getPointTemplatesToTransfer(DeviceBase device, DeviceDefinition deviceDefinition) {
        return simpleDeviceDefinitionService.getPointTemplatesToTransfer(deviceDao.getYukonDeviceForDevice(device), deviceDefinition);
    }

    public boolean isDeviceTypeChangeable(DeviceBase device) {
        return simpleDeviceDefinitionService.isDeviceTypeChangeable(deviceDao.getYukonDeviceForDevice(device));
    }
}

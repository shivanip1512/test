package com.cannontech.common.device.config.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.DeviceConfigCategory;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.service.DeviceConfigurationService;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

public class DeviceConfigurationServiceImpl implements DeviceConfigurationService {    
    private static final String CONFIG_OBJECT_TYPE = "config";
    private static final String DEVICE_OBJECT_TYPE = "device";
    private static final String CATEGORY_OBJECT_TYPE = "category";

    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private DbChangeManager dbChangeManager;
    
    @Override
    public int saveConfigurationBase(Integer deviceConfigurationId, String name, String description) {
        DbChangeType changeType = deviceConfigurationId == null ? DbChangeType.ADD : DbChangeType.UPDATE;
        
        int configId = deviceConfigurationDao.saveConfigurationBase(deviceConfigurationId, name, description);
        
        dbChangeManager.processDbChange(configId, 
                                        DBChangeMsg.CHANGE_CONFIG_DB, 
                                        DBChangeMsg.CAT_DEVICE_CONFIG, 
                                        CONFIG_OBJECT_TYPE, 
                                        changeType);
        
        return configId;
    }
    
    @Override
    public void deleteConfiguration(int deviceConfigurationId) {
        deviceConfigurationDao.deleteConfiguration(deviceConfigurationId);
        
        dbChangeManager.processDbChange(deviceConfigurationId, 
                                        DBChangeMsg.CHANGE_CONFIG_DB, 
                                        DBChangeMsg.CAT_DEVICE_CONFIG, 
                                        CONFIG_OBJECT_TYPE, 
                                        DbChangeType.DELETE);
    }
    
    @Override
    public int saveCategory(DeviceConfigCategory category) {
        DbChangeType changeType = category.getCategoryId() == null ? DbChangeType.ADD : DbChangeType.UPDATE;
        
        int categoryId = deviceConfigurationDao.saveCategory(category);
        
        dbChangeManager.processDbChange(categoryId, 
                                        DBChangeMsg.CHANGE_CONFIG_DB, 
                                        DBChangeMsg.CAT_DEVICE_CONFIG, 
                                        CATEGORY_OBJECT_TYPE, 
                                        changeType);
        
        return categoryId;
    }
    
    @Override
    public void deleteCategory(int categoryId) {
        deviceConfigurationDao.deleteCategory(categoryId);
        
        dbChangeManager.processDbChange(categoryId, 
                                        DBChangeMsg.CHANGE_CONFIG_DB, 
                                        DBChangeMsg.CAT_DEVICE_CONFIG, 
                                        CATEGORY_OBJECT_TYPE, 
                                        DbChangeType.DELETE);
    }
    
    @Override
    public void assignConfigToDevice(LightDeviceConfiguration configuration, YukonDevice device)
            throws InvalidDeviceTypeException {
        deviceConfigurationDao.assignConfigToDevice(configuration, device);
        
        boolean isConfigUpdate = deviceConfigurationDao.findConfigurationForDevice(device) != null;
        
        dbChangeManager.processDbChange(device.getPaoIdentifier().getPaoId(), 
                                        DBChangeMsg.CHANGE_CONFIG_DB, 
                                        DBChangeMsg.CAT_DEVICE_CONFIG, 
                                        DEVICE_OBJECT_TYPE, 
                                        isConfigUpdate ? DbChangeType.UPDATE : DbChangeType.ADD);
    }

    @Override
    public void unassignConfig(YukonDevice device) throws InvalidDeviceTypeException {
        deviceConfigurationDao.unassignConfig(device);
        
        dbChangeManager.processDbChange(device.getPaoIdentifier().getPaoId(), 
                                        DBChangeMsg.CHANGE_CONFIG_DB, 
                                        DBChangeMsg.CAT_DEVICE_CONFIG, 
                                        DEVICE_OBJECT_TYPE, 
                                        DbChangeType.DELETE);
    }
}
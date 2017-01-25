package com.cannontech.common.device.config.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.InvalidConfigurationRemovalException;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.DeviceConfigCategory;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.common.device.config.service.DeviceConfigurationService;
import com.cannontech.common.events.loggers.DeviceConfigEventLogService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.IDatabaseCache;

public class DeviceConfigurationServiceImpl implements DeviceConfigurationService {
    private static final String CONFIG_OBJECT_TYPE = "config";
    private static final String DEVICE_OBJECT_TYPE = "device";
    private static final String CATEGORY_OBJECT_TYPE = "category";

    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private DeviceConfigEventLogService eventLogService;
    
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
    public void deleteConfiguration(int deviceConfigurationId) throws InvalidConfigurationRemovalException {

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
    public void assignConfigToDevice(LightDeviceConfiguration configuration, YukonDevice device, LiteYukonUser user)
            throws InvalidDeviceTypeException {
        
        String deviceName = dbCache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId()).getPaoName();
        eventLogService.assignConfigToDeviceInitiated(configuration.getName(), deviceName, user);
        try {
            deviceConfigurationDao.assignConfigToDevice(configuration, device);
            eventLogService.assignConfigToDeviceSucceeded(configuration.getName(), deviceName);
        } catch (InvalidDeviceTypeException e) {
            eventLogService.assignConfigToDeviceFailed(configuration.getName(), deviceName);
            throw e;
        }
      
   
        boolean isConfigUpdate = deviceConfigurationDao.findConfigurationForDevice(device) != null;
        
        dbChangeManager.processDbChange(device.getPaoIdentifier().getPaoId(),
                                        DBChangeMsg.CHANGE_CONFIG_DB,
                                        DBChangeMsg.CAT_DEVICE_CONFIG,
                                        DEVICE_OBJECT_TYPE,
                                        isConfigUpdate ? DbChangeType.UPDATE : DbChangeType.ADD);
    }

    @Override
    public void unassignConfig(YukonDevice device, LiteYukonUser user) throws InvalidDeviceTypeException {
        String deviceName = dbCache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId()).getPaoName();
        eventLogService.unassignConfigFromDeviceInitiated(deviceName, user);
        try{
            deviceConfigurationDao.unassignConfig(device);
            eventLogService.unassignConfigFromDeviceSucceeded(deviceName, user);
        }catch(InvalidDeviceTypeException e){
            eventLogService.unassignConfigFromDeviceFailed(deviceName, user);
            throw e;
        }

        dbChangeManager.processDbChange(device.getPaoIdentifier().getPaoId(),
                                        DBChangeMsg.CHANGE_CONFIG_DB,
                                        DBChangeMsg.CAT_DEVICE_CONFIG,
                                        DEVICE_OBJECT_TYPE,
                                        DbChangeType.DELETE);
    }
    
    @Override
    public void changeCategoryAssignment(int deviceConfigurationId, int newCategoryId, CategoryType categoryType) {
        deviceConfigurationDao.changeCategoryAssignment(deviceConfigurationId, newCategoryId, categoryType);

        dbChangeManager.processDbChange(deviceConfigurationId,
                                        DBChangeMsg.CHANGE_CONFIG_DB,
                                        DBChangeMsg.CAT_DEVICE_CONFIG,
                                        CONFIG_OBJECT_TYPE,
                                        DbChangeType.UPDATE);
    }
    
    @Override
    public void removeSupportedDeviceType(int deviceConfigurationId, PaoType paoType)
            throws InvalidConfigurationRemovalException {

        List<Integer> unassignedDeviceIds =
            deviceConfigurationDao.removeSupportedDeviceType(deviceConfigurationId, paoType);

        for (Integer deviceId : unassignedDeviceIds) {
            // Send db change messages for all of the devices that were implicitly unassigned.
            dbChangeManager.processDbChange(deviceId,
                                            DBChangeMsg.CHANGE_CONFIG_DB,
                                            DBChangeMsg.CAT_DEVICE_CONFIG,
                                            DEVICE_OBJECT_TYPE,
                                            DbChangeType.DELETE);
        }
    }
}
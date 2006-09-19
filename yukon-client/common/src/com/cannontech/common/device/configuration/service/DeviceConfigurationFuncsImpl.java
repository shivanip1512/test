package com.cannontech.common.device.configuration.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cannontech.common.device.configuration.dao.CategoryDao;
import com.cannontech.common.device.configuration.dao.DeviceConfigurationDao;
import com.cannontech.common.device.configuration.dao.ItemDao;
import com.cannontech.common.device.configuration.dao.TypeDao;
import com.cannontech.common.device.configuration.model.Category;
import com.cannontech.common.device.configuration.model.DeviceConfiguration;
import com.cannontech.common.device.configuration.model.Item;
import com.cannontech.common.device.configuration.model.Type;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;

/**
 * Implementation of DeviceConfigurationFuncs
 */
public class DeviceConfigurationFuncsImpl implements DeviceConfigurationFuncs {

    public List<LiteYukonPAObject> getPossibleDevicesForConfigType(int configTypeId) {

        List<String> deviceTypeList = DeviceConfigurationDao.getDeviceTypesForConfigType(configTypeId);

        List<LiteYukonPAObject> deviceList = new ArrayList<LiteYukonPAObject>();
        Iterator<String> deviceTypeIter = deviceTypeList.iterator();
        while (deviceTypeIter.hasNext()) {
            String deviceType = deviceTypeIter.next();

            deviceList.addAll(DaoFactory.getPaoDao()
                                        .getLiteYukonPAObjectByType(PAOGroups.getPAOType(PAOGroups.STRING_CAT_DEVICE,
                                                                                         deviceType)));
        }

        return deviceList;
    }

    public List<LiteYukonPAObject> getDevicesForConfig(int configId) {

        List<Integer> deviceIdList = DeviceConfigurationDao.getDeviceIdsConfig(configId);

        List<LiteYukonPAObject> deviceList = new ArrayList<LiteYukonPAObject>();
        Iterator<Integer> deviceIdIter = deviceIdList.iterator();
        while (deviceIdIter.hasNext()) {
            Integer deviceId = deviceIdIter.next();

            deviceList.add(DaoFactory.getPaoDao().getLiteYukonPAO(deviceId));
        }

        return deviceList;
    }

    public Map<String, List<DeviceConfiguration>> getDeviceTypeConfigMap() {
        return DeviceConfigurationDao.getDeviceTypeConfigMap();
    }

    public Map<String, Integer> getDeviceTypeConfigTypeMap() {
        return DeviceConfigurationDao.getDeviceTypeConfigTypeMap();
    }

    public boolean save(int paObjectId, int configId) {
        DeviceConfigurationDao.save(paObjectId, configId);
        return true;
    }

    public List<Type> getConfigTypesForDevice(String deviceType) {
        return TypeDao.getConfigTypesForDevice(deviceType);
    }

    public List<Type> getConfigTypes() {
        return TypeDao.getAllConfigTypes();
    }

    public List<DeviceConfiguration> getConfigsForType(int typeId) {
        return DeviceConfigurationDao.getForType(typeId);
    }

    public DeviceConfiguration getConfigForDevice(int deviceId) {
        return DeviceConfigurationDao.getForDevice(deviceId);
    }

    public Type loadConfigType(int typeId) {
        return TypeDao.getConfigTypeForId(typeId);
    }

    public Type loadCategoryType(int typeId) {
        return TypeDao.getCategoryTypeForId(typeId);
    }

    public DeviceConfiguration loadConfig(int configId) {
        return DeviceConfigurationDao.getForId(configId);

    }

    public boolean save(DeviceConfiguration configuration) {
        DeviceConfigurationDao.save(configuration);
        return true;
    }

    @SuppressWarnings("unchecked")
    public List<Category> getCategoriesForType(int typeId) {

        List<Category> categories = CategoryDao.getForType(typeId);
        Collections.sort(categories);

        return categories;
    }

    @SuppressWarnings("unchecked")
    public List<Type> getCategoryTypesForConfigType(int configType) {

        List<Type> types = TypeDao.getAllCategoryTypesForConfigType(configType);
        Collections.sort(types);

        return types;
    }

    @SuppressWarnings("unchecked")
    public List<Category> getCategoriesForConfig(int configId) {

        List<Category> categories = CategoryDao.getForConfig(configId);
        Collections.sort(categories);

        return categories;
    }

    public Category loadCategory(int categoryId) {
        return CategoryDao.getForId(categoryId);
    }

    public boolean save(Category category) {
        CategoryDao.save(category);
        return true;
    }

    public List<Item> getItemsForCategory(int categoryId) {
        return ItemDao.getForCategory(categoryId);
    }

    public List<Item> getItemsForCategoryType(int categoryType) {
        return ItemDao.getDefaultItemsForCategoryType(categoryType);
    }

    public boolean assignConfigToDevices(int configId, List<LiteYukonPAObject> devices) {
        this.removeConfigAssignmentForConfig(configId);
        DeviceConfigurationDao.assignConfigToDevices(configId, devices);
        return true;
    }

    public boolean removeConfigAssignmentForDevices(List<LiteYukonPAObject> devices) {
        DeviceConfigurationDao.removeConfigAssignmentForDevices(devices);
        return true;
    }

    public boolean removeConfigAssignmentForConfig(int configId) {
        DeviceConfigurationDao.removeConfigAssignmentForConfig(configId);
        return true;
    }

}

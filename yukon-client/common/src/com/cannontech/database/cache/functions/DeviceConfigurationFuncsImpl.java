package com.cannontech.database.cache.functions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.device.configuration.Category;
import com.cannontech.database.data.device.configuration.DeviceConfiguration;
import com.cannontech.database.data.device.configuration.Item;
import com.cannontech.database.data.device.configuration.Type;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;

/**
 * Implementation of DeviceConfigurationFuncs
 */
public class DeviceConfigurationFuncsImpl implements DeviceConfigurationFuncs {

    public List<LiteYukonPAObject> getPossibleDevicesForConfigType(int configTypeId) {

        List<LiteYukonPAObject> deviceList = null;

        try {
            List<String> deviceTypeList = DeviceConfigurationDao.getDeviceTypesForConfigType(configTypeId);

            deviceList = new ArrayList<LiteYukonPAObject>();
            Iterator<String> deviceTypeIter = deviceTypeList.iterator();
            while (deviceTypeIter.hasNext()) {
                String deviceType = deviceTypeIter.next();

                deviceList.addAll(DaoFactory.getPaoDao()
                                            .getLiteYukonPAObjectByType(PAOGroups.getPAOType(PAOGroups.STRING_CAT_DEVICE,
                                                                                             deviceType)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // TODO - handle exception
        }

        return deviceList;
    }

    public List<LiteYukonPAObject> getDevicesForConfig(int configId) {

        List<LiteYukonPAObject> deviceList = null;

        try {
            List<Integer> deviceIdList = DeviceConfigurationDao.getDeviceIdsConfig(configId);

            deviceList = new ArrayList<LiteYukonPAObject>();
            Iterator<Integer> deviceIdIter = deviceIdList.iterator();
            while (deviceIdIter.hasNext()) {
                Integer deviceId = deviceIdIter.next();

                deviceList.add(DaoFactory.getPaoDao().getLiteYukonPAO(deviceId));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // TODO - handle exception
        }

        return deviceList;
    }

    public Map<String, List<DeviceConfiguration>> getDeviceTypeConfigMap() {

        Map<String, List<DeviceConfiguration>> configMap = null;

        try {
            configMap = DeviceConfigurationDao.getDeviceTypeConfigMap();
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO - handle exception
        }

        return configMap;

    }

    public Map<String, Integer> getDeviceTypeConfigTypeMap() {

        Map<String, Integer> configMap = null;

        try {
            configMap = DeviceConfigurationDao.getDeviceTypeConfigTypeMap();
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO - handle exception
        }

        return configMap;

    }

    public boolean save(int paObjectId, int configId) {
        try {
            DeviceConfigurationDao.save(paObjectId, configId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
            // TODO - handle exception
        }

        return true;
    }

    public List<Type> getConfigTypesForDevice(String deviceType) {
        List<Type> configTypes = null;

        try {
            configTypes = TypeDao.getConfigTypesForDevice(deviceType);
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO - handle exception
        }

        return configTypes;
    }

    public List<Type> getConfigTypes() {
        List<Type> configTypes = null;

        try {
            configTypes = TypeDao.getAllConfigTypes();
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO - handle exception
        }

        return configTypes;
    }

    public List<DeviceConfiguration> getConfigsForType(int typeId) {
        List<DeviceConfiguration> configs = null;

        try {
            configs = DeviceConfigurationDao.getForType(typeId);
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO - handle exception
        }

        return configs;
    }

    public DeviceConfiguration getConfigForDevice(int deviceId) {
        DeviceConfiguration config = null;

        try {
            config = DeviceConfigurationDao.getForDevice(deviceId);
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO - handle exception
        }

        return config;

    }

    public Type loadConfigType(int typeId) {
        Type type = null;

        try {
            type = TypeDao.getConfigTypeForId(typeId);
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO - handle exception
        }

        return type;
    }

    public Type loadCategoryType(int typeId) {
        Type type = null;

        try {
            type = TypeDao.getCategoryTypeForId(typeId);
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO - handle exception
        }

        return type;
    }

    public DeviceConfiguration loadConfig(int configId) {
        DeviceConfiguration config = null;

        try {
            config = DeviceConfigurationDao.getForId(configId);
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO - handle exception
        }
        return config;

    }

    public boolean save(DeviceConfiguration configuration) {

        try {
            DeviceConfigurationDao.save(configuration);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
            // TODO - handle exception
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    public List<Category> getCategoriesForType(int typeId) {
        List<Category> categories = null;

        try {
            categories = CategoryDao.getForType(typeId);
            Collections.sort(categories);
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO - handle exception
        }

        return categories;
    }

    @SuppressWarnings("unchecked")
    public List<Type> getCategoryTypesForConfigType(int configType) {
        List<Type> types = null;

        try {
            types = TypeDao.getAllCategoryTypesForConfigType(configType);
            Collections.sort(types);
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO - handle exception
        }

        return types;
    }

    @SuppressWarnings("unchecked")
    public List<Category> getCategoriesForConfig(int configId) {
        List<Category> categories = null;

        try {
            categories = CategoryDao.getForConfig(configId);
            Collections.sort(categories);
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO - handle exception
        }

        return categories;
    }

    public Category loadCategory(int categoryId) {
        Category category = null;

        try {
            category = CategoryDao.getForId(categoryId);
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO - handle exception
        }

        return category;
    }

    public boolean save(Category category) {

        try {
            CategoryDao.save(category);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
            // TODO - handle exception
        }

        return true;
    }

    public List<Item> getItemsForCategory(int categoryId) {
        List<Item> items = null;

        try {
            items = ItemDao.getForCategory(categoryId);
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO - handle exception
        }

        return items;
    }

    public List<Item> getItemsForCategoryType(int categoryType) {
        List<Item> items = new LinkedList<Item>();

        try {
            items = ItemDao.getDefaultItemsForCategoryType(categoryType);

        } catch (SQLException e) {
            e.printStackTrace();
            // TODO - handle exception
        }

        return items;
    }

    public boolean assignConfigToDevices(int configId, List<LiteYukonPAObject> devices) {
        try {

            this.removeConfigAssignmentForDevices(devices);
            DeviceConfigurationDao.assignConfigToDevices(configId, devices);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
            // TODO - handle exception
        }
        return true;
    }

    public boolean removeConfigAssignmentForDevices(List<LiteYukonPAObject> devices) {
        try {
            DeviceConfigurationDao.removeConfigAssignmentForDevices(devices);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
            // TODO - handle exception
        }
        return true;
    }

}

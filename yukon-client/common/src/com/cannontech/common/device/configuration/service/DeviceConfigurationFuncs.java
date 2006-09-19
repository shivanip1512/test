package com.cannontech.common.device.configuration.service;

import java.util.List;
import java.util.Map;

import com.cannontech.common.device.configuration.model.Category;
import com.cannontech.common.device.configuration.model.DeviceConfiguration;
import com.cannontech.common.device.configuration.model.Item;
import com.cannontech.common.device.configuration.model.Type;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Functions available for device configuration
 */
public interface DeviceConfigurationFuncs {

    /**
     * Method to get a list of all devices that are compatible with a given
     * config type
     * @param configTypeId - Id of the config type in question
     * @return A list of devices
     */
    public List<LiteYukonPAObject> getPossibleDevicesForConfigType(int configTypeId);

    /**
     * Method to get a list of devices that have been assigned a given config
     * @param configId - Id of config in question
     * @return A list of devices
     */
    public List<LiteYukonPAObject> getDevicesForConfig(int configId);

    /**
     * Method to assign a device configuration to each device in a list
     * @param configId - Id of config to assign
     * @param devices - List of devices to assign config to
     * @return True if successful
     */
    public boolean assignConfigToDevices(int configId, List<LiteYukonPAObject> devices);

    /**
     * Method to remove configuration assignments for a list of devices.
     * @param devices - List of devices to unassign configs for
     * @return True if successful
     */
    public boolean removeConfigAssignmentForDevices(List<LiteYukonPAObject> devices);

    /**
     * Method to remove configuration assignments for a given configuration.
     * @param configId - Id of configuration to unassign all devices from
     * @return True if successful
     */
    public boolean removeConfigAssignmentForConfig(int configId);

    /**
     * Method to get a map with key: device type string, value: list of device
     * configuration for that device type
     * @return
     */
    public Map<String, List<DeviceConfiguration>> getDeviceTypeConfigMap();

    /**
     * Method to get a map with key: device type string, value: device
     * configuration type for that device type
     * @return
     */
    public Map<String, Integer> getDeviceTypeConfigTypeMap();

    /**
     * Method to save a configuration for a given device
     * @param paObjectId - Device to save config for
     * @param configId - Config to save
     * @return True if save successful
     */
    public boolean save(int paObjectId, int configId);

    /**
     * Method to get a list of all configuration types for a given device type
     * @param deviceType - Type of device to get config types for
     * @return Configuration type
     */
    public List<Type> getConfigTypesForDevice(String deviceType);

    /**
     * Method to get a list of all configuration types
     * @return A list of types
     */
    public List<Type> getConfigTypes();

    /**
     * Method to get a list of all configurations of a given type
     * @param typeId - The type of configurations to get
     * @return A list of configurations
     */
    public List<DeviceConfiguration> getConfigsForType(int typeId);

    /**
     * Method to get the config for a given device
     * @param typeId - The device to get the configuration for
     * @return A configurations
     */
    public DeviceConfiguration getConfigForDevice(int deviceId);

    /**
     * Method to load a configuration type
     * @param typeId - Id of type to load
     * @return Loaded type
     */
    public Type loadConfigType(int typeId);

    /**
     * Method to load a configuration
     * @param configId - Id of configuration to load
     * @return Loaded configuration
     */
    public DeviceConfiguration loadConfig(int configId);

    /**
     * Method to save (or update) a configuration
     * @param configuration - Configuration to save
     * @return True if save was successful
     */
    public boolean save(DeviceConfiguration configuration);

    /**
     * Method to get a list of all categories of a given type
     * @param typeId - The type of categories to get
     * @return A list of categories
     */
    public List<Category> getCategoriesForType(int typeId);

    /**
     * Method to load a category type
     * @param typeId - Id of type to load
     * @return Loaded type
     */
    public Type loadCategoryType(int typeId);

    /**
     * Method to get a list of all category types for a given configuration type
     * @param configType - Configuration type to get category types for
     * @return A list of category types
     */
    public List<Type> getCategoryTypesForConfigType(int configType);

    /**
     * Method to get a list of all categories for a given configuration
     * @param configId - Configuration to get the categories for
     * @return A list of categories
     */
    public List<Category> getCategoriesForConfig(int configId);

    /**
     * Method to load a category
     * @param categoryId - Id of category to load
     * @return Loaded category
     */
    public Category loadCategory(int categoryId);

    /**
     * Method to save (or update) a category
     * @param category - Category to save
     * @return True if save was successful
     */
    public boolean save(Category category);

    /**
     * Method to get a list of all items for a given category
     * @param categoryId - Category to get the items for
     * @return A list of items
     */
    public List<Item> getItemsForCategory(int categoryId);

    /**
     * Method to get a list of all items for a given category type
     * @param categoryType - Category type to get the items for
     * @return A list of items
     */
    public List<Item> getItemsForCategoryType(int categoryType);

}

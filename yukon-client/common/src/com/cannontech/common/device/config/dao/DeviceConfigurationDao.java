package com.cannontech.common.device.config.dao;

import java.util.List;

import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.DeviceConfigCategory;
import com.cannontech.common.device.config.model.DisplayableConfigurationCategory;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.model.jaxb.Category;
import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.core.dao.NotFoundException;

/**
 * Data Access class for device configurations
 */
public interface DeviceConfigurationDao {
    
    /**
     * 
     * @param categoryType
     * @return
     */
    public Category getCategoryByType(CategoryType categoryType);
    
    public int saveConfiguration(DeviceConfiguration configuration);

    public void deleteConfiguration(int deviceConfigurationId);
    
    public boolean isConfigurationDeletable(int configId);
    
    public int saveCategory(DeviceConfigCategory category);
    
    public void deleteCategory(int categoryId);
    
    public DeviceConfigCategory getDeviceConfigCategory(int categoryId);
    
    /**
     * Get all device configurations in the system.
     * @return a list containing all device configurations that exist in the database.
     */
    public List<DeviceConfiguration> getAllDeviceConfigurations();
    
    public List<LightDeviceConfiguration> getAllLightDeviceConfigurations();
    
    public List<DisplayableConfigurationCategory> getAllDeviceConfigurationCategories();
    
    /**
     * Load a configuration from the database, including all assigned categories and items.
     * @param configId the deviceConfigurationId for the configuration
     * @return a populated DeviceConfiguration object representing the configuration if one exists
     *      matching the configId specified.
     * @throws NotFoundException if no configuration exists for the given id.         
     */
    public DeviceConfiguration getDeviceConfiguration(int configId) throws NotFoundException;
    
    public boolean categoriesExistForType(String categoryType);
    
    /**
     * Get the number of devices a configuration is assigned to.
     * @param configId the configurationId of the configuration being checked
     * @return the number of devices assigned to the specified configuration.
     */
    public int getNumberOfDevicesForConfiguration(int configId);
    
    public int getNumberOfConfigurationsForCategory(int categoryId);
    
    public boolean isDeviceConfigurationAvailable(PaoType paoType);
    
    /**
     * Returns configuration of a device or null if the device is not assigned
     * @param device
     * @return ConfigurationBase
     */
    public LightDeviceConfiguration findConfigurationForDevice(YukonDevice device);
    
    public boolean isTypeSupportedByConfiguration(LightDeviceConfiguration configuration, PaoType paoType);
    
    /**
     * Returns the default DNP configuration
     */
    public DeviceConfiguration getDefaultDNPConfiguration();
    
    /**
     * Get the DNP configuration data out of a device configuration if the data is present.
     * @param configuration the configuration the DNP data is coming out of
     * @return a DNPConfiguration model object containing the DNP category data of the configuration.S
     */
    public DNPConfiguration getDnpConfiguration(DeviceConfiguration configuration);

    public List<LightDeviceConfiguration> getAllConfigurationsByType(PaoType paoType);

    /**
     * This method will assign a configuration to a device if it doesn't already have an entry
     * in the DeviceConfigurationDeviceMap table or update the device's entry if it already has
     * a device configuration.
     * @param device - Device to assgin configuration to
     * @throws InvalidDeviceTypeException 
     */
    public void assignConfigToDevice(LightDeviceConfiguration configuration, YukonDevice device) 
            throws InvalidDeviceTypeException;

    /**
     * Method to remove the configuration assignment from the device
     * @param deviceId - Id of device to remove config for
     * @throws InvalidDeviceTypeException 
     */
    public void unassignConfig(YukonDevice device) throws InvalidDeviceTypeException;
    
    /**
     * Returns the value of the device config item for the given 
     * config id and field name.
     * @param configId
     * @param fieldName
     * @return
     */
    public String getValueForItemName(int configId, String fieldName);
    
    
    /**
     * Returns true if there is already a configuration in the database
     * with a different ID
     */
    public boolean checkForNameConflict(String name, Integer id);
}

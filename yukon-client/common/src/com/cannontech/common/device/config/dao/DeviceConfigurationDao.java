package com.cannontech.common.device.config.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.device.config.model.DeviceConfigCategory;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.DisplayableConfigurationCategory;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.model.jaxb.Category;
import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.common.device.config.service.DeviceConfigurationService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.core.dao.NotFoundException;

/**
 * Data Access class for device configurations
 */
public interface DeviceConfigurationDao {
    
    /**
     * Returns the JAXB Category class whose category type matches the provided type.
     * @param categoryType
     */
    public Category getCategoryByType(CategoryType categoryType);
    
    /**
     * Saves the configuration information to the database, updating it if it already exists, inserting it otherwise.
     * Does not send a DBChange message. 
     * {@link DeviceConfigurationService#saveConfigurationBase(Integer, String, String)} should almost always be used 
     * instead of this method, since it handles the sending of the DBChange messaging.
     * @param deviceConfigurationId the identifier of the configuration being saved.
     * @param name the name of the configuration being saved.
     * @param description user-defined description of the configuration being saved.
     * @return the deviceConfigurationId of the configuration that was saved.
     */
    public int saveConfigurationBase(Integer deviceConfigurationId, String name, String description);
    
    /**
     * Removes the device configuration with the specified id from the database. Does not send a DBChange message.
     * {@link DeviceConfigurationService#deleteConfiguration(int)} should almost always be used instead of this method,
     * since it handles the sending of the DBChange messaging.
     * @param deviceConfigurationId the id of the configuration to remove.
     */
    public void deleteConfiguration(int deviceConfigurationId);

    /**
     * Determines whether or not the specified configuration is deletable or not.
     * @param configId the identifier of the configuration being checked.
     * @return true if the configId passed in isn't the default DNP configuration ID and has no entries in the
     *    DeviceConfigurationDeviceMap table, false otherwise.
     */
    public boolean isConfigurationDeletable(int configId);
    
    /**
     * Saves the provided category to the database, updating it if it already exists, inserting it otherwise. Does not 
     * send a DBChange message. {@link DeviceConfigurationService#saveCategory(DeviceConfigCategory)} should almost 
     * always be used instead of this method, since it handles the sending of the DBChange messaging.
     * @param category the category being saved to the database
     * @return the categoryId of the category that was saved.
     */
    public int saveCategory(DeviceConfigCategory category);
    
    /**
     * Deletes the specified category from the database. Does not send a DBChange message. 
     * {@link DeviceConfigurationService#deleteCategory(int)} should almost always be used instead of this method, 
     * since it handles the sending of the DBChange messaging.
     * @param categoryId the categoryId of the category being deleted.
     */
    public void deleteCategory(int categoryId);
    
    /**
     * Retrieve the specified category from the database.
     * @param categoryId the category id of the category being retrieved.
     * @return the populated category object whose identifier was provided.
     */
    public DeviceConfigCategory getDeviceConfigCategory(int categoryId);
    
    /**
     * Retrieve a category from the database
     * @param configId the identifier of the config whose category we're retrieving
     * @param categoryType the type of category being retrieved
     * @return the populated category object identified by the parameters.
     */
    public DeviceConfigCategory getDeviceConfigCategory(int configId, CategoryType categoryType);
    
    /**
     * Get the pao types supported by a configuration.
     * @param deviceConfigurationId the configuration id of the device configuration
     * @return a set containing the supported pao types (if any exist) for the specified configuration. Returns an 
     *      empty set (rather than null) in the case that the configuration has no supported types.
     */
    public Set<PaoType> getSupportedTypesForConfiguration(int deviceConfigurationId);
    
    /**
     * Changes the category assignment for a specific category type on a configuration.
     * @param deviceConfigurationId the configuration id of the configuration whose assignment is being changed
     * @param newCategoryId the category id of the category being assigned
     * @param categoryType the category type being affected.
     */
    public void changeCategoryAssignment(int deviceConfigurationId, int newCategoryId, CategoryType categoryType);
    
    /**
     * Add a set of supported device types for a configuration
     * @param deviceConfigurationId the identifier of the configuration whose supported types are being updated
     * @param paoTypes the set of pao types being added for the configuration.
     */
    public void addSupportedDeviceTypes(int deviceConfigurationId, Set<PaoType> paoTypes);

    /**
     * Remove a pao type from the list of supported pao types for a configuration
     * @param deviceConfigurationId the identifier of the configuration whose supported types are being updated
     * @param paoType the pao type being removed from the configuration.
     */
    public void removeSupportedDeviceType(int deviceConfigurationId, PaoType paoType);
    
    /**
     * Find the difference in category types for a configuration if the provided pao type were removed as a
     * supported type
     * @param paoType the pao type whose removal outcome is being checked
     * @param configId the identifier of the configuration whose category type difference is being checked
     * @return a set containing the category type difference between the configuration's current set of category types
     *      and the category types that would remain following the removal of the provided pao type. That is, if the
     *      configuration's current category types is the set {A, B, Z} and the set of category types that would exist
     *      following the removal of the provided pao type would be the set {A, B} then this method will return {Z}.
     */
    public Set<CategoryType> getCategoryDifferenceForPaoTypeRemove(PaoType paoType, int configId);
    
    /**
     * Find the difference in category types for a configuration if the provided set of pao types were added as
     * supported types.
     * @param paoTypes the set of pao types whose addition outcome is being checked.
     * @param configId the identifier of the configuration whose category difference is being checked.
     * @return a set containing the category type difference between the configuration's current set of category types
     *      and the category types that would remain following the addition of the provided pao types. That is, if the
     *      configuration's current category types is the set {A, B} and the set of category types that would exist
     *      following the addition of the provided pao types would be the set {A, B, X, Y, Z} then this method 
     *      will return {X, Y, Z}.
     */
    public Set<CategoryType> getCategoryDifferenceForPaoTypesAdd(Set<PaoType> paoTypes, int configId);

    /**
     * Get all basic device configuration information from the database.
     * @return a list of {@link LightDeviceConfiguration} objects representing the complete list of device 
     *      configurations in the database.
     */
    public List<LightDeviceConfiguration> getAllLightDeviceConfigurations();
    
    /**
     * Get all basic category information from the database. 
     * @return a list of {@link DisplayableConfigurationCategory} objects representing the complete list of categories
     *      in the database.
     */
    public List<DisplayableConfigurationCategory> getAllDeviceConfigurationCategories();
    
    /**
     * Load a configuration from the database, including all assigned categories and items.
     * @param configId the deviceConfigurationId for the configuration
     * @return a populated DeviceConfiguration object representing the configuration if one exists
     *      matching the configId specified.
     * @throws NotFoundException if no configuration exists for the given id.         
     */
    public DeviceConfiguration getDeviceConfiguration(int configId) throws NotFoundException;
    
    /**
     * Check if any categories exist in the database for the given category type
     * @param categoryType the category type being checked
     * @return true if any categories in the database have the given category type, false otherwise.
     */
    public boolean categoriesExistForType(String categoryType);
    
    /**
     * Check if any other categories exist of the same type as the category specified.
     * @param categoryId the identifier of the category being checked.
     * @return true if categories with the same category type other than the one specified exist in the database,
     *      false otherwise.
     */
    public boolean otherCategoriesExistForType(int categoryId);
    
    /**
     * Get the number of devices a configuration is assigned to.
     * @param configId the configurationId of the configuration being checked
     * @return the number of devices assigned to the specified configuration.
     */
    public int getNumberOfDevicesForConfiguration(int configId);
    
    /**
     * Get the names of the configuration that a category is assigned to.
     * @param categoryId the identifier of the category whose assignments are being checked.
     * @return the list of configuration names that the category is assigned to.
     */
    public List<String> getConfigurationNamesForCategory(int categoryId);
    
    /**
     * Returns configuration of a device or null if the device is not assigned
     * @param device the device whose configuration information is being queried.
     * @return a {@link LightDeviceConfiguration} object containing the device's basic configuration information if
     *      the device has a configuration assignment, null otherwise.
     */
    public LightDeviceConfiguration findConfigurationForDevice(YukonDevice device);
    
    /**
     * Get the configuration for a device
     * @param device the device whose configuration information is being queried.
     * @return a populated {@link LightDeviceConfiguration} object containing the device's basic config information.
     * @throws NotFoundException if no configuration exists for the device.
     */
    public LightDeviceConfiguration getConfigurationForDevice(YukonDevice device) throws NotFoundException;
    
    /**
     * Check if a configuration supports a given pao type.
     * @param configuration the configuration being checked.
     * @param paoType the pao type being checked.
     * @return true if the provided device configuration supports the provided pao type, false otherwise.
     */
    public boolean isTypeSupportedByConfiguration(LightDeviceConfiguration configuration, PaoType paoType);
    
    /**
     * Get the default DNP configuration from the database
     * @return a fully populated {@link DeviceConfiguration} containing the default DNP configuration data.
     */
    public DeviceConfiguration getDefaultDNPConfiguration();
    
    /**
     * Get the DNP configuration data out of a device configuration if the data is present.
     * @param configuration the configuration the DNP data is coming out of
     * @return a DNPConfiguration model object containing the DNP category data of the configuration.S
     */
    public DNPConfiguration getDnpConfiguration(DeviceConfiguration configuration);

    /**
     * Find all configurations in the database that support the provided pao type.
     * @param paoType the pao type whose matching configurations are being retrieved.
     * @return a list of {@link LightDeviceConfiguration} objects representing all of the configurations in the 
     *      database that support the provided pao type.
     */
    public List<LightDeviceConfiguration> getAllConfigurationsByType(PaoType paoType);

    /**
     * This method will assign a configuration to a device if it doesn't already have an entry
     * in the DeviceConfigurationDeviceMap table or update the device's entry if it already has
     * a device configuration.
     * @param device - Device to assign configuration to
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
     * Returns the value of the device config item for the given config id and field name.
     * @param configId the identifier of the configuration containing the item value being queried.
     * @param categoryType the type of category the field belongs to.
     * @param fieldName the name of the field whose item value is being queried.
     * @return the string representation of the value for the specified item.
     */
    public String getValueForItemName(int configId, CategoryType categoryType, String itemName);
}

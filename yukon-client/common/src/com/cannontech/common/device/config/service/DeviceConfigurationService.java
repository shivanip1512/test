package com.cannontech.common.device.config.service;

import com.cannontech.common.device.config.dao.InvalidConfigurationRemovalException;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.DeviceConfigCategory;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;

public interface DeviceConfigurationService {

    /**
     * Makes the DAO call to save the configuration with the specified information, then sends a DBChange message
     * corresponding to the save.
     * @param deviceConfigurationId the identifier of the configuration being saved.
     * @param name the name of the configuration being saved.
     * @param description user-defined description of the configuration being saved.
     * @return the deviceConfigurationId of the configuration that was saved.
     */
    int saveConfigurationBase(Integer deviceConfigurationId, String name, String description);

    /**
     * Makes the DAO call to remove the specified device configuration from the database, then sends
     * a DBChange message corresponding to the deletion.
     *
     * @param deviceConfigurationId the identifier of the configuration being removed.
     * @throws InvalidConfigurationRemovalException if the configuration cannot be deleted
     *         because it is a required default configuration
     */
    void deleteConfiguration(int deviceConfigurationId) throws InvalidConfigurationRemovalException;

    /**
     * Makes the DAO call to save the provided category, then sends a DBChange message corresponding to the save.
     * @param category the category being saved to the database.
     * @return the categoryId of the category that was saved.
     */
    int saveCategory(DeviceConfigCategory category);

    /**
     * Makes the DAO call to remove the specified category from the database, then sends a DBChange message
     * corresponding to the deletion.
     * @param categoryId the identifier of the category being removed.
     */
    void deleteCategory(int categoryId);

    /**
     * Makes the DAO call to remove a pao type from the list of supported pao types for a configuration, then sends
     * a DBChange message corresponding to the devices that were implicitly unassigned as a result.
     *
     * @param deviceConfigurationId the identifier of the configuration whose supported types are being updated
     * @param paoType the pao type being removed from the configuration.
     * @throws InvalidConfigurationRemovalException if the configuration must support the PaoType
     *         because it is a required default configuration
     */
    void removeSupportedDeviceType(int deviceConfigurationId, PaoType paoType) throws InvalidConfigurationRemovalException;

    /**
     * Assign a configuration to a device.
     * @param configuration the configuration being assigned
     * @param device the device being assigned to
     * @throws InvalidDeviceTypeException if the configuration doesn't support the device type.
     */
    void assignConfigToDevice(LightDeviceConfiguration configuration, YukonDevice device)
            throws InvalidDeviceTypeException;

    /**
     * Remove a configuration from a device
     * @param device the device being unassigned from
     * @throws InvalidDeviceTypeException if the device is a DNP device (which cannot be configurationless.)
     */
    void unassignConfig(YukonDevice device) throws InvalidDeviceTypeException;

    /**
     * Change the assignment of a category to a configuration
     * @param deviceConfigurationId the configuration id the category is being assigned to.
     * @param newCategoryId the category being assigned to the configuration.
     * @param categoryType the type of category whose assignment is being updated.
     */
    void changeCategoryAssignment(int deviceConfigurationId, int newCategoryId, CategoryType categoryType);
}

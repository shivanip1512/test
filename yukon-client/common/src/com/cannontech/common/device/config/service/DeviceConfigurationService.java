package com.cannontech.common.device.config.service;

import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.DeviceConfigCategory;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
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
    public int saveConfigurationBase(Integer deviceConfigurationId, String name, String description);
    
    /**
     * Makes the DAO call to remove the specified device configuration from the database, then sends 
     * a DBChange message corresponding to the deletion.
     * @param deviceConfigurationId the identifier of the configuration being removed.
     */
    public void deleteConfiguration(int deviceConfigurationId); 
    
    /**
     * Makes the DAO call to save the provided category, then sends a DBChange message corresponding to the save.
     * @param category the category being saved to the database.
     * @return the categoryId of the category that was saved.
     */
    public int saveCategory(DeviceConfigCategory category);
    
    /**
     * Makes the DAO call to remove the specified category from the database, then sends a DBChange message
     * corresponding to the deletion.
     * @param categoryId the identifier of the category being removed.
     */
    public void deleteCategory(int categoryId);
    
    /**
     * Assign a configuration to a device.
     * @param configuration the configuration being assigned
     * @param device the device being assigned to
     * @throws InvalidDeviceTypeException if the configuration doesn't support the device type.
     */
    public void assignConfigToDevice(LightDeviceConfiguration configuration, YukonDevice device)
            throws InvalidDeviceTypeException;
    
    /**
     * Remove a configuration from a device
     * @param device the device being unassigned from
     * @throws InvalidDeviceTypeException if the device is a DNP device (which cannot be configurationless.)
     */
    public void unassignConfig(YukonDevice device) throws InvalidDeviceTypeException;
}

package com.cannontech.common.device.config.dao;

import java.util.Collection;
import java.util.List;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.config.model.ConfigurationTemplate;

/**
 * Data Access class for device configurations
 */
public interface DeviceConfigurationDao {

    /**
     * Method to get a list of all available configuration templates
     * @return A list of templates
     */
    public List<ConfigurationTemplate> getAllConfigurationTemplates();

    /**
     * Method to get a list of all existing configurations
     * @return A list of configurations
     */
    public List<ConfigurationBase> getAllConfigurations();

    /**
     * Method to get a configuration by id
     * @param id - Id of configuration
     * @return Configuration
     */
    public ConfigurationBase getConfiguration(int id);

    /**
     * Method to get a configuration template by name
     * @param name - Name of template to get
     * @return The template
     */
    public ConfigurationTemplate getConfigurationTemplate(String name);

    /**
     * Method to persist a configuration
     * @param configuration - Configuration to persist
     */
    public void save(ConfigurationBase configuration);

    /**
     * Method to delete a configuration by id
     * @param id - Id of configuration
     */
    public void delete(int id);

    /**
     * Method to assign a configuration to a collection of devices
     * @param devices - Devices to assgin configuration to
     */
    public void assignConfigToDevices(ConfigurationBase configuration,
            Collection<YukonDevice> devices);

    /**
     * Method to get a list of devices that have been assigned the given
     * configuration
     * @param configuration - Configuration to get device list for
     * @return List of devices assigned the configuration
     */
    public List<YukonDevice> getAssignedDevices(ConfigurationBase configuration);

    /**
     * Method to remove the configuration assignment from the device
     * @param deviceId - Id of device to remove config for
     */
    public void unassignConfig(Integer deviceId);
}

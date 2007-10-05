package com.cannontech.common.device.config.dao;

import java.util.List;

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

}

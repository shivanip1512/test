package com.cannontech.common.device.config.dao;

import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.config.model.MCT410Configuration;
import com.cannontech.common.device.config.model.MCT470Configuration;
import com.cannontech.common.search.criteria.MCT410Criteria;
import com.cannontech.common.search.criteria.MCT470Criteria;

/**
 * Enum for all the types of configurations
 */
public enum ConfigurationType {

    MCT470 {
        public ConfigurationBase getConfigurationClass() {
            return new MCT470Configuration();
        }

        public String getConfigurationTemplateName() {
            return "MCT 470 Configuration";
        }

        public Class getCriteria() {
            return MCT470Criteria.class;
        }

        public String[] getSupportedDeviceTypeList() {
            return MCT470Criteria.MCT_470_TYPES;
        }
    },
    MCT410 {
        public ConfigurationBase getConfigurationClass() {
            return new MCT410Configuration();
        }

        public String getConfigurationTemplateName() {
            return "MCT 410 Configuration";
        }

        public Class getCriteria() {
            return MCT410Criteria.class;
        }

        public String[] getSupportedDeviceTypeList() {
            return MCT410Criteria.MCT_410_TYPES;
        }
    };

    /**
     * Method to get a new instance of the configuration java bean for this
     * configuration type
     * @return New instance of configuration java bean
     */
    public abstract ConfigurationBase getConfigurationClass();

    /**
     * Method to get the name of the configuration template name for this
     * configuration type
     * @return The template name
     */
    public abstract String getConfigurationTemplateName();

    /**
     * Method to get the search criteria associated with this configuration type
     * @return Search criteria
     */
    public abstract Class getCriteria();

    /**
     * Method to get an array of the device types this configuration type
     * supports
     * @return Array of device types
     */
    public abstract String[] getSupportedDeviceTypeList();
}

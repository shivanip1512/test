package com.cannontech.common.device.config.dao;

import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.config.model.MCT410Configuration;
import com.cannontech.common.device.config.model.MCT470Configuration;

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
    },
    MCT410 {
        public ConfigurationBase getConfigurationClass() {
            return new MCT410Configuration();
        }

        public String getConfigurationTemplateName() {
            return "MCT 410 Configuration";
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
}

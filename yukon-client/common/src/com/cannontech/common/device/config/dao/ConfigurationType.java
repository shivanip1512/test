package com.cannontech.common.device.config.dao;

import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.device.config.model.MCT410Configuration;
import com.cannontech.common.device.config.model.MCT420Configuration;
import com.cannontech.common.device.config.model.MCT430Configuration;
import com.cannontech.common.device.config.model.MCT440Configuration;
import com.cannontech.common.device.config.model.MCT470Configuration;
import com.cannontech.common.pao.definition.model.PaoTag;

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

        public PaoTag getSupportedDeviceTag() {
            return PaoTag.DEVICE_CONFIGURATION_470;
        }
    },
    MCT440 {
        public ConfigurationBase getConfigurationClass() {
            return new MCT440Configuration();
        }

        public String getConfigurationTemplateName() {
            return "MCT 440 Configuration";
        }

        public PaoTag getSupportedDeviceTag() {
            return PaoTag.DEVICE_CONFIGURATION_440;
        }
    },
    MCT430 {
        public ConfigurationBase getConfigurationClass() {
            return new MCT430Configuration();
        }

        public String getConfigurationTemplateName() {
            return "MCT 430 Configuration";
        }

        public PaoTag getSupportedDeviceTag() {
            return PaoTag.DEVICE_CONFIGURATION_430;
        }
    },
    MCT420 {
        public ConfigurationBase getConfigurationClass() {
            return new MCT420Configuration();
        }
        
        public String getConfigurationTemplateName() {
            return "MCT 420 Configuration";
        }
        
        public PaoTag getSupportedDeviceTag() {
            return PaoTag.DEVICE_CONFIGURATION_420;
        }
    },
    MCT410 {
        public ConfigurationBase getConfigurationClass() {
            return new MCT410Configuration();
        }

        public String getConfigurationTemplateName() {
            return "MCT 410 Configuration";
        }

        public PaoTag getSupportedDeviceTag() {
            return null;
        }
    },
    DNP {
        public ConfigurationBase getConfigurationClass() {
            return new DNPConfiguration();
        };
        
        public String getConfigurationTemplateName() {
            return "DNP Configuration";
        }
        
        public PaoTag getSupportedDeviceTag() {
            return PaoTag.DEVICE_CONFIGURATION_DNP;
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
     * Method to get a device tag representing the devices this configuration type
     * supports
     * @return Array of device types
     */
    public abstract PaoTag getSupportedDeviceTag();
}

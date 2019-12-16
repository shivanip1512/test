package com.cannontech.azure.service;

import com.cannontech.azure.model.AzureServices;
import com.cannontech.cloud.service.CloudService;
import com.cannontech.message.model.ConfigurationSettings;

/**
 * This is base class for all azure services.
 */
public abstract class AzureCloudService implements CloudService {

    /**
     * Returns name of the azure service
     */
    public abstract AzureServices getName();

    /**
     * Gets configuration setting for this azure service.
     */
    public abstract ConfigurationSettings getConfigurationSetting();

}

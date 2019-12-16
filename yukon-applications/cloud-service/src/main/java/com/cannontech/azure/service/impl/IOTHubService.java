package com.cannontech.azure.service.impl;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.azure.model.AzureServices;
import com.cannontech.azure.service.AzureCloudService;
import com.cannontech.data.provider.DataProvider;
import com.cannontech.message.model.ConfigurationSettings;
import com.cannontech.message.model.SystemData;

/**
 * This service connects to IOT hub and push data on it.
 * It will be pushing telemetry and property data to show on IOT dashboard.
 */
@Service
public class IOTHubService extends AzureCloudService {
    Logger log = (Logger) LogManager.getLogger(IOTHubService.class);

    @Autowired DataProvider dataProviderService;

    @Override
    public ConfigurationSettings getConfigurationSetting() {
        log.info("Get Configuration setting for IOT Hub Service");
        // Get configuration from cache
        ConfigurationSettings config = dataProviderService.getConfigurationSetting(getName());
        return config;
    }

    @Override
    public void createConnection() {
        // Create connection to IOT Hub
        ConfigurationSettings config = getConfigurationSetting();
    }

    @Override
    public boolean shouldStart() {
        return true;
    }

    @Override
    public void start() {
        log.info("Starting IOT Hub service");
        ConfigurationSettings config = getConfigurationSetting();
        // start scheduler with the frequency available in the config information
        // This scheduler will run at defined frequency, when scheduler run it will call createConnection()
        // for creating connection with IOT Hub and then will push data, prepareAndPushData()
        createConnection();
        prepareAndPushData();

    }

    /* This will read data from cache and push it */
    private void prepareAndPushData() {
        Map<String, SystemData> data = dataProviderService.getSystemInformation();
        pushData();
    }

    // This method can be further split based on what data is getting pushed, Telemetry, Property
    private void pushData() {
        // Code to push data
    }

    @Override
    public AzureServices getName() {
        return AzureServices.IOT_HUB_SERVICE;
    }

}

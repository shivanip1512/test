package com.cannontech.data.provider;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.azure.model.AzureServices;
import com.cannontech.message.model.ConfigurationSettings;
import com.cannontech.message.model.SystemData;
import com.cannontech.message.publisher.service.Publisher;
import com.cannontech.message.publisher.service.SupportedDataType;

/**
 * This class will be providing data required for cloud services.
 * Data will be held in cache and will be updated by the ActiveMQ Listeners.
 */
@Service
public class DataProvider {
    Logger log = (Logger) LogManager.getLogger(DataProvider.class);

    @Autowired Publisher publisher;

    // This is cache for holding configuration settings for each azure service
    Map<AzureServices, ConfigurationSettings> settings = new HashMap<>();

    // This is cache for holding system data for IOT azure service.
    // This cache will always hold the latest value
    // Key will be map of fieldName and value will be object of type SystemData
    Map<String, SystemData> systemData = new HashMap<>();

    /**
     * This method looks for configuration setting in cache.
     * If cache has the setting it will send back.
     * If not then message will be send to publisher to get data and it will wait for listener to provide data.
     */
    public ConfigurationSettings getConfigurationSetting(AzureServices service) {
        if (!settings.containsKey(service)) {
            publisher.requestData(SupportedDataType.CONFIGURATION_DATA);
            waitForResponse(service);
            log.info("Received configuration settings for service", service);
        }
        return settings.get(service);
    }
    
    /*
     * Check every 30 sec to see configuration settings are available or not.
     */
    private void waitForResponse(AzureServices service) {
        log.info("Waiting... for configuration settings for service", service);

        while (!checkIfExists(service)) {
            try {
                Thread.sleep(30000);
                log.info("Checking status....", service);
            } catch (InterruptedException e) {
                log.error("Error when waiting for configuration settings ", e);
            }
        }
    }
    
    /*
     * Check configuration settings are available in cache.
     */
    private boolean checkIfExists(AzureServices service) {
        return settings.containsKey(service);
    }

    /**
     * This method will get all the values from cache and will convert it to json and return.
     */
    public Map<String, SystemData> getSystemInformation() {
        // Read the system information cache which have latest value and pass it as string
        log.debug("Systems data: " + systemData);
        return systemData;
    }

    /**
     * ActiveMQ listener will call this method to pass the updated system information.
     * This will be updated in the cache.
     */
    public void updateSystemInformation(SystemData data) {
        log.info("Updating system information");
        systemData.put(data.getFieldName(), data);
    }

    /**
     * ActiveMQ listener will call this method to pass the updated configuration information.
     * This will be updated in the cache.
     */
    public void updateConfigurationInformation(AzureServices service, ConfigurationSettings confSettings) {
        log.info("Updating configuration information");
        settings.put(service, confSettings);
        
    }

}

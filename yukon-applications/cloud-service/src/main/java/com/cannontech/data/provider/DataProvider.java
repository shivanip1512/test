package com.cannontech.data.provider;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.azure.model.AzureServices;
import com.cannontech.message.model.ConfigurationSettings;
import com.cannontech.message.model.SupportedDataType;
import com.cannontech.message.model.SystemData;
import com.cannontech.message.publisher.service.Publisher;

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
        // If configuration information exists in cache then send it.
        // If it does not exists then send a request and wait for certain amount of time to get this information
        // If this information is not received this service cannot work, keep on waiting before quitting.

        // Call publisher which support this type of data
        log.info("Getting configuration information from cache");
        publisher.requestData(SupportedDataType.CONFIGURATION_DATA);
        return new ConfigurationSettings();
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
        log.debug("Updating system information " + data);
        systemData.put(data.getFieldName(), data);
    }

    /**
     * ActiveMQ listener will call this method to pass the updated configuration information.
     * This will be updated in the cache.
     */
    public void updateConfigurationInformation(ConfigurationSettings settings) {
        // configuration settings should have for which cloud service these are
        log.info("Updating configuration information");
    }

}

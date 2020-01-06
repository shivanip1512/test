package com.cannontech.configurationSettingPublisher.service;

import com.cannontech.services.configurationSettingMessage.model.ConfigurationSettings;

public interface ConfigurationSettingPublisherService {

    /**
     * Publish configurationSetting to Message Broker with a specified Topic.
     */
    public void publish(ConfigurationSettings configurationSetting);
}

package com.cannontech.configurationSettingPublisher.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.services.configurationSettingMessage.model.ConfigurationSettings;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class ConfigurationSettingBuilder {

    @Autowired private GlobalSettingDao globalSettingDao;

    public ConfigurationSettings buildConfigurationSetting() {

        // Build object of ConfigurationSettings with IOT-hub connection string, cloud data sending frequency and proxy setting.
        String connectionString = globalSettingDao.getString(GlobalSettingType.CLOUD_IOT_HUB_CONNECTION_STRING);
        int frequency = globalSettingDao.getInteger(GlobalSettingType.CLOUD_DATA_SENDING_FREQUENCY);
        String proxySetting = globalSettingDao.getString(GlobalSettingType.HTTP_PROXY);

        return new ConfigurationSettings(connectionString, proxySetting, frequency);
    }
}

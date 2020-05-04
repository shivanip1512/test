package com.cannontech.services.systemDataPublisher.service.impl;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.services.systemDataPublisher.listener.CloudDataConfigurationsAdvisoryListener;
import com.cannontech.services.systemDataPublisher.service.CloudDataConfigurationsPublisherService;
import com.cannontech.services.systemDataPublisher.watcher.SystemPublisherMetadataWatcher;
import com.cannontech.services.systemDataPublisher.yaml.YamlConfigManager;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfigurations;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class SystemDataServiceInitializer {

    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private CloudDataConfigurationsAdvisoryListener advisoryListener;
    @Autowired private CloudDataConfigurationsPublisherService cloudDataConfigurationsPublisherService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private SystemPublisherMetadataWatcher systemPublisherMetadataWatcher;
    @Autowired private YamlConfigManager yamlConfigManager;

    private static final Logger log = YukonLogManager.getLogger(SystemDataServiceInitializer.class);

    /**
     * The init method will read the systemDataMetadata.yaml file. Once the configuration is read
     * we will create processor based on different scalars defined in YAML file. Also using
     * the frequency field we will create & execute the scheduler to publish data on topic.
     */
    @PostConstruct
    private void init() {
        publishCloudDataConfigurations();
        new Thread(advisoryListener.advisoryListener()).start();
        new Thread(systemPublisherMetadataWatcher.watch()).start();
    }

    /**
     * Method to publish CloudDataConfigurations data to the topic.
     */
    public void publishCloudDataConfigurations() {
        String connectionString = globalSettingDao.getString(GlobalSettingType.CLOUD_IOT_HUB_CONNECTION_STRING);
        if (StringUtils.isNotEmpty(connectionString)) {
            cloudDataConfigurationsPublisherService.publish(readYamlConfiguration());
        } else {
            publishEmptyConfiguration();
        }

        asyncDynamicDataSource.addDatabaseChangeEventListener(event -> {
            if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.CLOUD_IOT_HUB_CONNECTION_STRING)) {
                String connectionStr = globalSettingDao.getString(GlobalSettingType.CLOUD_IOT_HUB_CONNECTION_STRING);
                if (StringUtils.isNotEmpty(connectionStr)) {
                    cloudDataConfigurationsPublisherService.publish(readYamlConfiguration());
                } else {
                    publishEmptyConfiguration();
                }
            }
        });
    }

    private void publishEmptyConfiguration() {
        log.info("Not publishing Cloud Data Configurations as global setting Cloud IoT Hub Connection String is not set ");
        CloudDataConfigurations conf = new CloudDataConfigurations();
        cloudDataConfigurationsPublisherService.publish(conf);
    }

    /**
     * This method will read the yaml configuration file.
     */
    private CloudDataConfigurations readYamlConfiguration() {
        CloudDataConfigurations cloudDataConfigurations = yamlConfigManager.getCloudDataConfigurations();
        if (log.isDebugEnabled()) {
            cloudDataConfigurations.getConfigurations()
                    .stream()
                    .forEach(configuration -> {
                        log.debug("Retrieved CloudDataConfiguration values = " + configuration.toString());
                    });
        }
        return cloudDataConfigurations;
    }
}

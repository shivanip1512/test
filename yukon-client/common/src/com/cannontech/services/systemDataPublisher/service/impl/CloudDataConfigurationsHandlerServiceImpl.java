package com.cannontech.services.systemDataPublisher.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.services.systemDataPublisher.context.NetworkManagerDBConfig;
import com.cannontech.services.systemDataPublisher.processor.SystemDataHandler;
import com.cannontech.services.systemDataPublisher.service.CloudDataConfigurationsHandlerService;
import com.cannontech.services.systemDataPublisher.service.SystemDataPublisher;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;

@Service
public class CloudDataConfigurationsHandlerServiceImpl implements CloudDataConfigurationsHandlerService {

    @Autowired private NetworkManagerDBConfig networkManagerDBConfig;
    @Autowired private SystemDataHandler systemDataHandler;

    @Override
    public void handleCloudConfiguration(List<CloudDataConfiguration> cloudDataConfigurations) {

        List<CloudDataConfiguration> cloudConfigurationToProcess = filterRelevantConfigurations(cloudDataConfigurations);
        systemDataHandler.handle(cloudConfigurationToProcess);

    }

    /**
     * This method filters the fields which Yukon will process.
     * Yukon will process only Yukon and Others section fields.
     */
    private List<CloudDataConfiguration> filterRelevantConfigurations(
            List<CloudDataConfiguration> cloudDataConfigurations) {

        boolean networkManagerDBConfigured = networkManagerDBConfig.isNetworkManagerDBConnectionConfigured();

        List<CloudDataConfiguration> releventConfigurations = cloudDataConfigurations.stream()
                .filter(e -> (e.getDataPublisher() == SystemDataPublisher.YUKON
                        || e.getDataPublisher() == SystemDataPublisher.OTHER
                        || (networkManagerDBConfigured && e.getDataPublisher() == SystemDataPublisher.NETWORK_MANAGER
                                && StringUtils.isNotEmpty(e.getSource()))))
                .collect(Collectors.toList());
        return releventConfigurations;
    }

}

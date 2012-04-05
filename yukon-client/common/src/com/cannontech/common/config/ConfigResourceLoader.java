package com.cannontech.common.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import com.cannontech.common.config.retrieve.ConfigFile;
import com.cannontech.common.config.retrieve.ConfigFileRetriever;

public class ConfigResourceLoader {

    private List<ConfigFileRetriever> configFileRetrievers;
    
    public Resource getResource(ConfigFile config) {
        for (ConfigFileRetriever retriever : configFileRetrievers) {
            if (retriever.getType() == config) return retriever.retrieve();
        }
        throw new IllegalArgumentException("Unknown config file retriever: " + config);
    }
    
    @Autowired
    public void setConfigFileRetrievers(List<ConfigFileRetriever> configFileRetrievers) {
        this.configFileRetrievers = configFileRetrievers;
    }
    
}
package com.cannontech.common.config.retrieve;

import org.springframework.core.io.Resource;

/**
 * Interface to provide config file retrieving strategies.
 */
public interface ConfigFileRetriever {
    
    public Resource retrieve();
    
    public ConfigFile getType();
    
}
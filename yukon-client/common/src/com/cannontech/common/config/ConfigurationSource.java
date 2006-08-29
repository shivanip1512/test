package com.cannontech.common.config;

import com.cannontech.common.exception.BadConfigurationException;

public interface ConfigurationSource {

    /**
     * Returns value of key from the configuration file. Throws an exception if the key did not
     * exist in the file.
     * @param key
     * @throws BadConfigurationException if key didn't exist
     * @return the value fo the key, never null
     */
    public String getRequiredString(String key);

    /**
     * Returns value of key from the configuration file. Returns a blank string if
     * the key did not exist in the file.
     * @param key
     * @return the value fo the key, or "" if the key didn't exist
     */
    public String getString(String key);

}
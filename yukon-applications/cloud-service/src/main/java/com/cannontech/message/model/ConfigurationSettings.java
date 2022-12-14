package com.cannontech.message.model;

import java.io.Serializable;

/**
 * This object hold configuration settings for Cloud service.
 * This object can be converted per service basis, if required in future.
 */
public class ConfigurationSettings implements Serializable {

    private static final long serialVersionUID = 1L;
    private String connectionString;
    private String proxySetting;
    private int frequency;

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getProxySetting() {
        return proxySetting;
    }

    public void setProxySetting(String proxySetting) {
        this.proxySetting = proxySetting;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}

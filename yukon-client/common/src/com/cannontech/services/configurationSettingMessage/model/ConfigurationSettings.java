package com.cannontech.services.configurationSettingMessage.model;

import java.io.Serializable;

public class ConfigurationSettings implements Serializable {

    private static final long serialVersionUID = 1L;
    private String connectionString;
    private String proxySetting;
    private int frequency;

    public ConfigurationSettings(String connectionString, String proxySetting, int frequency) {
        this.connectionString = connectionString;
        this.proxySetting = proxySetting;
        this.frequency = frequency;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public String getProxySetting() {
        return proxySetting;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public void setProxySetting(String proxySetting) {
        this.proxySetting = proxySetting;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}

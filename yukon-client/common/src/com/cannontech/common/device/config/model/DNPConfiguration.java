package com.cannontech.common.device.config.model;

import java.io.Serializable;

import com.cannontech.common.device.config.dao.ConfigurationType;

public class DNPConfiguration extends ConfigurationBase implements Serializable {
    
    public static final int DEFAULT_DNP_CONFIG_ID = -1;
    
    private int internalRetries = 2;
    private boolean localTime = false;
    private boolean enableDnpTimesyncs = false;
    private boolean omitTimeRequest = false;
    private boolean enableUnsolicitedMessages = true;
    
    @Override
    public ConfigurationType getType() {
        return ConfigurationType.DNP;
    }

    public int getInternalRetries() {
        return internalRetries;
    }

    public void setInternalRetries(int internalRetries) {
        this.internalRetries = internalRetries;
    }

    public boolean isLocalTime() {
        return localTime;
    }

    public void setLocalTime(boolean localTime) {
        this.localTime = localTime;
    }

    public boolean isEnableDnpTimesyncs() {
        return enableDnpTimesyncs;
    }

    public void setEnableDnpTimesyncs(boolean enableDnpTimesyncs) {
        this.enableDnpTimesyncs = enableDnpTimesyncs;
    }

    public boolean isOmitTimeRequest() {
        return omitTimeRequest;
    }

    public void setOmitTimeRequest(boolean omitTimeRequest) {
        this.omitTimeRequest = omitTimeRequest;
    }

    public boolean isEnableUnsolicitedMessages() {
        return enableUnsolicitedMessages;
    }

    public void setEnableUnsolicitedMessages(boolean enableUnsolicitedMessages) {
        this.enableUnsolicitedMessages = enableUnsolicitedMessages;
    }
}

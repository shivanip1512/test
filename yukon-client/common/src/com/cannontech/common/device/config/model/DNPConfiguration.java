package com.cannontech.common.device.config.model;

import com.cannontech.common.device.config.dao.ConfigurationType;

public class DNPConfiguration extends ConfigurationBase {

    public static final int InternalRetriesDefault = 2;
    public static final boolean LocalTimeDefault = false;
    public static final boolean EnableTimesyncsDefault = false;
    public static final boolean OmitTimeRequestDefault = false;
    public static final boolean EnableUnsolicitedDefault = true;
    
    private int internalRetries = InternalRetriesDefault;
    private boolean localTime = LocalTimeDefault;
    private boolean enableDnpTimesyncs = EnableTimesyncsDefault;
    private boolean omitTimeRequest = OmitTimeRequestDefault;
    private boolean enableUnsolicitedMessages = EnableTimesyncsDefault;
    
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

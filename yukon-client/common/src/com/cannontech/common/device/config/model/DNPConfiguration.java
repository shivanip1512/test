package com.cannontech.common.device.config.model;

import com.cannontech.common.device.config.dao.ConfigurationType;

public class DNPConfiguration extends ConfigurationBase {

    public static final Integer InternalRetriesDefault = 2;
    public static final Boolean LocalTimeDefault = false;
    public static final Boolean EnableTimesyncsDefault = false;
    public static final Boolean OmitTimeRequestDefault = false;
    public static final Boolean EnableUnsolicitedDefault = true;
    
    private Integer internalRetries = InternalRetriesDefault;
    private Boolean localTime = LocalTimeDefault;
    private Boolean enableDnpTimesyncs = EnableTimesyncsDefault;
    private Boolean omitTimeRequest = OmitTimeRequestDefault;
    private Boolean enableUnsolicitedMessages = EnableTimesyncsDefault;
    
    @Override
    public ConfigurationType getType() {
        return ConfigurationType.DNP;
    }

    public Integer getInternalRetries() {
        return internalRetries;
    }

    public void setInternalRetries(Integer internalRetries) {
        this.internalRetries = internalRetries;
    }

    public Boolean getLocalTime() {
        return localTime;
    }

    public void setLocalTime(Boolean localTime) {
        this.localTime = localTime;
    }

    public Boolean getEnableDnpTimesyncs() {
        return enableDnpTimesyncs;
    }

    public void setEnableDnpTimesyncs(Boolean enableDnpTimesyncs) {
        this.enableDnpTimesyncs = enableDnpTimesyncs;
    }

    public Boolean getOmitTimeRequest() {
        return omitTimeRequest;
    }

    public void setOmitTimeRequest(Boolean omitTimeRequest) {
        this.omitTimeRequest = omitTimeRequest;
    }

    public Boolean getEnableUnsolicitedMessages() {
        return enableUnsolicitedMessages;
    }

    public void setEnableUnsolicitedMessages(Boolean enableUnsolicitedMessages) {
        this.enableUnsolicitedMessages = enableUnsolicitedMessages;
    }

}

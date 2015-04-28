package com.cannontech.common.device.config.model;

/**
 * A small model object for the DNP configuration data. The DNP configuration data is
 * displayed in a couple special places (DB editor and CBC editor pages) and merits its
 * own model object to simplify the process.
 */
public class DNPConfiguration extends LightDeviceConfiguration {
    private final static long serialVersionUID = 1L;

    private int internalRetries = 2;
    private boolean localTime = false;
    private boolean enableDnpTimesyncs = false;
    private boolean omitTimeRequest = false;
    private boolean enableUnsolicitedMessageClass1 = true;
    private boolean enableUnsolicitedMessageClass2 = true;
    private boolean enableUnsolicitedMessageClass3 = true;
    
    public DNPConfiguration(Integer configurationId, String name, String description) {
        super(configurationId, name, description);
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

    public boolean isEnableUnsolicitedMessageClass1() {
        return enableUnsolicitedMessageClass1;
    }
    
    public void setEnableUnsolicitedMessageClass1(boolean enableUnsolicitedMessageClass1) {
        this.enableUnsolicitedMessageClass1 = enableUnsolicitedMessageClass1;
    }
    
    public boolean isEnableUnsolicitedMessageClass2() {
        return enableUnsolicitedMessageClass2;
    }
    
    public void setEnableUnsolicitedMessageClass2(boolean enableUnsolicitedMessageClass2) {
        this.enableUnsolicitedMessageClass2 = enableUnsolicitedMessageClass2;
    }
    
    public boolean isEnableUnsolicitedMessageClass3() {
        return enableUnsolicitedMessageClass3;
    }
    
    public void setEnableUnsolicitedMessageClass3(boolean enableUnsolicitedMessageClass3) {
        this.enableUnsolicitedMessageClass3 = enableUnsolicitedMessageClass3;
    }
}
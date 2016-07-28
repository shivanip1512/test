package com.cannontech.web.rfn.dataStreaming.model;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.web.rfn.dataStreaming.DataStreamingConfigException;

public class VerificationInformation {
    
    private boolean verificationPassed;
    private DataStreamingConfig configuration;
    private List<GatewayLoading> gatewayLoadingInfo = new ArrayList<GatewayLoading>();
    private List<DeviceUnsupported> deviceUnsupported = new ArrayList<DeviceUnsupported>();
    private List<DataStreamingConfigException> exceptions = new ArrayList<>();
    
    public boolean isVerificationPassed() {
        return verificationPassed;
    }

    public void setVerificationPassed(boolean verificationPassed) {
        this.verificationPassed = verificationPassed;
    }

    public List<DataStreamingConfigException> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<DataStreamingConfigException> exceptions) {
        this.exceptions = exceptions;
    }

    public DataStreamingConfig getConfiguration() {
        return configuration;
    }
    
    public void setConfiguration(DataStreamingConfig configuration) {
        this.configuration = configuration;
    }
    
    public List<GatewayLoading> getGatewayLoadingInfo() {
        return gatewayLoadingInfo;
    }
    
    public void setGatewayLoadingInfo(List<GatewayLoading> gatewayLoadingInfo) {
        this.gatewayLoadingInfo = gatewayLoadingInfo;
    }
    
    public List<DeviceUnsupported> getDeviceUnsupported() {
        return deviceUnsupported;
    }
    
    public void setDeviceUnsupported(List<DeviceUnsupported> deviceUnsupported) {
        this.deviceUnsupported = deviceUnsupported;
    }
    

}

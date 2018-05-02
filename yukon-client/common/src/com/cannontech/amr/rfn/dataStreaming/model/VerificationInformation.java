package com.cannontech.amr.rfn.dataStreaming.model;

import java.util.ArrayList;
import java.util.List;

public class VerificationInformation {
    
    private boolean verificationPassed;
    private DataStreamingConfig configuration;
    private List<GatewayLoading> gatewayLoadingInfo = new ArrayList<>();
    private List<DeviceUnsupported> deviceUnsupported = new ArrayList<>();
    private List<DataStreamingConfigException> exceptions = new ArrayList<>();
    private List<Integer> failedVerificationDevices = new ArrayList<>();
    
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
    
    public void setFailedVerificationDevices(List<Integer> failedVerificationDevices) {
        this.failedVerificationDevices = failedVerificationDevices;
    }
    
    public List<Integer> getFailedVerificationDevices() {
        return failedVerificationDevices;
    }

}

package com.cannontech.web.rfn.dataStreaming.model;

import java.util.ArrayList;
import java.util.List;

public class VerificationInformation {
    
    private DataStreamingConfig configuration;
    private List<GatewayLoading> gatewayLoadingInfo = new ArrayList<GatewayLoading>();
    private List<DeviceUnsupported> deviceUnsupported = new ArrayList<DeviceUnsupported>();
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

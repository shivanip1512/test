package com.cannontech.common.rfn.simulation;

import java.io.Serializable;

import com.cannontech.common.rfn.message.datastreaming.device.DeviceDataStreamingConfigError;

/**
 * Settings for the data streaming simulator that determine how it responds to requests.
 */
public class SimulatedDataStreamingSettings implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private boolean overloadGatewaysOnVerification;
    private DeviceDataStreamingConfigError deviceErrorOnVerification;
    private boolean networkManagerFailOnVerification;
    
    private boolean overloadGatewaysOnConfig;
    private DeviceDataStreamingConfigError deviceErrorOnConfig;
    private boolean networkManagerFailOnConfig;
    
    //VERIFICATION
    
    public boolean isOverloadGatewaysOnVerification() {
        return overloadGatewaysOnVerification;
    }
    
    public void setOverloadGatewaysOnVerification(boolean overloadGatewaysOnVerification) {
        this.overloadGatewaysOnVerification = overloadGatewaysOnVerification;
    }
    
    public DeviceDataStreamingConfigError getDeviceErrorOnVerification() {
        return deviceErrorOnVerification;
    }
    
    public void setDeviceErrorOnVerification(DeviceDataStreamingConfigError deviceErrorOnVerification) {
        this.deviceErrorOnVerification = deviceErrorOnVerification;
    }
    
    public boolean isNetworkManagerFailOnVerification() {
        return networkManagerFailOnVerification;
    }
    
    public void setNetworkManagerFailOnVerification(boolean networkManagerFailOnVerification) {
        this.networkManagerFailOnVerification = networkManagerFailOnVerification;
    }
    
    //CONFIG
    
    public boolean isOverloadGatewaysOnConfig() {
        return overloadGatewaysOnConfig;
    }
    
    public void setOverloadGatewaysOnConfig(boolean overloadGatewaysOnConfig) {
        this.overloadGatewaysOnConfig = overloadGatewaysOnConfig;
    }
    
    public DeviceDataStreamingConfigError getDeviceErrorOnConfig() {
        return deviceErrorOnConfig;
    }
    
    public void setDeviceErrorOnConfig(DeviceDataStreamingConfigError deviceErrorOnConfig) {
        this.deviceErrorOnConfig = deviceErrorOnConfig;
    }
    
    public boolean isNetworkManagerFailOnConfig() {
        return networkManagerFailOnConfig;
    }
    
    public void setNetworkManagerFailOnConfig(boolean networkManagerFailOnConfig) {
        this.networkManagerFailOnConfig = networkManagerFailOnConfig;
    }
    
    //TODO
    //Device specific errors
    //all DeviceDataStreamingConfigError values?
    
    
}

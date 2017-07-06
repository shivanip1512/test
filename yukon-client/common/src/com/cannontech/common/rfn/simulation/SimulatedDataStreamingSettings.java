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
    private boolean deviceErrorOnVerificationEnabled;
    private boolean networkManagerFailOnVerification;
    private int numberOfDevicesToErrorOnVerification;
    
    private boolean overloadGatewaysOnConfig;
    private DeviceDataStreamingConfigError deviceErrorOnConfig;
    private boolean deviceErrorOnConfigEnabled;
    private boolean networkManagerFailOnConfig;
    private int numberOfDevicesToErrorOnConfig;
    
    private boolean acceptedWithError;
    
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
    
    public int getNumberOfDevicesToErrorOnVerification() {
        return numberOfDevicesToErrorOnVerification;
    }

    public void setNumberOfDevicesToErrorOnVerification(int numberOfDevicesToErrorOnVerification) {
        this.numberOfDevicesToErrorOnVerification = numberOfDevicesToErrorOnVerification;
    }
    
    public void setDeviceErrorOnVerificationEnabled(boolean deviceErrorOnVerificationEnabled) {
        this.deviceErrorOnVerificationEnabled = deviceErrorOnVerificationEnabled;
    }
    
    public boolean isDeviceErrorOnVerificationEnabled() {
        return deviceErrorOnVerificationEnabled;
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

    
    public int getNumberOfDevicesToErrorOnConfig() {
        return numberOfDevicesToErrorOnConfig;
    }

    public void setNumberOfDevicesToErrorOnConfig(int numberOfDevicesToErrorOnConfig) {
        this.numberOfDevicesToErrorOnConfig = numberOfDevicesToErrorOnConfig;
    }
    
    public boolean isAcceptedWithError() {
        return acceptedWithError;
    }

    public void setAcceptedWithError(boolean acceptedWithError) {
        this.acceptedWithError = acceptedWithError;
    }
    
    public void setDeviceErrorOnConfigEnabled(boolean deviceErrorOnConfigEnabled) {
        this.deviceErrorOnConfigEnabled = deviceErrorOnConfigEnabled;
    }
    
    public boolean isDeviceErrorOnConfigEnabled() {
        return deviceErrorOnConfigEnabled;
    }
}

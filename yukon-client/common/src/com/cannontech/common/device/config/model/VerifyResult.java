package com.cannontech.common.device.config.model;

import java.util.List;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.common.pao.DisplayablePao;
import com.google.common.collect.Lists;

public class VerifyResult {
    private DisplayablePao device;
    private LightDeviceConfiguration config;
    private List<String> matching = Lists.newArrayList();
    private List<String> discrepancies = Lists.newArrayList();
    private DeviceError error;

    public VerifyResult(DisplayablePao device, LightDeviceConfiguration config) {
        this.device = device;
        this.config = config;
    }
    
    public VerifyResult(DisplayablePao device) {
        this.device = device;
    }

    public DisplayablePao getDevice() {
        return device;
    }
    
    public void setDevice(DisplayablePao device) {
        this.device = device;
    }

    public LightDeviceConfiguration getConfig() {
        return config;
    }

    public void setConfig(LightDeviceConfiguration config) {
        this.config = config;
    }

    public List<String> getMatching() {
        return matching;
    }
    
    public List<String> getDiscrepancies() {
        return discrepancies;
    }
    
    public void setError(DeviceError error) {
        this.error = error;
    }
    
    public DeviceError getError() {
        return error;
    }
    
    public boolean isSynced() {
        return getDiscrepancies().isEmpty();
    }
}